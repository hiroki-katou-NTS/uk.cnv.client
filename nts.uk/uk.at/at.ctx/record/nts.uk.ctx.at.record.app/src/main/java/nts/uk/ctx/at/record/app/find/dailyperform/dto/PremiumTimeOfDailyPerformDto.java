package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/** 日別勤怠の割増時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumTimeOfDailyPerformDto implements ItemConst, AttendanceItemDataGate {

	/** 割増時間: 割増時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = PREMIUM, 
			listMaxLength = 10, indexField = DEFAULT_INDEX_FIELD_NAME)
	private List<PremiumTimeDto> premiumTimes;
	
	/** 割増金額合計: 勤怠日別金額 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = TOTAL + AMOUNT)
	@AttendanceItemValue(type = ValueType.AMOUNT_NUM)
	private Integer totalAmount;

	/** 割増労働時間合計: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = TOTAL + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer totalTime;

	@Override
	public PremiumTimeOfDailyPerformDto clone() {
		return new PremiumTimeOfDailyPerformDto(
				premiumTimes == null ? null : premiumTimes.stream().map(t -> t.clone()).collect(Collectors.toList()),
				totalAmount.intValue(),
				totalTime.intValue());
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch(path) {
		case (TOTAL + AMOUNT):
			return Optional.of(ItemValue.builder().value(totalAmount).valueType(ValueType.AMOUNT_NUM));
		case (TOTAL + TIME):
			return Optional.of(ItemValue.builder().value(totalTime).valueType(ValueType.TIME));
		default:
			return AttendanceItemDataGate.super.valueOf(path);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		switch (path) {
		case PREMIUM:
			return (List<T>) this.premiumTimes;
		default:
		}
		return AttendanceItemDataGate.super.gets(path);
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch(path) {
		case (TOTAL + AMOUNT):
			this.totalAmount = value.valueOrDefault(0);
			break;
		case (TOTAL + TIME):
			this.totalTime = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		switch (path) {
		case PREMIUM:
			this.premiumTimes = (List<PremiumTimeDto>) value;
			break;
		default:
			break;
		}
	}

	@Override
	public int size(String path) {
		switch (path) {
		case PREMIUM:
			return 10;
		default:
			break;
		}
		return AttendanceItemDataGate.super.size(path);
	}

	@Override
	public PropType typeOf(String path) {
		switch(path) {
		case PREMIUM:
			return PropType.IDX_LIST;
		case (TOTAL + AMOUNT):
		case (TOTAL + TIME):
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}

	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		if(path.equals(PREMIUM)) {
			return new PremiumTimeDto();
		}
		return null;
	}

	public static PremiumTimeOfDailyPerformDto toDto(PremiumTimeOfDailyPerformance domain) {
		return domain == null ? new PremiumTimeOfDailyPerformDto() : new PremiumTimeOfDailyPerformDto(
				domain.getPremiumTimes().stream().map(p -> PremiumTimeDto.toDto(p)).collect(Collectors.toList()),
				domain.getTotalAmount().v(),
				domain.getTotalWorkingTime().v());
	}
	
	public PremiumTimeOfDailyPerformance toDomain() {
		return new PremiumTimeOfDailyPerformance(
				ConvertHelper.mapTo(premiumTimes, c -> c.toDomain()),
				toAttendanceAmountDaily(this.totalAmount),
				toAttendanceTime(this.totalTime));
	}
	
	private AttendanceTime toAttendanceTime(Integer value) {
		return value == null ? AttendanceTime.ZERO : new AttendanceTime(value);
	}
	
	private AttendanceAmountDaily toAttendanceAmountDaily(Integer value) {
		return value == null ? AttendanceAmountDaily.ZERO : new AttendanceAmountDaily(value);
	}
}
