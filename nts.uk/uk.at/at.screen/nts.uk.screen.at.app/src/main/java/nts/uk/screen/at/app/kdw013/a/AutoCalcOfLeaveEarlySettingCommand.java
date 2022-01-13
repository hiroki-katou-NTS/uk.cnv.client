package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;

@AllArgsConstructor
@Getter
public class AutoCalcOfLeaveEarlySettingCommand {
	/** The late. */
	private boolean late;

	/** The leave early. */
	private boolean leaveEarly;

	public AutoCalcOfLeaveEarlySetting toDomain() {
		return new AutoCalcOfLeaveEarlySetting(this.isLate(), this.isLeaveEarly());
	}
}
