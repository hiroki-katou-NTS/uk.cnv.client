package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.SystemFixedErrorAlarm;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.CheckExcessAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.shr.com.context.AppContexts;

/**
 * 日別勤怠の勤務時間 (new)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.実働時間.日別勤怠の勤務時間
 * 
 * @author nampt 日別実績の勤務実績時間 (old)
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActualWorkingTimeOfDaily {

	// 割増時間
	private PremiumTimeOfDailyPerformance premiumTimeOfDailyPerformance;

	// 拘束差異時間
	private AttendanceTime constraintDifferenceTime;

	// 拘束時間
	private ConstraintTime constraintTime;

	// 時差勤務時間
	private AttendanceTime timeDifferenceWorkingHours;

	// 総労働時間
	private TotalWorkingTime totalWorkingTime;

	// //代休発生情報
	// private SubHolOccurrenceInfo subHolOccurrenceInfo;

	// 乖離時間
	@Setter
	private DivergenceTimeOfDaily divTime;

	/**
	 * Constructor
	 */
	public ActualWorkingTimeOfDaily(AttendanceTime constraintDiffTime, ConstraintTime constraintTime,
			AttendanceTime timeDiff, TotalWorkingTime totalWorkingTime, DivergenceTimeOfDaily divTime,
			PremiumTimeOfDailyPerformance premiumTime) {
		this.premiumTimeOfDailyPerformance = premiumTime;
		this.constraintDifferenceTime = constraintDiffTime;
		this.constraintTime = constraintTime;
		this.timeDifferenceWorkingHours = timeDiff;
		this.totalWorkingTime = totalWorkingTime;
		this.divTime = divTime;
	}

	public static ActualWorkingTimeOfDaily of(TotalWorkingTime totalWorkTime, int midBind, int totalBind, int bindDiff,
			int diffTimeWork) {
		return new ActualWorkingTimeOfDaily(new AttendanceTime(bindDiff),
				new ConstraintTime(new AttendanceTime(midBind), new AttendanceTime(totalBind)),
				new AttendanceTime(diffTimeWork), totalWorkTime, new DivergenceTimeOfDaily(),
				PremiumTimeOfDailyPerformance.createEmpty());
	}

    public static ActualWorkingTimeOfDaily of(TotalWorkingTime totalWorkTime, int midBind, int totalBind, int bindDiff,
            int diffTimeWork, DivergenceTimeOfDaily divTime) {
        return new ActualWorkingTimeOfDaily(new AttendanceTime(bindDiff),
                new ConstraintTime(new AttendanceTime(midBind), new AttendanceTime(totalBind)),
                new AttendanceTime(diffTimeWork), totalWorkTime, divTime, PremiumTimeOfDailyPerformance.createEmpty());
    }

    public static ActualWorkingTimeOfDaily of(TotalWorkingTime totalWorkTime, int midBind, int totalBind, int bindDiff,
            int diffTimeWork, DivergenceTimeOfDaily divTime, PremiumTimeOfDailyPerformance premiumTime) {
        return new ActualWorkingTimeOfDaily(new AttendanceTime(bindDiff),
                new ConstraintTime(new AttendanceTime(midBind), new AttendanceTime(totalBind)),
                new AttendanceTime(diffTimeWork), totalWorkTime, divTime, premiumTime);
    }
    
    public static ActualWorkingTimeOfDaily of(AttendanceTime constraintDiffTime, ConstraintTime constraintTime,
			AttendanceTime timeDiff, TotalWorkingTime totalWorkingTime, DivergenceTimeOfDaily divTime,
			PremiumTimeOfDailyPerformance premiumTime) {
    	return new ActualWorkingTimeOfDaily(constraintDiffTime,constraintTime,
    										timeDiff,totalWorkingTime,divTime,premiumTime);
    }
    
    public ActualWorkingTimeOfDaily inssertTotalWorkingTime(TotalWorkingTime time) {
    	return new ActualWorkingTimeOfDaily(this.constraintDifferenceTime,this.constraintTime,this.timeDifferenceWorkingHours,time,this.divTime,this.premiumTimeOfDailyPerformance);
    }
    
	/**
	 * 日別実績の実働時間の計算
	 * @param recordClass 
	 * @param vacationClass 
	 * @param workType 
	 * @param workTimeDailyAtr 
	 * @param flexCalcMethod 
	 * @param bonusPayAutoCalcSet 
	 * @param eachCompanyTimeSet 
	 * @param forCalcDivergenceDto 
	 * @param divergenceTimeList 
	 * @param conditionItem 
	 * @param predetermineTimeSetByPersonInfo 
	 * @param workScheduleTime 
	 * @param recordWorkTimeCode 
	 * @param declareResult 申告時間帯作成結果
	 */
	public static ActualWorkingTimeOfDaily calcRecordTime(ManageReGetClass recordClass,
			   VacationClass vacationClass,
			   WorkType workType,
		       Optional<WorkTimeDailyAtr> workTimeDailyAtr,
			   Optional<SettingOfFlexWork> flexCalcMethod,
			   List<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
			   DailyRecordToAttendanceItemConverter forCalcDivergenceDto,
			   List<DivergenceTimeRoot> divergenceTimeList, 
			   WorkingConditionItem conditionItem,
			   Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			   WorkScheduleTimeOfDaily workScheduleTime,
			   Optional<WorkTimeCode> recordWorkTimeCode,
			   DeclareTimezoneResult declareResult) {
		
		/* 総労働時間の計算 */
		val totalWorkingTime = TotalWorkingTime.calcAllDailyRecord(recordClass,
				    vacationClass,
				    workType,
				    workTimeDailyAtr,
				    flexCalcMethod,
					eachCompanyTimeSet,
					conditionItem,
					predetermineTimeSetByPersonInfo,
					recordWorkTimeCode,
					declareResult);
		
		TotalWorkingTime calcResultOotsuka = totalWorkingTime;
		// 大塚モードの確認
		if (AppContexts.optionLicense().customize().ootsuka()){
			// 勤務種類が1日特休かどうか確認する
			if (workType.getDailyWork().decisionMatchWorkType(WorkTypeClassification.SpecialHoliday).isFullTime()){
				// 大塚モード(特休時計算)
				calcResultOotsuka = totalWorkingTime.SpecialHolidayCalculationForOotsuka(
						recordClass,
						vacationClass,
						workType,
						workTimeDailyAtr,
						flexCalcMethod,
						eachCompanyTimeSet,
						conditionItem,
						predetermineTimeSetByPersonInfo);
			}
			else{
				// 大塚残業
				calcResultOotsuka = calcOotsuka(
						recordClass,
						totalWorkingTime,
						workType,
						workScheduleTime.getRecordPrescribedLaborTime(),
						conditionItem.getLaborSystem());
			}
			// 大塚モードの計算（欠勤控除時間）
			// 1日出勤系の場合は処理を呼ばないように作成が必要
			if (recordClass.getCalculatable()){
				// 大塚モード休憩未取得
				calcResultOotsuka = calcResultOotsuka.reCalcLateLeave(
						recordClass.getWorkTimezoneCommonSet(),
						recordClass.getFixRestTimeSetting(),
						recordClass.getFixWoSetting(),
						recordClass.getIntegrationOfDaily().getAttendanceLeave(),
						workScheduleTime.getRecordPrescribedLaborTime(), 
						workType);	

			}
		}
		
		/*拘束差異時間*/
		val constraintDifferenceTime = new AttendanceTime(0);
		/*拘異時間*/
		val constraintTime = new ConstraintTime(new AttendanceTime(0), new AttendanceTime(0));
		/* 時差勤務時間*/
		val timeDifferenceWorkingHours = new AttendanceTime(0);
		
		/* 割増時間の計算 */
		val premiumTime = PremiumTimeOfDailyPerformance.createEmpty();
		/* 乖離時間の計算 */
//		val divergenceTimeOfDaily = createDivergenceTimeOfDaily(
//													   forCalcDivergenceDto,
//													   divergenceTimeList,
//													   recordClass.getIntegrationOfDaily().getCalAttr(),
//													   recordClass.getFixRestTimeSetting(),
//													   calcResultOotsuka
//													   );
//		DivergenceTimeOfDaily divergenceTimeOfDaily = new DivergenceTimeOfDaily(Collections.emptyList());
		
		/*返値*/
		return new ActualWorkingTimeOfDaily(
				constraintDifferenceTime,
				constraintTime,
				timeDifferenceWorkingHours,
				calcResultOotsuka,
				new DivergenceTimeOfDaily(new ArrayList<>()),
				premiumTime
				);
		
	}

	/**
	 * エラーチェックの指示メソッド 
	 * @param attendanceItemConverter 
	 * @return 社員のエラーチェック一覧
	 */
	public List<EmployeeDailyPerError> requestCheckError(String employeeId,GeneralDate targetDate,
			   											 SystemFixedErrorAlarm fixedErrorAlarmCode,
			   											 CheckExcessAtr checkAtr) {
		return this.getTotalWorkingTime().checkOverTimeExcess(employeeId, targetDate, fixedErrorAlarmCode, checkAtr);
	}
	/////****************************************************:大塚専用:********************************************************/////
	/**
	 * 大塚モードの計算(休憩未取得)
	 * @param workingSystem 
	 * @param totalWorkingTime
	 * @param fixRestTimeSetting 固定休憩時間の時間帯設定
	 * @param predetermineTime 所定時間
	 * @param dailyUnit 
	 * @param timeLeavingOfDailyPerformance 
	 * @param workType 
	 * @param attendanceTime 
	 * @return
	 */
	private static TotalWorkingTime calcOotsuka(ManageReGetClass recordClass, 
									TotalWorkingTime totalWorkingTime,
									WorkType workType, 
									AttendanceTime acutualPredTime,
									WorkingSystem workingSystem) {
		if(!recordClass.getCalculatable() || recordClass.getIntegrationOfDaily().getAttendanceLeave() == null || !recordClass.getIntegrationOfDaily().getAttendanceLeave().isPresent()) return totalWorkingTime;
//		if((recordClass.getPersonalInfo().getWorkingSystem().isRegularWork() || recordClass.getPersonalInfo().getWorkingSystem().isVariableWorkingTimeWork()){
//			/*緊急対応　固定勤務時　就業時間帯or計算設定で遅刻早退控除しない　なら、休憩未取得処理飛ばす*/

			if(recordClass.getIntegrationOfWorkTime().isPresent()
					&& recordClass.getIntegrationOfWorkTime().get().getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm().isFixed()
					&& recordClass.getAddSetting().getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet().isPresent()){
				boolean lateEarlyDeductFlag = recordClass.getAddSetting().getVacationCalcMethodSet().getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().isDeductLateLeaveEarly(recordClass.getWorkTimezoneCommonSet());
				if(!lateEarlyDeductFlag) return totalWorkingTime; 
			}
		
			if(recordClass.getOotsukaFixedWorkSet().isPresent()&& !workType.getDailyWork().isHolidayWork()) {
//		        if((recordClass.getPersonalInfo().getWorkingSystem().isRegularWork() || recordClass.getPersonalInfo().getWorkingSystem().isVariableWorkingTimeWork())&&recordClass.getOotsukaFixedWorkSet().isPresent()&& !workType.getDailyWork().isHolidayWork()) {
				//休憩未取得時間の計算
				AttendanceTime unUseBreakTime =
						workingSystem.isRegularWork() ?
								totalWorkingTime.getBreakTimeOfDaily().calcUnUseBrekeTime(
										recordClass.getFixRestTimeSetting().get(),
										recordClass.getFixWoSetting(),
										recordClass.getIntegrationOfDaily().getAttendanceLeave().get()) 
								: new AttendanceTime(0);
				unUseBreakTime = unUseBreakTime.greaterThan(0)?unUseBreakTime:new AttendanceTime(0);
				//所てない休憩未取得時間を算出する際に使用する所定時間に加算する時間(就業時間帯マスタに設定されている所定内の休憩時間の合計)
				int withinBreakTime = 0;
				//就業時間帯に設定されている休憩のループ
				for(DeductionTime breakTImeSheet : recordClass.getFixRestTimeSetting().get().getTimezones()) {
					//就業時間帯に設定されている勤務時間帯のstream
					withinBreakTime += recordClass.getFixWoSetting().stream().filter(tc -> tc.getTimezone().isOverlap(breakTImeSheet))
														  .map(tt -> tt.getTimezone().getDuplicatedWith(breakTImeSheet.timeSpan()).get().lengthAsMinutes())
														  .collect(Collectors.summingInt(ts -> ts));
						
					
				}
				//所定内休憩未取得時間の計算
				AttendanceTime unUseWithinBreakTime = totalWorkingTime.getWithinStatutoryTimeOfDaily().calcUnUseWithinBreakTime(unUseBreakTime,acutualPredTime,new AttendanceTime(withinBreakTime));
				//所定外休憩未取得時間
				AttendanceTime unUseExcessBreakTime = unUseBreakTime.minusMinutes(unUseWithinBreakTime.valueAsMinutes());
				
										
				
				//日別実績の総労働からとってくる
				AttendanceTime vacationAddTime = totalWorkingTime.getVacationAddTime();
				//残業時間
				if(totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
					//休憩未取得時間から残業時間計算
					totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().calcOotsukaOverTime(
							totalWorkingTime.getWithinStatutoryTimeOfDaily().getActualWorkTime(),
							unUseExcessBreakTime,
							vacationAddTime,/*休暇加算時間*/
							recordClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc().getpredetermineTime(workType.getDailyWork()),
							recordClass.getOotsukaFixedWorkSet(),
							recordClass.getIntegrationOfDaily().getCalAttr().getOvertimeSetting(),
							recordClass.getDailyUnit(),
							recordClass.getFixRestTimeSetting(),
							recordClass.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet(),
							recordClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc()
							);
					
				}
				
				//就業時間から休憩未取得時間を減算(休憩未取得を残業時間として計算する　であれば差し引く)
				if(recordClass.getOotsukaFixedWorkSet() != null
				   && recordClass.getOotsukaFixedWorkSet().isPresent()
				   && recordClass.getOotsukaFixedWorkSet().get().getOverTimeCalcNoBreak() != null
				   && recordClass.getOotsukaFixedWorkSet().get().getOverTimeCalcNoBreak().getCalcMethod() != null
				   && !recordClass.getOotsukaFixedWorkSet().get().getOverTimeCalcNoBreak().getCalcMethod().isCalcAsWorking() ) {
					//totalWorkingTime.getWithinStatutoryTimeOfDaily().workTimeMinusUnUseBreakTimeForOotsuka(unUseBreakTime);
					totalWorkingTime.getWithinStatutoryTimeOfDaily().workTimeMinusUnUseBreakTimeForOotsuka(unUseExcessBreakTime);
				}
				
				//休暇加算を残業として計算する場合、ロジックの関係上、就業時間計算時に休暇加算が合算されてしまう
				//ここでは、合算されてしまっている休暇加算を差し引いている
				if(recordClass.getOotsukaFixedWorkSet() != null && recordClass.getOotsukaFixedWorkSet().isPresent() ) {
					if(recordClass.getOotsukaFixedWorkSet().get().getExceededPredAddVacationCalc().getCalcMethod() != null
						 && recordClass.getOotsukaFixedWorkSet().get().getExceededPredAddVacationCalc().getCalcMethod().isCalcAsOverTime()
						 && totalWorkingTime.getWithinStatutoryTimeOfDaily().getWorkTime().greaterThan(recordClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc().getAdditionSet().getPredTime().getOneDay().valueAsMinutes())) {
						totalWorkingTime.setWithinWorkTime(recordClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc().getAdditionSet().getPredTime().getOneDay());
					}
				}
	
	//		}
		}
		return totalWorkingTime;
	}
	
	/////****************************************************:大塚専用:********************************************************/////
	public static ActualWorkingTimeOfDaily defaultValue(){
		return new ActualWorkingTimeOfDaily(AttendanceTime.ZERO, ConstraintTime.defaultValue(), AttendanceTime.ZERO, 
											TotalWorkingTime.createAllZEROInstance(), 
											new DivergenceTimeOfDaily(new ArrayList<>()), 
											PremiumTimeOfDailyPerformance.createEmpty());
	}
	
	public AttendanceTime getWorkHolidayTime() {
		return this.getTotalWorkingTime().getWorkHolidayTime();
	}
	
	public AttendanceTime getOverTime() {
		return this.getTotalWorkingTime().getOverTime();
	}
}
