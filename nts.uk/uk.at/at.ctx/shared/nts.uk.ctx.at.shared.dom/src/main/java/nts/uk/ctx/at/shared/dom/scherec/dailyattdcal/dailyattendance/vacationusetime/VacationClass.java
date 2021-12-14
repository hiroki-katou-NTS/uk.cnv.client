package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.BreakDownTimeDay;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.*;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.VacationAddTime;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.VacationCategory;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 休暇クラス
 * 
 * @author keisuke_hoshina
 *
 */
@Value
public class VacationClass {
	private HolidayOfDaily holidayOfDaily;
	
	/**
	 * 全て0で作成する
	 * 
	 * @return 休暇クラス
	 */
	public static VacationClass createAllZero() {
		return new VacationClass(
				new HolidayOfDaily(
						new AbsenceOfDaily(new AttendanceTime(0)),
						new TimeDigestOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
						new YearlyReservedOfDaily(new AttendanceTime(0)),
						new SubstituteHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
						new OverSalaryOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
						new SpecialHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
						new AnnualOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
						new TransferHolidayOfDaily(new AttendanceTime(0))));
	}

	/**
	 * 休暇使用時間の計算
	 * @param workType 勤務種類
	 * @param siftCode 就業時間帯コード
	 * @param conditionItem 労働条件項目
	 * @param goOutTimeOfDaily 日別実績の外出時間(List)
	 * @param lateTimeOfDaily 日別実績の遅刻時間(List)
	 * @param leaveEarlyTimeOfDaily 日別実績の早退時間(list)
	 * @param recordReGet 実績
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @return 日別実績の休暇
	 */
	public static HolidayOfDaily calcUseRestTime(
			WorkType workType,
			Optional<WorkTimeCode> siftCode,
			WorkingConditionItem conditionItem,
			List<OutingTimeOfDaily> goOutTimeOfDaily,
			List<LateTimeOfDaily> lateTimeOfDaily,
			List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily,
			ManageReGetClass recordReGet,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo) {

		Optional<PredetermineTimeSetForCalc> predSetting = recordReGet.getCalculatable()
				? Optional.of(recordReGet.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc())
				: Optional.empty();
		//欠勤使用時間
		AttendanceTime absenceUseTime = vacationTimeOfcalcDaily(workType, VacationCategory.Absence, predSetting,
				predetermineTimeSetByPersonInfo, siftCode, conditionItem, recordReGet.getHolidayAddtionSet());
		val absenceOfDaily = new AbsenceOfDaily(absenceUseTime);

		//時間消化休暇使用時間
		AttendanceTime timeDigest = new AttendanceTime(0);
		val timeDigestOfDaily = new TimeDigestOfDaily(timeDigest, new AttendanceTime(0));

		//積立年休使用時間
		AttendanceTime yearlyReservedTime = vacationTimeOfcalcDaily(workType, VacationCategory.YearlyReserved,
				predSetting, predetermineTimeSetByPersonInfo, siftCode, conditionItem,
				recordReGet.getHolidayAddtionSet());
		val yearlyReservedOfDaily = new YearlyReservedOfDaily(yearlyReservedTime);

		//代休使用時間の計算
		AttendanceTime substituUseTime = vacationTimeOfcalcDaily(workType, VacationCategory.SubstituteHoliday,
				predSetting, predetermineTimeSetByPersonInfo, siftCode, conditionItem,
				recordReGet.getHolidayAddtionSet());
		int sumSubTime = goOutTimeOfDaily.stream()
				.map(tc -> tc.getTimeVacationUseOfDaily().getTimeCompensatoryLeaveUseTime().valueAsMinutes())
				.collect(Collectors.summingInt(tc -> tc))
				+ lateTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeCompensatoryLeaveUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc))
				+ leaveEarlyTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeCompensatoryLeaveUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc));
		substituUseTime = substituUseTime.addMinutes(sumSubTime);

		val substituteOfDaily = new SubstituteHolidayOfDaily(substituUseTime, new AttendanceTime(0));

		//超過有休使用時間
		AttendanceTime overSalaryTime = vacationTimeOfcalcDaily(workType, VacationCategory.TimeDigestVacation,
				predSetting, predetermineTimeSetByPersonInfo, siftCode, conditionItem,
				recordReGet.getHolidayAddtionSet());
		int sumOverTime = goOutTimeOfDaily.stream()
				.map(tc -> tc.getTimeVacationUseOfDaily().getSixtyHourExcessHolidayUseTime().valueAsMinutes())
				.collect(Collectors.summingInt(tc -> tc))
				+ lateTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getSixtyHourExcessHolidayUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc))
				+ leaveEarlyTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getSixtyHourExcessHolidayUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc));
		overSalaryTime = overSalaryTime.addMinutes(sumOverTime);

		val overSalaryOfDaily = new OverSalaryOfDaily(overSalaryTime, new AttendanceTime(0));

		//特別休暇使用時間の計算
		AttendanceTime specHolidayTime = vacationTimeOfcalcDaily(workType, VacationCategory.SpecialHoliday, predSetting,
				predetermineTimeSetByPersonInfo, siftCode, conditionItem, recordReGet.getHolidayAddtionSet());
		int sumSpecTime = goOutTimeOfDaily.stream()
				.map(tc -> tc.getTimeVacationUseOfDaily().getTimeSpecialHolidayUseTime().valueAsMinutes())
				.collect(Collectors.summingInt(tc -> tc))
				+ lateTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeSpecialHolidayUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc))
				+ leaveEarlyTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeSpecialHolidayUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc));
		specHolidayTime = specHolidayTime.addMinutes(sumSpecTime);

		val specHolidayOfDaily = new SpecialHolidayOfDaily(specHolidayTime, new AttendanceTime(0));

		//年休使用時間の計算
		AttendanceTime annualUseTime = vacationTimeOfcalcDaily(workType, VacationCategory.AnnualHoliday, predSetting,
				predetermineTimeSetByPersonInfo, siftCode, conditionItem, recordReGet.getHolidayAddtionSet());
		int sumAnnTime = goOutTimeOfDaily.stream()
				.map(tc -> tc.getTimeVacationUseOfDaily().getTimeAnnualLeaveUseTime().valueAsMinutes())
				.collect(Collectors.summingInt(tc -> tc))
				+ lateTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeAnnualLeaveUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc))
				+ leaveEarlyTimeOfDaily.stream()
						.map(tc -> tc.getTimePaidUseTime().getTimeAnnualLeaveUseTime().valueAsMinutes())
						.collect(Collectors.summingInt(tc -> tc));
		annualUseTime = annualUseTime.addMinutes(sumAnnTime);

		val annualOfDaily = new AnnualOfDaily(annualUseTime, new AttendanceTime(0));
		
		//振休使用時間の計算
		AttendanceTime transferHolidayUseTime = vacationTimeOfcalcDaily(workType, VacationCategory.Pause, predSetting,
				predetermineTimeSetByPersonInfo, siftCode, conditionItem, recordReGet.getHolidayAddtionSet());

		val transferHolidayOfDaily =new TransferHolidayOfDaily(transferHolidayUseTime);		

		return new HolidayOfDaily(absenceOfDaily, timeDigestOfDaily, yearlyReservedOfDaily, substituteOfDaily,
				overSalaryOfDaily, specHolidayOfDaily, annualOfDaily, transferHolidayOfDaily);
	}

	/**
	 * 日数単位の休暇時間計算
	 * 
	 * @return 1日の時間内訳時間
	 */
	public static AttendanceTime vacationTimeOfcalcDaily(WorkType workType, VacationCategory vacationCategory,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSet,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo, Optional<WorkTimeCode> siftCode,
			WorkingConditionItem conditionItem, Optional<HolidayAddtionSet> holidayAdditionSet) {
		// 使用する区分を持っている休暇種類であるかを判定しつつ、区分を持ってる休暇は使用するしないも見る
		if (!holidayAdditionSet.isPresent()) // ||
												// holidayAdditionSet.get().getAdditionVacationSet().dicisionExistAndNotUse(vacationCategory))
			return new AttendanceTime(0);
		BreakDownTimeDay breakDownTimeDay = getVacationAddSet(predetermineTimeSet, siftCode, holidayAdditionSet.get(),
				conditionItem, predetermineTimeSetByPersonInfo);
		switch (workType.getDailyWork().decisionMatchWorkType(vacationCategory.convertWorkTypeClassification())) {
		// 1日出勤系
		case FULL_TIME:
			return breakDownTimeDay.getOneDay();
		// 午前出勤系
		case MORNING:
			return breakDownTimeDay.getMorning();
		// 午後出勤系
		case AFTERNOON:
			return breakDownTimeDay.getAfternoon();
		// 1日休暇
		case HOLIDAY:
			return new AttendanceTime(0);
		// 例外
		default:
			throw new RuntimeException("unknown WorkType");
		}
	}

	/**
	 * 休暇加算時間の設定の取得
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param siftCode 就業時間帯コード
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @return 休暇加算時間
	 */
	private static BreakDownTimeDay getVacationAddSet(Optional<PredetermineTimeSetForCalc> predetermineTimeSet,
			Optional<WorkTimeCode> siftCode, HolidayAddtionSet holidayAddtionSet, WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo) {

		// Refactor code QA #40197
		BreakDownTimeDay result = null;
        VacationAdditionTimeRef vacationAdditionTimeRef = holidayAddtionSet.getReference().getReferenceSet();
        if (vacationAdditionTimeRef == VacationAdditionTimeRef.REFER_PERSONAL_SET) {
			result = getAddVacationTimeFromEmpInfo(holidayAddtionSet, conditionItem, predetermineTimeSetByPersonInfo);
            if (result == null) {
            	// 会社一律の設定を取得
				result = new BreakDownTimeDay(
						holidayAddtionSet.getReference().getComUniformAdditionTime().getOneDay().v(),
						holidayAddtionSet.getReference().getComUniformAdditionTime().getMorning().v(),
						holidayAddtionSet.getReference().getComUniformAdditionTime().getAfternoon().v()
				);
			}
        } else {
            if (siftCode.isPresent()) {
                return predetermineTimeSet.isPresent() ? predetermineTimeSet.get().getAdditionSet().getAddTime()
						: new BreakDownTimeDay(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0));
            } else {
				switch (vacationAdditionTimeRef) {
					case REFER_ACTUAL_WORK_OR_COM_SET: {
						// 会社一律の設定を取得
						result = new BreakDownTimeDay(
								holidayAddtionSet.getReference().getComUniformAdditionTime().getOneDay().v(),
								holidayAddtionSet.getReference().getComUniformAdditionTime().getMorning().v(),
								holidayAddtionSet.getReference().getComUniformAdditionTime().getAfternoon().v()
						);
						break;
					}
					case REFER_ACTUAL_WORK_OR_INDIVIDUAL_SET: {
						result = getAddVacationTimeFromEmpInfo(holidayAddtionSet, conditionItem, predetermineTimeSetByPersonInfo);
						if (result == null) {
							// 会社一律の設定を取得
							result = new BreakDownTimeDay(
									holidayAddtionSet.getReference().getComUniformAdditionTime().getOneDay().v(),
									holidayAddtionSet.getReference().getComUniformAdditionTime().getMorning().v(),
									holidayAddtionSet.getReference().getComUniformAdditionTime().getAfternoon().v()
							);
						}
						break;
					}
				}
            }
        }
        return result;

	}

	/**
	 * 日単位の休暇加算時間の計算
	 * アルゴリズム：日単位の休暇加算時間の計算
	 * @author ken_takasu
	 * @param premiumAtr 割増区分（"通常"、"割増")
	 * @param workType 勤務種類
	 * @param siftCode 就業時間帯コード
	 * @param conditionItem 労働条件項目
	 * @param addSetting 加算設定
	 * @param holidayAdditionSet 休暇加算時間設定
	 * @param predTimeSettingForCalc 計算用所定時間設定
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @return 休暇加算時間
	 */
	public VacationAddTime calcVacationAddTime(
			PremiumAtr premiumAtr,
			WorkType workType,
			Optional<WorkTimeCode> siftCode,
			WorkingConditionItem conditionItem,
			AddSetting addSetting,
			Optional<HolidayAddtionSet> holidayAdditionSet,
			Optional<PredetermineTimeSetForCalc> predTimeSettingForCalc,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo) {
		
		VacationAddTime vacationAddTime =
				new VacationAddTime(AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceTime.ZERO);
		// 休暇加算するかどうか判断
		if (addSetting.getNotUseAtr(premiumAtr) == NotUseAtr.NOT_USE){
			// 加算しない時、休暇加算時間を全て0で返す
			return vacationAddTime;
		}
		// 加算時間の設定を取得
		BreakDownTimeDay breakdownTimeDay = getVacationAddSet(predTimeSettingForCalc, siftCode,
				holidayAdditionSet.get(), conditionItem, predetermineTimeSetByPersonInfo);
		// 休暇の計算方法の設定を確認する
		HolidayCalcMethodSet holidayCalcMethodSet = addSetting.getVacationCalcMethodSet();
		// 休暇加算時間を加算するかどうか判断
		vacationAddTime = judgeVacationAddTime(breakdownTimeDay, premiumAtr,
				holidayAdditionSet.get(), workType, holidayCalcMethodSet);
		// 休暇加算時間を返す
		return vacationAddTime;
	}

	/**
	 * 休暇加算時間の計算
	 * @author ken_takasu
	 * @param breakdownTimeDay 休暇加算時間
	 * @param premiumAtr 割増区分
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param workType 勤務種類
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @return 休暇加算時間
	 */
	public VacationAddTime judgeVacationAddTime(
			BreakDownTimeDay breakdownTimeDay,
			PremiumAtr premiumAtr,
			HolidayAddtionSet holidayAddtionSet,
			WorkType workType,
			HolidayCalcMethodSet holidayCalcMethodSet) {
		
		VacationAddTime vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0));
		// 加算する休暇設定を取得
		LeaveSetAdded leaveSetAdded = getAddVacationSet(premiumAtr, holidayAddtionSet, holidayCalcMethodSet);
		// 勤務区分をチェックする
		if (workType.isOneDay()) {
			val addTimes = checkVacationToAdd(leaveSetAdded, workType.getDailyWork().getOneDay(), breakdownTimeDay.getOneDay());
			vacationAddTime = vacationAddTime.addVacationAddTime(addTimes);
		} else {
			vacationAddTime = vacationAddTime.addVacationAddTime(
					checkVacationToAdd(leaveSetAdded,workType.getDailyWork().getMorning(), breakdownTimeDay.getMorning()));
			vacationAddTime = vacationAddTime.addVacationAddTime(
					checkVacationToAdd(leaveSetAdded,workType.getDailyWork().getAfternoon(), breakdownTimeDay.getAfternoon()));
		}
		return vacationAddTime;
	}

	/***
	 * 加算する休暇の種類を取得
	 * 
	 * @author ken_takasu
	 * @return
	 */
	public LeaveSetAdded getAddVacationSet(PremiumAtr premiumAtr,
			HolidayAddtionSet holidayAddtionSet, HolidayCalcMethodSet holidayCalcMethodSet) {
		LeaveSetAdded leaveSetAdded = new LeaveSetAdded(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE, NotUseAtr.NOT_USE);// 下のif文に入らない場合は全てしないを返す
		// 休暇加算設定の取得
		if (holidayCalcMethodSet.getNotUseAtr(premiumAtr) == NotUseAtr.USE) {// 加算する場合
			leaveSetAdded = holidayAddtionSet.getAdditionVacationSet();
		}
		return leaveSetAdded;
	}

	/**
	 * 休暇加算時間を加算するかチェックする
	 * 
	 * @author ken_takasu
	 * @return
	 */
	private VacationAddTime checkVacationToAdd(LeaveSetAdded leaveSetAdded,
			WorkTypeClassification workTypeClassification, AttendanceTime attendanceTime) {
		VacationAddTime vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0),
				new AttendanceTime(0));
		if (workTypeClassification.isAnnualLeave()) {// 年休の場合
			if (leaveSetAdded.getAnnualHoliday() == NotUseAtr.USE) {
				vacationAddTime = new VacationAddTime(attendanceTime, new AttendanceTime(0), new AttendanceTime(0));
			}
		} else if (workTypeClassification.isYearlyReserved()) {// 積立年休の場合
			if (leaveSetAdded.getYearlyReserved() == NotUseAtr.USE) {
				vacationAddTime = new VacationAddTime(new AttendanceTime(0), attendanceTime, new AttendanceTime(0));
			}
		} else if (workTypeClassification.isSpecialHoliday()) {// 特別休暇の場合
			if (leaveSetAdded.getSpecialHoliday() == NotUseAtr.USE) {
				vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0), attendanceTime);
			}
		}
		return vacationAddTime;
	}

	/**
	 * 社員設定から休暇加算時間取得
	 * 
	 * @param holidayAdditionSet 休暇加算時間設定
	 * @param workConditionItem  労働条件項目ドメイン
	 * @param                    predTimeForCalc 所定時間設定(計算用)
	 * @return 1日の時間内訳
	 */
	public static BreakDownTimeDay getAddVacationTimeFromEmpInfo(HolidayAddtionSet holidayAdditionSet,
			WorkingConditionItem workConditionItem, Optional<PredetermineTimeSetForCalc> predTimeForCalc) {
		// Refactor code QA #40197
        Optional<VacationSpecifiedTimeRefer> vacationSpecifiedTimeRefer = holidayAdditionSet.getReference().getReferIndividualSet();
        if (vacationSpecifiedTimeRefer.isPresent()) {
			switch (vacationSpecifiedTimeRefer.get()) {
				case WORK_HOUR_DUR_WEEKDAY: {
					// 休暇加算時間利用区分を確認
					if (workConditionItem.getVacationAddedTimeAtr().value == 0) {
						return null;
					}
					// 休暇加算時間設定を取得する
					if (workConditionItem.getHolidayAddTimeSet().isPresent()) {
						// 1日の時間内訳を返す
						return new BreakDownTimeDay(
								workConditionItem.getHolidayAddTimeSet().get().getOneDay().v(),
								workConditionItem.getHolidayAddTimeSet().get().getMorning().v(),
								workConditionItem.getHolidayAddTimeSet().get().getAfternoon().v()
						);
					} else {
						// 1日の時間内訳を０で返す
						return new BreakDownTimeDay(0,0,0);
					}
				}
				case WORK_HOUR_DUR_hd: {
					// 平日時の就業時間帯コードを取得する
					Optional<WorkTimeCode> workTimeCode = workConditionItem.getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode();
					if (workTimeCode.isPresent()) {
						// 所定時間を取得する
						if (predTimeForCalc.isPresent())
							return predTimeForCalc.get().getAdditionSet().getPredTime();
						else
							return null;
					} else {
						// 1日の時間内訳を０で返す
						return new BreakDownTimeDay(0,0,0);
					}
				}
			}
		}
		return null;
	}

//	/**
//	 * 休暇加算設定の取得
//	 * @author ken_takasu
//	 * @param workingSystem
//	 * @return
//	 */
//	private CalculationByActualTimeAtr getCalculationByActualTimeAtr(WorkingSystem workingSystem,
//																	 StatutoryDivision statutoryDivision,
//																	 AddSettingOfRegularWork addSettingOfRegularWork,
//																	 AddSettingOfIrregularWork addSettingOfIrregularWork,
//																	 AddSettingOfFlexWork addSettingOfFlexWork) {
//		switch (workingSystem) {
//		case REGULAR_WORK:
//			return addSettingOfRegularWork.getCalculationByActualTimeAtr(statutoryDivision);
//
//		case FLEX_TIME_WORK:
//			return addSettingOfFlexWork.getCalculationByActualTimeAtr(statutoryDivision);
//
//		case VARIABLE_WORKING_TIME_WORK:
//			return addSettingOfIrregularWork.getCalculationByActualTimeAtr(statutoryDivision);
//
//		case EXCLUDED_WORKING_CALCULATE:
//			throw new RuntimeException("不正な労働制です");
//		default:
//			throw new RuntimeException("不正な労働制です");
//		}
//	}

}
