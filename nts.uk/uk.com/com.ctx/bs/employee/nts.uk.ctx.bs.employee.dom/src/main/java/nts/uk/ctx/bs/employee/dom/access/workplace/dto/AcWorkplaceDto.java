/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.dom.access.workplace.dto;

import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * The Class AcWorkplaceDto.
 */
@Data
public class AcWorkplaceDto {
	/** The company id. */
	private String companyId;

	/** The workplace id. */
	private String workplaceId;

	/** The workplace code. */
	private String workplaceCode;

	/** The workplace name. */
	private String workplaceName;

	/** The start date. */
	private GeneralDate startDate;

	/** The end date. */
	private GeneralDate endDate;

	/**
	 * Instantiates a new pub workplace dto.
	 *
	 * @param companyId
	 *            the company id
	 * @param workplaceId
	 *            the workplace id
	 * @param workplaceCode
	 *            the workplace code
	 * @param workplaceName
	 *            the workplace name
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 */
	public AcWorkplaceDto(String companyId, String workplaceId, String workplaceCode,
			String workplaceName, GeneralDate startDate, GeneralDate endDate) {
		super();
		this.companyId = companyId;
		this.workplaceId = workplaceId;
		this.workplaceCode = workplaceCode;
		this.workplaceName = workplaceName;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
