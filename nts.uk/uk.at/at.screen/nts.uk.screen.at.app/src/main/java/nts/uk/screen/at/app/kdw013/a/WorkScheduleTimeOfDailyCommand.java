package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;

@AllArgsConstructor
@Getter
public class WorkScheduleTimeOfDailyCommand {

	// 勤務予定時間
	private WorkScheduleTimeCommand workScheduleTime;

	// 実績所定労働時間
	private Integer recordPrescribedLaborTime;

	public WorkScheduleTimeOfDaily toDomain() {

		return new WorkScheduleTimeOfDaily(this.getWorkScheduleTime().toDomain(),
				new AttendanceTime(this.getRecordPrescribedLaborTime()));
	}

}
