/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.app.wagetable.command.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class RangeItem.
 */
@Getter
@Setter
public class RangeItemDto implements BaseItemDto {

	/** The order number. */
	private Integer orderNumber;

	/** The start val. */
	private Double startVal;

	/** The end val. */
	private Double endVal;

	/** The uuid. */
	private String uuid;

}
