package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author chungnt
 *
 */
public class CreateAttendanceTimeZoneHelper {

	public static OuenWorkTimeSheetOfDailyAttendance get() {
		return OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(1),
				WorkContent.create(WorkplaceOfWorkEachOuen
						.create(new nts.uk.ctx.at.shared.dom.common.WorkplaceId("DUMMY"), new WorkLocationCD("DUMMY")),
					Optional.empty(), Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1),
						Optional.of(new WorkTimeInformation(
								new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class), Optional.empty()),
								new TimeWithDayAttr(100))),
						Optional.of(new WorkTimeInformation(
								new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class), Optional.empty()),
								new TimeWithDayAttr(1000)))), Optional.empty());
	}

}
