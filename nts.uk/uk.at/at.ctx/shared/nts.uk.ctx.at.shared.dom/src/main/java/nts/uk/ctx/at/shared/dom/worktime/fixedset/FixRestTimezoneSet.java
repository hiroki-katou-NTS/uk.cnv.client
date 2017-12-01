/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.fixedset;

import java.util.List;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;

/**
 * The Class FixRestTimezoneSet.
 */
//固定勤務の休憩時間帯
@Getter
public class FixRestTimezoneSet extends DomainObject {

	/** The lst timezone. */
	// 時間帯
	private List<DeductionTime> lstTimezone;
}
