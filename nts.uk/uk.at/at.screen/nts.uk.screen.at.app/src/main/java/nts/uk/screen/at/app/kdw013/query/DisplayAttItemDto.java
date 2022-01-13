package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayAttItem;

/**
 * @author thanhpv
 *
 */
@Getter
@NoArgsConstructor
public class DisplayAttItemDto {
	
	//項目ID: 勤怠項目ID
	public Integer attendanceItemId;
	
	//表示順
	public Integer order;

	public DisplayAttItemDto(DisplayAttItem domain) {
		super();
		this.attendanceItemId = domain.getAttendanceItemId();
		this.order = domain.getOrder();
	}

}
