package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumRate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

@Getter
@AllArgsConstructor
/**　nts.uk.ctx.at.record.dom.premiumtime.PremiumTime　参照 */
/** 割増時間 */
public class PremiumTime {
	// 割増時間NO - primitive value
	private ExtraTimeItemNo premiumTimeNo;

	// 割増時間
	private AttendanceTime premitumTime;

	/** 割増金額 */
	private AttendanceAmountDaily premiumAmount;

	/** 単価 **/
	private WorkingHoursUnitPrice unitPrice;


	public PremiumTime(ExtraTimeItemNo premiumTimeNo, AttendanceTime premitumTime, AttendanceAmountDaily premiumAmount) {
		super();
		this.premiumTimeNo = premiumTimeNo;
		this.premitumTime = premitumTime;
		this.premiumAmount = premiumAmount;
		this.unitPrice = WorkingHoursUnitPrice.ZERO;
	}
	
	/**
	 * 全て0で作成する
	 * @param premiumTimeNo 割増時間NO
	 * @return 割増時間
	 */
	public static PremiumTime createAllZero(ExtraTimeItemNo premiumTimeNo) {
		return new PremiumTime(premiumTimeNo, AttendanceTime.ZERO, AttendanceAmountDaily.ZERO, WorkingHoursUnitPrice.ZERO);
	}
	
	/**
	 * 割増時間計算
	 * @param dailyRecordDto 日別勤怠コンバーター
	 * @param unitPriceHistory 社員単価履歴項目
	 * @param personCostCalculation 人件費計算設定
	 * @param premiumSetting 処理中の割増設定
	 * @return 割増時間
	 */
	public static PremiumTime create(
			DailyRecordToAttendanceItemConverter dailyRecordDto,
			Optional<EmployeeUnitPriceHistoryItem> unitPriceHistory,
			PersonCostCalculation personCostCalculation,
			PremiumSetting premiumSetting) {
		if(premiumSetting.getAttendanceItems().isEmpty()) {
			return PremiumTime.createAllZero(premiumSetting.getID());
		}
		
		//割増時間
		AttendanceTime time = new AttendanceTime(dailyRecordDto.convert(premiumSetting.getAttendanceItems()).stream()
				.filter(c -> c.value() != null)
				.mapToInt(r -> (int)r.value())
				.sum());
		
		//社員時間単価を取得する
		WorkingHoursUnitPrice priceUnit = WorkingHoursUnitPrice.ZERO;
		if(unitPriceHistory.isPresent()) {
			priceUnit = personCostCalculation.getPremiumUnitPrice(premiumSetting.getID(), unitPriceHistory.get());
		}
		
		//割増金額
		AttendanceAmountDaily amount = calcPremiumAmount(priceUnit, premiumSetting.getRate(), time, personCostCalculation.getRoundingSetting());
		
		return new PremiumTime(premiumSetting.getID(), time, amount, priceUnit);
	}
	
	/**
	 * 割増時間計算（応援用）
	 * @param dailyRecordDto 日別勤怠コンバーター
	 * @param unitPriceHistory 社員単価履歴項目
	 * @param personCostCalculation 人件費計算設定
	 * @param premiumSetting 処理中の割増設定
	 * @return 割増時間
	 */
	public static PremiumTime createForSupport(
			DailyRecordToAttendanceItemConverter dailyRecordDto,
			Optional<EmployeeUnitPriceHistoryItem> unitPriceHistory,
			PersonCostCalculation personCostCalculation,
			PremiumSetting premiumSetting) {
		if(premiumSetting.getAttendanceItems().isEmpty()) {
			return PremiumTime.createAllZero(premiumSetting.getID());
		}
		
		final int FLEX_TIME_ITEM_ID = 556; //フレックス時間（勤怠項目ID）
		
		//割増時間
		AttendanceTime time = new AttendanceTime(dailyRecordDto.convert(premiumSetting.getAttendanceItems()).stream()
				.filter(c -> c.value() != null && c.getItemId() != FLEX_TIME_ITEM_ID) //フレックス時間を除く
				.mapToInt(r -> (int)r.value())
				.sum());
		
		//社員時間単価を取得する
		WorkingHoursUnitPrice priceUnit = WorkingHoursUnitPrice.ZERO;
		if(unitPriceHistory.isPresent()) {
			priceUnit = personCostCalculation.getPremiumUnitPrice(premiumSetting.getID(), unitPriceHistory.get());
		}
		
		//割増金額
		AttendanceAmountDaily amount = calcPremiumAmount(priceUnit, premiumSetting.getRate(), time, personCostCalculation.getRoundingSetting());
		
		return new PremiumTime(premiumSetting.getID(), time, amount, priceUnit);
	}
	
	/**
	 * 再計算する
	 * @param personCostCalc 人件費計算設定
	 * @return 割増時間
	 */
	public PremiumTime reCalc(PersonCostCalculation personCostCalc) {
		//割増金額
		AttendanceAmountDaily amount = AttendanceAmountDaily.ZERO;
		Optional<PremiumSetting> premiumSetting = personCostCalc.getPremiumSetting(this.premiumTimeNo);
		if(premiumSetting.isPresent()) {
			amount = calcPremiumAmount(this.unitPrice, premiumSetting.get().getRate(), this.premitumTime, personCostCalc.getRoundingSetting());
		}
		return new PremiumTime(this.premiumTimeNo, this.premitumTime, amount, this.unitPrice);
	}
	
	/**
	 * 割増金額を計算する
	 * @param priceUnit 単価
	 * @param premiumRate 割増率
	 * @param premiumTime 割増時間
	 * @param roundingSet 人件費丸め設定
	 * @return 割増金額
	 */
	private static AttendanceAmountDaily calcPremiumAmount(WorkingHoursUnitPrice priceUnit, PremiumRate premiumRate,
			AttendanceTime premiumTime, PersonCostRoundingSetting roundingSet) {
		// A = 単価 * 割増率
		BigDecimal afterPremium = BigDecimal.valueOf(priceUnit.v()).multiply(premiumRate.toDecimal());
		// Aを丸める
		BigDecimal afterPremiumRounding = roundingSet.getRoundingOfPremium().round(afterPremium);
		// B = A * 割増時間
		BigDecimal amount = afterPremiumRounding.multiply(premiumTime.hourWithDecimal());
		// Bを丸める
		BigDecimal afterAmountRounding = roundingSet.getAmountRoundingSetting().round(amount);
		// Bを返す
		return  new AttendanceAmountDaily(afterAmountRounding.intValue());
	}
}
