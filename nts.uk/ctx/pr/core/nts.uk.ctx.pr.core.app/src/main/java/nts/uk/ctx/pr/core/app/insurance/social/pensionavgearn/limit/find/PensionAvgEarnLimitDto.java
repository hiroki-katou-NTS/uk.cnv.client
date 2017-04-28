/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.insurance.social.pensionavgearn.limit.find;

import lombok.Builder;
import lombok.Getter;
import nts.uk.ctx.pr.core.dom.insurance.social.healthavgearn.limit.HealthAvgEarnLimit;

/**
 * The Class HealthAvgEarnLimitDto.
 */
@Builder
@Getter
public class PensionAvgEarnLimitDto {

	/** The pension level. */
	private Integer grade;

	/** The avg earn. */
	private Long avgEarn;

	/** The sal limit. */
	private Long salLimit;

	/**
	 * From domain.
	 *
	 * @param domain the domain
	 * @return the health avg earn limit dto
	 */
	public static PensionAvgEarnLimitDto fromDomain(HealthAvgEarnLimit domain) {
		return PensionAvgEarnLimitDto.builder().grade(domain.getGrade())
				.avgEarn(domain.getAvgEarn()).salLimit(domain.getSalLimit()).build();
	}

}
