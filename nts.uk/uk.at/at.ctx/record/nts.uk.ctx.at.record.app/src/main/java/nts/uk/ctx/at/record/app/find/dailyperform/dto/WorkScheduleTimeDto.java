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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTime;

/** 勤務予定時間 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleTimeDto implements ItemConst, AttendanceItemDataGate {

	/** 合計時間: 勤怠時間 */
	@AttendanceItemLayout(layout = LAYOUT_A, jpPropertyName = PLAN)
	@AttendanceItemValue(type = ValueType.TIME)
	private int total;
	
	/** 所定外時間: 勤怠時間 */
	private int excessOfStatutoryTime; 
	
	/** 所定内時間: 勤怠時間 */
	private int withinStatutoryTime;
	
	@Override
	public WorkScheduleTimeDto clone() {
		return new WorkScheduleTimeDto(total, excessOfStatutoryTime, withinStatutoryTime);
	}

	@Override
	public Optional<ItemValue> valueOf(String path) {
		if (PLAN.equals(path)) {
			return Optional.of(ItemValue.builder().value(total).valueType(ValueType.TIME));
		}
		return AttendanceItemDataGate.super.valueOf(path);
	}
	
	@Override
	public void set(String path, ItemValue value) {
		if (PLAN.equals(path)) {
			total = value.valueOrDefault(0);
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		if (PLAN.equals(path)) {
			return PropType.VALUE;
		}
		return PropType.OBJECT;
	}

	public static WorkScheduleTimeDto fromDomain(WorkScheduleTime domain) {
		return new WorkScheduleTimeDto(domain.getTotal().v(), domain.getExcessOfStatutoryTime().v(),
				domain.getWithinStatutoryTime().v());
	}
}
