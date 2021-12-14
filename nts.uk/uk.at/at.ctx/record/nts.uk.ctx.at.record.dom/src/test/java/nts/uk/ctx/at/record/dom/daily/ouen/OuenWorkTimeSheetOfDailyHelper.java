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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author laitv
 *
 */
public class OuenWorkTimeSheetOfDailyHelper {
	
	public static OuenWorkTimeSheetOfDaily getOuenWorkTimeSheetOfDailyDefault() {
		List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheets = new ArrayList<>();
		OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet = OuenWorkTimeSheetOfDailyAttendance.create(
				1, 
				WorkContent.create( 
						WorkplaceOfWorkEachOuen.create(new WorkplaceId("workplaceId"), new WorkLocationCD("WCD")), 
						Optional.empty(), Optional.empty()), 
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()));
		ouenTimeSheets.add(ouenTimeSheet);
		return OuenWorkTimeSheetOfDaily.create(
				"empId", 
				GeneralDate.today(), 
				ouenTimeSheets);
	}
}
