package nts.uk.ctx.at.request.dom.applicationreflect.service;

/*import nts.arc.task.data.TaskDataSetter;*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.getapp.GetApplicationForReflect;
import nts.uk.ctx.at.request.dom.applicationreflect.service.getapp.GetApplicationRequireImpl;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExeStateOfCalAndSumImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExecutionLogRequestImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.ExecutionTypeExImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.SetInforReflAprResultImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.TargetPersonImport;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.dailymonthlyprocessing.TargetPersonRequestImport;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AppReflectManagerFromRecordImpl implements AppReflectManagerFromRecord {
	@Inject
	private TargetPersonRequestImport targetPerson;
	@Inject
	private ExecutionLogRequestImport execuLog;
	@Inject
	private ApplicationRepository applicationRepo;
	@Inject
	private AppReflectManager appRefMng;
	@Inject
	private ManagedParallelWithContext managedParallelWithContext;
	@Inject
	private GetApplicationRequireImpl getApp;

	@Inject
	private ApplicationRepository repoApp;
	
	@Inject
	private EmployeeRequestAdapter employeeAdapter;
	
	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepository;

	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public ProcessStateReflect applicationRellect(String workId, DatePeriod workDate, AsyncCommandHandlerContext asyncContext) {
		val dataSetter = asyncContext.getDataSetter();
		dataSetter.setData("reflectApprovalCount", 0);
		dataSetter.setData("reflectApprovalHasError", ErrorPresent.NO_ERROR.nameId );
//		String cid = AppContexts.user().companyId();
		//ドメインモデル「申請承認設定」を取得する
//		Optional<RequestSetting> optRequesSetting = requestSettingRepo.findByCompany(cid);
//		if(!optRequesSetting.isPresent()) {
//			return ProcessStateReflect.SUCCESS;
//		}
		//再実行かどうか判断する 
		Optional<SetInforReflAprResultImport> optRefAppResult = execuLog.optReflectResult(workId, 2);//2: 承認結果反映 
		//対象社員を取得
		List<TargetPersonImport> lstPerson = targetPerson.getTargetPerson(workId)
				.stream()
				.sorted(Comparator.comparing(TargetPersonImport::getEmployeeId))
				.collect(Collectors.toList());
		ExecutionTypeExImport aprResult = optRefAppResult.isPresent() ? optRefAppResult.get().getExecutionType() 
				: ExecutionTypeExImport.NORMAL_EXECUTION;
		//InformationSettingOfEachApp reflectSetting = appSetting.getSettingOfEachApp();
		AtomicInteger count = new AtomicInteger(0);
		List<ProcessStateReflect> status = Collections.synchronizedList(new ArrayList<>());

		this.managedParallelWithContext.forEach(lstPerson, x -> {
			if(status.stream().anyMatch(c -> c == ProcessStateReflect.INTERRUPTION)) {
				return;
			}
			count.incrementAndGet();
			//データ更新
			//状態確認
			Optional<ExeStateOfCalAndSumImport> optState = execuLog.executionStatus(workId);
			if(optState.isPresent() && optState.get() == ExeStateOfCalAndSumImport.START_INTERRUPTION) {
				asyncContext.finishedAsCancelled();	
				dataSetter.updateData("reflectApprovalStatus", ExecutionStatusReflect.STOPPING.nameId);
				status.add(ProcessStateReflect.INTERRUPTION);
			}
			//処理した社員の実行状況を「完了」にする
			execuLog.updateLogInfo(x.getEmployeeId(), workId, 2, 0);
			execuLog.updateLogInfo(workId, 2, 0);
			if(dataSetter != null) {
				dataSetter.updateData("reflectApprovalStatus", ExecutionStatusReflect.DONE.nameId);	
			}	
			
			dataSetter.updateData("reflectApprovalStatus", ExecutionStatusReflect.PROCESSING.nameId);
			dataSetter.updateData("reflectApprovalCount", count);
			//社員の申請を反映 (Phản ánh nhân viên)
			this.reflectAppOfEmployee(workId, x.getEmployeeId(),
					workDate, 
					aprResult);
		});
		if(status.stream().anyMatch(c -> c == ProcessStateReflect.INTERRUPTION)) {
			return ProcessStateReflect.INTERRUPTION;
		}
		dataSetter.updateData("reflectApprovalCount", lstPerson.size());
		//処理した社員の実行状況を「完了」にする
		return ProcessStateReflect.SUCCESS;
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void reflectAppOfEmployee(String workId, String sid, DatePeriod datePeriod,
			ExecutionTypeExImport refAppResult) {
		// ドメインモデル「締め状態管理」を取得する
		List<DatePeriod> lstPeriodMag = closureStatusManagementRepository.getAllByEmpId(sid).stream()
				.map(x -> x.getPeriod()).sorted((x, y) -> x.end().compareTo(y.end())).collect(Collectors.toList());
		DatePeriod dateProcess = datePeriod;
		for(DatePeriod datePeriodCls : lstPeriodMag) {
			/**
			 * Input :      |*****************| 
			 * 
			 * closure |**********************|
			 */
			if (datePeriod.start().afterOrEquals(datePeriodCls.start())
					&& datePeriod.end().beforeOrEquals(datePeriodCls.end())) {
				return;
			}
			
			/**
			 * Input :                        |*************************************| 
			 * 
			 * closure |**********************|
			 */
			if (datePeriod.start().before(datePeriodCls.end()) && datePeriod.end().after(datePeriodCls.end())) {
				dateProcess = new DatePeriod(datePeriodCls.end().addDays(1), datePeriod.end());
			}
			
		}
		
		this.reflectAppOfAppDate(workId, sid, refAppResult, dateProcess);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void reflectAppOfAppDate(String workId, String sid, ExecutionTypeExImport refAppResult,
			DatePeriod appDatePeriod) {
		
		List<SEmpHistImport> sEmpHistImport = employeeAdapter.getEmpHist(AppContexts.user().companyId(), sid);
		
		List<Application> lstApp = this.getApps(sid, appDatePeriod, refAppResult);
		
		List<Application> lstAppTmp = new ArrayList<>(lstApp);
		if(!lstAppTmp.isEmpty()) {
			for (Application x : lstAppTmp) {
				if(!x.getOpAppStartDate().get().equals(x.getOpAppEndDate().get())) {
					this.getAppByManyDay(lstApp, sid, new DatePeriod(x.getOpAppStartDate().get().getApplicationDate(), x.getOpAppEndDate().get().getApplicationDate()), refAppResult);
				}
			}
		}				
		lstApp = lstApp.stream().sorted((a,b) -> a.getInputDate().compareTo(b.getInputDate())).collect(Collectors.toList());
		lstApp.stream().forEach(x -> {
			appRefMng.reflectEmployeeOfApp(x, sEmpHistImport, refAppResult, workId, 0);
		});
	}
	
	private void getAppByManyDay(List<Application> lstApp, String sid, DatePeriod appDate, ExecutionTypeExImport refAppResult) {
		List<Application> lstTmp = this.getApps(sid, appDate, refAppResult);
		boolean isAdd = false;
		if(!lstTmp.isEmpty()) {
			for (Application a : lstTmp) {
				List<Application> tmp = lstApp.stream().filter(b -> b.getAppID().equals(a.getAppID())).collect(Collectors.toList());
				if(tmp.isEmpty()) {
					lstApp.add(a);
					isAdd = true;
				}
			}	
		}
		if(isAdd) {
			lstTmp.stream().forEach(x -> {
				if(!x.getOpAppStartDate().get().equals(x.getOpAppEndDate().get())) {
					this.getAppByManyDay(lstApp, sid, new DatePeriod(x.getOpAppStartDate().get().getApplicationDate(), x.getOpAppEndDate().get().getApplicationDate()), refAppResult);
				}
			});	
		}
	}
	
	@Override
	public List<Application> getApps(String sid, DatePeriod datePeriod, ExecutionTypeExImport exeType) {
		return getAppsForReflect(sid, datePeriod, exeType);
	}

	//NEW
	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請反映.申請反映Mgrクラス.アルゴリズム.申請の取得.申請の取得
	 */
	@Override
	public List<Application> getAppsForReflect(String sid, DatePeriod datePeriod, ExecutionTypeExImport exeType) {
		List<Integer> lstApptype = new ArrayList<>();
		lstApptype.add(ApplicationType.WORK_CHANGE_APPLICATION.value);
		lstApptype.add(ApplicationType.BUSINESS_TRIP_APPLICATION.value);
		lstApptype.add(ApplicationType.GO_RETURN_DIRECTLY_APPLICATION.value);
		lstApptype.add(ApplicationType.STAMP_APPLICATION.value);
		lstApptype.add(ApplicationType.ANNUAL_HOLIDAY_APPLICATION.value);
		lstApptype.add(ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION.value);
		lstApptype.add(ApplicationType.OVER_TIME_APPLICATION.value);
		lstApptype.add(ApplicationType.ABSENCE_APPLICATION.value);
		lstApptype.add(ApplicationType.HOLIDAY_WORK_APPLICATION.value);
		lstApptype.add(ApplicationType.COMPLEMENT_LEAVE_APPLICATION.value);
		lstApptype.add(ApplicationType.OPTIONAL_ITEM_APPLICATION.value);
		
		List<Integer> lstRecordStatus = new ArrayList<>();
		List<Integer> lstScheStatus = new ArrayList<>();
		//実行種別を確認
		if(exeType.equals(ExecutionTypeExImport.RERUN)) {//再実行
			
			//反映済みも含めて申請を取得
			lstRecordStatus.add(ReflectedState.WAITREFLECTION.value);
			lstRecordStatus.add(ReflectedState.REFLECTED.value);
			lstScheStatus.add(ReflectedState.WAITREFLECTION.value);
			lstScheStatus.add(ReflectedState.REFLECTED.value);
		}else {//通常実行
			
			//反映待ちの申請を取得
			lstRecordStatus.add(ReflectedState.WAITREFLECTION.value);
			lstScheStatus.add(ReflectedState.WAITREFLECTION.value);	
		}
		
		List<Application> lstApp = this.getAppsWaitReflect(sid, datePeriod, exeType, lstApptype, lstScheStatus, lstRecordStatus);
		List<Application> lstAppMuildayRela = new ArrayList<>();
		//上記の申請Listからパラメータ「期間」をセットする
		if(!lstApp.isEmpty()) {
			DatePeriod period = new DatePeriod(datePeriod.start(), this.finMaxDate(lstApp));
			lstAppMuildayRela = this.getAppsWaitReflect(sid, period, exeType, lstApptype, lstScheStatus, lstRecordStatus);
		}
		lstApp.addAll(lstAppMuildayRela);
		lstApp = lstApp.stream().filter(distinctByKey(x -> x.getAppID())).collect(Collectors.toList());
		//申請日でソートする（条件：入力日　ASC）
		Collections.sort(lstApp, Comparator.comparing(Application::getInputDate));
		//申請を取得 (lấy đơn)//HOATT
		List<Application> lstResult = new ArrayList<>();
		lstApp.forEach(c -> {
			Application app = GetApplicationForReflect.getAppData(getApp.createImpl(), AppContexts.user().companyId(), c.getAppType(), c.getAppID(), c);
			if(app != null) lstResult.add(app);
		});
		
		//申請(List)を返す
		return lstResult;
	}
	private GeneralDate finMaxDate(List<Application> lstApp) {
		//SORT DESC
		lstApp.sort((a,b) -> b.getOpAppEndDate().get().getApplicationDate().compareTo(a.getOpAppEndDate().get().getApplicationDate()));
		GeneralDate maxDate = lstApp.get(0).getOpAppEndDate().get().getApplicationDate();
		return maxDate;
	}
	/**
	 * 反映待ち・反映済みの申請を取得
	 * @param sid
	 * @param datePeriod
	 * @param exeType
	 * @return
	 */
	private List<Application> getAppsWaitReflect(String sid, DatePeriod datePeriod, ExecutionTypeExImport exeType, 
			List<Integer> lstApptype, List<Integer> lstScheStatus, List<Integer> lstRecordStatus) {
		List<Application> lstApp = new ArrayList<>();
		//実行種別を確認
		if(exeType.equals(ExecutionTypeExImport.RERUN)) {//再実行
			
			//反映済みも含めて申請を取得
			lstApp = applicationRepo.getAppForReflect(sid, datePeriod, lstRecordStatus, lstScheStatus, lstApptype);
		}else {//通常実行
			
			//反映待ちの申請を取得
			lstApp = applicationRepo.getAppForReflect(sid, datePeriod, lstRecordStatus, lstScheStatus, lstApptype);
			//同日の反映済み申請を取得する
			lstApp.addAll(this.getAppReflectSameDay(sid, lstApp));
		}
		//申請(List)を返す
		return lstApp;
	}
	
	/**
	 * 同日の反映済み申請を取得する
	 * @param sid
	 * @param lstApp
	 * @return
	 */
	private List<Application> getAppReflectSameDay(String sid, List<Application> lstApp) {
		List<Application> lstResult1 = new ArrayList<>();
		//input.申請リストの申請日をdistinctで取得
		List<GeneralDate> lstDate = lstApp.stream().map(c -> c.getAppDate().getApplicationDate()).distinct().collect(Collectors.toList());
		lstDate.forEach(c -> {
			List<Application> lst = applicationRepo.getAppReflected(sid, c);
			lst.forEach(d-> {
				//該当日のみ、"反映待ち"に変更する
				d.getReflectionStatus().getListReflectionStatusOfDay().stream().filter(e -> e.getTargetDate().equals(c)).map(e -> {
					e.setActualReflectStatus(ReflectedState.WAITREFLECTION);
					e.setScheReflectStatus(ReflectedState.WAITREFLECTION);
					return  e;
				}).collect(Collectors.toList());
			});
			lstResult1.addAll(lst);
		});
		List<Application> lstResult2 = lstResult1.stream().filter(distinctByKey(x -> x.getAppID())).collect(Collectors.toList());
		return lstResult2;
	}
	
	public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		final Set<Object> seen = new HashSet<>();
		return t -> seen.add(keyExtractor.apply(t));
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public ProcessStateReflect reflectAppOfEmployeeTotal(String workId, String sid, DatePeriod datePeriod) {
		Optional<ExeStateOfCalAndSumImport> optState = execuLog.executionStatus(workId);
		if(optState.isPresent() && optState.get() == ExeStateOfCalAndSumImport.START_INTERRUPTION) {
			return ProcessStateReflect.INTERRUPTION;
		}
		//再実行かどうか判断する 
		Optional<SetInforReflAprResultImport> optRefAppResult = execuLog.optReflectResult(workId, 2);//2: 承認結果反映 
		ExecutionTypeExImport aprResult = ExecutionTypeExImport.NORMAL_EXECUTION;
		if(optRefAppResult.isPresent()) {
			aprResult = optRefAppResult.get().getExecutionType();
		}
		this.reflectAppOfEmployee(workId, sid, datePeriod, aprResult);
		return ProcessStateReflect.SUCCESS;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public void reflectApplication(List<String> lstID) {
		List<Application> lstApplication = repoApp.findByListID(AppContexts.user().companyId(), lstID);
		lstApplication = lstApplication.stream().sorted((a, b) -> a.getInputDate().compareTo(b.getInputDate()))
				.collect(Collectors.toList());
		lstApplication.stream().forEach(app -> {
			GeneralDate startDate = app.getOpAppStartDate().isPresent()
					? app.getOpAppStartDate().get().getApplicationDate()
					: app.getAppDate().getApplicationDate();
			GeneralDate endDate = app.getOpAppEndDate().isPresent() ? app.getOpAppEndDate().get().getApplicationDate()
					: app.getAppDate().getApplicationDate();
			this.reflectAppOfAppDate(IdentifierUtil.randomUniqueId(), app.getEmployeeID(), ExecutionTypeExImport.RERUN,
					new DatePeriod(startDate, endDate));
		});
	}

}
