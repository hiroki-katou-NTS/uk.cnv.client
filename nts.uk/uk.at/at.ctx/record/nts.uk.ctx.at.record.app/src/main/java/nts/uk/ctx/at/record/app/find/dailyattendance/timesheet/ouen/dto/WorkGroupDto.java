/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * @author laitv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 作業グループ */
public class WorkGroupDto  implements ItemConst, AttendanceItemDataGate{
	
	/** 作業CD1 */
	@AttendanceItemLayout(layout = LAYOUT_G, jpPropertyName = WORKCODE + LAYOUT_A)
	@AttendanceItemValue()
	private String workCD1;
	
	/** 作業CD2 */
	@AttendanceItemLayout(layout = LAYOUT_H, jpPropertyName = WORKCODE + LAYOUT_B)
	@AttendanceItemValue()
	private String workCD2;
	
	/** 作業CD3 */
	@AttendanceItemLayout(layout = LAYOUT_I, jpPropertyName = WORKCODE + LAYOUT_C)
	@AttendanceItemValue()
	private String workCD3;
	
	/** 作業CD4 */
	@AttendanceItemLayout(layout = LAYOUT_J, jpPropertyName = WORKCODE + LAYOUT_D)
	@AttendanceItemValue()
	private String workCD4;
	
	/** 作業CD5 */
	@AttendanceItemLayout(layout = LAYOUT_K, jpPropertyName = WORKCODE + LAYOUT_E)
	@AttendanceItemValue()
	private String workCD5;
	
	public static WorkGroupDto from(WorkGroup domain) {
		if (domain == null) return null;
		
		if (domain.getWorkCD1() == null 
				&& !domain.getWorkCD2().isPresent() && !domain.getWorkCD3().isPresent()
				&& !domain.getWorkCD4().isPresent() && !domain.getWorkCD5().isPresent())
			return null;
		
		return new WorkGroupDto(domain.getWorkCD1() == null ? "" : domain.getWorkCD1().v(), 
								domain.getWorkCD2().map(c -> c.v()).orElse(null), 
								domain.getWorkCD3().map(c -> c.v()).orElse(null), 
								domain.getWorkCD4().map(c -> c.v()).orElse(null), 
								domain.getWorkCD5().map(c -> c.v()).orElse(null));
	}

	public WorkGroup domain() {
		return WorkGroup.create(workCD1, workCD2, workCD3, workCD4, workCD5);
	}

	@Override
	protected WorkGroupDto clone() {
		return new WorkGroupDto(workCD1, workCD2, workCD3, workCD4, workCD5);
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case WORKCODE + LAYOUT_A:
			return Optional.of(ItemValue.builder().value(workCD1).valueType(ValueType.CODE));
		case WORKCODE + LAYOUT_B:
			return Optional.of(ItemValue.builder().value(workCD2).valueType(ValueType.CODE));
		case WORKCODE + LAYOUT_C:
			return Optional.of(ItemValue.builder().value(workCD3).valueType(ValueType.CODE));
		case WORKCODE + LAYOUT_D:
			return Optional.of(ItemValue.builder().value(workCD4).valueType(ValueType.CODE));
		case WORKCODE + LAYOUT_E:
			return Optional.of(ItemValue.builder().value(workCD5).valueType(ValueType.CODE));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case WORKCODE + LAYOUT_A:
			this.workCD1 = value.valueOrDefault(null);
			break;
		case WORKCODE + LAYOUT_B:
			this.workCD2 = value.valueOrDefault(null);
			break;
		case WORKCODE + LAYOUT_C:
			this.workCD3 = value.valueOrDefault(null);
			break;
		case WORKCODE + LAYOUT_D:
			this.workCD4 = value.valueOrDefault(null);
			break;
		case WORKCODE + LAYOUT_E:
			this.workCD5 = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case WORKCODE + LAYOUT_A:
		case WORKCODE + LAYOUT_B:
		case WORKCODE + LAYOUT_C:
		case WORKCODE + LAYOUT_D:
		case WORKCODE + LAYOUT_E:
			return PropType.VALUE;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}
	
}
