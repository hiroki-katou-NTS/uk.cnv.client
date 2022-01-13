package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

/** 割増時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumTimeDto implements ItemConst, AttendanceItemDataGate {

	/** 割増時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = PREMIUM)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer premitumTime;
	
	/** 割増金額: 勤怠日別金額 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = PREMIUM + AMOUNT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer premiumAmount;
	
	/** 単価: 社員時間単価 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = PRICE_UNIT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer unitPrice;
	
	/** 割増時間NO: 割増時間NO */
	private int no;

	@Override
	public PremiumTimeDto clone() {
		return new PremiumTimeDto(premitumTime, premiumAmount, unitPrice, no);
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch(path) {
		case PREMIUM:
			return Optional.of(ItemValue.builder().value(premitumTime).valueType(ValueType.TIME));
		case (PREMIUM + AMOUNT):
			return Optional.of(ItemValue.builder().value(premiumAmount).valueType(ValueType.AMOUNT_NUM));
		case PRICE_UNIT:
			return Optional.of(ItemValue.builder().value(unitPrice).valueType(ValueType.AMOUNT_NUM));
		default:
			return AttendanceItemDataGate.super.valueOf(path);
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch(path) {
		case PREMIUM:
			this.premitumTime = value.valueOrDefault(0);
			break;
		case (PREMIUM + AMOUNT):
			this.premiumAmount = value.valueOrDefault(0);
			break;
		case PRICE_UNIT:
			this.unitPrice = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch(path) {
		case PREMIUM:
		case (PREMIUM + AMOUNT):
		case PRICE_UNIT:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}
	
	public PremiumTime toDomain() {
		return new PremiumTime(
				ExtraTimeItemNo.valueOf(this.no),
				this.premitumTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.premitumTime),
				this.premiumAmount == null ? AttendanceAmountDaily.ZERO : new AttendanceAmountDaily(this.premiumAmount),
				this.unitPrice == null ? WorkingHoursUnitPrice.ZERO : new WorkingHoursUnitPrice(this.unitPrice));
	}
	
	public static PremiumTimeDto toDto(PremiumTime domain) {
		return new PremiumTimeDto(
				domain.getPremitumTime().valueAsMinutes(),
				domain.getPremiumAmount().v(),
				domain.getUnitPrice().v(),
				domain.getPremiumTimeNo().value);
	}
}
