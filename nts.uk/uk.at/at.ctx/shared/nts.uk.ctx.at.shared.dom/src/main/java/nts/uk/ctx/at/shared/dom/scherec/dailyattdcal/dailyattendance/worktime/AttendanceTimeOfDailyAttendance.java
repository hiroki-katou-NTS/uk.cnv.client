package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.arc.diagnose.stopwatch.Stopwatch.TimeUnit;
import nts.arc.diagnose.stopwatch.Stopwatches;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.SystemFixedErrorAlarm;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.secondorder.medical.MedicalCareTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportWorkTimeOfDailyAttendanceService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.AttendanceItemDictionaryForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareEditState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.CheckExcessAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.specific.UpperLimitTotalWorkingHour;
import nts.uk.ctx.at.shared.dom.worktime.common.GoLeavingWorkAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
/**
 * 日別勤怠の勤怠時間 (new)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.実働時間.日別勤怠の勤怠時間
 * @author tutk
 *
 */
@Getter
public class AttendanceTimeOfDailyAttendance implements DomainObject {
	
	//勤務予定時間 - 日別実績の勤務予定時間
	private WorkScheduleTimeOfDaily workScheduleTimeOfDaily;
	
	//実働時間/実績時間  - 日別実績の勤務実績時間 - 勤務時間
	private ActualWorkingTimeOfDaily actualWorkingTimeOfDaily;
	
	//滞在時間 - 日別実績の滞在時間 change tyle
	private StayingTimeOfDaily stayingTime;
	
	//不就労時間 - 勤怠時間
	private AttendanceTimeOfExistMinus unEmployedTime;
	
	//予実差異時間 - 勤怠時間
	private AttendanceTimeOfExistMinus budgetTimeVariance;
	
	//医療時間 - 日別実績の医療時間
	private MedicalCareTimeOfDaily medicalCareTime;

	/**
	 * @param schedule 	予定時間
	 * @param actual 勤務時間
	 * @param stay 滞在時間
	 * @param budget 予実差異時間
	 * @param unEmploy 不就労時間
	 */
	public AttendanceTimeOfDailyAttendance (
			 WorkScheduleTimeOfDaily schedule,
			 ActualWorkingTimeOfDaily actual,
			 StayingTimeOfDaily stay,
			 AttendanceTimeOfExistMinus budget,
			 AttendanceTimeOfExistMinus unEmploy) {
		
		this.workScheduleTimeOfDaily = schedule;
		this.actualWorkingTimeOfDaily = actual;
		this.stayingTime = stay;
		this.budgetTimeVariance = budget;
		this.unEmployedTime = unEmploy;
	}
				
	/**
	 * @param workScheduleTimeOfDaily 予定時間
	 * @param actualWorkingTimeOfDaily 勤務時間
	 * @param stayingTime 滞在時間
	 * @param unEmployedTime 不就労時間
	 * @param budgetTimeVariance 予実差異時間
	 * @param medicalCareTime 医療時間
	 */
	public AttendanceTimeOfDailyAttendance(
			WorkScheduleTimeOfDaily workScheduleTimeOfDaily,
			ActualWorkingTimeOfDaily actualWorkingTimeOfDaily, 
			StayingTimeOfDaily stayingTime,
			AttendanceTimeOfExistMinus unEmployedTime, 
			AttendanceTimeOfExistMinus budgetTimeVariance,
			MedicalCareTimeOfDaily medicalCareTime) {
		super();
		this.workScheduleTimeOfDaily = workScheduleTimeOfDaily;
		this.actualWorkingTimeOfDaily = actualWorkingTimeOfDaily;
		this.stayingTime = stayingTime;
		this.unEmployedTime = unEmployedTime;
		this.budgetTimeVariance = budgetTimeVariance;
		this.medicalCareTime = medicalCareTime;
	}

	/**
	 * エラーチェックの指示メソッド 
	 * @param attendanceItemConverter 
	 * @return 社員のエラーチェック一覧
	 */
	public List<EmployeeDailyPerError> getErrorList(String employeeId,GeneralDate targetDate,
			   										SystemFixedErrorAlarm fixedErrorAlarmCode, CheckExcessAtr checkAtr) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getActualWorkingTimeOfDaily() != null) {
			return this.getActualWorkingTimeOfDaily().requestCheckError(employeeId, targetDate, fixedErrorAlarmCode, checkAtr);
		}
		return returnErrorItem;
	}
	
	public AttendanceTimeOfDailyAttendance inssertActualWorkingTimeOfDaily(ActualWorkingTimeOfDaily time) {
		return new AttendanceTimeOfDailyAttendance (
						this.workScheduleTimeOfDaily, time, this.stayingTime, this.budgetTimeVariance,
						this.unEmployedTime
				); 
	}
	
	/**
	 * 時間・回数・乖離系(計算で求める全ての値)が全て０
	 * @return
	 */
	public static AttendanceTimeOfDailyAttendance allZeroValue() {
		return new AttendanceTimeOfDailyAttendance(
				WorkScheduleTimeOfDaily.defaultValue(),
				ActualWorkingTimeOfDaily.defaultValue(),
				StayingTimeOfDaily.defaultValue(),
				new AttendanceTimeOfExistMinus(0),
				new AttendanceTimeOfExistMinus(0), 
				MedicalCareTimeOfDaily.defaultValue());
	}
	
	/**
	 * 日別実績の勤怠時間の計算
	 * @param oneDay 1日の範囲クラス
	 * @param schePreTimeSet 
	 * @param ootsukaFixCalsSet 
	 * @param workTimeDailyAtr2 
	 * @param scheduleReGetClass 
	 * @param schePred 
	 * @param converter 
	 * @param companyCommonSetting 
	 * @param personalSetting 
	 * @param recordWorkTimeCode 
	 * @param integrationOfDaily2 
	 * @param declareResult 申告時間帯作成結果
	 * @return 日別実績(Work)クラス
	 */
	public static IntegrationOfDaily calcTimeResult(
			VacationClass vacation, WorkType workType,
			Optional<SettingOfFlexWork> flexCalcMethod,
			List<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
			List<DivergenceTimeRoot> divergenceTimeList,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime, ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			DailyRecordToAttendanceItemConverter converter, ManagePerCompanySet companyCommonSetting, Optional<WorkTimeCode> recordWorkTimeCode,
			DeclareTimezoneResult declareResult) {

		Optional<AttendanceTimeOfDailyAttendance> calcResult = Optional.empty();
		
		IntegrationOfDaily result = converter.setData(recordReGetClass.getIntegrationOfDaily()).toDomain();;

		/* 日別実績(Work)の退避 */
		IntegrationOfDaily copyIntegrationOfDaily = converter.setData(recordReGetClass.getIntegrationOfDaily()).toDomain();
		
		// 乖離時間計算用 勤怠項目ID紐づけDto作成
		DailyRecordToAttendanceItemConverter forCalcDivergenceDto = converter.setData(copyIntegrationOfDaily);
		
		//連続勤務の時は予定は計算を行い、実績は計算不要なため
		//2019.3.11時点
		if(scheduleReGetClass.getIntegrationOfWorkTime().isPresent() && !recordReGetClass.getIntegrationOfWorkTime().isPresent()) {
			calcResult = Optional.of(calcTimeResultForContinusWork(recordReGetClass, 
																   workType, 
																   vacation, 
																   flexCalcMethod, 
																   eachCompanyTimeSet, 
																   scheduleReGetClass,
																   conditionItem, 
																   predetermineTimeSetByPersonInfo, 
																   recordWorkTimeCode));
			// // 編集状態を取得（日別実績の編集状態が持つ勤怠項目IDのみのList作成）
			List<Integer> attendanceItemIdList = recordReGetClass.getIntegrationOfDaily().getEditState().stream()
					.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());

			copyIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(calcResult.isPresent()?Optional.of(calcResult.get()):Optional.empty());
			result = copyIntegrationOfDaily;
			
			List<ItemValue> itemValueList = Collections.emptyList();
			if (!attendanceItemIdList.isEmpty()) {
				DailyRecordToAttendanceItemConverter beforDailyRecordDto = forCalcDivergenceDto.setData(recordReGetClass.getIntegrationOfDaily());
				itemValueList = beforDailyRecordDto.convert(attendanceItemIdList);
				DailyRecordToAttendanceItemConverter afterDailyRecordDto = forCalcDivergenceDto
						.setData(copyIntegrationOfDaily);
				afterDailyRecordDto.merge(itemValueList);

				// 手修正された項目の値を計算前に戻す
				result = afterDailyRecordDto.toDomain();
			}
		}
		else {
			calcResult = Optional.of(collectCalculationResult(vacation,
															  workType,
															  flexCalcMethod,
															  eachCompanyTimeSet,
															  forCalcDivergenceDto,
															  divergenceTimeList,
															  calculateOfTotalConstraintTime,
															  scheduleReGetClass,
															  recordReGetClass,
															  conditionItem,
															  predetermineTimeSetByPersonInfo,
															  recordWorkTimeCode,
															  declareResult));
			// // 編集状態を取得（日別実績の編集状態が持つ勤怠項目IDのみのList作成）
			List<Integer> attendanceItemIdList = recordReGetClass.getIntegrationOfDaily().getEditState().stream()
					.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());

			copyIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(calcResult.isPresent()?Optional.of(calcResult.get()):Optional.empty());
			Stopwatches.start("勤怠項目コンバーター計測");
			List<ItemValue> itemValueList = Collections.emptyList();
			if (!attendanceItemIdList.isEmpty()) {
				DailyRecordToAttendanceItemConverter beforDailyRecordDto = forCalcDivergenceDto.setData(recordReGetClass.getIntegrationOfDaily());
				itemValueList = beforDailyRecordDto.convert(attendanceItemIdList);
				DailyRecordToAttendanceItemConverter afterDailyRecordDto = forCalcDivergenceDto
						.setData(copyIntegrationOfDaily);
				afterDailyRecordDto.merge(itemValueList);

				// 手修正された項目の値を計算前に戻す
				copyIntegrationOfDaily = afterDailyRecordDto.toDomain();
				// マイナスの乖離時間を0にする
				AttendanceTimeOfDailyAttendance.divergenceMinusValueToZero(copyIntegrationOfDaily);
			}
			Stopwatches.stop("勤怠項目コンバーター計測");
			Stopwatches.printAll(TimeUnit.MILLI_SECOND);
			Stopwatches.reset("勤怠項目コンバーター計測");
			// 手修正後の再計算
			result = reCalc(copyIntegrationOfDaily,
					recordReGetClass.getCalculationRangeOfOneDay(), recordReGetClass.getIntegrationOfDaily().getEmployeeId(), companyCommonSetting , forCalcDivergenceDto,
					attendanceItemIdList, recordReGetClass.getIntegrationOfDaily().getYmd(), PremiumAtr.RegularWork, recordReGetClass.getHolidayCalcMethodSet(),
					recordReGetClass.getWorkTimezoneCommonSet(), recordReGetClass);
			
			// 日別勤怠の応援作業時間(List)を計算する
			result.setOuenTime(SupportWorkTimeOfDailyAttendanceService.create(
					scheduleReGetClass,
					recordReGetClass,
					workType,
					recordWorkTimeCode,
					predetermineTimeSetByPersonInfo,
					calculateOfTotalConstraintTime,
					converter,
					result));
			
			DailyRecordToAttendanceItemConverter afterReCalcDto = forCalcDivergenceDto.setData(result);
			
			if (!attendanceItemIdList.isEmpty()) {
				// 手修正された項目の値を計算値に戻す(手修正再計算の後Ver)
				afterReCalcDto.merge(itemValueList);
				result = afterReCalcDto.toDomain();
				// マイナスの乖離時間を0にする
				AttendanceTimeOfDailyAttendance.divergenceMinusValueToZero(result);
			}
			// 手修正後の再計算(2回目)
			result = secondReCalc(companyCommonSetting, recordReGetClass.getPersonDailySetting(), result);
		}
		// 申告結果に応じて編集状態を更新する
		AttendanceTimeOfDailyAttendance.updateEditStateForDeclare(result, declareResult);
		// 日別実績(Work)を返す
		return result;	
	}
	
	/**
	 * マイナスの乖離時間を0にする
	 * @param itgOfDaily 日別実績(Work)
	 */
	public static void divergenceMinusValueToZero(IntegrationOfDaily itgOfDaily){
		
		if (!itgOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) return;
		AttendanceTimeOfDailyAttendance attendanceTime = itgOfDaily.getAttendanceTimeOfDailyPerformance().get();
		TotalWorkingTime totalWorkingTime = attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime();
		ExcessOfStatutoryTimeOfDaily excessStatTime = totalWorkingTime.getExcessOfStatutoryTimeOfDaily();
		// 残業枠時間
		if (excessStatTime.getOverTimeWork().isPresent()){
			OverTimeOfDaily overtime = excessStatTime.getOverTimeWork().get();
			OverTimeOfDaily.divergenceMinusValueToZero(overtime.getOverTimeWorkFrameTime());
		}
		// 休出枠時間
		if (excessStatTime.getWorkHolidayTime().isPresent()){
			HolidayWorkTimeOfDaily holidayWorkTime = excessStatTime.getWorkHolidayTime().get();
			HolidayWorkTimeOfDaily.divergenceMinusValueToZero(holidayWorkTime.getHolidayWorkFrameTime());
		}
	}
	
	/**
	 * 手修正後の再計算
	 * 
	 * @param calcResultIntegrationOfDaily
	 * @param calculationRangeOfOneDay
	 * @param companyId
	 * @param companyCommonSetting
	 * @param overTotalTime
	 *            手修正前の残業時間の合計
	 * @param attendanceItemIdList
	 * @param recordReGetClass
	 * @param holidayWorkTotalTime
	 *            手修正前の休出時間の合計
	 * @return
	 */
	private static IntegrationOfDaily reCalc(IntegrationOfDaily calcResultIntegrationOfDaily,
			CalculationRangeOfOneDay calculationRangeOfOneDay, String companyId,
			ManagePerCompanySet companyCommonSetting, DailyRecordToAttendanceItemConverter converter,
			List<Integer> attendanceItemIdList, GeneralDate targetDate, PremiumAtr premiumAtr,
			HolidayCalcMethodSet holidayCalcMethodSet, Optional<WorkTimezoneCommonSet> commonSetting,
			ManageReGetClass recordReGetClass) {
		
		 List<ItemValue> beforeItemValue = getBeforeItemValue(converter, calcResultIntegrationOfDaily);
		// 乖離時間(AggregateRoot)取得
		List<DivergenceTimeRoot> divergenceTimeList = companyCommonSetting.getDivergenceTime();
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {

			AttendanceTimeOfDailyAttendance attendanceTimeOfDailyPerformance = calcResultIntegrationOfDaily
					.getAttendanceTimeOfDailyPerformance().get();

			ActualWorkingTimeOfDaily actualWorkingTimeOfDaily = attendanceTimeOfDailyPerformance
					.getActualWorkingTimeOfDaily();

			calcResultIntegrationOfDaily
					.setAttendanceTimeOfDailyPerformance(Optional.of(attendanceTimeOfDailyPerformance
							.inssertActualWorkingTimeOfDaily(actualWorkingTimeOfDaily.inssertTotalWorkingTime(
									actualWorkingTimeOfDaily.getTotalWorkingTime().calcDiverGenceTime()))));

		}

		// //深夜時間
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime() != null) {
				;
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().reCalcMidNightTime();
			}
		}

		// 総労働時間
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime() != null) {
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().calcTotalWorkingTimeForReCalc();
			}
			// 就業時間金額
			calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().reCalcWithinWorkTimeAmount(companyCommonSetting.getPersonnelCostSetting().get(targetDate));
		}

		// 予実差異時間
		AttendanceTimeOfExistMinus scheActDiffTime = new AttendanceTimeOfExistMinus(0);
		// 総労働時間が編集している項目リストに含まれていなければ再計算
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			AttendanceTimeOfExistMinus scheTime = new AttendanceTimeOfExistMinus(
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getWorkScheduleTimeOfDaily().getWorkScheduleTime().getTotal().valueAsMinutes());
			scheActDiffTime = calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getBudgetTimeVariance();
			if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily() != null
					&& calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily().getTotalWorkingTime() != null
					&& !attendanceItemIdList.contains(new Integer(559))) {
				AttendanceTimeOfExistMinus totalWorkTime = new AttendanceTimeOfExistMinus(
						calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime().valueAsMinutes());
				scheActDiffTime = totalWorkTime.minusMinutes(scheTime.valueAsMinutes());
			}
		}
		// 不就労時間
		AttendanceTimeOfExistMinus alreadlyDedBindTime = new AttendanceTimeOfExistMinus(0);
		// 総労働時間が編集している項目リストに含まれていなければ再計算
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			alreadlyDedBindTime = calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getUnEmployedTime();
			if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime() != null
					&& calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily() != null
					&& !attendanceItemIdList.contains(new Integer(559))) {
				// ↓で総控除時間を引く
				alreadlyDedBindTime = new AttendanceTimeOfExistMinus(
						calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getStayingTime()
								.getStayingTime()
								.minusMinutes(calcResultIntegrationOfDaily
										.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
										.getTotalWorkingTime().calcTotalDedTime(recordReGetClass, premiumAtr)
										.valueAsMinutes())
								.valueAsMinutes());
				alreadlyDedBindTime = alreadlyDedBindTime.minusMinutes(calcResultIntegrationOfDaily
						.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.recalcActualTime().valueAsMinutes());
			}
		}

		// 実働時間
		calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().calcActualTimeForReCalc();

		// 乖離時間計算用 勤怠項目ID紐づけDto作成
		DailyRecordToAttendanceItemConverter forCalcDivergenceDto = converter.setData(calcResultIntegrationOfDaily);

		if (calcResultIntegrationOfDaily != null
				&& calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			// 乖離時間を計算する
			val reCalcDivergence = DivergenceTimeOfDaily.create(forCalcDivergenceDto,
					divergenceTimeList, calcResultIntegrationOfDaily.getCalAttr(),
					recordReGetClass.getFixRestTimeSetting(),
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTotalWorkingTime(),
							recordReGetClass.getWorkTimeSetting(),
							recordReGetClass.getWorkType());
			if(!beforeItemValue.isEmpty()) {
				calcResultIntegrationOfDaily = mergeValueEdited(forCalcDivergenceDto, beforeItemValue);
				forCalcDivergenceDto = converter.setData(calcResultIntegrationOfDaily);
			}
			
			// 割増時間の計算
			PremiumTimeOfDailyPerformance premiumTimeOfDailyPerformance = PremiumTimeOfDailyPerformance.calcPremiumTime(
					forCalcDivergenceDto,
					recordReGetClass.getPersonDailySetting().getUnitPrice(),
					companyCommonSetting.getPersonnelCostSetting().get(targetDate));
			
			val reCreateActual = ActualWorkingTimeOfDaily.of(
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getConstraintDifferenceTime(),
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getConstraintTime(),
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTimeDifferenceWorkingHours(),
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTotalWorkingTime(),
					reCalcDivergence,
					// calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getPremiumTimeOfDailyPerformance());
					premiumTimeOfDailyPerformance);

			val reCreateAttendanceTime = new AttendanceTimeOfDailyAttendance(
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
						.getWorkScheduleTimeOfDaily(),
					reCreateActual,
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getStayingTime(),
					alreadlyDedBindTime, scheActDiffTime,
					calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getMedicalCareTime());
			calcResultIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(Optional.of(reCreateAttendanceTime));
		}
		
		
		// 総労働の上限設定
		Optional<UpperLimitTotalWorkingHour> upperControl = companyCommonSetting.getUpperControl();
		if(upperControl.isPresent()) {
			upperControl.get().controlUpperLimit(calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily().getTotalWorkingTime());
		}
		return calcResultIntegrationOfDaily;
	}
	
	private static List<ItemValue> getBeforeItemValue(DailyRecordToAttendanceItemConverter converter, IntegrationOfDaily domain) {
		List<Integer> attendanceItemIdList = domain.getEditState().stream()
				.map(editState -> editState.getAttendanceItemId()).distinct().collect(Collectors.toList());
		DailyRecordToAttendanceItemConverter beforDailyRecordDto = converter.setData(domain);
		List<ItemValue> itemValueList = beforDailyRecordDto.convert(attendanceItemIdList);
		return itemValueList;
	}
	
	private static IntegrationOfDaily mergeValueEdited(DailyRecordToAttendanceItemConverter converter,
			List<ItemValue> itemValueList) {
		converter.merge(itemValueList);
		return converter.toDomain();
	}
	
	/**
	 * 手修正後の再計算（応援用）
	 * @param calcResultIntegrationOfDaily 日別勤怠(WORK)
	 * @param converter コンバーター
	 * @param recordReGetClass
	 * @return 日別勤怠(WORK)
	 */
	public static IntegrationOfDaily reCalcForSupport(
			IntegrationOfDaily calcResultIntegrationOfDaily,
			DailyRecordToAttendanceItemConverter converter,
			ManageReGetClass recordReGetClass) {
		
		if (!calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent())
			return calcResultIntegrationOfDaily;

		AttendanceTimeOfDailyAttendance attendanceTimeOfDailyPerformance = calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get();
		
		ActualWorkingTimeOfDaily actualWorkingTimeOfDaily = attendanceTimeOfDailyPerformance.getActualWorkingTimeOfDaily();

		calcResultIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(
				Optional.of(attendanceTimeOfDailyPerformance.inssertActualWorkingTimeOfDaily(
						actualWorkingTimeOfDaily.inssertTotalWorkingTime(
								actualWorkingTimeOfDaily.getTotalWorkingTime().calcDiverGenceTime()))));

		// 深夜時間
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime() != null) {
			calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().reCalcMidNightTime();
		}

		// 総労働時間
		if (calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime() != null) {
			calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime().calcTotalWorkingTimeForReCalc();
			// 就業時間金額
			calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
					.getTotalWorkingTime().getWithinStatutoryTimeOfDaily().reCalcWithinWorkTimeAmount(
							recordReGetClass.getCompanyCommonSetting().getPersonnelCostSetting().get(recordReGetClass.getIntegrationOfDaily().getYmd()));
		}
		
		// 乖離時間計算用 勤怠項目ID紐づけDto作成
		DailyRecordToAttendanceItemConverter forCalcDivergenceDto = converter.setData(calcResultIntegrationOfDaily);

		// 割増時間の計算
		PremiumTimeOfDailyPerformance premiumTimeOfDailyPerformance = PremiumTimeOfDailyPerformance.calcPremiumTimeForSupport(
						forCalcDivergenceDto,
						recordReGetClass.getPersonDailySetting().getUnitPrice(),
						recordReGetClass.getCompanyCommonSetting().getPersonnelCostSetting().get(recordReGetClass.getIntegrationOfDaily().getYmd()));

		val reCreateActual = ActualWorkingTimeOfDaily.of(
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getConstraintDifferenceTime(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getConstraintTime(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTimeDifferenceWorkingHours(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getDivTime(),
				premiumTimeOfDailyPerformance);

		val reCreateAttendanceTime = new AttendanceTimeOfDailyAttendance(
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getWorkScheduleTimeOfDaily(),
				reCreateActual,
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getStayingTime(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getUnEmployedTime(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getBudgetTimeVariance(),
				calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getMedicalCareTime());
		
		calcResultIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(Optional.of(reCreateAttendanceTime));
		
		// 総労働の上限設定
		Optional<UpperLimitTotalWorkingHour> upperControl = recordReGetClass.getCompanyCommonSetting().getUpperControl();
		upperControl.ifPresent(tc -> {
			tc.controlUpperLimit(calcResultIntegrationOfDaily.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily().getTotalWorkingTime());
		});

		return calcResultIntegrationOfDaily;
	}
	
	/**
	 * 手修正後の再計算（2回目）
	 * @param companyCommonSet 会社別設定管理
	 * @param personDailySet 社員設定管理
	 * @param calcResult 手修正後の日別勤怠(Work)
	 * @return 日別勤怠(Work)
	 */
	public static IntegrationOfDaily secondReCalc(ManagePerCompanySet companyCommonSet, ManagePerPersonDailySet personDailySet, IntegrationOfDaily calcResult) {
		Optional<AttendanceTimeOfDailyAttendance> attendanceTime = calcResult.getAttendanceTimeOfDailyPerformance();
		if(!attendanceTime.isPresent()) {
			return calcResult;
		}
		// 割増時間
		PremiumTimeOfDailyPerformance old = attendanceTime.get().getActualWorkingTimeOfDaily().getPremiumTimeOfDailyPerformance();
		PremiumTimeOfDailyPerformance reCalc = old.reCalc(companyCommonSet.getPersonnelCostSetting().get(calcResult.getYmd()));
		
		ActualWorkingTimeOfDaily reCreateActual = ActualWorkingTimeOfDaily.of(
				calcResult.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getConstraintDifferenceTime(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getConstraintTime(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTimeDifferenceWorkingHours(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getDivTime(),
				reCalc);

		AttendanceTimeOfDailyAttendance reCreateAttendanceTime = new AttendanceTimeOfDailyAttendance(
				calcResult.getAttendanceTimeOfDailyPerformance().get().getWorkScheduleTimeOfDaily(),
				reCreateActual,
				calcResult.getAttendanceTimeOfDailyPerformance().get().getStayingTime(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getUnEmployedTime(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getBudgetTimeVariance(),
				calcResult.getAttendanceTimeOfDailyPerformance().get().getMedicalCareTime());
		
		calcResult.setAttendanceTimeOfDailyPerformance(Optional.of(reCreateAttendanceTime));
		
		return calcResult;
	}

	/**
	 * 
	 * @param recordReGetClass 実績のデータ管理クラス
	 * @param workType 実績側の勤務種類
	 * @param scheduleReGetClass 予定のデータ管理クラス
	 * @param recordWorkTimeCode 
	 * @return　計算結果
	 */
	public static AttendanceTimeOfDailyAttendance calcTimeResultForContinusWork(ManageReGetClass recordReGetClass, WorkType workType, 
			VacationClass vacation, Optional<SettingOfFlexWork> flexCalcMethod, 
			List<CompensatoryOccurrenceSetting> eachCompanyTimeSet, ManageReGetClass scheduleReGetClass, WorkingConditionItem conditionItem, 
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo, 
			Optional<WorkTimeCode> recordWorkTimeCode){
		
		val workScheduleTime = calcWorkSheduleTime(recordReGetClass, workType, 
												   vacation, 
												   flexCalcMethod, 
												   eachCompanyTimeSet, scheduleReGetClass,conditionItem,
												   predetermineTimeSetByPersonInfo,recordWorkTimeCode);
		
		return new AttendanceTimeOfDailyAttendance(
				workScheduleTime,
				ActualWorkingTimeOfDaily.defaultValue(),
				StayingTimeOfDaily.defaultValue(),
				new AttendanceTimeOfExistMinus(0),
				new AttendanceTimeOfExistMinus(0),
				MedicalCareTimeOfDaily.defaultValue());
	}
	
	/**
	 * 時間の計算結果をまとめて扱う
	 * @param schePred 
	 * @param recordWorkTimeCode 
	 * @param schePreTimeSet 
	 * @param breakTimeCount 
	 * @param ootsukaFixCalsSet 
	 * @param integrationOfDaily 
	 * @param flexSetting 
	 * @param 1日の範囲クラス
	 * @param declareResult 申告時間帯作成結果
	 * @return 計算結果
	 */
	private static AttendanceTimeOfDailyAttendance collectCalculationResult(
				VacationClass vacation, WorkType workType,
				Optional<SettingOfFlexWork> flexCalcMethod,
				List<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
				DailyRecordToAttendanceItemConverter forCalcDivergenceDto, List<DivergenceTimeRoot> divergenceTimeList,
				CalculateOfTotalConstraintTime calculateOfTotalConstraintTime, ManageReGetClass scheduleReGetClass,
				ManageReGetClass recordReGetClass,WorkingConditionItem conditionItem,
				Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
//				Optional<PredetermineTimeSetForCalc> schePred,
				Optional<WorkTimeCode> recordWorkTimeCode,
				DeclareTimezoneResult declareResult) {
		
		/*日別実績の勤務予定時間の計算*/
		val workScheduleTime = calcWorkSheduleTime(recordReGetClass, workType, 
													vacation, 
												   flexCalcMethod,  
												    eachCompanyTimeSet, scheduleReGetClass,conditionItem,
												    predetermineTimeSetByPersonInfo,
												    recordWorkTimeCode);
		
			/*日別実績の実績時間の計算*/
		Optional<WorkTimeDailyAtr> workDailyAtr = recordReGetClass.getWorkTimeSetting() != null && recordReGetClass.getWorkTimeSetting().isPresent()?
													Optional.of(recordReGetClass.getWorkTimeSetting().get().getWorkTimeDivision().getWorkTimeDailyAtr()):
													Optional.empty();
		ActualWorkingTimeOfDaily actualWorkingTimeOfDaily = ActualWorkingTimeOfDaily.calcRecordTime(recordReGetClass,
			    vacation,
			    workType,
			    workDailyAtr,
			    flexCalcMethod,
				eachCompanyTimeSet,
				forCalcDivergenceDto,
				divergenceTimeList,
				conditionItem,
				predetermineTimeSetByPersonInfo,
				workScheduleTime, recordWorkTimeCode,
				declareResult);

		/* 滞在時間の計算 */
		StayingTimeOfDaily stayingTime = new StayingTimeOfDaily(
				recordReGetClass.getIntegrationOfDaily().getPcLogOnInfo().isPresent()
						? recordReGetClass.getIntegrationOfDaily().getPcLogOnInfo().get().calcPCLogOnCalc(
								recordReGetClass.getIntegrationOfDaily().getAttendanceLeave(),
								GoLeavingWorkAtr.LEAVING_WORK)
						: new AttendanceTimeOfExistMinus(0),
				recordReGetClass.getIntegrationOfDaily().getPcLogOnInfo().isPresent()
						? recordReGetClass.getIntegrationOfDaily().getPcLogOnInfo().get().calcPCLogOnCalc(
								recordReGetClass.getIntegrationOfDaily().getAttendanceLeave(), GoLeavingWorkAtr.GO_WORK)
						: new AttendanceTimeOfExistMinus(0),
				recordReGetClass.getIntegrationOfDaily().getAttendanceLeavingGate().isPresent()
						? recordReGetClass.getIntegrationOfDaily().getAttendanceLeavingGate().get()
								.calcBeforeAttendanceTime(recordReGetClass.getIntegrationOfDaily().getAttendanceLeave(),
										GoLeavingWorkAtr.GO_WORK)
						: new AttendanceTimeOfExistMinus(0),
				StayingTimeOfDaily.calcStayingTimeOfDaily(
						recordReGetClass.getIntegrationOfDaily().getAttendanceLeavingGate(),
						recordReGetClass.getIntegrationOfDaily().getPcLogOnInfo(),
						recordReGetClass.getIntegrationOfDaily().getAttendanceLeave(), calculateOfTotalConstraintTime),
				recordReGetClass.getIntegrationOfDaily().getAttendanceLeavingGate().isPresent()
						? recordReGetClass.getIntegrationOfDaily().getAttendanceLeavingGate().get()
								.calcBeforeAttendanceTime(recordReGetClass.getIntegrationOfDaily().getAttendanceLeave(),
										GoLeavingWorkAtr.LEAVING_WORK)
						: new AttendanceTimeOfExistMinus(0));

		/*不就労時間*/
		val deductedBindTime = stayingTime.getStayingTime().minusMinutes(actualWorkingTimeOfDaily.getTotalWorkingTime().calcTotalDedTime(recordReGetClass, PremiumAtr.RegularWork).valueAsMinutes());
		val unEmployedTime = deductedBindTime.minusMinutes(actualWorkingTimeOfDaily.getTotalWorkingTime().getActualTime().valueAsMinutes());
		/*予定差異時間の計算*/
		val budgetTimeVariance = new AttendanceTimeOfExistMinus(actualWorkingTimeOfDaily.getTotalWorkingTime().getTotalTime().minusMinutes(workScheduleTime.getWorkScheduleTime().getTotal().valueAsMinutes()).valueAsMinutes());
		/*医療時間*/
		val medicalCareTime = new MedicalCareTimeOfDaily(WorkTimeNightShift.DAY_SHIFT,
														 new AttendanceTime(0),
														 new AttendanceTime(0),
														 new AttendanceTime(0));

		return new AttendanceTimeOfDailyAttendance(
				workScheduleTime,
				actualWorkingTimeOfDaily,
				stayingTime,
				new AttendanceTimeOfExistMinus(unEmployedTime.valueAsMinutes()),
				budgetTimeVariance,
				medicalCareTime);
		
	}
	
	/**
	 * 計画所定の算出
	 * @param recordOneDay　実績の1日の範囲クラス
	 * @param scheduleOneDay　予定の1日の範囲クラス
	 * @param schePreTime　労働条件の個人勤務区分別の就時コードから取得した所定時間クラス
	 * @param workType
	 * @param scheWorkType 
	 * @param overTimeAutoCalcSet 
	 * @param holidayAutoCalcSetting 
	 * @param personalCondition 
	 * @param vacationClass 
	 * @param late 
	 * @param leaveEarly 
	 * @param workingSystem 
	 * @param illegularAddSetting 
	 * @param flexAddSetting 
	 * @param regularAddSetting 
	 * @param holidayAddtionSet 
	 * @param overTimeAutoCalcAtr 
	 * @param scheWorkTimeDailyAtr 
	 * @param flexCalcMethod 
	 * @param holidayCalcMethodSet 
	 * @param raisingAutoCalcSet 
	 * @param bonusPayAutoCalcSet 
	 * @param calcAtrOfDaily 
	 * @param eachWorkTimeSet 
	 * @param eachCompanyTimeSet 
	 * @param schePred 
	 * @param recordWorkTimeCode 
	 * @param breakTimeCount 
	 * @param integrationOfDaily 
	 * @param flexAutoCalSet 
	 * @param coreTimeSetting 
	 * @param statutoryFrameNoList 
	 * @return
	 */
	private static WorkScheduleTimeOfDaily calcWorkSheduleTime(ManageReGetClass recordReGetClass,
															   WorkType workType, 
															   VacationClass vacationClass, 
															   Optional<SettingOfFlexWork> flexCalcMethod,
															   List<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
															   ManageReGetClass scheRegetManage
															   ,WorkingConditionItem conditionItem,
															   Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
															   Optional<WorkTimeCode> recordWorkTimeCode) {
		//勤務予定時間を計算
		//val schedulePredWorkTime = (scheduleOneDay.getWorkInformastionOfDaily().getRecordInfo().getWorkTimeCode() == null)?new AttendanceTime(0):recordOneDay.getPredetermineTimeSetForCalc().getpredetermineTime(workType.getDailyWork());
		AttendanceTime scheTotalTime = new AttendanceTime(0);
		AttendanceTime scheExcessTotalTime = new AttendanceTime(0);
		AttendanceTime scheWithinTotalTime = new AttendanceTime(0);
		//実績所定労働時間の計算
		val actualPredWorkTime = ( recordReGetClass.getCalculationRangeOfOneDay() == null
									|| !recordWorkTimeCode.isPresent()
									|| workType.getDailyWork().isHolidayWork()
									|| recordReGetClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc() == null)
									?new AttendanceTime(0)
//									:recordOneDay.getPredetermineTimeSetForCalc().getPredetermineTimeByAttendanceAtr(workType.getDailyWork().decisionNeedPredTime());
									:recordReGetClass.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc().getpredetermineTime(workType.getDailyWork());	
		//予定勤務種類が設定されてなかったら、実績の所定労働のみ埋めて返す
		if(!scheRegetManage.getWorkType().isPresent()) return new WorkScheduleTimeOfDaily(new WorkScheduleTime(scheTotalTime,scheExcessTotalTime,scheWithinTotalTime), actualPredWorkTime);
		
		Optional<WorkTimeDailyAtr> workDailyAtr = (scheRegetManage.getWorkTimeSetting() != null && scheRegetManage.getWorkTimeSetting().isPresent()) ? Optional.of(scheRegetManage.getWorkTimeSetting().get().getWorkTimeDivision().getWorkTimeDailyAtr()) : Optional.empty();
		TotalWorkingTime totalWorkingTime = TotalWorkingTime.createAllZEROInstance();
		Optional<PredetermineTimeSetForCalc> schePreTimeSet = Optional.empty();
		if(!scheRegetManage.getIntegrationOfWorkTime().isPresent() && recordReGetClass.getIntegrationOfWorkTime().isPresent()) {
			totalWorkingTime = TotalWorkingTime.calcAllDailyRecord(scheRegetManage,
																   vacationClass, 
																   scheRegetManage.getWorkType().get(), 
																   workDailyAtr, //就業時間帯依存
																   flexCalcMethod, //詳細が決まってなさそう(2018.6.21)
																   eachCompanyTimeSet, //会社共通 
																   conditionItem,
																   predetermineTimeSetByPersonInfo,
																   recordWorkTimeCode,
																   new DeclareTimezoneResult());
			scheTotalTime = totalWorkingTime.getTotalTime();
			if(totalWorkingTime.getWithinStatutoryTimeOfDaily() != null)
				scheWithinTotalTime = totalWorkingTime.getWithinStatutoryTimeOfDaily().getWorkTime();
			int overWorkTime = totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()?totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().calcTotalFrameTime().v():0;
			overWorkTime += totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()?totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().calcTransTotalFrameTime().v():0;
			int holidayWorkTime = totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()?totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().calcTotalFrameTime().v():0;
			holidayWorkTime += totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()?totalWorkingTime.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get().calcTransTotalFrameTime().v():0;
			scheExcessTotalTime = new AttendanceTime(overWorkTime + holidayWorkTime);
			//計画所定時間の計算
			schePreTimeSet = Optional.of(scheRegetManage.getCalculationRangeOfOneDay().getPredetermineTimeSetForCalc());
		}
		return new WorkScheduleTimeOfDaily(new WorkScheduleTime(scheTotalTime,scheExcessTotalTime,scheWithinTotalTime), actualPredWorkTime);
	}

	/**
	 * 申告結果に応じて編集状態を更新する
	 * @param itgOfDaily 日別実績(Work)(ref)
	 * @param declareResult 申告時間帯作成結果
	 */
	private static void updateEditStateForDeclare(
			IntegrationOfDaily itgOfDaily,
			DeclareTimezoneResult declareResult){
		
		if (!declareResult.getDeclareCalcRange().isPresent()) return;
		DeclareEditState editState = declareResult.getDeclareCalcRange().get().getEditState();
		AttendanceItemDictionaryForCalc attdIdDic = AttendanceItemDictionaryForCalc.setDictionaryValue();
		
		// 申告編集状態．残業を確認する
		for (OverTimeFrameNo frameNo : editState.getOvertime()){
			// 申告用編集状態を追加する
			attdIdDic.findId("残業時間" + frameNo.v()).ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
			attdIdDic.findId("振替残業時間" + frameNo.v()).ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
		}
		// 申告編集状態．休出を確認する
		for (HolidayWorkFrameNo frameNo : editState.getHolidayWork()){
			// 申告用編集状態を追加する
			attdIdDic.findId("休出時間" + frameNo.v()).ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
			attdIdDic.findId("振替時間" + frameNo.v()).ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
		}
		// 申告編集状態．残業深夜を確認する
		if (editState.isOvertimeMn()){
			attdIdDic.findId("就外残業深夜時間").ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
		}
		// 申告編集状態．休出深夜を確認する
		for (StaturoryAtrOfHolidayWork statAtr : editState.getHolidayWorkMn()){
			switch(statAtr){
			case WithinPrescribedHolidayWork:
				attdIdDic.findId("法内休出外深夜").ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
				break;
			case ExcessOfStatutoryHolidayWork:
				attdIdDic.findId("法外休出外深夜").ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
				break;
			case PublicHolidayWork:
				attdIdDic.findId("就外法外祝日深夜").ifPresent(itemId -> itgOfDaily.addEditStateForDeclare(itemId));
				break;
			}
		}
	}

	/**
	 * 時間帯を変更して日別勤怠の勤怠時間を計算する
	 * @param scheduleReGetClass 予定
	 * @param recordReGetClass 実績
	 * @param workType 勤務種類
	 * @param recordWorkTimeCode 就業時間帯コード
	 * @param predetermineTimeSetByPersonInfo 所定時間設定(計算用クラス)
	 * @param schePred 所定時間設定（予定用)
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param calculateOfTotalConstraintTime  総拘束時間の計算
	 * @param converter コンバーター
	 * @param timeSpan 変更する時間
	 * @return 日別勤怠の勤怠時間
	 */
	public static AttendanceTimeOfDailyAttendance calcAfterChangedRange(
			ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,
			WorkType workType,
			Optional<WorkTimeCode> recordWorkTimeCode,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime,
			DailyRecordToAttendanceItemConverter converter,
			TimeSpanForDailyCalc timeSpan) {
		
		//1日の計算範囲を重複する時間帯で作り直す
		Optional<ManageReGetClass> duplicate = recordReGetClass.recreateWithDuplicate(timeSpan);
		
		if(!duplicate.isPresent()) {
			return AttendanceTimeOfDailyAttendance.allZeroValue();
		}
		//常に「打刻から計算する」にする。※申請のみで運用する場合に応援作業の残業等が計算されない問題の対策
		duplicate.get().getIntegrationOfDaily().setCalAttr(CalAttrOfDailyAttd.createAllCalculate());
		
		//日別勤怠の勤怠時間を計算する
		return collectCalculationResult(
				VacationClass.createAllZero(),
				workType,
				Optional.of(SettingOfFlexWork.defaultValue()),
				duplicate.get().getCompanyCommonSetting().getCompensatoryLeaveComSet().getCompensatoryOccurrenceSetting(),
				converter,
				duplicate.get().getCompanyCommonSetting().getDivergenceTime(),
				calculateOfTotalConstraintTime,
				scheduleReGetClass,
				duplicate.get(),
				duplicate.get().getPersonDailySetting().getPersonInfo(),
				predetermineTimeSetByPersonInfo,
				recordWorkTimeCode,
				new DeclareTimezoneResult());
	}
	
	/**
	 * 遅刻時間を取得する
	 * @return
	 */
	public List<LateTimeOfDaily> getLateTimeOfDaily(){
		//@勤務時間.総労働時間.遅刻時間
		return this.actualWorkingTimeOfDaily.getTotalWorkingTime().getLateTimeOfDaily();
	}
	
	/**
	 * 早退時間を取得する
	 * @return
	 */
	public List<LeaveEarlyTimeOfDaily> getLeaveEarlyTimeOfDaily(){
		//@勤務時間.総労働時間.早退時間
		return this.actualWorkingTimeOfDaily.getTotalWorkingTime().getLeaveEarlyTimeOfDaily();
	}
	
	/**
	 * 外出時間を取得する
	 * @return
	 */
	public List<OutingTimeOfDaily> getOutingTimeOfDaily() {
		//@勤務時間.総労働時間.外出時間
		return this.actualWorkingTimeOfDaily.getTotalWorkingTime().getOutingTimeOfDailyPerformance();
	}
	
	public static AttendanceTimeOfDailyAttendance createDefault() {
		return new AttendanceTimeOfDailyAttendance(WorkScheduleTimeOfDaily.defaultValue(),
				ActualWorkingTimeOfDaily.defaultValue(), StayingTimeOfDaily.defaultValue(),
				AttendanceTimeOfExistMinus.ZERO, AttendanceTimeOfExistMinus.ZERO,
				MedicalCareTimeOfDaily.defaultValue());
	}
}

