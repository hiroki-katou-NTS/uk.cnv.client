package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.service.helper;

import java.util.ArrayList;
import java.util.List;

import nts.uk.ctx.at.schedule.dom.adapter.ScTimeImport;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.workscheduletime.PersonFeeTime;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.workscheduletime.WorkScheduleTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

public class Helper {

	public static WorkScheduleTime scTimeImportToWorkScheduleTime(ScTimeImport scTimeImport) {
		List<AttendanceTime> listPersonFeeTime = scTimeImport.getPersonalExpenceTime();
		List<PersonFeeTime> personFeeTime = new ArrayList<>();
		for (int i = 0; i < listPersonFeeTime.size(); i++) {
			personFeeTime.add(new PersonFeeTime(ExtraTimeItemNo.valueOf(i + 1), listPersonFeeTime.get(i)));
		}
		WorkScheduleTime scheduleTime = new WorkScheduleTime(personFeeTime, scTimeImport.getBreakTime(),
				scTimeImport.getActualWorkTime(), scTimeImport.getWeekDayTime(), scTimeImport.getPreTime(),
				scTimeImport.getTotalWorkTime(), scTimeImport.getChildTime(), scTimeImport.getCareTime(),
				scTimeImport.getFlexTime());
		return scheduleTime;
	}
}
