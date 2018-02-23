package nts.uk.ctx.at.record.dom.daily.withinworktime;

import java.util.Collections;
import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.record.dom.daily.DeductionTotalTime;
import nts.uk.ctx.at.record.dom.daily.LateTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.TimeWithCalculation;
import nts.uk.ctx.at.record.dom.daily.breaktimegoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.record.dom.daily.breaktimegoout.BreakTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.midnight.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.BreakTimeManagement;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculationRangeOfOneDay;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.DeductionTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.LateTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.LeaveEarlyTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.VacationClass;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.employment.statutory.worktime.employment.WorkingSystem;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfIrregularWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfRegularWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.CalculationByActualTimeAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.StatutoryDivision;
import nts.uk.ctx.at.shared.dom.workrule.addsettingofworktime.VacationAddTimeSet;
import nts.uk.ctx.at.shared.dom.workrule.waytowork.PersonalLaborCondition;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 日別実績の法定内時間
 * @author keisuke_hoshina
 *
 */
@Getter
public class WithinStatutoryTimeOfDaily {
	//就業時間
	private AttendanceTime workTime;
	//就業時間(休暇加算時間を含む)
	private AttendanceTime workTimeIncludeVacationTime = new AttendanceTime(0);
	//所定内割増時間
	private AttendanceTime withinPrescribedPremiumTime = new AttendanceTime(0);
	//法定内深夜時間
	private WithinStatutoryMidNightTime withinStatutoryMidNightTime = new WithinStatutoryMidNightTime(TimeWithCalculation.sameTime(new AttendanceTime(0)));
	//休暇加算時間
	private AttendanceTime vacationAddTime = new AttendanceTime(0);  
	
	/**
	 * Constructor
	 * @param workTime 就業時間
	 */
	private WithinStatutoryTimeOfDaily(AttendanceTime workTime) {
		this.workTime = workTime;
	}
	
	/**
	 * 日別実績の法定内時間の計算
	 */
	public static WithinStatutoryTimeOfDaily calcStatutoryTime(CalculationRangeOfOneDay oneDay,			   Optional<PersonalLaborCondition> personalCondition,
			   VacationClass vacationClass,
			   WorkType workType,
			   LateTimeSheet lateTimeSheet,
			   LeaveEarlyTimeSheet leaveEarlyTimeSheet,
			   LateTimeOfDaily lateTimeOfDaily,
			   LeaveEarlyTimeOfDaily leaveEarlyTimeOfDaily,
			   boolean late,  //日別実績の計算区分.遅刻早退の自動計算設定.遅刻
			   boolean leaveEarly,  //日別実績の計算区分.遅刻早退の自動計算設定.早退
			   WorkingSystem workingSystem,
			   AddSettingOfIrregularWork addSettingOfIrregularWork,
			   AddSettingOfFlexWork addSettingOfFlexWork,
			   AddSettingOfRegularWork addSettingOfRegularWork,
			   VacationAddTimeSet vacationAddTimeSet) {
		AttendanceTime workTime = new AttendanceTime(0);
		DeductionTimeSheet dedSheet = oneDay.getTemporaryDeductionTimeSheet().isPresent()
												?oneDay.getTemporaryDeductionTimeSheet().get()
												:new DeductionTimeSheet(Collections.emptyList(), Collections.emptyList());
		if(oneDay.getWithinWorkingTimeSheet().isPresent()) {
			workTime =  oneDay.getWithinWorkingTimeSheet().get().calcWorkTimeForStatutory(CalculationByActualTimeAtr.CalculationByActualTime,dedSheet,oneDay.getTimeVacationAdditionRemainingTime().get(),
																						  vacationClass,StatutoryDivision.Nomal,workType,oneDay.getPredetermineTimeSetForCalc(),
																						   Optional.empty(),//Optional.of(new WorkTimeCode(oneDay.getWorkInformationOfDaily().getRecordWorkInformation().getWorkTimeCode().toString())),
																						   personalCondition,
																						   lateTimeSheet,
																						   leaveEarlyTimeSheet,
																						   lateTimeOfDaily,
																						   leaveEarlyTimeOfDaily,
																						   late,  //日別実績の計算区分.遅刻早退の自動計算設定.遅刻
																						   leaveEarly,  //日別実績の計算区分.遅刻早退の自動計算設定.早退
																						   workingSystem,
																						   addSettingOfIrregularWork,
																						   addSettingOfFlexWork,
																						   addSettingOfRegularWork,
																						   vacationAddTimeSet);
		}
		
		return new WithinStatutoryTimeOfDaily(workTime);
	}
	
	/**
	 * 指定した引数で日別実績の法定内時間を作成する
	 * @author ken_takasu
	 * @param workTime
	 * @param workTimeIncludeVacationTime
	 * @param withinPrescribedPremiumTime
	 * @param withinStatutoryMidNightTime
	 * @param vacationAddTime
	 * @return
	 */
	public static WithinStatutoryTimeOfDaily createWithinStatutoryTimeOfDaily(AttendanceTime workTime,
																	   AttendanceTime workTimeIncludeVacationTime,
																	   AttendanceTime withinPrescribedPremiumTime,
																	   WithinStatutoryMidNightTime withinStatutoryMidNightTime,
																	   AttendanceTime vacationAddTime) {
		WithinStatutoryTimeOfDaily withinStatutoryTimeOfDaily = new WithinStatutoryTimeOfDaily(workTime);
		withinStatutoryTimeOfDaily.workTimeIncludeVacationTime = workTimeIncludeVacationTime;
		withinStatutoryTimeOfDaily.withinPrescribedPremiumTime = withinPrescribedPremiumTime;
		withinStatutoryTimeOfDaily.withinStatutoryMidNightTime = withinStatutoryMidNightTime;
		withinStatutoryTimeOfDaily.vacationAddTime = vacationAddTime;
		return withinStatutoryTimeOfDaily;
	}
}