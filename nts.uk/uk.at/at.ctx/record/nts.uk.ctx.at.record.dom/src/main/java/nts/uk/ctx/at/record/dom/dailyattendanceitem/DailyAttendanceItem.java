//package nts.uk.ctx.at.record.dom.dailyattendanceitem;
//
//import lombok.Getter;
//import nts.arc.enums.EnumAdaptor;
//import nts.arc.layer.dom.AggregateRoot;
//import nts.uk.ctx.at.record.dom.dailyattendanceitem.enums.DailyAttendanceAtr;
//import nts.uk.ctx.at.record.dom.dailyattendanceitem.enums.UseSetting;
//import nts.uk.ctx.at.record.dom.dailyattendanceitem.primitivevalue.AttendanceName;
//
///**
// * 日次の勤怠項目
// * @author nampt
// *
// */
//@Getter
//public class DailyAttendanceItem extends AggregateRoot {
//
//	private String companyId;
//
//	/* 勤怠項目ID */
//	private int attendanceItemId;
//
//	/* 勤怠項目名称 */
//	private AttendanceName attendanceName;
//
//	/* 表示番号 */
//	private int displayNumber;
//
//	/* 使用区分 */
//	private UseSetting userCanUpdateAtr;
//
//	/* 勤怠項目属性 */
//	private DailyAttendanceAtr dailyAttendanceAtr;
//
//	/* ユーザーが値を変更できる */
//	private int nameLineFeedPosition;
//
//	public DailyAttendanceItem(String companyId, int attendanceItemId, AttendanceName attendanceName, int displayNumber,
//			UseSetting userCanUpdateAtr, DailyAttendanceAtr dailyAttendanceAtr, int nameLineFeedPosition) {
//		super();
//		this.companyId = companyId;
//		this.attendanceItemId = attendanceItemId;
//		this.attendanceName = attendanceName;
//		this.displayNumber = displayNumber;
//		this.userCanUpdateAtr = userCanUpdateAtr;
//		this.dailyAttendanceAtr = dailyAttendanceAtr;
//		this.nameLineFeedPosition = nameLineFeedPosition;
//	}
//
//	public static DailyAttendanceItem createFromJavaType(String companyId, int attendanceItemId, String attendanceName,
//			int displayNumber, int userCanUpdateAtr, int dailyAttendanceAtr, int nameLineFeedPosition) {
//		return new DailyAttendanceItem(companyId, attendanceItemId, new AttendanceName(attendanceName), displayNumber,
//				EnumAdaptor.valueOf(userCanUpdateAtr, UseSetting.class),
//				EnumAdaptor.valueOf(dailyAttendanceAtr, DailyAttendanceAtr.class), nameLineFeedPosition);
//	}
//
//}
