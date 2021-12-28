package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalFlexOvertimeSetting;

@AllArgsConstructor
@Getter
public class AutoCalFlexOvertimeSettingCommand {
	/** The flex ot time. */
	private AutoCalSettingCommand flexOtTime;

	public AutoCalFlexOvertimeSetting toDomain() {

		return new AutoCalFlexOvertimeSetting(this.getFlexOtTime().toDomain());
	}
}
