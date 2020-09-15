package nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.workschedule;

import java.util.Collections;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.SchedulePerformance;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.other.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.other.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.other.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.timezone.other.BonusPayAutoCalcSet;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.timezone.other.HolidayTimesheetCalculationSetting;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.timezone.other.OvertimeTimesheetCalculationSetting;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.timezone.other.WorkingTimesheetCalculationSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneShortTimeWorkSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowOTSet;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;

/**
 * 日別勤怠の勤務予定時間 (new)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.勤務予定.日別勤怠の勤務予定時間
 * 
 * @author nampt
 * 日別実績の勤務予定時間 (old)
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkScheduleTimeOfDaily {
	
	//勤務予定時間
	private WorkScheduleTime workScheduleTime;
	
	//計画所定労働時間
	private AttendanceTime schedulePrescribedLaborTime;
	
	//実績所定労働時間
	private AttendanceTime recordPrescribedLaborTime;
	
	public static WorkScheduleTimeOfDaily defaultValue(){
		return new WorkScheduleTimeOfDaily(WorkScheduleTime.defaultValue(), new AttendanceTime(0), new AttendanceTime(0));
	}
	
	/**
	* 時刻から所定時間を計算する
	* @param companyCommonSetting 会社別設定管理
	* @param personDailySetting 社員設定管理
	* @param integrationOfDaily 日別実績(Work)（実績）
	* @param schedulePerformance 予定実績
	* @param FlowOTSet 流動残業設定
	* @return 所定時間
	*/
	public static AttendanceTime calcPredeterminedFromTime(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			IntegrationOfDaily integrationOfDaily,
			SchedulePerformance schedulePerformance,
			FlowOTSet flowOTSet) {
		
		ManageReGetClass scheduleManageReGetClass = new ManageReGetClass(
				schedulePerformance.getCalculationRangeOfOneDay(),
				companyCommonSetting,
				personDailySetting,
				schedulePerformance.getWorkType(),
				schedulePerformance.getIntegrationOfWorkTime(),
				integrationOfDaily);
		
		//設定を退避する
		WorkTimezoneShortTimeWorkSet cloneWorkTimezoneShortTimeWorkSet = scheduleManageReGetClass.getWorkTimezoneCommonSet().get().getShortTimeWorkSet().clone();
		
		//短時間勤務を勤務として扱う
		//変動させる場合にしか呼ばれない為、引数として渡す「所定変動区分」には「変動しない」以外が入っている。
		scheduleManageReGetClass.getWorkTimezoneCommonSet().get().getShortTimeWorkSet().correctDataForFixedChange(flowOTSet.getFixedChangeAtr());
		
		//総労働時間計算用にクラスを作成
		//加給時間計算設定
		String companyId = companyCommonSetting.getCompensatoryLeaveComSet().getCompanyId();
		BonusPayAutoCalcSet bonusPayAutoCalcSet = new BonusPayAutoCalcSet(new CompanyId(companyId), 1,
				WorkingTimesheetCalculationSetting.CalculateAutomatic,
				OvertimeTimesheetCalculationSetting.CalculateAutomatic,
				HolidayTimesheetCalculationSetting.CalculateAutomatical);
		
		//時刻から所定時間を計算
		TotalWorkingTime totalWorkingTime = TotalWorkingTime.calcAllDailyRecord(
				scheduleManageReGetClass,
				VacationClass.createAllZero(),
				schedulePerformance.getWorkType().get(),
				Optional.of(WorkTimeDailyAtr.REGULAR_WORK),
				Optional.empty(),
				bonusPayAutoCalcSet,
				Collections.emptyList(),
				personDailySetting.getPersonInfo(),
				Optional.of(schedulePerformance.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc()),
				personDailySetting.getAddSetting().getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().getNotDeductLateLeaveEarly(),
				Optional.of(schedulePerformance.getCalculationRangeOfOneDay().getWorkInformationOfDaily().getScheduleInfo().getWorkTimeCode()));
		
		//設定を元に戻す
		scheduleManageReGetClass.getWorkTimezoneCommonSet().get().getShortTimeWorkSet().restoreWorkTimezoneShortTimeWorkSet(cloneWorkTimezoneShortTimeWorkSet);

		return totalWorkingTime.getTotalTime();
	}
}
