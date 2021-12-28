package nts.uk.screen.at.app.kdw006.j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyAttendanceItemDto {

	/* 会社ID */
	private String companyId;

	/* 勤怠項目ID */
	private int attendanceItemId;

	/* 勤怠項目名称 */
	private String attendanceName;

	/* 表示番号 */
	private int displayNumber;

	/* ユーザーが値を変更できる */
	private int userCanUpdateAtr;

	/* 勤怠項目属性 */
	private int dailyAttendanceAtr;

	/* 名称の改行位置 */
	private int nameLineFeedPosition;

	/* マスタの種類 */
	private Integer masterType;

	/* 怠項目のPrimitiveValue */
	private Integer primitiveValue;

	/* 表示名称 */
	private String displayName;

	public DailyAttendanceItemDto(DailyAttendanceItem dailyAttendanceItem) {
		super();
		this.companyId = dailyAttendanceItem.getCompanyId();
		this.attendanceItemId = dailyAttendanceItem.getAttendanceItemId();
		this.attendanceName = dailyAttendanceItem.getAttendanceName().v();
		this.displayNumber = dailyAttendanceItem.getDisplayNumber();
		this.userCanUpdateAtr = dailyAttendanceItem.getUserCanUpdateAtr().value;
		this.dailyAttendanceAtr = dailyAttendanceItem.getDailyAttendanceAtr().value;
		this.nameLineFeedPosition = dailyAttendanceItem.getNameLineFeedPosition();
		this.masterType = dailyAttendanceItem.getMasterType().map(c->c.value).orElse(null);
		this.primitiveValue = dailyAttendanceItem.getPrimitiveValue().map(c->c.value).orElse(null);
		this.displayName = dailyAttendanceItem.getDisplayName().map(c->c.v()).orElse(null);
	}

	public void changeName (String displayName, String attendanceName) {
		this.displayName = displayName;
		this.attendanceName = attendanceName;
	}

}
