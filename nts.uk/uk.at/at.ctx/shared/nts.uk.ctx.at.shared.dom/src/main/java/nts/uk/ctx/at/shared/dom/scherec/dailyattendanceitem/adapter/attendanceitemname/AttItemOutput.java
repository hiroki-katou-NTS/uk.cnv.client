package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname;

import lombok.Data;

@Data
public class AttItemOutput {
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
    
    private Integer optionalItemAtr;

    public AttItemOutput() {
        super();
    }
    
    public AttItemOutput(AttItemName itemName, Integer optionalItemAtr) {
        this.attendanceItemId = itemName.getAttendanceItemId();
        this.oldName = itemName.getOldName();
        this.attendanceItemName = itemName.getAttendanceItemName();
        this.attendanceItemDisplayNumber = itemName.getAttendanceItemDisplayNumber();
        this.userCanUpdateAtr = itemName.getUserCanUpdateAtr();
        this.typeOfAttendanceItem = itemName.getTypeOfAttendanceItem();
        this.nameLineFeedPosition = itemName.getNameLineFeedPosition();
        this.frameCategory = itemName.getFrameCategory();
        this.authority = itemName.getAuthority();
        this.optionalItemAtr = optionalItemAtr;
    }
}
