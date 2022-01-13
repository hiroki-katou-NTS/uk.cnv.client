package nts.uk.screen.at.app.dailyperformance.correction.lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.ApprovalStatusAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.ApproveRootStatusForEmpImport;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalStatusForEmployee;
import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.IGetDailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.StatusActualDay;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.StatusLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.AvailabilityAtr;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.Identification;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.IdentityProcessUseSet;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentificationRepository;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentityProcessUseSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author ThanhNX 日の実績の状況を取得する
 */
@Stateless
public class GetDailyLock implements IGetDailyLock {

	@Inject
	private DPLock dpLock;
	
	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	
	@Inject
	private ApprovalStatusAdapter approvalStatusAdapter;
	
	@Inject
	private IdentityProcessUseSetRepository identityProcessUseSetRepo;
	
	@Inject
	private IdentificationRepository identificationRepository;

	@Override
	public DailyLock getDailyLock(StatusActualDay satusActual) {
		String comapnyId = AppContexts.user().companyId();
		DPLockDto dpLockDto = dpLock.checkLockAll(comapnyId, satusActual.getEmployeeId(), satusActual.getDate());
		//実績ロックされているか判定する
		boolean lockDay = dpLock.checkLockDay(dpLockDto.getLockDayAndWpl(), satusActual.getClosureId(),
				satusActual.getEmployeeId(), satusActual.getDate());
		//処理年月と締め期間を取得する
		//対象の職場が就業確定されているかチェックする
		boolean lockWpl = dpLock.checkLockWork(dpLockDto.getLockDayAndWpl(), satusActual.getWplId(),
				satusActual.getEmployeeId(), satusActual.getDate());
		//「ロック状態一覧.過去実績のロック」を更新する
		boolean lockHist = dpLock.lockHist(dpLockDto.getLockHist(), satusActual.getEmployeeId(), satusActual.getDate());
		
		
		// ドメインモデル「承認処理の利用設定」を取得する
		///取得している「承認処理の利用設定．日の承認者確認を利用する」をチェックする
		Optional<ApprovalProcessingUseSetting> optApprovalUse = iFindDataDCRecord.findApprovalByCompanyId(comapnyId);
		boolean lockApproval = false;
		if(optApprovalUse.isPresent() && optApprovalUse.get().getUseDayApproverConfirm()!= null && optApprovalUse.get().getUseDayApproverConfirm().booleanValue()) {
			List<ApproveRootStatusForEmpImport> lstApprovalStatus = approvalStatusAdapter
					.getApprovalByListEmplAndListApprovalRecordDateNew(Arrays.asList(satusActual.getDate()),
							Arrays.asList(satusActual.getEmployeeId()), 1); //1 : 日別確認 
			if(!lstApprovalStatus.isEmpty() && lstApprovalStatus.get(0).getApprovalStatus() != ApprovalStatusForEmployee.UNAPPROVED) {
				lockApproval = true;
			}
		}
		
		// ドメインモデル「本人確認処理の利用設定」を取得する
		Optional<IdentityProcessUseSet> identityProcessUseSet = identityProcessUseSetRepo.findByKey(comapnyId);
		boolean lockConfirmDay = false;
		// 取得している「本人確認処理の利用設定．日の本人確認を利用する」をチェックする
		if (identityProcessUseSet.isPresent() && identityProcessUseSet.get().isUseConfirmByYourself()) {
			List<Identification> indentitys = identificationRepository.findByEmployeeID(satusActual.getEmployeeId(), Arrays.asList(satusActual.getDate()));
			if (!indentitys.isEmpty()) {
				lockConfirmDay=true;
			}
		}

		return new DailyLock(satusActual.getEmployeeId(), satusActual.getDate(),
				lockDay ? StatusLock.LOCK : StatusLock.UN_LOCK, lockWpl ? StatusLock.LOCK : StatusLock.UN_LOCK,
				StatusLock.UN_LOCK, 
				StatusLock.UN_LOCK, 
				lockApproval? StatusLock.LOCK : StatusLock.UN_LOCK, //日別実績の承認
				lockConfirmDay? StatusLock.LOCK : StatusLock.UN_LOCK, //日別実績の確認
				lockHist ? StatusLock.LOCK : StatusLock.UN_LOCK);
	}

}
