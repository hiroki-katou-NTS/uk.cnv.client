package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
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

//		for (int i = 1; i < 100; i++) {
//			ouenTimeSheet.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(i), WorkContent.create(
//					WorkplaceOfWorkEachOuen.create(new WorkplaceId("WorkplaceId"), new WorkLocationCD("000" + i)),
//					Optional.of(WorkGroup.create(new WorkCode("00000" + i), Optional.of(new WorkCode("00000" + i)),
//							Optional.of(new WorkCode("00000" + i)), Optional.of(new WorkCode("00000" + i)),
//							Optional.of(new WorkCode("00000" + i)))),
//					Optional.of(new WorkinputRemarks("DUMMY"))),
//					TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(i),
//							Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
//							Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080))))));
//		}
//		
//		for (int i = 10; i < 21; i++) {
//			ouenTimeSheet.add(OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(i), WorkContent.create(
//					WorkplaceOfWorkEachOuen.create(new WorkplaceId("WorkplaceId"), new WorkLocationCD("000" + i)),
//					Optional.of(WorkGroup.create(new WorkCode("00000" + i), Optional.of(new WorkCode("00000" + i)),
//							Optional.of(new WorkCode("00000" + i)), Optional.of(new WorkCode("00000" + i)),
//							Optional.of(new WorkCode("00000" + i)))),
//					Optional.of(new WorkinputRemarks("DUMMY"))),
//					TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(i),
//							Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(500))),
//							Optional.of(WorkTimeInformation.createByAutomaticSet(new TimeWithDayAttr(1080))))));
//		}

		OuenWorkTimeSheetOfDaily daily = new OuenWorkTimeSheetOfDaily(empId, ymd, ouenTimeSheet);

		
		for (int i = 1; i < 6; i++) {
			result.add(daily);
		}
		return result;
	}

}
