package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.at.record.dom.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.record.dom.daily.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.record.dom.workinformation.enums.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.BreakdownTimeDay;
import nts.uk.ctx.at.shared.dom.employment.statutory.worktime.employment.WorkingSystem;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfIrregularWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.AddSettingOfRegularWork;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.CalculationByActualTimeAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.StatutoryDivision;
import nts.uk.ctx.at.shared.dom.workrule.addsettingofworktime.AddVacationSet;
import nts.uk.ctx.at.shared.dom.workrule.addsettingofworktime.NotUseAtr;
import nts.uk.ctx.at.shared.dom.workrule.addsettingofworktime.VacationAddTimeSet;
import nts.uk.ctx.at.shared.dom.workrule.waytowork.PersonalLaborCondition;
import nts.uk.ctx.at.shared.dom.worktime.SiftCode;
import nts.uk.ctx.at.shared.dom.worktype.VacationCategory;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;

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
	 * 日数単位の休暇時間計算
	 * 
	 * @return 1日の時間内訳時間
	 */
	public static AttendanceTime vacationTimeOfcalcDaily(WorkType workType, VacationCategory vacationCategory,
			PredetermineTimeSetForCalc predetermineTimeSet, Optional<SiftCode> siftCode,
			Optional<PersonalLaborCondition> personalCondition, VacationAddTimeSet vacationAddTimeSet) {
		BreakdownTimeDay breakDownTimeDay = getVacationAddSet(predetermineTimeSet, siftCode, personalCondition,
				vacationAddTimeSet);
		switch (workType.getDailyWork().decisionMatchWorkType(vacationCategory.convertWorkTypeClassification())) {
		case FULL_TIME:
			return breakDownTimeDay.getOneDay();
		case MORNING:
			return breakDownTimeDay.getMorning();
		case AFTERNOON:
			return breakDownTimeDay.getAfternoon();
		default:
			throw new RuntimeException("unknown WorkType");
		}
	}

	/**
	 * 休暇加算設定の取得
	 * 
	 * @return
	 */
	public static BreakdownTimeDay getVacationAddSet(PredetermineTimeSetForCalc predetermineTimeSet,
			Optional<SiftCode> siftCode, Optional<PersonalLaborCondition> personalCondition,
			VacationAddTimeSet vacationAddTimeSet) {
		BreakdownTimeDay breakdownTimeDay;
		if (siftCode.isPresent()) {
			breakdownTimeDay = predetermineTimeSet.getAdditionSet().getAddTime();
		} else {
			if (personalCondition.isPresent()) {
				breakdownTimeDay = personalCondition.get().getHolidayAddTimeSet().getAddTime();
			} else {
				breakdownTimeDay = vacationAddTimeSet.getAdditionTime();
			}
			return predetermineTimeSet.getAdditionSet().getAddTime();
		}
		return breakdownTimeDay;
	}

	/**
	 * 日単位の休暇加算時間の計算
	 * @author ken_takasu
	 * 
	 * @param statutoryDivision  割増区分（"通常"、"割増")
	 * @param workingSystem　　　　　労働制
	 * @param addSettingOfRegularWork　　
	 * @param vacationAddTimeSet
	 * @param workType
	 * @param predetermineTimeSet
	 * @param siftCode
	 * @param personalCondition
	 * @param addSettingOfIrregularWork
	 * @param addSettingOfFlexWork
	 * @return
	 */
	public VacationAddTime calcVacationAddTime(StatutoryDivision statutoryDivision,
											   WorkingSystem workingSystem,
											   AddSettingOfRegularWork addSettingOfRegularWork,
											   VacationAddTimeSet vacationAddTimeSet,
											   WorkType workType,
											   PredetermineTimeSetForCalc predetermineTimeSet,
											   Optional<SiftCode> siftCode,
											   Optional<PersonalLaborCondition> personalCondition, 
											   AddSettingOfIrregularWork addSettingOfIrregularWork,
											   AddSettingOfFlexWork addSettingOfFlexWork
											   ) {
		VacationAddTime vacationAddTime;
		if (getCalculationByActualTimeAtr(workingSystem, statutoryDivision, addSettingOfRegularWork,
				addSettingOfIrregularWork, addSettingOfFlexWork).isCalclationByActualTime()) {// 実働時間以外も含めて計算する 場合
			// 加算時間の設定を取得
			BreakdownTimeDay breakdownTimeDay = getVacationAddSet(predetermineTimeSet, siftCode, personalCondition,
					vacationAddTimeSet);
			// 休暇加算時間を加算するかどうか判断
			vacationAddTime = judgeVacationAddTime(breakdownTimeDay, workingSystem, statutoryDivision,
					addSettingOfRegularWork, vacationAddTimeSet, workType, addSettingOfIrregularWork,
					addSettingOfFlexWork);
		} else {// 実働時間のみで計算する 場合
				// 休暇加算時間を全て 0 で返す
			vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0), new AttendanceTime(0));
		}
		return vacationAddTime;
	}

	/**
	 * 休暇加算時間の計算
	 * @author ken_takasu
	 * @return
	 */
	public VacationAddTime judgeVacationAddTime(BreakdownTimeDay breakdownTimeDay, 
												WorkingSystem workingSystem,
												StatutoryDivision statutoryDivision, 
												AddSettingOfRegularWork addSettingOfRegularWork,
												VacationAddTimeSet vacationAddTimeSet, 
												WorkType workType,
												AddSettingOfIrregularWork addSettingOfIrregularWork, 
												AddSettingOfFlexWork addSettingOfFlexWork) {
		VacationAddTime vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0),
				new AttendanceTime(0));
		// 加算する休暇設定を取得
		AddVacationSet addVacationSet = getAddVacationSet(workingSystem, statutoryDivision, addSettingOfRegularWork,
				vacationAddTimeSet, addSettingOfIrregularWork, addSettingOfFlexWork);
		// 勤務区分をチェックする
		if (workType.isOneDay()) {
			vacationAddTime.addVacationAddTime(checkVacationToAdd(addVacationSet, workType.getDailyWork().getOneDay(),
					breakdownTimeDay.getOneDay()));
		} else {
			vacationAddTime.addVacationAddTime(checkVacationToAdd(addVacationSet, workType.getDailyWork().getMorning(),
					breakdownTimeDay.getMorning()));
			vacationAddTime.addVacationAddTime(checkVacationToAdd(addVacationSet,
					workType.getDailyWork().getAfternoon(), breakdownTimeDay.getAfternoon()));
		}
		return vacationAddTime;
	}

	/***
	 * 加算する休暇の種類を取得 
	 * @author ken_takasu
	 * @return
	 */
	public AddVacationSet getAddVacationSet(WorkingSystem workingSystem, 
											StatutoryDivision statutoryDivision,
											AddSettingOfRegularWork addSettingOfRegularWork,
											VacationAddTimeSet vacationAddTimeSet,
											AddSettingOfIrregularWork addSettingOfIrregularWork, 
											AddSettingOfFlexWork addSettingOfFlexWork) {
		AddVacationSet addVacationSet = new AddVacationSet(NotUseAtr.Donot, NotUseAtr.Donot, NotUseAtr.Donot);// 下のif文に入らない場合は全てしないを返す
		// 休暇加算設定の取得
		if (getUseAtr(workingSystem, statutoryDivision, addSettingOfRegularWork, addSettingOfIrregularWork,
				addSettingOfFlexWork).isUse()) {// 加算する場合
			addVacationSet = vacationAddTimeSet.getAddVacationSet();
		}
		return addVacationSet;
	}

	/**
	 * 休暇加算設定の取得
	 * @author ken_takasu
	 * @param workingSystem
	 * @return
	 */
	private NotUseAtr getUseAtr(WorkingSystem workingSystem, 
								StatutoryDivision statutoryDivision,
								AddSettingOfRegularWork addSettingOfRegularWork,
								AddSettingOfIrregularWork addSettingOfIrregularWork,
								AddSettingOfFlexWork addSettingOfFlexWork) {
		switch (workingSystem) {
		case RegularWork:
			return addSettingOfRegularWork.getNotUseAtr(statutoryDivision);

		case FlexTimeWork:
			return addSettingOfFlexWork.getNotUseAtr(statutoryDivision);

		case VariableWorkingTimeWork:
			return addSettingOfIrregularWork.getNotUseAtr(statutoryDivision);

		case ExcludedWorkingCalculate:
			throw new RuntimeException("不正な労働制です");
		default:
			throw new RuntimeException("不正な労働制です");
		}
	}

	/**
	 * 休暇加算時間を加算するかチェックする
	 * @author ken_takasu
	 * @return
	 */
	private VacationAddTime checkVacationToAdd(AddVacationSet addVacationSet,
											   WorkTypeClassification workTypeClassification, 
											   AttendanceTime attendanceTime) {
		VacationAddTime vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0),
				new AttendanceTime(0));
		if (workTypeClassification.isAnnualLeave()) {// 年休の場合
			if (addVacationSet.getAnnualLeave().isUse()) {
				vacationAddTime = new VacationAddTime(attendanceTime, new AttendanceTime(0), new AttendanceTime(0));
			}
		} else if (workTypeClassification.isYearlyReserved()) {// 積立年休の場合
			if (addVacationSet.getＲetentionYearly().isUse()) {
				vacationAddTime = new VacationAddTime(new AttendanceTime(0), attendanceTime, new AttendanceTime(0));
			}
		} else if (workTypeClassification.isSpecialHoliday()) {// 特別休暇の場合
			if (addVacationSet.getSpecialHoliday().isUse()) {
				vacationAddTime = new VacationAddTime(new AttendanceTime(0), new AttendanceTime(0), attendanceTime);
			}
		}
		return vacationAddTime;
	}

	/**
	 * 休暇加算設定の取得
	 * @author ken_takasu
	 * @param workingSystem
	 * @return
	 */
	private CalculationByActualTimeAtr getCalculationByActualTimeAtr(WorkingSystem workingSystem,
																	 StatutoryDivision statutoryDivision,
																	 AddSettingOfRegularWork addSettingOfRegularWork,
																	 AddSettingOfIrregularWork addSettingOfIrregularWork, 
																	 AddSettingOfFlexWork addSettingOfFlexWork) {
		switch (workingSystem) {
		case RegularWork:
			return addSettingOfRegularWork.getCalculationByActualTimeAtr(statutoryDivision);

		case FlexTimeWork:
			return addSettingOfFlexWork.getCalculationByActualTimeAtr(statutoryDivision);

		case VariableWorkingTimeWork:
			return addSettingOfIrregularWork.getCalculationByActualTimeAtr(statutoryDivision);

		case ExcludedWorkingCalculate:
			throw new RuntimeException("不正な労働制です");
		default:
			throw new RuntimeException("不正な労働制です");
		}
	}

}
