package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceTimeOfDailyAttendanceDto {
	// 勤務予定時間 - 日別実績の勤務予定時間
	private WorkScheduleTimeOfDailyDto workScheduleTimeOfDaily;

	// 実働時間/実績時間 - 日別実績の勤務実績時間 - 勤務時間
	private ActualWorkingTimeOfDailyDto actualWorkingTimeOfDaily;

	// 滞在時間 - 日別実績の滞在時間 change tyle
	private StayingTimeOfDailyDto stayingTime;

	// 不就労時間 - 勤怠時間
	private Integer unEmployedTime;

	// 予実差異時間 - 勤怠時間
	private Integer budgetTimeVariance;

	// 医療時間 - 日別実績の医療時間
	private MedicalCareTimeOfDailyDto medicalCareTime;

	public static AttendanceTimeOfDailyAttendanceDto fromDomain(Optional<AttendanceTimeOfDailyAttendance> domain) {
		return domain.map(x -> new AttendanceTimeOfDailyAttendanceDto(
				WorkScheduleTimeOfDailyDto.fromDomain(x.getWorkScheduleTimeOfDaily()),
				ActualWorkingTimeOfDailyDto.fromDomain(x.getActualWorkingTimeOfDaily()),
				StayingTimeOfDailyDto.fromDomain(x.getStayingTime()), 
				x.getUnEmployedTime().v(), 
				x.getBudgetTimeVariance().v(), 
				MedicalCareTimeOfDailyDto.fromDomain(x.getMedicalCareTime())
				)).orElse(null);
	}
}
