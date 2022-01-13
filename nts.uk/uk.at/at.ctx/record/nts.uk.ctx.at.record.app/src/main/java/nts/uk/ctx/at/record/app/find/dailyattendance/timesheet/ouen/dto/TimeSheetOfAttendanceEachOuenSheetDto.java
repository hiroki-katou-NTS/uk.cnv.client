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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author laitv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/** 時間帯別勤怠の時間帯 */
public class TimeSheetOfAttendanceEachOuenSheetDto implements ItemConst, AttendanceItemDataGate {
	

	/** 勤務枠No: 勤務NO */
	private int no;
	
	/** 開始: 勤務時刻情報 */
	@AttendanceItemLayout(layout = LAYOUT_L, jpPropertyName = START)
	private WorkTimeInformationDto start;
	
	/** 終了: 勤務時刻情報 */
	@AttendanceItemLayout(layout = LAYOUT_M, jpPropertyName = END)
	private WorkTimeInformationDto end;
	
	public TimeSheetOfAttendanceEachOuenSheet domain( ) {
		
		return TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), 
				Optional.ofNullable(start == null ? null : start.domain()), 
				Optional.ofNullable(end == null ? null : end.domain()));
	}
	
	@Override
	public TimeSheetOfAttendanceEachOuenSheetDto clone() {
		TimeSheetOfAttendanceEachOuenSheetDto result = new TimeSheetOfAttendanceEachOuenSheetDto();
		result.setNo(1);
		result.setStart(start == null ? null : start.clone());
		result.setEnd(end == null ? null : end.clone());
		return result;
	}
	
	@Override
	public AttendanceItemDataGate newInstanceOf(String path) {
		switch (path) {
		case START:
		case END:
			return new WorkTimeInformationDto();
		default:
			return null;
		}
	}
	
	@Override
	public void set(String path, AttendanceItemDataGate value) {
		switch (path) {
		case START:
			start = (WorkTimeInformationDto) value;
			break;
		case END:
			end = (WorkTimeInformationDto) value;
			break;
		default:
			break;
		}
	}
	
	@Override
	public Optional<AttendanceItemDataGate> get(String path) {
		switch (path) {
		case START:
			return Optional.ofNullable(start);
		case END:
			return Optional.ofNullable(end);
		default:
			return Optional.empty();
		}
	}
	
	@Override
	public PropType typeOf(String path) {
		switch (path) {
		case START:
		case END:
			return PropType.OBJECT;
		default:
			break;
		}
		return AttendanceItemDataGate.super.typeOf(path);
	}
}
