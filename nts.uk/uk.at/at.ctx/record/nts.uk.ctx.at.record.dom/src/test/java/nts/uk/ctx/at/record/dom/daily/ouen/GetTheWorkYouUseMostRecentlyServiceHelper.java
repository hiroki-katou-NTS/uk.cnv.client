package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkinputRemarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author chungnt
 *
 */

public class GetTheWorkYouUseMostRecentlyServiceHelper {

	public static List<OuenWorkTimeSheetOfDaily> get() {
		List<OuenWorkTimeSheetOfDaily> result = new ArrayList<>();
		List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet = new ArrayList<>();
		String empId = "empId";
		GeneralDate ymd = GeneralDate.today();

		OuenWorkTimeSheetOfDailyAttendance attendance1 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000000"),
								Optional.of(new WorkCode("000001")),
								Optional.of(new WorkCode("000002")),
								Optional.of(new WorkCode("000003")),
								Optional.of(new WorkCode("000004")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));

		OuenWorkTimeSheetOfDailyAttendance attendance2 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000020"),
								Optional.of(new WorkCode("000021")),
								Optional.of(new WorkCode("000022")),
								Optional.of(new WorkCode("000023")),
								Optional.of(new WorkCode("000024")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance3 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000030"),
								Optional.of(new WorkCode("000031")),
								Optional.of(new WorkCode("000032")),
								Optional.of(new WorkCode("000033")),
								Optional.of(new WorkCode("000034")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance4 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000040"),
								Optional.of(new WorkCode("000041")),
								Optional.of(new WorkCode("000042")),
								Optional.of(new WorkCode("000043")),
								Optional.of(new WorkCode("000044")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance5 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000050"),
								Optional.of(new WorkCode("000051")),
								Optional.of(new WorkCode("000052")),
								Optional.of(new WorkCode("000053")),
								Optional.of(new WorkCode("000054")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));

		OuenWorkTimeSheetOfDailyAttendance attendance6 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000060"),
								Optional.of(new WorkCode("000061")),
								Optional.of(new WorkCode("000062")),
								Optional.of(new WorkCode("000063")),
								Optional.of(new WorkCode("000064")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance7 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000070"),
								Optional.of(new WorkCode("000071")),
								Optional.of(new WorkCode("000072")),
								Optional.of(new WorkCode("000073")),
								Optional.of(new WorkCode("000074")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance8 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000080"),
								Optional.of(new WorkCode("000081")),
								Optional.of(new WorkCode("000082")),
								Optional.of(new WorkCode("000083")),
								Optional.of(new WorkCode("000084")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance9 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000090"),
								Optional.of(new WorkCode("000091")),
								Optional.of(new WorkCode("000092")),
								Optional.of(new WorkCode("000093")),
								Optional.of(new WorkCode("000094")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance10 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000100"),
								Optional.of(new WorkCode("000101")),
								Optional.of(new WorkCode("000102")),
								Optional.of(new WorkCode("000103")),
								Optional.of(new WorkCode("000104")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		OuenWorkTimeSheetOfDailyAttendance attendance11 = OuenWorkTimeSheetOfDailyAttendance.create(
				1,
				WorkContent.create(
						WorkplaceOfWorkEachOuen.create(
								new WorkplaceId("WorkplaceId"),
								new WorkLocationCD("0000")),
						Optional.of(WorkGroup.create(
								new WorkCode("000110"),
								Optional.of(new WorkCode("000111")),
								Optional.of(new WorkCode("000112")),
								Optional.of(new WorkCode("000113")),
								Optional.of(new WorkCode("000114")))),
						Optional.of(new WorkinputRemarks("DUMMY"))),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
						Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080)))));
		
		ouenTimeSheet.add(attendance1);
		ouenTimeSheet.add(attendance2);
		ouenTimeSheet.add(attendance3);
		ouenTimeSheet.add(attendance4);
		ouenTimeSheet.add(attendance5);
		ouenTimeSheet.add(attendance6);
		ouenTimeSheet.add(attendance7);
		ouenTimeSheet.add(attendance8);
		ouenTimeSheet.add(attendance9);
		ouenTimeSheet.add(attendance10);
		ouenTimeSheet.add(attendance11);

		OuenWorkTimeSheetOfDaily daily = new OuenWorkTimeSheetOfDaily(empId, ymd, ouenTimeSheet);

		result.add(daily);
		result.add(daily);
		result.add(daily);
		result.add(daily);
		result.add(daily);
		 
		return result;
	}

}
