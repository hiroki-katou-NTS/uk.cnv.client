package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess;

import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.CheckAchievementConfirmation.ConfirmClsStatus;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         事前チェック処理（勤務実績）
 */
public class PreCheckProcessWorkRecord {

	public static PreCheckProcessResult preCheck(Require require, String companyId, Application application,
			int closureId, boolean isCalWhenLock, ReflectStatusResult reflectStatus, GeneralDate targetDate, List<SEmpHistImport> empHist) {

		// ロック中処理のチェック
		List<DatePeriod> periodLst = require.getPeriodProcess(application.getEmployeeID(),
				new DatePeriod(targetDate, targetDate),
				empHist.stream().map(x -> new EmploymentHistoryImported(x.getEmployeeId(), x.getEmploymentCode(),
						x.getPeriod())).collect(Collectors.toList()),
				isCalWhenLock ? IgnoreFlagDuringLockImport.CAN_CAL_LOCK : IgnoreFlagDuringLockImport.CANNOT_CAL_LOCK,
				AchievementAtrImport.DAILY);
		if (periodLst.isEmpty()) {
			// 日別実績反映不可理由に「1：実績がロックされている」をセットする
			reflectStatus.setReasonNotReflectWorkRecord(ReasonNotReflectDaily.ACHIEVEMENTS_LOCKED);
			return new PreCheckProcessResult(NotUseAtr.NOT_USE, reflectStatus);
		}

		// 事前の残業申請かどうかチェック
		if (application.getAppType() == ApplicationType.OVER_TIME_APPLICATION
				&& application.getPrePostAtr() == PrePostAtr.PREDICT) {
			return new PreCheckProcessResult(NotUseAtr.USE, reflectStatus);
		}

		// 対象期間内で本人確認をした日をチェックする
		if (!require.getProcessingYMD(companyId, application.getEmployeeID(), new DatePeriod(targetDate, targetDate))
				.isEmpty()) {
			// 日別実績反映不可理由に「2：本人確認、上司確認済」をセット
			reflectStatus.setReasonNotReflectWorkRecord(ReasonNotReflectDaily.SELF_CONFIRMED_BOSS_CONFIRMED);
			return new PreCheckProcessResult(NotUseAtr.NOT_USE, reflectStatus);
		}

		// 対象の職場が就業確定されているかチェックする
		ConfirmClsStatus confirmClsStatus = CheckAchievementConfirmation.check(require, companyId,
				application.getEmployeeID(), targetDate, closureId);
		if (confirmClsStatus == ConfirmClsStatus.Confirm) {
			reflectStatus.setReasonNotReflectWorkRecord(ReasonNotReflectDaily.ACTUAL_CONFIRMED);
			return new PreCheckProcessResult(NotUseAtr.NOT_USE, reflectStatus);
		}

		return new PreCheckProcessResult(NotUseAtr.USE, reflectStatus);
	}

	public static interface Require extends CheckAchievementConfirmation.Require {

		// IdentificationAdapter
		public List<GeneralDate> getProcessingYMD(String companyID, String employeeID, DatePeriod period);
		
		//GetPeriodCanProcesseAdapter
		public List<DatePeriod> getPeriodProcess(String employeeId, DatePeriod period, List<EmploymentHistoryImported> listEmploymentHis,
				IgnoreFlagDuringLockImport ignoreFlagDuringLock, AchievementAtrImport achievementAtr);
	}
}
