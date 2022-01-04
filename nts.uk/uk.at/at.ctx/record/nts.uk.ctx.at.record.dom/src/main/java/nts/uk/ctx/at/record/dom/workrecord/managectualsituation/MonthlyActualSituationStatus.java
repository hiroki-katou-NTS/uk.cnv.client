package nts.uk.ctx.at.record.dom.workrecord.managectualsituation;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.DetermineActualResultLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.LockStatus;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.PerformanceType;
//import nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm.CreateEmployeeDailyPerError;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.Identification;
import nts.uk.ctx.at.record.dom.workrecord.manageactualsituation.approval.monthly.MonthlyApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.manageactualsituation.identity.monthly.IdentityConfirmProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmed;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmedRepository;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobTitleHisImport;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;

/**
 * 月の実績の状況を取得する
 */
@Stateless
public class MonthlyActualSituationStatus {
	/** 実績ロックされているか判定する */
	@Inject
	private DetermineActualResultLock lockStatusService;
	/** 対象月の月の承認が済んでいるかチェックする */
	@Inject 
	private MonthlyApprovalProcess monthlyApprovalProcess;
	
	@Inject
	private IdentityConfirmProcess indentityStatus;
	
	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private EmploymentConfirmedRepository employmentConfirmedRepository;
	
//	@Inject
//	private CreateEmployeeDailyPerError dailyRecordError;
	/**
	 * 月の実績の状況を取得する
	 * @param param 実績状況を取得する
	 * @return «Output» 月の実績の状況
	 */
	public MonthlyActualSituationOutput getMonthlyActualSituationStatus(AcquireActualStatus param,Optional<ApprovalProcess> approvalProcOp,List<SharedAffJobTitleHisImport> listShareAff,boolean checkIdentityOp,List<Identification> listIdentification,
			boolean checkExistRecordErrorListDate, List<GeneralDate> lstDateCheck){		
		MonthlyActualSituationOutput monthlyResult = new MonthlyActualSituationOutput();
		//社員の締め情報
		monthlyResult.setEmployeeClosingInfo(new EmployeeClosingInfo(param.employeeId, param.getClosureId(), param.getClosureDate(), param.getProcessDateYM(), param.getDuration()));
		//実績ロックされているか判定する
		LockStatus lockStatus = lockStatusService.getDetermineActualLocked(param.getCompanyId(), 
				param.getClosureDate(), param.getClosureId(), PerformanceType.MONTHLY);
		monthlyResult.setMonthlyLockStatus(lockStatus);
		//対象の職場が就業確定されているかチェックする
		//TODO 就業確定が対象外のため「未確定」で固定にしてください
		boolean checkWkp = this.checkWkpConfirmedToWork(param.getCompanyId(), param.getClosureDate(), param.getWorkplaceId(), param.getClosureId());
		monthlyResult.setEmploymentFixedStatus(checkWkp ? EmploymentFixedStatus.CONFIRM : EmploymentFixedStatus.PENDING);
		//対象月の月の承認が済んでいるかチェックする
		monthlyResult.setApprovalStatus(monthlyApprovalProcess.monthlyApprovalCheck(param.getCompanyId(), param.getEmployeeId(), param.getProcessDateYM(), param.getClosureId(), param.getClosureDate(),approvalProcOp,listShareAff));		
		DailyActualSituation dailyActualSituation = new DailyActualSituation();
		//TODO 対象外
		//日の実績が存在する＝TRUE　で固定
		dailyActualSituation.setDailyAchievementsExist(true);
		//対象日の本人確認が済んでいるかチェックする
		dailyActualSituation.setIdentificationCompleted(indentityStatus.identityConfirmCheck(param.getCompanyId(), param.getEmployeeId(),checkIdentityOp,listIdentification, lstDateCheck));
		//対象期間に日別実績のエラーが発生しているかチェックする
		dailyActualSituation.setDailyRecordError(checkExistRecordErrorListDate);
		
		//日別実績が存在しているかチェックする
		monthlyResult.setDailyActualSituation(dailyActualSituation);
		
		return monthlyResult;		
	}
	
	/*private List<GeneralDate> getDaysBetween(GeneralDate startDate, GeneralDate endDate) {
		List<GeneralDate> daysBetween = new ArrayList<>();

		while (startDate.beforeOrEquals(endDate)) {
			daysBetween.add(startDate);
			GeneralDate temp = startDate.addDays(1);
			startDate = temp;
		}

		return daysBetween;
	}*/
	
	public boolean checkWkpConfirmedToWork(String companyId, GeneralDate baseDate, String workplaceId, int closureId) {
		//ドメインモデル「締め」を取得する
		Optional<Closure> closure = closureRepository.findById(companyId, closureId);
		if (!closure.isPresent())
			return false;

		// 指定した年月日時点の締め期間を取得する
		Optional<ClosurePeriod> closurePeriodOpt = closure.get().getClosurePeriodByYmd(baseDate);
		if (!closurePeriodOpt.isPresent())
			return false;
		
		//ドメインモデル「就業確定」を取得する
		Optional<EmploymentConfirmed> data = employmentConfirmedRepository.get(companyId, workplaceId,ClosureId.valueOf(closureId), closurePeriodOpt.get().getYearMonth());
		if(data.isPresent()) {
			return true;
		}
		return false;
	}
	
}
