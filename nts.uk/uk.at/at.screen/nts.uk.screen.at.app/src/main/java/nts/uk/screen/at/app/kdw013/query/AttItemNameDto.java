package nts.uk.screen.at.app.kdw013.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;

/**
 * @author thanhpv
 *
 */
@NoArgsConstructor
@Getter
public class AttItemNameDto {

	public Integer attendanceItemId;
	
	//	・勤怠項目.旧名称 ←「名称」(to ver7)
	public String oldName;
	
	// 	・勤怠項目.名称 ←「表示名称」 (to ver7)
	public String attendanceItemName;
	
	public Integer attendanceItemDisplayNumber;

	public Integer userCanUpdateAtr;

	public Integer typeOfAttendanceItem;

	public Integer nameLineFeedPosition;

	public Integer frameCategory;

	public AttItemAuthorityDto authority;
	
	public Integer frameNo;
	
	public Integer attendanceAtr;
	
	public List<FrameNoAdapter1Dto> dailyAttendanceLinkingList;

	public AttItemNameDto(AttItemName domain) {
		super();
		this.attendanceItemId = domain.getAttendanceItemId();
		this.oldName = domain.getOldName();
		this.attendanceItemName = domain.getAttendanceItemName();
		this.attendanceItemDisplayNumber = domain.getAttendanceItemDisplayNumber();
		this.userCanUpdateAtr = domain.getUserCanUpdateAtr();
		this.typeOfAttendanceItem = domain.getTypeOfAttendanceItem();
		this.nameLineFeedPosition = domain.getNameLineFeedPosition();
		this.frameCategory = domain.getFrameCategory();
		this.authority = domain.getAuthority() == null ? new AttItemAuthorityDto(): new AttItemAuthorityDto(domain.getAuthority());
		this.frameNo = domain.getFrameNo();
		this.attendanceAtr = domain.getAttendanceAtr();
		this.dailyAttendanceLinkingList = domain.getDailyAttendanceLinkingList() == null? new ArrayList<FrameNoAdapter1Dto>() : domain.getDailyAttendanceLinkingList().stream().map(c -> new FrameNoAdapter1Dto(c)).collect(Collectors.toList());
	}

}
