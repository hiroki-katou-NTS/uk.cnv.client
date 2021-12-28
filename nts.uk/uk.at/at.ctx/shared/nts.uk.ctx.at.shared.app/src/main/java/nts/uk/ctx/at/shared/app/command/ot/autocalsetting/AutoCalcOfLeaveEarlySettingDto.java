package nts.uk.ctx.at.shared.app.command.ot.autocalsetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;

/**
 * The Class AutoCalcOfLeaveEarlySettingDto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoCalcOfLeaveEarlySettingDto {

	/** The late. */
	private boolean late;

	/** The leave early. */
	private boolean leaveEarly;

	public static AutoCalcOfLeaveEarlySettingDto fromDomain(AutoCalcOfLeaveEarlySetting domain) {
		return new AutoCalcOfLeaveEarlySettingDto(domain.isLate(), domain.isLeaveEarly());
	}
}
