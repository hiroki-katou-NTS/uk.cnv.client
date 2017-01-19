/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.insurance.social.healthrate;

import lombok.Data;

/**
 * The Class AvgEarnLevelMasterSetting.
 */
@Data
public class AvgEarnLevelMasterSetting {

	/** The code. */
	private Integer code;

	/** The health level. */
	private Integer healthLevel;
	
	/** The pension level. */
	private Integer pensionLevel;

	/** The avg earn. */
	private Long avgEarn;

	/** The sal limit. */
	private Long salLimit;

}
