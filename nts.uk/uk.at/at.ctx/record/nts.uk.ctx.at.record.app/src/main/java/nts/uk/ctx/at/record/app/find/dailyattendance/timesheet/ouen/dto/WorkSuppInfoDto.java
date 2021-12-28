/**
 * 
 */
package nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.attendance.util.item.AttendanceItemDataGate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.ItemConst;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppInfo;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 作業内容 */
public class WorkSuppInfoDto implements  ItemConst, AttendanceItemDataGate {
	
	/** 補足時間情報: List<補足情報の時間項目> */
	private List<SuppInfoIntItemDto> time;
	
	/** 補足数値情報: List<補足情報の数値項目> */
	private List<SuppInfoIntItemDto> num;
	
	/** 補足コメント情報: List<補足情報のコメント項目> */
	private List<SuppInfoStringItemDto> comment;
	
	/** 補足選択項目情報: List<補足情報の選択項目> */
	private List<SuppInfoStringItemDto> selection;

	public static WorkSuppInfoDto from(WorkSuppInfo domain) {
		if (domain == null) return null;
		
		return new WorkSuppInfoDto(
				domain.getSuppInfoTimeItems().stream().map(c -> SuppInfoIntItemDto.from(c)).collect(Collectors.toList()), 
				domain.getSuppInfoNumItems().stream().map(c -> SuppInfoIntItemDto.from(c)).collect(Collectors.toList()), 
				domain.getSuppInfoCommentItems().stream().map(c -> SuppInfoStringItemDto.from(c)).collect(Collectors.toList()), 
				domain.getSuppInfoSelectionItems().stream().map(c -> SuppInfoStringItemDto.from(c)).collect(Collectors.toList()));
	}

	public WorkSuppInfo domain() {
		return new WorkSuppInfo(
				time == null ? new ArrayList<>() : time.stream().map(c -> c.toTime()).collect(Collectors.toList()), 
				num == null ? new ArrayList<>() : num.stream().map(c -> c.toNum()).collect(Collectors.toList()), 
				comment == null ? new ArrayList<>() : comment.stream().map(c -> c.toComment()).collect(Collectors.toList()), 
				selection == null ? new ArrayList<>() : selection.stream().map(c -> c.toSelect()).collect(Collectors.toList()));
	}
	
	@Override
	public WorkSuppInfoDto clone() {
		WorkSuppInfoDto result = new WorkSuppInfoDto();
		result.setTime(time == null ? null : time.stream().map(c -> c.clone()).collect(Collectors.toList()));
		result.setNum(num == null ? null : num.stream().map(c -> c.clone()).collect(Collectors.toList()));
		result.setComment(comment == null ? null : comment.stream().map(c -> c.clone()).collect(Collectors.toList()));
		result.setSelection(selection == null ? null : selection.stream().map(c -> c.clone()).collect(Collectors.toList()));
		return result;
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case TIME:
		case NUMBER:
			return new SuppInfoIntItemDto();
		case REMARK:
		case SELECTION:
			return new SuppInfoStringItemDto();
		default:
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AttendanceItemDataGate> void set(String path, List<T> value) {
		switch (path) {
		case TIME:
			time = (List<SuppInfoIntItemDto>) value;
			break;
		case NUMBER:
			num = (List<SuppInfoIntItemDto>) value;
			break;
		case REMARK:
			comment = (List<SuppInfoStringItemDto>) value;
			break;
		case SELECTION:
			selection = (List<SuppInfoStringItemDto>) value;
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AttendanceItemDataGate> List<T> gets(String path) {
		switch (path) {
		case TIME:
			return (List<T>) time;
		case NUMBER:
			return (List<T>) num;
		case REMARK:
			return (List<T>) comment;
		case SELECTION:
			return (List<T>) selection;
		default:
			return AttendanceItemDataGate.super.gets(path);
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case TIME:
		case NUMBER:
		case REMARK:
		case SELECTION:
			return PropType.IDX_IN_IDX;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}

}
