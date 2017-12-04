/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.find.worktime.fixedset.dto;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.TimeRoundingSettingDto;

/**
 * The Class TimeZoneRoundingDto.
 */
@Getter
@Setter
public class TimeZoneRoundingDto {

	/** The rounding. */
	private TimeRoundingSettingDto rounding;

	/** The start. */
	private Integer start;
	
	/** The end. */
	private Integer end;
}
