package nts.uk.ctx.at.record.app.command.monthly;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.command.monthly.absenceleave.AbsenceLeaveRemainMonthlyCommand;
import nts.uk.ctx.at.record.app.command.monthly.affliation.AffiliationInfoOfMonthlyCommand;
import nts.uk.ctx.at.record.app.command.monthly.annualleave.AnnLeaRemNumEachMonthCommand;
import nts.uk.ctx.at.record.app.command.monthly.anyitem.AnyItemOfMonthlyCommand;
import nts.uk.ctx.at.record.app.command.monthly.attendancetime.AttendanceTimeOfMonthlyCommand;
import nts.uk.ctx.at.record.app.command.monthly.care.MonthCareRemainCommand;
import nts.uk.ctx.at.record.app.command.monthly.childcare.MonthChildCareRemainCommand;
import nts.uk.ctx.at.record.app.command.monthly.dayoff.DayOffRemainMonthlyCommand;
import nts.uk.ctx.at.record.app.command.monthly.publicholiday.MonthPublicHolidayRemainCommand;
import nts.uk.ctx.at.record.app.command.monthly.remarks.MonthlyRemarksCommand;
import nts.uk.ctx.at.record.app.command.monthly.reserveleave.RsvLeaRemNumEachMonthCommand;
import nts.uk.ctx.at.record.app.command.monthly.specialholiday.SpecialHolidayRemainMonthlyCommand;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ConvertibleAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

public class MonthlyRecordWorkCommand extends MonthlyWorkCommonCommand {
	
	private final List<ItemValue> itemValues = new ArrayList<>();

	/** 月別実績の所属情報 */
	@Getter
	private final AffiliationInfoOfMonthlyCommand affiliationInfo = new AffiliationInfoOfMonthlyCommand();

	/** 月別実績の勤怠時間 */
	@Getter
	private final AttendanceTimeOfMonthlyCommand attendanceTime = new AttendanceTimeOfMonthlyCommand();

	/** 月別実績の任意項目 */
	@Getter
	private final AnyItemOfMonthlyCommand anyItem = new AnyItemOfMonthlyCommand();

	/** 年休月別残数データ */
	@Getter
	private final AnnLeaRemNumEachMonthCommand annualLeave = new AnnLeaRemNumEachMonthCommand();

	/** 積立年休月別残数データ */
	@Getter
	private final RsvLeaRemNumEachMonthCommand reserveLeave = new RsvLeaRemNumEachMonthCommand();
	
	/** 特別休暇月別残数データ */
	@Getter
	private final SpecialHolidayRemainMonthlyCommand specialHoliday = new SpecialHolidayRemainMonthlyCommand();

	/** 代休月別残数データ */
	@Getter
	private final DayOffRemainMonthlyCommand dayOff = new DayOffRemainMonthlyCommand();

	/** 振休月別残数データ */
	@Getter
	private final AbsenceLeaveRemainMonthlyCommand absence = new AbsenceLeaveRemainMonthlyCommand();

	/** 月別実績の備考 */
	@Getter
	private final MonthlyRemarksCommand remarks = new MonthlyRemarksCommand();
	
	/** 介護休暇月別残数データ */
	@Getter
	private final MonthCareRemainCommand care = new MonthCareRemainCommand();
	
	/** 子の看護月別残数データ */
	@Getter
	private final MonthChildCareRemainCommand childCare = new MonthChildCareRemainCommand();
	
	private final MonthPublicHolidayRemainCommand publicHoliday = new MonthPublicHolidayRemainCommand();

	public MonthlyWorkCommonCommand getCommand(String group){
		MonthlyWorkCommonCommand command = null;
		switch (group) {
		case MONTHLY_AFFILIATION_INFO_NAME:
			command = this.affiliationInfo;
			break;
		case MONTHLY_ATTENDANCE_TIME_NAME:
			command = this.attendanceTime;
			break;
		case MONTHLY_OPTIONAL_ITEM_NAME:
			command = this.anyItem;
			break;
		case MONTHLY_ANNUAL_LEAVING_REMAIN_NAME:
			command = this.annualLeave;
			break;
		case MONTHLY_RESERVE_LEAVING_REMAIN_NAME:
			command = this.reserveLeave;
			break;
		case MONTHLY_ABSENCE_LEAVE_REMAIN_NAME:
			command = this.absence;
			break;
		case MONTHLY_SPECIAL_HOLIDAY_REMAIN_NAME:
			command = this.specialHoliday;
			break;
		case MONTHLY_OFF_REMAIN_NAME:
			command = this.dayOff;
			break;
		case MONTHLY_REMARKS_NAME:
			command = this.remarks;
			break;
		case MONTHLY_CARE_HD_REMAIN_NAME:
			command = this.care;
			break;
		case MONTHLY_CHILD_CARE_HD_REMAIN_NAME:
			command = this.childCare;
			break;
		case MONTHLY_PUBLIC_HOLIDAYREMAIN_NAME:
			command = this.publicHoliday;
			break;
		default:
			break;
		}
		return command;
	}
	
	@Override
	public void setRecords(ConvertibleAttendanceItem item) {
		MonthlyRecordWorkDto fullDto = (MonthlyRecordWorkDto) item;
		this.affiliationInfo.setRecords(fullDto.getAffiliation());
		this.attendanceTime.setRecords(fullDto.getAttendanceTime());
		this.anyItem.setRecords(fullDto.getAnyItem());
		this.annualLeave.setRecords(fullDto.getAnnLeave());
		this.reserveLeave.setRecords(fullDto.getRsvLeave());
		this.specialHoliday.setRecords(fullDto.getSpecialHoliday());
		this.dayOff.setRecords(fullDto.getDayOff());
		this.absence.setRecords(fullDto.getAbsenceLeave());
		this.remarks.setRecords(fullDto.getRemarks());
		this.care.setRecords(fullDto.getCare());
		this.childCare.setRecords(fullDto.getChildCare());
		this.publicHoliday.setRecords(fullDto.getPublicHoliday());
	}

	@Override
	public void forEmployee(String employeId) {
		super.employeeId(employeId);
		this.affiliationInfo.forEmployee(employeId);
		this.attendanceTime.forEmployee(employeId);
		this.anyItem.forEmployee(employeId);
		this.annualLeave.forEmployee(employeId);
		this.reserveLeave.forEmployee(employeId);
		this.specialHoliday.forEmployee(employeId);
		this.dayOff.forEmployee(employeId);
		this.absence.forEmployee(employeId);
		this.remarks.forEmployee(employeId);
		this.care.forEmployee(employeId);
		this.childCare.forEmployee(employeId);
		this.publicHoliday.forEmployee(employeId);
	}
	
	public MonthlyRecordWorkCommand fromItems(List<ItemValue> itemValues){
		this.itemValues.addAll(itemValues);
		return this;
	}
	
	public MonthlyRecordWorkCommand withData(MonthlyRecordWorkDto data){
		this.setRecords(data);
//		if (this.dayOff.getData().getDatePeriod() == null) this.dayOff.getData().setDatePeriod(this.attendanceTime.getData().getDatePeriod());
		return this;
	}
	
	public List<ItemValue> itemValues(){
		return new ArrayList<>(this.itemValues);
	}

	@Override
	public void updateData(Object data) {
	}

	@Override
	public void yearMonth(YearMonth yearMonth) {
		super.yearMonth(yearMonth);
		this.affiliationInfo.yearMonth(yearMonth);
		this.attendanceTime.yearMonth(yearMonth);
		this.anyItem.yearMonth(yearMonth);
		this.annualLeave.yearMonth(yearMonth);
		this.reserveLeave.yearMonth(yearMonth);
		this.specialHoliday.yearMonth(yearMonth);
		this.dayOff.yearMonth(yearMonth);
		this.absence.yearMonth(yearMonth);
		this.remarks.yearMonth(yearMonth);
		this.care.yearMonth(yearMonth);
		this.childCare.yearMonth(yearMonth);
		this.publicHoliday.yearMonth(yearMonth);
	}

	@Override
	public void closureId(int closureId) {
		super.closureId(closureId);
		this.affiliationInfo.closureId(closureId);
		this.attendanceTime.closureId(closureId);
		this.anyItem.closureId(closureId);
		this.annualLeave.closureId(closureId);
		this.reserveLeave.closureId(closureId);
		this.specialHoliday.closureId(closureId);
		this.dayOff.closureId(closureId);
		this.absence.closureId(closureId);
		this.remarks.closureId(closureId);
		this.care.closureId(closureId);
		this.childCare.closureId(closureId);
		this.publicHoliday.closureId(closureId);
	}

	@Override
	public void closureDate(ClosureDateDto closureDate) {
		super.closureDate(closureDate);
		this.affiliationInfo.closureDate(closureDate);
		this.attendanceTime.closureDate(closureDate);
		this.anyItem.closureDate(closureDate);
		this.annualLeave.closureDate(closureDate);
		this.reserveLeave.closureDate(closureDate);
		this.specialHoliday.closureDate(closureDate);
		this.dayOff.closureDate(closureDate);
		this.absence.closureDate(closureDate);
		this.remarks.closureDate(closureDate);
		this.care.closureDate(closureDate);
		this.childCare.closureDate(closureDate);
		this.publicHoliday.closureDate(closureDate);
	}
}
