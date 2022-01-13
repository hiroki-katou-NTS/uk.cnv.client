package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;

@AllArgsConstructor
@Getter
public class ConstraintTimeCommand {

	/** 深夜拘束時間 : 勤怠時間 */

	private Integer lateNightConstraintTime;

	/** 総拘束時間: 勤怠時間 */
	private Integer totalConstraintTime;

	public ConstraintTime toDomain() {

		return new ConstraintTime(new AttendanceTime(this.getLateNightConstraintTime()),
				new AttendanceTime(this.getTotalConstraintTime()));
	}
}
