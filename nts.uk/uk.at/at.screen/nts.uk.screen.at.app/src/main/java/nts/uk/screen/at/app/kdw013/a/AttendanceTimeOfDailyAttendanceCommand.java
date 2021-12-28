package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;

@AllArgsConstructor
@Getter
public class AttendanceTimeOfDailyAttendanceCommand {
	// 勤務予定時間 - 日別実績の勤務予定時間
		private WorkScheduleTimeOfDailyCommand workScheduleTimeOfDaily;

		// 実働時間/実績時間 - 日別実績の勤務実績時間 - 勤務時間
		private ActualWorkingTimeOfDailyCommand actualWorkingTimeOfDaily;

		// 滞在時間 - 日別実績の滞在時間 change tyle
		private StayingTimeOfDailyCommand stayingTime;

		// 不就労時間 - 勤怠時間
		private Integer unEmployedTime;

		// 予実差異時間 - 勤怠時間
		private Integer budgetTimeVariance;

		// 医療時間 - 日別実績の医療時間
		private MedicalCareTimeOfDailyCommand medicalCareTime;

		public AttendanceTimeOfDailyAttendance toDomain() {
			return new AttendanceTimeOfDailyAttendance(
					this.getWorkScheduleTimeOfDaily().toDomain(),
					this.getActualWorkingTimeOfDaily().toDomain(),
					this.getStayingTime().toDomain(), 
					new AttendanceTimeOfExistMinus(this.getUnEmployedTime()) , 
					new AttendanceTimeOfExistMinus(this.getBudgetTimeVariance()), 
					this.getMedicalCareTime().toDomain());
		}
}
