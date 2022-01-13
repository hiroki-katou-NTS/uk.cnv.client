package nts.uk.ctx.at.record.app.find.dailyperform.workrecord.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.PremiumTimeDto;
import nts.uk.ctx.at.shared.app.util.attendanceitem.ConvertHelper;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenMovementTimeEachTimeSheet;

/** 応援別勤務の移動時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OuenMovementTimeEachTimeSheetDto implements ItemConst, AttendanceItemDataGate {

	/** 総移動時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = MOVE + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer totalTime;
	
	/** 所定内移動時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = WITHIN_STATUTORY + MOVE + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer withinTime;
	
	/** 休憩時間：勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_C, jpPropertyName = BREAK + TIME)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer breakTime;
	
	/** 割増時間：割増時間 */
	@AttendanceItemLayout(layout = LAYOUT_D, jpPropertyName = PREMIUM + TIME, 
			listMaxLength = 10, indexField = DEFAULT_INDEX_FIELD_NAME)
	private List<PremiumTimeDto> premiumTimes;
	
	public OuenMovementTimeEachTimeSheet toDomain() {
		return OuenMovementTimeEachTimeSheet.create(
				this.totalTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.totalTime),
				this.breakTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.breakTime),
				this.withinTime == null ? AttendanceTime.ZERO : new AttendanceTime(this.withinTime),
				new PremiumTimeOfDailyPerformance(ConvertHelper.mapTo(premiumTimes, c -> c.toDomain()), AttendanceAmountDaily.ZERO, AttendanceTime.ZERO));
	}
	
	public static OuenMovementTimeEachTimeSheetDto toDto(OuenMovementTimeEachTimeSheet domain) {
		return new OuenMovementTimeEachTimeSheetDto(
				domain.getTotalMoveTime().valueAsMinutes(),
				domain.getBreakTime().valueAsMinutes(),
				domain.getWithinMoveTime().valueAsMinutes(),
				ConvertHelper.mapTo(domain.getPremiumTime().getPremiumTimes(), c -> PremiumTimeDto.toDto(c)));
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case (MOVE + TIME):
		case (WITHIN_STATUTORY + MOVE + TIME):
		case (BREAK + TIME):
			return PropType.VALUE;
		case (PREMIUM + TIME):
			return PropType.IDX_IN_IDX;
		default:
			return PropType.OBJECT;
		}
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		if(path.equals(PREMIUM + TIME)) {
			return new PremiumTimeDto();
		}
		return null;
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case (MOVE + TIME):
			return Optional.of(ItemValue.builder().value(this.totalTime).valueType(ValueType.TIME));
		case (WITHIN_STATUTORY + MOVE + TIME):
			return Optional.of(ItemValue.builder().value(this.breakTime).valueType(ValueType.TIME));
		case (BREAK + TIME):
			return Optional.of(ItemValue.builder().value(this.withinTime).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case (MOVE + TIME):
			this.totalTime = value.valueOrDefault(0);
			break;
		case (WITHIN_STATUTORY + MOVE + TIME):
			this.breakTime = value.valueOrDefault(0);
			break;
		case (BREAK + TIME):
			this.withinTime = value.valueOrDefault(0);
			break;
		default:
			break;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		if(path.equals(PREMIUM + TIME)) {
			return (List<T>) this.premiumTimes;
		}
		return new ArrayList<>();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		if(path.equals(PREMIUM + TIME)) {
			this.premiumTimes = (List<PremiumTimeDto>) value;
		}
	}
	
	@Override
	public int size(String path) {
		return 10;
	}
}
