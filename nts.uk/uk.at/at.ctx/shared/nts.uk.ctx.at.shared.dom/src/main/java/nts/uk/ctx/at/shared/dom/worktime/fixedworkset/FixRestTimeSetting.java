/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.fixedworkset;

import java.util.List;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.worktime.DeductionTime;

/**
 * The Class BreakTimeSetting.
 */
@Getter
// 固定休憩時間の時間帯設定
public class FixRestTimeSetting {

	// 時間帯
	private List<DeductionTime> rangeTimes;
}
