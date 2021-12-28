package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname;

import java.util.List;

import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.FrameNoAdapterDto;

@Data
public class AttItemName {

	private int attendanceItemId;
	
	//	・勤怠項目.旧名称 ←「名称」(to ver7)
	private String oldName;
	
	// 	・勤怠項目.名称 ←「表示名称」 (to ver7)
	private String attendanceItemName;
	
	private int attendanceItemDisplayNumber;

	private int userCanUpdateAtr;

	private Integer typeOfAttendanceItem;

	private int nameLineFeedPosition;

	private Integer frameCategory;

	private AttItemAuthority authority;
	
	private Integer frameNo;
	
	private Integer attendanceAtr;
	
	
	private List<FrameNoAdapterDto> dailyAttendanceLinkingList;

	public AttItemName() {
		super();
	}
}
