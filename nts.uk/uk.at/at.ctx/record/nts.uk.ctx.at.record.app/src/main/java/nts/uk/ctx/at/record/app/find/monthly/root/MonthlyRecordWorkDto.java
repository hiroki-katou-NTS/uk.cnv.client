package nts.uk.ctx.at.record.app.find.monthly.root;

import java.util.List;

import lombok.Data;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.MonthlyItemCommon;
import nts.uk.ctx.at.record.dom.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.record.dom.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.attendance.util.anno.AttendanceItemRoot;

@Data
/** 月別実績（WORK） */
@AttendanceItemRoot(isContainer = true, itemType = AttendanceItemType.MONTHLY_ITEM)
public class MonthlyRecordWorkDto extends MonthlyItemCommon {

	/** 年月: 年月 */
	private YearMonth yearMonth;

	/** 社員ID: 社員ID */
	private String employeeId;

	/** 締めID: 締めID */
	private int closureID;

	/** 締め日: 日付 */
	private ClosureDateDto closureDate;

	/** 月別実績の所属情報: 月別実績の所属情報 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_AFFILIATION_INFO_NAME, layout = MONTHLY_AFFILIATION_INFO_CODE)
	private AffiliationInfoOfMonthlyDto affiliation;

	/** 月別実績の勤怠時間: 月別実績の勤怠時間 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_ATTENDANCE_TIME_NAME, layout = MONTHLY_ATTENDANCE_TIME_CODE)
	private AttendanceTimeOfMonthlyDto attendanceTime;

	/** 月別実績の任意項目 */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_OPTIONAL_ITEM_NAME, layout = MONTHLY_OPTIONAL_ITEM_CODE)
	private AnyItemOfMonthlyDto anyItem;
	
	/** 年休月別残数データ: 年休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, layout = MONTHLY_ANNUAL_LEAVING_REMAIN_CODE)
	private AnnLeaRemNumEachMonthDto annLeave;

	/** 積立年休月別残数データ: 積立年休月別残数データ */
	@AttendanceItemLayout(jpPropertyName = MONTHLY_RESERVE_LEAVING_REMAIN_NAME, layout = MONTHLY_RESERVE_LEAVING_REMAIN_CODE)
	private RsvLeaRemNumEachMonthDto rsvLeave;
	
	@Override
	public String employeeId() {
		return this.employeeId;
	}

	@Override
	public YearMonth yearMonth() {
		return yearMonth;
	}

	public static MonthlyRecordWorkDto builder(){
		return new MonthlyRecordWorkDto();
	}
	
	public MonthlyRecordWorkDto withAffiliation(AffiliationInfoOfMonthlyDto affiliation){
		this.affiliation = affiliation;
		return this;
	}
	
	public MonthlyRecordWorkDto withAttendanceTime(AttendanceTimeOfMonthlyDto attendanceTime){
		this.attendanceTime = attendanceTime;
		return this;
	}
	
	public MonthlyRecordWorkDto withAnyItem(AnyItemOfMonthlyDto anyItem){
		this.anyItem = anyItem;
		return this;
	}
	
	public MonthlyRecordWorkDto withAnnLeave(AnnLeaRemNumEachMonthDto annLeave){
		this.annLeave = annLeave;
		return this;
	}
	
	public MonthlyRecordWorkDto withRsvLeave(RsvLeaRemNumEachMonthDto rsvLeave){
		this.rsvLeave = rsvLeave;
		return this;
	}
	
	public AffiliationInfoOfMonthly toAffiliation(){
		return this.affiliation.toDomain();
	}
	
	public AttendanceTimeOfMonthly toAttendanceTime(){
		return this.attendanceTime.toDomain();
	}
	
	public List<AnyItemOfMonthly> toAnyItems(){
		return this.anyItem.toDomain();
	}
	
	public AnnLeaRemNumEachMonth toAnnLeave(){
		return this.annLeave.toDomain();
	}
	
	public RsvLeaRemNumEachMonth toRsvLeave(){
		return this.rsvLeave.toDomain();
	}
}
