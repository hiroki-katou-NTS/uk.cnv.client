package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;

/**
 * 日別勤怠の割増時間 (new)
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.割増時間.日別勤怠の割増時間
 *
 * @author nampt
 * 日別実績の割増時間 (old)
 *
 */
@Getter
@AllArgsConstructor
public class PremiumTimeOfDailyPerformance {

	/** 割増時間 **/
	private List<PremiumTime> premiumTimes;


	/** 割増金額合計 **/
	private AttendanceAmountDaily totalAmount;

	/** 割増労働時間合計 **/
	private AttendanceTime totalWorkingTime;


	// TODO 既存コード用に追加 要修正
	public PremiumTimeOfDailyPerformance(List<PremiumTime> premiumTimes) {
		this.premiumTimes = premiumTimes;
		this.totalAmount = new AttendanceAmountDaily( 0 );
		this.totalWorkingTime = new AttendanceTime( 0 );
	}

	/**
	 * 空で作成する
	 * @return 日別実績の割増時間
	 */
	public static PremiumTimeOfDailyPerformance createEmpty() {
		return new PremiumTimeOfDailyPerformance(Collections.emptyList(), AttendanceAmountDaily.ZERO, AttendanceTime.ZERO);
	}

	/**
	 * 日別勤怠の割増時間の計算
	 * @param dailyRecordDto 日別勤怠コンバーター
	 * @param unitPriceHistory 社員単価履歴
	 * @param personCostCalculation 人件費計算設定
	 * @return 日別勤怠の割増時間
	 */
	public static PremiumTimeOfDailyPerformance calcPremiumTime(
			DailyRecordToAttendanceItemConverter dailyRecordDto,
			Optional<EmployeeUnitPriceHistoryItem> unitPriceHistory,
			Optional<PersonCostCalculation> personCostCalc) {
		if(!personCostCalc.isPresent()) {
			return PremiumTimeOfDailyPerformance.createEmpty();
		}
		//割増時間
		List<PremiumTime> times = personCostCalc.get().getPremiumSettings().stream()
				.map(pcc -> PremiumTime.create(dailyRecordDto, unitPriceHistory, personCostCalc.get(), pcc))
				.collect(Collectors.toList());
		
		//割増労働時間合計
		AttendanceTime totalTime = new AttendanceTime(times.stream()
				.filter(t -> personCostCalc.get().isIncludeTotalTime(t.getPremiumTimeNo()))
				.map(t -> t.getPremitumTime().valueAsMinutes())
				.collect(Collectors.summingInt(v -> v)));
		
		//割増金額合計
		AttendanceAmountDaily totalAmount = new AttendanceAmountDaily(times.stream()
				.map(t -> t.getPremiumAmount().v())
				.collect(Collectors.summingInt(v -> v)));
		
		return new PremiumTimeOfDailyPerformance(times, totalAmount, totalTime);
	}
	
	/**
	 * 日別勤怠の割増時間の計算（応援用）
	 * @param dailyRecordDto 日別勤怠コンバーター
	 * @param unitPriceHistory 社員単価履歴
	 * @param personCostCalc 人件費計算設定
	 * @return 日別勤怠の割増時間
	 */
	public static PremiumTimeOfDailyPerformance calcPremiumTimeForSupport(
			DailyRecordToAttendanceItemConverter dailyRecordDto,
			Optional<EmployeeUnitPriceHistoryItem> unitPriceHistory,
			Optional<PersonCostCalculation> personCostCalc) {
		if(!personCostCalc.isPresent()) {
			return PremiumTimeOfDailyPerformance.createEmpty();
		}
		//割増時間
		List<PremiumTime> times = personCostCalc.get().getPremiumSettings().stream()
			.map(pcc -> PremiumTime.createForSupport(dailyRecordDto, unitPriceHistory, personCostCalc.get(), pcc))
			.collect(Collectors.toList());

		//割増労働時間合計
		AttendanceTime totalTime = new AttendanceTime(times.stream()
				.filter(t -> personCostCalc.get().isIncludeTotalTime(t.getPremiumTimeNo()))
				.map(t -> t.getPremitumTime().valueAsMinutes())
				.collect(Collectors.summingInt(v -> v)));
		
		//割増金額合計
		AttendanceAmountDaily totalAmount = new AttendanceAmountDaily(times.stream()
				.map(t -> t.getPremiumAmount().v())
				.collect(Collectors.summingInt(v -> v)));

		return new PremiumTimeOfDailyPerformance(times, totalAmount, totalTime);
	}

	//指定されたNoに一致する割増時間を取得する
	public Optional<PremiumTime> getPremiumTime(ExtraTimeItemNo number){
		return this.premiumTimes.stream().filter(tc -> tc.getPremiumTimeNo().equals(number)).findFirst();
	}

	/**
	 * 再計算する
	 * @param personCostCalculation 人件費計算設定
	 * @return 日別勤怠の割増時間
	 */
	public PremiumTimeOfDailyPerformance reCalc(Optional<PersonCostCalculation> personCostCalculation) {
		if(!personCostCalculation.isPresent()) {
			return this;
		}
		//割増時間
		List<PremiumTime> times = this.premiumTimes.stream()
			.map(p -> p.reCalc(personCostCalculation.get()))
			.collect(Collectors.toList());

		//割増労働時間合計
		AttendanceTime totalTime = new AttendanceTime(times.stream()
				.filter(t -> personCostCalculation.get().isIncludeTotalTime(t.getPremiumTimeNo()))
				.map(t -> t.getPremitumTime().valueAsMinutes())
				.collect(Collectors.summingInt(v -> v)));
		
		//割増金額合計
		AttendanceAmountDaily totalAmount = new AttendanceAmountDaily(times.stream()
				.map(t -> t.getPremiumAmount().v())
				.collect(Collectors.summingInt(v -> v)));

		return new PremiumTimeOfDailyPerformance(times, totalAmount, totalTime);
	}
}
