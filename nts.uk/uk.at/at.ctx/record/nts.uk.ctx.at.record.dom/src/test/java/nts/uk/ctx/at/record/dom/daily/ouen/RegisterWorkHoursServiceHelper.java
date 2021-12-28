package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.ctx.at.record.dom.daily.ouen.TimeZone;

/**
 * 
 * @author chungnt
 *
 */

public class RegisterWorkHoursServiceHelper {
	
	private static String empId = "empId";
	private static GeneralDate ymd = GeneralDate.today();
	private static String cid = "cid";

	public static List<WorkDetailsParam> get() {

		List<WorkDetailsParam> result = new ArrayList<>();

		WorkDetailsParam detailsParam = new WorkDetailsParam(
				new SupportFrameNo(1),
				new TimeZone(
						new WorkTimeInformation(new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class),
								Optional.empty()), new TimeWithDayAttr(100)), 
						new WorkTimeInformation(
						new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class), Optional.empty()),
						new TimeWithDayAttr(1000)),
						Optional.empty()),
				Optional.empty(),
				Optional.empty(),
				Optional.of(new WorkLocationCD("Dummy")));

		result.add(detailsParam);
		result.add(detailsParam);
		result.add(detailsParam);
		result.add(detailsParam);

		return result;
	}

	public static List<OuenWorkTimeSheetOfDailyAttendance> getListOuenWorkTime() {

		List<OuenWorkTimeSheetOfDailyAttendance> result = new ArrayList<>();

		OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet = OuenWorkTimeSheetOfDailyAttendance
				.create(new nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo(1),
						WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("workplaceId"),
								new WorkLocationCD("WCD")), Optional.empty(), Optional.empty()),
						TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()), Optional.empty());

		result.add(ouenTimeSheet);
		result.add(ouenTimeSheet);
		result.add(ouenTimeSheet);
		result.add(ouenTimeSheet);

		return result;
	}
	
	public static IntegrationOfDaily getIntegrationOfDailyEmpty() {
		
		IntegrationOfDaily result = new IntegrationOfDaily(
				"employeeId",
				GeneralDate.today(),
				null,
				null,
				null,
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				null,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				Optional.empty());
	
		return result;
	}
	
public static IntegrationOfDaily getIntegrationOfDailyNotEmpty() {
		
		List<EmployeeDailyPerError> employeeError = new ArrayList<>();
		EmployeeDailyPerError error = new EmployeeDailyPerError(cid,
				empId,
				ymd,
				new ErrorAlarmWorkRecordCode("T001"),
				new ArrayList<>());
		
		employeeError.add(error);
		employeeError.add(error);
		employeeError.add(error);
		
		IntegrationOfDaily result = new IntegrationOfDaily(
				"employeeId",
				GeneralDate.today(),
				null,
				null,
				null,
				Optional.empty(),
				employeeError,
				Optional.empty(),
				null,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				Optional.empty());
	
		return result;
	}

}
