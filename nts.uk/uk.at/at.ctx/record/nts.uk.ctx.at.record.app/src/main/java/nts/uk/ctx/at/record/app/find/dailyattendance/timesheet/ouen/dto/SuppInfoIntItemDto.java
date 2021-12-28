/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoTimeItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppNumValue;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 作業内容 */
public class SuppInfoIntItemDto implements  ItemConst, AttendanceItemDataGate {
	
	
	/** 補足情報NO: 作業補足情報NO */
	private int no;
	
	/** 補足 */
	private Integer value;
	
	private boolean time;
	
	public static SuppInfoIntItemDto from(SuppInfoTimeItem domain) {
		if (domain == null) return null;
		
		return new SuppInfoIntItemDto(domain.getSuppInfoNo().v(),domain.getAttTime() == null ? null: domain.getAttTime().valueAsMinutes(), true);
	}

	public SuppInfoTimeItem toTime() {
		return new SuppInfoTimeItem(new SuppInfoNo(no), value == null ? null : new AttendanceTime(value));
	}
	
	public static SuppInfoIntItemDto from(SuppInfoNumItem domain) {
		if (domain == null) return null;
		
		return new SuppInfoIntItemDto(domain.getSuppInfoNo().v(),
				domain.getSuppNumValue() == null ? null : domain.getSuppNumValue().v(), false);
	}

	public SuppInfoNumItem toNum() {
		return new SuppInfoNumItem(new SuppInfoNo(no), new SuppNumValue(value));
	}
	
	@Override
	public SuppInfoIntItemDto clone() {
		SuppInfoIntItemDto result = new SuppInfoIntItemDto();
		result.setNo(no);
		result.setValue(value);
		return result;
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case VALUE :
			return Optional.of(ItemValue.builder().value(value).valueType(time ? ValueType.TIME : ValueType.NUMBER));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case VALUE:
			this.value = value.valueOrDefault(null);
			break;
		default:
			break;
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case VALUE:
			return PropType.VALUE;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}

}
