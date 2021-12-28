package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calculationsettings.totalrestrainttime.CalculateOfTotalConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.enu.DailyDomainGroup;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

@Getter
/** 時間帯別勤怠の時間 */
public class OuenAttendanceTimeEachTimeSheet implements DomainObject {

	/** 総労働時間: 勤怠時間 */
	private AttendanceTime totalTime;
	
	/** 休憩時間: 勤怠時間 */
	private AttendanceTime breakTime;
	
	/** 所定内時間: 勤怠時間 */
	private AttendanceTime withinTime;
	
	/** 所定内時間金額: 勤怠日別金額 */
	private AttendanceAmountDaily withinAmount;
	
	/** 割増時間: 割増時間 */
	private PremiumTimeOfDailyPerformance premiumTime;
	
	/** 医療時間: 時間帯別勤怠の医療時間 */
	private List<MedicalCareTimeEachTimeSheet> medicalTime;

	private OuenAttendanceTimeEachTimeSheet(AttendanceTime totalTime, AttendanceTime breakTime,
			AttendanceTime withinTime, AttendanceAmountDaily withinAmount, List<MedicalCareTimeEachTimeSheet> medicalTime,
			PremiumTimeOfDailyPerformance premiumTime) {
		super();
		this.totalTime = totalTime;
		this.breakTime = breakTime;
		this.withinTime = withinTime;
		this.withinAmount = withinAmount;
		this.medicalTime = medicalTime;
		this.premiumTime = premiumTime;
	}
	
	public static OuenAttendanceTimeEachTimeSheet create(AttendanceTime totalTime, 
			AttendanceTime breakTime, AttendanceTime withinTime, AttendanceAmountDaily withinAmount,
			List<MedicalCareTimeEachTimeSheet> medicalTime, PremiumTimeOfDailyPerformance premiumTime) {
		
		return new OuenAttendanceTimeEachTimeSheet(totalTime, breakTime, withinTime, withinAmount, medicalTime, premiumTime);
	}
	
	/**
	 * 時間帯別勤怠の時間を作成する
	 * @param scheduleReGetClass 予定
	 * @param recordReGetClass 実績
	 * @param workType 勤務種類
	 * @param recordWorkTimeCode 就業時間帯コード
	 * @param predetermineTimeSetByPersonInfo 所定時間設定(計算用クラス)
	 * @param bonusPayAutoCalcSet 加給自動計算設定
	 * @param calculateOfTotalConstraintTime  総拘束時間の計算
	 * @param converter コンバーター
	 * @param ouenWorkTimeSheet 応援作業時間帯
	 * @param integrationOfDaily 手修正を戻した日別勤怠(Work)
	 * @return 時間帯別勤怠の時間
	 */
	public static OuenAttendanceTimeEachTimeSheet create(
			ManageReGetClass scheduleReGetClass,
			ManageReGetClass recordReGetClass,
			WorkType workType,
			Optional<WorkTimeCode> recordWorkTimeCode,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			CalculateOfTotalConstraintTime calculateOfTotalConstraintTime,
			DailyRecordToAttendanceItemConverter converter,
			OuenWorkTimeSheetOfDailyAttendance ouenWorkTimeSheet,
			IntegrationOfDaily integrationOfDaily) {
		
		//作業時間帯の開始終了を取得する
		Optional<TimeSpanForDailyCalc> startEnd = ouenWorkTimeSheet.getTimeSheet().getStartAndEnd();
		
		if(!startEnd.isPresent())
			return OuenAttendanceTimeEachTimeSheet.createAllZero();
		
		//日別実績(Work)の退避
		IntegrationOfDaily copyIntegrationOfDaily = converter.setData(integrationOfDaily).toDomain();
		
		//時間帯を変更して日別勤怠の勤怠時間を計算する
		AttendanceTimeOfDailyAttendance calcResult = AttendanceTimeOfDailyAttendance.calcAfterChangedRange(
				scheduleReGetClass,
				recordReGetClass,
				workType,
				recordWorkTimeCode,
				predetermineTimeSetByPersonInfo,
				calculateOfTotalConstraintTime,
				converter,
				startEnd.get());
		
		copyIntegrationOfDaily.setAttendanceTimeOfDailyPerformance(Optional.of(calcResult));
		
		//手修正項目を戻した後の計算処理
		IntegrationOfDaily result = AttendanceTimeOfDailyAttendance.reCalcForSupport(
				copyIntegrationOfDaily,
				converter,
				recordReGetClass);
		
		//手修正を戻す前
		OuenAttendanceTimeEachTimeSheet beforeEdit = OuenAttendanceTimeEachTimeSheet.createAllZero();
		Optional<PersonCostCalculation> personCost = recordReGetClass.getCompanyCommonSetting().getPersonnelCostSetting().get(integrationOfDaily.getYmd());
		
		if(result.getAttendanceTimeOfDailyPerformance().isPresent()) {
			beforeEdit = create(result.getAttendanceTimeOfDailyPerformance().get(), personCost);
			result.getOuenTime().clear();
			result.getOuenTime().add(OuenWorkTimeOfDailyAttendance.create(
					ouenWorkTimeSheet.getWorkNo(),
					beforeEdit,
					OuenMovementTimeEachTimeSheet.createAllZero(),
					AttendanceAmountDaily.ZERO));
		}
		//応援のみ手修正を戻す
		result = revertSupportOnly(converter, integrationOfDaily, result);
		//手修正後の再計算(2回目)
		return OuenAttendanceTimeEachTimeSheet.secondReCalc(
				personCost,
				result,
				ouenWorkTimeSheet.getWorkNo());
	}
	
	/**
	 * 全て0で作成する
	 * @return
	 */
	public static OuenAttendanceTimeEachTimeSheet createAllZero() {
		return new OuenAttendanceTimeEachTimeSheet(AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceTime.ZERO, AttendanceAmountDaily.ZERO,
				Collections.emptyList(), PremiumTimeOfDailyPerformance.createEmpty());
	}
	
	/**
	 * 所定内と割増の金額を合計する
	 * @return 合計金額
	 */
	public AttendanceAmountDaily calcTotalAmount() {
		return new AttendanceAmountDaily(Math.addExact(this.withinAmount.v(), this.premiumTime.getTotalAmount().v()));
	}
	
	/**
	 * 日別勤怠の勤怠時間から作成する
	 * @param attendanceTime 日別勤怠の勤怠時間
	 * @return 時間帯別勤怠の時間
	 */
	private static OuenAttendanceTimeEachTimeSheet create(AttendanceTimeOfDailyAttendance attendanceTime, Optional<PersonCostCalculation> personCost) {
		AttendanceTime withinTime = attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getActualWorkTime().addMinutes(
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getActualWithinPremiumTime().minute());
		BigDecimal withinAmount = withinTime.hourWithDecimal().multiply(
						BigDecimal.valueOf(attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getUnitPrice().v()));
		
		return new OuenAttendanceTimeEachTimeSheet(
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime(),
				attendanceTime.getActualWorkingTimeOfDaily().getTotalWorkingTime().getBreakTimeOfDaily().getToRecordTotalTime().getTotalTime().getTime(),
				withinTime,
				personCost.map(p -> p.getRoundingSetting().roundWorkTimeAmount(withinAmount)).orElse(new AttendanceAmountDaily(withinAmount.intValue())),
				MedicalCareTimeEachTimeSheet.createAllZero(),//様式9が未実装の為、全て0
				attendanceTime.getActualWorkingTimeOfDaily().getPremiumTimeOfDailyPerformance());
	}
	
	/**
	 * 応援のみ手修正を戻す
	 * @param converter コンバーター
	 * @param beforeCalc 計算前の日別勤怠(Work)
	 * @param afterCalc 計算後の日別勤怠(Work)
	 * @return 手修正を戻した日別勤怠(Work)
	 */
	private static IntegrationOfDaily revertSupportOnly(DailyRecordToAttendanceItemConverter converter, IntegrationOfDaily beforeCalc, IntegrationOfDaily afterCalc) {
		//編集状態を取得（日別実績の編集状態が持つ勤怠項目IDのみのList作成）
		List<Integer> editIds = beforeCalc.getEditState().stream()
				.map(editState -> editState.getAttendanceItemId())
				.distinct()
				.collect(Collectors.toList());
		
		//応援の勤怠項目ID
		List<Integer> supportIds =AttendanceItemIdContainer.getItemIdByDailyDomains(DailyDomainGroup.SUPPORT_TIME);
		
		//編集状態のある応援の勤怠項目ID
		List<Integer> editSuppuortIds = editIds.stream().filter(e -> supportIds.contains(e)).collect(Collectors.toList());
		
		if (!editSuppuortIds.isEmpty()) {
			List<ItemValue> itemValueList = Collections.emptyList();
			DailyRecordToAttendanceItemConverter beforeDailyRecordDto = converter.setData(beforeCalc);
			itemValueList = beforeDailyRecordDto.convert(editSuppuortIds);
			DailyRecordToAttendanceItemConverter afterDailyRecordDto = converter.setData(afterCalc);
			afterDailyRecordDto.merge(itemValueList);
			
			// 手修正された項目の値を計算前に戻す
			afterCalc = afterDailyRecordDto.toDomain();
			// マイナスの乖離時間を0にする
			AttendanceTimeOfDailyAttendance.divergenceMinusValueToZero(afterCalc);
		}
		return afterCalc;
	}
	
	/**
	 * 手修正後の再計算（応援用、2回目）
	 * @param companyCommonSet 会社別設定管理
	 * @param personDailySet 社員設定管理
	 * @param calcResult 手修正後の日別勤怠(Work)
	 * @return 日別勤怠(Work)
	 */
	private static OuenAttendanceTimeEachTimeSheet secondReCalc(Optional<PersonCostCalculation> personCost, IntegrationOfDaily calcResult, SupportFrameNo supportNo) {
		Optional<OuenWorkTimeOfDailyAttendance> support = calcResult.getOuenTime().stream()
				.filter(o -> o.getWorkNo().equals(supportNo))
				.findFirst();
		if(!support.isPresent()) {
			return OuenAttendanceTimeEachTimeSheet.createAllZero();
		}
		//割増金額、合計
		PremiumTimeOfDailyPerformance old = support.get().getWorkTime().getPremiumTime();
		PremiumTimeOfDailyPerformance reCalc = old.reCalc(personCost);
		
		//単価
		WorkingHoursUnitPrice unitPrice = WorkingHoursUnitPrice.ZERO;
		if(calcResult.getAttendanceTimeOfDailyPerformance().isPresent()) {
			unitPrice = calcResult.getAttendanceTimeOfDailyPerformance().get()
					.getActualWorkingTimeOfDaily().getTotalWorkingTime().getWithinStatutoryTimeOfDaily().getUnitPrice();
		}
		//所定内時間金額
		BigDecimal withinAmount = support.get().getWorkTime().getWithinTime().hourWithDecimal().multiply(BigDecimal.valueOf(unitPrice.v()));
		
		return OuenAttendanceTimeEachTimeSheet.create(
				support.get().getWorkTime().getTotalTime(),
				support.get().getWorkTime().getBreakTime(),
				support.get().getWorkTime().getWithinTime(),
				personCost.map(p -> p.getRoundingSetting().roundWorkTimeAmount(withinAmount)).orElse(new AttendanceAmountDaily(withinAmount.intValue())),
				support.get().getWorkTime().getMedicalTime(),
				reCalc);
	}
}
