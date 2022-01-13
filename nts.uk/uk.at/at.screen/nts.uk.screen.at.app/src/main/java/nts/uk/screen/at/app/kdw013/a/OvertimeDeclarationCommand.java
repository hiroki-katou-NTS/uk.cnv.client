package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;

@AllArgsConstructor
@Getter
public class OvertimeDeclarationCommand {
	/**
	 * 時間外時間
	 * 就業時間帯コード old
	 */
	private Integer overTime;

	/**
	 * 時間外深夜時間
	 */
	private Integer overLateNightTime;

	public OvertimeDeclaration toDomain() {
		return new OvertimeDeclaration(new AttendanceTime(this.getOverTime()),
				new AttendanceTime(this.getOverLateNightTime()));

	}
}
