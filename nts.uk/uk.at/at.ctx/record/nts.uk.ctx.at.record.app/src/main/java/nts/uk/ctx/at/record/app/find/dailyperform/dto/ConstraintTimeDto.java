package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;

/** 総拘束時間 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintTimeDto implements ItemConst, AttendanceItemDataGate {

	/** 総拘束時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = TOTAL)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer totalConstraintTime;

	/** 深夜拘束時間 : 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_B, jpPropertyName = LATE_NIGHT)
	@AttendanceItemValue(type = ValueType.TIME)
	private Integer lateNightConstraintTime;
	
	@Override
	public ConstraintTimeDto clone() {
		return new ConstraintTimeDto(totalConstraintTime, lateNightConstraintTime);
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case TOTAL:
			return Optional.of(ItemValue.builder().value(totalConstraintTime).valueType(ValueType.TIME));
		case LATE_NIGHT:
			return Optional.of(ItemValue.builder().value(lateNightConstraintTime).valueType(ValueType.TIME));
		default:
			return Optional.empty();
		}
	}

	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case TOTAL:
			this.totalConstraintTime = value.valueOrDefault(null);
			break;
		case LATE_NIGHT:
			this.lateNightConstraintTime = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case TOTAL:
		case LATE_NIGHT:
			return PropType.VALUE;
		default:
			return PropType.OBJECT;
		}
	}

	public static ConstraintTimeDto fromDomain(ConstraintTime domain) {

		return new ConstraintTimeDto(domain.getTotalConstraintTime().v(), domain.getLateNightConstraintTime().v());
	}
}
