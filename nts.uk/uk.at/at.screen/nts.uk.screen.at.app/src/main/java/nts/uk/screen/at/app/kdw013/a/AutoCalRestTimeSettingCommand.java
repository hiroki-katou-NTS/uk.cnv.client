package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalRestTimeSetting;

@AllArgsConstructor
@Getter
public class AutoCalRestTimeSettingCommand {
	/** The rest time. */
	private AutoCalSettingCommand restTime;

	/** The late night time. */
	// 休出深夜時間
	private AutoCalSettingCommand lateNightTime;

	public AutoCalRestTimeSetting toDomain() {

		return new AutoCalRestTimeSetting(this.getRestTime().toDomain(), this.getLateNightTime().toDomain());
	}
}
