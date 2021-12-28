package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

@AllArgsConstructor
@Getter
public class PremiumTimeCommand {

	// 割増時間NO - primitive value
	private Integer premiumTimeNo;

	// 割増時間
	private Integer premitumTime;

	/** 割増金額 */
	private Integer premiumAmount;

	/** 単価 **/
	private Integer unitPrice;

	public PremiumTime toDomain() {
		return new PremiumTime(
				EnumAdaptor.valueOf(this.getPremiumTimeNo(), ExtraTimeItemNo.class),
				new AttendanceTime(this.getPremitumTime()), 
				new AttendanceAmountDaily(this.getPremiumAmount()),
				new WorkingHoursUnitPrice(this.getUnitPrice()));
	}

}
