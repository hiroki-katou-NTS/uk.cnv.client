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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoCommentItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoSelectionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppComment;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 作業内容 */
public class SuppInfoStringItemDto implements  ItemConst, AttendanceItemDataGate {
	
	
	/** 補足情報NO: 作業補足情報NO */
	private int no;
	
	/** 補足 */
	private String value;
	
	private boolean code;

	public static SuppInfoStringItemDto from(SuppInfoCommentItem domain) {
		if (domain == null) return null;
		
		return new SuppInfoStringItemDto(domain.getSuppInfoNo().v(), domain.getWorkSuppComment()==null ? null: domain.getWorkSuppComment().v(), false);
	}

	public SuppInfoCommentItem toComment() {
		return new SuppInfoCommentItem(new SuppInfoNo(no), new WorkSuppComment(value));
	}
	
	public static SuppInfoStringItemDto from(SuppInfoSelectionItem domain) {
		if (domain == null) return null;
		
		return new SuppInfoStringItemDto(domain.getSuppInfoSelectionNo().v(),domain.getChoiceCode() ==null ?null: domain.getChoiceCode().v(), true);
	}

	public SuppInfoSelectionItem toSelect() {
		return new SuppInfoSelectionItem(new SuppInfoNo(no), new ChoiceCode(value));
	}
	
	@Override
	public SuppInfoStringItemDto clone() {
		SuppInfoStringItemDto result = new SuppInfoStringItemDto();
		result.setNo(no);
		result.setValue(value);
		return result;
	}
	
	@Override
	public Optional<ItemValue> valueOf(String path) {
		switch (path) {
		case VALUE :
			return Optional.of(ItemValue.builder().value(value).valueType(code ? ValueType.CODE : ValueType.TEXT));
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public void set(String path, ItemValue value) {
		switch (path) {
		case VALUE:
			this.value = value.valueOrDefault("");
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
