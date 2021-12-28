package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoTimeItem;

@AllArgsConstructor
@Getter
public class SuppInfoTimeItemCommand {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;

	/** 補足時間 */
	private Integer attTime;

	public SuppInfoTimeItem toDomain() {

		return new SuppInfoTimeItem(new SuppInfoNo(this.getSuppInfoNo()),
				this.getAttTime() == null ? null : new AttendanceTime(this.getAttTime()));
	}

}
