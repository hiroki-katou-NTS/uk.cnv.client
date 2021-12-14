package nts.uk.ctx.at.request.dom.applicationreflect.service;

/*import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.PriorStampRequestAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.ScheAndRecordSameChangeFlg;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.ScheTimeReflectRequesAtr;*/
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.persistence.exceptions.OptimisticLockException;

import lombok.extern.slf4j.Slf4j;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.gul.error.ThrowableAnalyzer;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.applicationreflect.manager.algorithm.employee.CreateRequireReflectionProcess;
import nts.uk.ctx.at.request.dom.applicationreflect.manager.algorithm.employee.ReflectionProcess;
import nts.uk.ctx.at.request.dom.applicationreflect.object.OneDayReflectStatusOutput;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.AppReflectProcessRecord;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExecutionLogRequestImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExecutionTypeExImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Slf4j
public class AppReflectManagerImpl implements AppReflectManager {
	@Inject
	private ApplicationRepository appRepo;
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRegister;
	@Inject
	private AppReflectProcessRecord proRecord;
	@Resource
	private SessionContext scContext;
	private AppReflectManager self;

	@Inject
    private ExecutionLogRequestImport executionLogRequestImport;	
	
	@Inject
	private CreateRequireReflectionProcess createRequireReflectionProcess;
	
	@PostConstruct
	public void postContruct() {
		this.self = scContext.getBusinessObject(AppReflectManager.class);
	}
	
	@Override	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reflectEmployeeOfApp(Application appInfor, List<SEmpHistImport> sEmpHistImport, 
			ExecutionTypeExImport execuTionType, String excLogId, int currentRecord) {
		try {
			self.reflectEmployeeOfAppWithTransaction(appInfor, sEmpHistImport, execuTionType, excLogId);
			
		} catch(Exception ex) {
			boolean isError = new ThrowableAnalyzer(ex).findByClass(OptimisticLockException.class).isPresent();
			if(!isError) {
				log.info(isError + " 反映処理：　申請ID　＝　" + appInfor.getAppID() + " 申請者ID　＝　" + appInfor.getEmployeeID());
				//return khong dung duoc do neu 1 ngay co nhieu don thi no cu tim trang thai don haneimachi lam khong ket thuc duoc vong lap
				throw ex;
			}		
			if(!excLogId.isEmpty()) {
				proRecord.createLogError(appInfor.getEmployeeID(), appInfor.getAppDate().getApplicationDate(), excLogId);	
			}
			int newCountRerun = currentRecord + 1;
			if (newCountRerun == 10) {
				log.info(isError + " 反映処理：　申請ID　＝　" + appInfor.getAppID() + " 申請者ID　＝　" + appInfor.getEmployeeID());
				throw ex;
			}
			try {
				Thread.sleep(newCountRerun * 50);				
			} catch (InterruptedException e){
				throw ex;
			}	
			
			self.reflectEmployeeOfApp(appInfor, sEmpHistImport, execuTionType, excLogId, newCountRerun);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional
	public void reflectEmployeeOfAppWithTransaction(Application appInfor, List<SEmpHistImport> sEmpHistImport, ExecutionTypeExImport execuTionType,
			String excLogId) {
		String companyID = AppContexts.user().companyId();
		
		// 再実行かどうか判断する (xác nhận xem có thực hiện lại hay k)
		List<GeneralDate> lstDate = new ArrayList<>();
		for(int i = 0; appInfor.getOpAppStartDate().get().getApplicationDate().daysTo(appInfor.getOpAppEndDate().get().getApplicationDate()) - i >= 0; i++){
			GeneralDate loopDate = appInfor.getOpAppStartDate().get().getApplicationDate().addDays(i);

            Boolean isCalWhenLock = this.executionLogRequestImport.isCalWhenLock(excLogId, 2);
          //TODO: new Process
			Pair<Optional<OneDayReflectStatusOutput>, Optional<AtomTask>> resultAfterReflect = ReflectionProcess
					.process(createRequireReflectionProcess.createImpl(), companyID, appInfor,
							isCalWhenLock == null ? false : isCalWhenLock, loopDate, sEmpHistImport);
			
			resultAfterReflect.getRight().ifPresent(x -> {
				x.run();
			});
			resultAfterReflect.getKey().ifPresent(x -> {
				appInfor.getReflectionStatus().getListReflectionStatusOfDay().replaceAll(y -> {
					if(y.getTargetDate().equals(loopDate)) {
						return x.createReflectStatus(loopDate);
					}else {
						return y;
					}
				});
				appRepo.update(appInfor);
				if (x.reflect()) {
					lstDate.add(loopDate);
				}
			});
		}
		//暫定データの登録
		if (!lstDate.isEmpty()) {
			interimRegister.registerDateChange(companyID, appInfor.getEmployeeID(),
					lstDate.stream().distinct().collect(Collectors.toList()));
		}
	}	
}
