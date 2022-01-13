package nts.uk.ctx.at.record.app.find.monthly.root.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.AggregatePremiumTime;

@Data
/** 集計割増時間 */
@NoArgsConstructor
@AllArgsConstructor
public class AggregatePremiumTimeDto implements ItemConst, AttendanceItemDataGate {

	/** 割増時間項目NO: 割増時間項目NO */
	private int no;

	/** 時間: 勤怠月間時間 */
	@AttendanceItemValue(type = ValueType.TIME)
	@AttendanceItemLayout(jpPropertyName = TIME, layout = LAYOUT_A)
	private int time;

	/** 金額: 勤怠月間金額 */
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	@AttendanceItemLayout(jpPropertyName = AMOUNT, layout = LAYOUT_B)
	private long amount;
	
	public static AggregatePremiumTimeDto from (AggregatePremiumTime domain) {
		AggregatePremiumTimeDto dto = new AggregatePremiumTimeDto();
		if(domain != null) {
			dto.setNo(domain.getPremiumTimeItemNo().value);
			dto.setTime(domain.getTime() == null ? 0 : domain.getTime().valueAsMinutes());
			dto.setAmount(domain.getAmount() == null ? 0L : domain.getAmount().v());
		}
		return dto;
	}

	public AggregatePremiumTime toDomain(){
		return AggregatePremiumTime.of(ExtraTimeItemNo.valueOf(no), new AttendanceTimeMonth(time), new AttendanceAmountMonth(amount));
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case TIME:
			return Optional.of(ItemValue.builder().value(time).valueType(ValueType.TIME));
		case AMOUNT:
			return Optional.of(ItemValue.builder().value(amount).valueType(ValueType.TIME));
		default:
			return AttendanceItemDataGate.super.valueOf(path);
		}
		
	}

	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case TIME:
		case AMOUNT:
			return PropType.VALUE;
		default:
			return AttendanceItemDataGate.super.typeOf(path);
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case TIME:
			time = value.valueOrDefault(0); break;
		case AMOUNT:
			amount = value.valueOrDefault(0L); break;
		default:
		}
	}
}
