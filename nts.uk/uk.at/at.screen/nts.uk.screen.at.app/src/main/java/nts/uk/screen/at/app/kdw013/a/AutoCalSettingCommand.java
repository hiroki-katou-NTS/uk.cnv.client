package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;

@AllArgsConstructor
@Getter
public class AutoCalSettingCommand {
	/** The up limit ot set. */
	// 上限残業時間の設定
	private Integer upLimitOtSet;

	/** The cal atr. */
	/// 計算区分
	private Integer calAtr;

	public AutoCalSetting toDomain() {

		return new AutoCalSetting(
				EnumAdaptor.valueOf(this.getUpLimitOtSet(), TimeLimitUpperLimitSetting.class),
				EnumAdaptor.valueOf(this.getCalAtr(), AutoCalAtrOvertime.class));
	}
}
