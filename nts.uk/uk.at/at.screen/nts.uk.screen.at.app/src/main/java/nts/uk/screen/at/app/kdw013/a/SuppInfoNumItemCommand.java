package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppNumValue;

@AllArgsConstructor
@Getter
public class SuppInfoNumItemCommand {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;

	/** 補足数値: 作業補足数値 */
	private Integer suppNumValue;

	public SuppInfoNumItem toDomain() {
		return new SuppInfoNumItem(
									new SuppInfoNo(this.getSuppInfoNo()),
									this.getSuppNumValue() == null ? null : new SuppNumValue(this.getSuppNumValue())
											);
	}
}
