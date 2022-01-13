package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.screen.at.app.kdw006.j.DailyAttendanceItemDto;

/**
 * @author thanhpv
 * @name data for Query 日次の勤怠項目を取得する
 */
@Data
public class ManHourRecordAttendanceItemLinkAttendanceItemsDto {

	//List<日次の勤怠項目>
	public List<DailyAttendanceItemDto> attendanceItems;
	
	//List<工数実績項目と勤怠項目の紐付け>
	public List<ManHourRecordAndAttendanceItemLinkDto> manHourRecordAndAttendanceItemLink;

	public ManHourRecordAttendanceItemLinkAttendanceItemsDto(List<DailyAttendanceItem> attendanceItems,
			List<ManHourRecordAndAttendanceItemLink> manHourRecordAndAttendanceItemLink) {
		super();
		this.attendanceItems = attendanceItems.stream().map(c->new DailyAttendanceItemDto(c)).collect(Collectors.toList());
		this.manHourRecordAndAttendanceItemLink = manHourRecordAndAttendanceItemLink.stream().map(c -> new ManHourRecordAndAttendanceItemLinkDto(c)).collect(Collectors.toList());
	} 
}
