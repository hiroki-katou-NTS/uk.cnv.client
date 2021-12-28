package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;

@AllArgsConstructor
@Getter
public class AutoCalOvertimeSettingCommand {
	/** The early ot time. */
	private AutoCalSettingCommand earlyOtTime;

	/** The early mid ot time. */
	private AutoCalSettingCommand earlyMidOtTime;

	/** The normal ot time. */
	private AutoCalSettingCommand normalOtTime;

	/** The normal mid ot time. */
	private AutoCalSettingCommand normalMidOtTime;

	/** The legal ot time. */
	private AutoCalSettingCommand legalOtTime;

	/** The legal mid ot time. */
	private AutoCalSettingCommand legalMidOtTime;

	public AutoCalOvertimeSetting toDomain() {

		return new AutoCalOvertimeSetting(
				this.getEarlyOtTime().toDomain(), 
				this.getEarlyMidOtTime().toDomain(), 
				this.getNormalOtTime().toDomain(), 
				this.getNormalMidOtTime().toDomain(), 
				this.getLegalOtTime().toDomain(),
				this.getLegalMidOtTime().toDomain());
	}

}
