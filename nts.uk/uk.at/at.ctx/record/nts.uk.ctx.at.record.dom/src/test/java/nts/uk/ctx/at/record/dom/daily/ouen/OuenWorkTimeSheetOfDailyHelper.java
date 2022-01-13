/**
 * 
 */
package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author laitv
 *
 */
public class OuenWorkTimeSheetOfDailyHelper {

	public static List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheets = new ArrayList<>();

	public static OuenWorkTimeSheetOfDaily getOuenWorkTimeSheetOfDailyDefault() {
		OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet = OuenWorkTimeSheetOfDailyAttendance
				.create(new SupportFrameNo(1),
						WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("workplaceId"),
								new WorkLocationCD("WCD")), Optional.empty(), Optional.empty()),
						TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()), Optional.empty());
		ouenTimeSheets.add(ouenTimeSheet);
		return OuenWorkTimeSheetOfDaily.create("empId", GeneralDate.today(), ouenTimeSheets);
	}

	public static List<OuenWorkTimeSheetOfDailyAttendance> getListOuenWorkTime() {

		OuenWorkTimeSheetOfDailyAttendance attendance = OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(1),
				WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("DUMMY"), new WorkLocationCD("1")),
						Optional.empty(), Optional.empty()),
				null, Optional.empty());

		ouenTimeSheets.add(attendance);
		ouenTimeSheets.add(attendance);

		return ouenTimeSheets;
	}

	public static List<OuenWorkTimeSheetOfDailyAttendance> getlist() {
		List<OuenWorkTimeSheetOfDailyAttendance> list = new ArrayList<>();
		list.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(1),
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(new WorkplaceId("Dummy"), new WorkLocationCD("Dummy")),
						Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(),
								Optional.empty(), Optional.empty())),
						Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()), Optional.empty()));
		list.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(2),
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(new WorkplaceId("Dummy"), new WorkLocationCD("Dummy")),
						Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(),
								Optional.empty(), Optional.empty())),
						Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(2), Optional.empty(), Optional.empty()), Optional.empty()));
		list.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(3),
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(new WorkplaceId("Dummy"), new WorkLocationCD("Dummy")),
						Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(),
								Optional.empty(), Optional.empty())),
						Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(3), Optional.empty(), Optional.empty()), Optional.empty()));
		
		return list;
	}
	
	public static List<OuenWorkTimeSheetOfDailyAttendance> getlist2() {
		List<OuenWorkTimeSheetOfDailyAttendance> list = new ArrayList<>();
		list.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(2),
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(new WorkplaceId("Dummy"), new WorkLocationCD("Dummy")),
						Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(),
								Optional.empty(), Optional.empty())),
						Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(2), Optional.empty(), Optional.empty()), Optional.empty()));
		return list;
	}
}
