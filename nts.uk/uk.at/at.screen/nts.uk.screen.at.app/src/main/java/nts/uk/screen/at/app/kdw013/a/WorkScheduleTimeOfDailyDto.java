package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.WorkScheduleTimeDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTimeOfDaily;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkScheduleTimeOfDailyDto {
	// 勤務予定時間
	private WorkScheduleTimeDto workScheduleTime;

	// 実績所定労働時間
	private Integer recordPrescribedLaborTime;

	public static WorkScheduleTimeOfDailyDto fromDomain(WorkScheduleTimeOfDaily domain) {

		return new WorkScheduleTimeOfDailyDto(WorkScheduleTimeDto.fromDomain(domain.getWorkScheduleTime()),
				domain.getRecordPrescribedLaborTime().v());
	}
}
