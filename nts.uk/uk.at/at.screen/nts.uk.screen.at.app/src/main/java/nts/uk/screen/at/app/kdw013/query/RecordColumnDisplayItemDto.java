package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.RecordColumnDisplayItem;

/**
 * @author thanhpv
 *
 */
@Getter
@NoArgsConstructor
public class RecordColumnDisplayItemDto {
	
	//表示順
	public Integer order;
	
	//対象項目: 勤怠項目ID
	public Integer attendanceItemId;
	
	//名称: 実績欄表示名称
	public String displayName;

	public RecordColumnDisplayItemDto(RecordColumnDisplayItem domain) {
		super();
		this.order = domain.getOrder();
		this.attendanceItemId = domain.getAttendanceItemId();
		this.displayName = domain.getDisplayName().v();
	}
}
