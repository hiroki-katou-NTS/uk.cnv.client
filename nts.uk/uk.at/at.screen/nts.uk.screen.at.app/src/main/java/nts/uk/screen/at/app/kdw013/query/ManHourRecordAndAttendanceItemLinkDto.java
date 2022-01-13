package nts.uk.screen.at.app.kdw013.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordAndAttendanceItemLink;

/**
 * @author thanhPV
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ManHourRecordAndAttendanceItemLinkDto{

	/** 応援勤務枠No*/
	private Integer frameNo;
	
	/** 工数実績項目ID*/
	private Integer itemId;
	
	/** 勤怠項目ID*/
	private Integer attendanceItemId;

	public ManHourRecordAndAttendanceItemLinkDto(ManHourRecordAndAttendanceItemLink domain) {
		super();
		this.frameNo = domain.getFrameNo().v();
		this.itemId = domain.getItemId();
		this.attendanceItemId = domain.getAttendanceItemId();
	}
	
}
