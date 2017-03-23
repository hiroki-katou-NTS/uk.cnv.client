/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.insurance.avgearn;

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

	/**
	 * Instantiates a new avg earn level master setting.
	 */
	public AvgEarnLevelMasterSetting() {
		super();
	}

	public AvgEarnLevelMasterSetting(Integer code, Integer healthLevel, Integer pensionLevel, Long avgEarn,
			Long salLimit) {
		super();
		this.code = code;
		this.healthLevel = healthLevel;
		this.pensionLevel = pensionLevel;
		this.avgEarn = avgEarn;
		this.salLimit = salLimit;
	}

}
