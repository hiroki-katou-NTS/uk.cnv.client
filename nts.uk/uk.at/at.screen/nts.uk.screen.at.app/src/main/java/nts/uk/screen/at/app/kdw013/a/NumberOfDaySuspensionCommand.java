package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;

@AllArgsConstructor
@Getter
public class NumberOfDaySuspensionCommand {
	// 振休振出日数
	private Double days;

	// 振休振出区分
	private int classifiction;

	public NumberOfDaySuspension toDomain() {

		return new NumberOfDaySuspension(new UsedDays(this.days),
				EnumAdaptor.valueOf(this.classifiction, FuriClassifi.class));
	}
}
