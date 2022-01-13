package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workschedule.WorkScheduleTime;

@AllArgsConstructor
@Getter
public class WorkScheduleTimeCommand {
	
	/** 合計時間: 勤怠時間 */
	private Integer total;
	
	/** 所定外時間: 勤怠時間 */
	private Integer excessOfStatutoryTime; 
	
	/** 所定内時間: 勤怠時間 */
	private Integer withinStatutoryTime;

	public WorkScheduleTime toDomain() {
		
		return new WorkScheduleTime(new AttendanceTime(this.getTotal()),
				new AttendanceTime(this.getExcessOfStatutoryTime()), 
				new AttendanceTime(this.getWithinStatutoryTime()));
	}

}
