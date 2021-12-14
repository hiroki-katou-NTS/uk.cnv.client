package nts.uk.ctx.at.record.app.command.monthly;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.command.monthly.absenceleave.AbsenceLeaveRemainMonthlyCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.affliation.AffiliationInfoOfMonthlyCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.annualleave.AnnLeaRemNumEachMonthCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.anyitem.AnyItemOfMonthlyCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.attendancetime.AttendanceTimeOfMonthlyCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.care.MonthCareRemainCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.childcare.MonthChildCareRemainCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.dayoff.DayOffRemainMonthlyCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.remarks.MonthlyRemarksCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.reserveleave.RsvLeaRemNumEachMonthCommandHandler;
import nts.uk.ctx.at.record.app.command.monthly.specialholiday.SpecialHolidayRemainMonthlyCommandHandler;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.RecordHandler;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.anno.AttendanceItemLayout;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

@Stateless
public class MonthlyRecordWorkCommandHandler extends RecordHandler {

	/** 月別実績の所属情報： 月別実績の所属情報 */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_AFFILIATION_INFO_CODE, 
		jpPropertyName = MONTHLY_AFFILIATION_INFO_NAME, index = 1)
	private AffiliationInfoOfMonthlyCommandHandler affiliationHandler;

	/** 月別実績の勤怠時間： 月別実績の勤怠時間 */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_ATTENDANCE_TIME_CODE, 
		jpPropertyName = MONTHLY_ATTENDANCE_TIME_NAME, index = 2)
	private AttendanceTimeOfMonthlyCommandHandler attendanceTimeHandler;
	
	/** 月別実績の任意項目 */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_OPTIONAL_ITEM_CODE, 
		jpPropertyName = MONTHLY_OPTIONAL_ITEM_NAME, index = 3)
	private AnyItemOfMonthlyCommandHandler anyItem;

	/** 年休月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_ANNUAL_LEAVING_REMAIN_CODE, 
		jpPropertyName = MONTHLY_ANNUAL_LEAVING_REMAIN_NAME, index = 4)
	private AnnLeaRemNumEachMonthCommandHandler annualLeave;

	/** 積立年休月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_RESERVE_LEAVING_REMAIN_CODE, 
		jpPropertyName = MONTHLY_RESERVE_LEAVING_REMAIN_NAME, index = 5)
	private RsvLeaRemNumEachMonthCommandHandler reserveLeave;
	
	/** 特別休暇月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_SPECIAL_HOLIDAY_REMAIN_CODE, 
		jpPropertyName = MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME, index = 6)
	private SpecialHolidayRemainMonthlyCommandHandler specialHoliday;

	/** 代休月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_OFF_REMAIN_CODE, 
		jpPropertyName = MONTHLY_OFF_REMAIN_NAME, index = 7)
	private DayOffRemainMonthlyCommandHandler dayOff;

	/** 振休月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_ABSENCE_LEAVE_REMAIN_CODE, 
		jpPropertyName = MONTHLY_ABSENCE_LEAVE_REMAIN_NAME, index = 8)
	private AbsenceLeaveRemainMonthlyCommandHandler absenceLeave;

	/** 月別実績の備考 */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_REMARKS_CODE, 
		jpPropertyName = MONTHLY_REMARKS_NAME, index = 9)
	private MonthlyRemarksCommandHandler remarks;

	/** 介護休暇月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_CARE_HD_REMAIN_CODE, 
		jpPropertyName = MONTHLY_CARE_HD_REMAIN_NAME, index = 10)
	private MonthCareRemainCommandHandler care;

	/** 子の看護月別残数データ */
	@Inject
	@AttendanceItemLayout(layout = MONTHLY_CHILD_CARE_HD_REMAIN_CODE, 
		jpPropertyName = MONTHLY_CHILD_CARE_HD_REMAIN_NAME, index = 11)
	private MonthChildCareRemainCommandHandler childCare;

	public void handleAdd(MonthlyRecordWorkCommand command) {
		handler(command, false);
	}
	
	public void handleUpdate(MonthlyRecordWorkCommand command) {
		handler(command, true);
	}
	
	public void handleAdd(List<MonthlyRecordWorkCommand> commands) {
		commands.stream().forEach(c -> handleAdd(c));
	}

	public void handleUpdate(List<MonthlyRecordWorkCommand> commands) {
		commands.stream().forEach(c -> handleUpdate(c));
	}

	@SuppressWarnings({ "unchecked" })
	private <T extends MonthlyWorkCommonCommand> void handler(MonthlyRecordWorkCommand command, boolean isUpdate) {
		Set<String> mapped = command.itemValues().stream().map(c -> getGroup(c))
				.distinct().collect(Collectors.toSet());
		mapped.stream().forEach(c -> {
			CommandFacade<T> handler = (CommandFacade<T>) getHandler(c, isUpdate);
			if(handler != null){
				handler.handle((T) command.getCommand(c));
			}
		});
	}
	
	private CommandFacade<?> getHandler(String group, boolean isUpdate) {
		CommandFacade<?> handler = null;
		switch (group) {
		case MONTHLY_AFFILIATION_INFO_NAME:
			handler = this.affiliationHandler;
			break;
		case MONTHLY_ATTENDANCE_TIME_NAME:
			handler = this.attendanceTimeHandler;
			break;
		case MONTHLY_OPTIONAL_ITEM_NAME:
			handler = this.anyItem;
			break;
		case MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			handler = this.annualLeave;
			break;
		case MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			handler = this.reserveLeave;
			break;
		case MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			handler = this.absenceLeave;
			break;
		case MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			handler = this.specialHoliday;
			break;
		case MONTHLY_OFF_REMAIN_NAME:
			handler = this.dayOff;
			break;
		case MONTHLY_REMARKS_NAME:
			handler = this.remarks;
			break;
		case MONTHLY_CARE_HD_REMAIN_NAME:
			handler = this.care;
			break;
		case MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			handler = this.childCare;
			break;
		default:
			break;
		}
		return handler;
	}
}
