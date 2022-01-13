package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.PCLogOnInfoOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.remarks.RemarksOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.snapshot.SnapShot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
//move at record -> at shared
public interface DailyRecordToAttendanceItemConverter extends AttendanceItemConverter {

	
	Optional<ItemValue> convert(int attendanceItemId);

	List<ItemValue> convert(Collection<Integer> attendanceItemIds);
	
	void merge(ItemValue value);
	
	void merge(Collection<ItemValue> values);
		
	IntegrationOfDaily toDomain();
	
	DailyRecordToAttendanceItemConverter setData(IntegrationOfDaily domain);

	DailyRecordToAttendanceItemConverter withWorkInfo(WorkInfoOfDailyAttendance domain);

	DailyRecordToAttendanceItemConverter withCalcAttr(CalAttrOfDailyAttd domain);

//	DailyRecordToAttendanceItemConverter withBusinessType(WorkTypeOfDailyPerformance domain);
	
	DailyRecordToAttendanceItemConverter withAffiliationInfo(AffiliationInforOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withEmployeeErrors(List<EmployeeDailyPerError> domain);

	DailyRecordToAttendanceItemConverter withOutingTime(OutingTimeOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withBreakTime(BreakTimeOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withAttendanceTime(AttendanceTimeOfDailyAttendance domain);
//	DailyRecordToAttendanceItemConverter withAttendanceTimeByWork(AttendanceTimeByWorkOfDaily domain);

	DailyRecordToAttendanceItemConverter withTimeLeaving(TimeLeavingOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withShortTime(ShortTimeOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withSpecificDateAttr(SpecificDateAttrOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withAttendanceLeavingGate(AttendanceLeavingGateOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withAnyItems(AnyItemValueOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withEditStates(EditStateOfDailyAttd domain);

	DailyRecordToAttendanceItemConverter withEditStates(List<EditStateOfDailyAttd> domain);

	DailyRecordToAttendanceItemConverter withTemporaryTime(TemporaryTimeOfDailyAttd domain);
	
	DailyRecordToAttendanceItemConverter withPCLogInfo(PCLogOnInfoOfDailyAttd domain);
	
	DailyRecordToAttendanceItemConverter withRemarks(List<RemarksOfDailyAttd> domain);
	
	DailyRecordToAttendanceItemConverter withOuenWorkTime(List<OuenWorkTimeOfDailyAttendance> domain);
	
	DailyRecordToAttendanceItemConverter withSnapshot(SnapShot domain);
	
	DailyRecordToAttendanceItemConverter withOuenSheet(List<OuenWorkTimeSheetOfDailyAttendance> ouenSheet);

	DailyRecordToAttendanceItemConverter employeeId(String employeeId);

	DailyRecordToAttendanceItemConverter workingDate(GeneralDate workingDate);

	DailyRecordToAttendanceItemConverter completed();
	
	WorkInfoOfDailyAttendance workInfo();

	CalAttrOfDailyAttd calcAttr();

//	Optional<WorkTypeOfDailyPerformance> businessType();
	
	AffiliationInforOfDailyAttd affiliationInfo();

	Optional<OutingTimeOfDailyAttd> outingTime();

	BreakTimeOfDailyAttd breakTime();

	Optional<AttendanceTimeOfDailyAttendance> attendanceTime();

//	Optional<AttendanceTimeByWorkOfDaily> attendanceTimeByWork();

	Optional<TimeLeavingOfDailyAttd> timeLeaving();

	Optional<ShortTimeOfDailyAttd> shortTime();

	Optional<SpecificDateAttrOfDailyAttd> specificDateAttr();

	Optional<AttendanceLeavingGateOfDailyAttd> attendanceLeavingGate();

	Optional<AnyItemValueOfDailyAttd> anyItems();

	List<EditStateOfDailyAttd> editStates();

	Optional<TemporaryTimeOfDailyAttd> temporaryTime();
	
	Optional<PCLogOnInfoOfDailyAttd> pcLogInfo();
	
	List<RemarksOfDailyAttd> remarks();
	
	List<OuenWorkTimeOfDailyAttendance> ouenWorkTimes();
	
	Optional<SnapShot> snapshot();

	List<OuenWorkTimeSheetOfDailyAttendance> ouenSheet();

}