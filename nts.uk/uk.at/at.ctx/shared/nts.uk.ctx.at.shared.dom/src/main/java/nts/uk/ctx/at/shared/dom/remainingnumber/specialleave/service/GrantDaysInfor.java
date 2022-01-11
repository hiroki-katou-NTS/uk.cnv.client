package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
/**
 * 付与日数
 * @author do_dt
 *
 */
@AllArgsConstructor
@Setter
@Getter
public class GrantDaysInfor {
	/**
	 * 年月日
	 */
	private GeneralDate ymd;
	/**
	 * 付与日数
	 */
	private double grantDays;
}
