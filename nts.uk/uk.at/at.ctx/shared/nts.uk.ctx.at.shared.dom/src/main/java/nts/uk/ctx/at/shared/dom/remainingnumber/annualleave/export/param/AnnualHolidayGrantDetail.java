package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.param;

import lombok.AllArgsConstructor;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;

//年休使用詳細
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AnnualHolidayGrantDetail {
	/**社員ID	 */
	private String sid;
	/**	年月日	 */
	private GeneralDate ymd;
	/**使用数	 */
	private AnnualLeaveUsedNumber usedNumbers;
	/**参照元区分	 */
	ReferenceAtr referenceAtr;
	/**午前午後区分	 */
	private AmPmAtr amPmAtr;
	/** フレックス補填フラグ */
	private boolean isFlexFlag;
}
