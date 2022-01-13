package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author chungnt
 *
 */

public class CreateAttendanceTimeZoneForEachSupportWorkHelper {

	private static GeneralDate ymd = GeneralDate.today();

	public static Task getTask() {
		return new Task(new TaskCode("Code"), new TaskFrameNo(1), null, new ArrayList<>(), new DatePeriod(ymd, ymd),
				null);
	}

	public static List<WorkDetailsParam> getWorkDetailsParams() {
		List<WorkDetailsParam> result = new ArrayList<>();

		for (int i = 1; i < 4; i++) {
			result.add(new WorkDetailsParam(new SupportFrameNo(i),
					new TimeZone(
							new WorkTimeInformation(new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class),
									Optional.empty()), new TimeWithDayAttr(i * 100)),
							new WorkTimeInformation(new ReasonTimeChange(EnumAdaptor.valueOf(i, TimeChangeMeans.class),
									Optional.empty()), new TimeWithDayAttr(i * 1000)),
							Optional.empty()),
					Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(),
							Optional.empty(), Optional.empty())),
					Optional.empty(), Optional.empty()));
		}

		return result;
	}

}
