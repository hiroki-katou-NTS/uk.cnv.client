/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.dom.department;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.bs.employee.dom.workplace.WorkplaceId;

/**
 * The Class WorkHierarchy.
 */
@Getter
//職場階層
public class DeapartmentHierarchy extends DomainObject {

	/** The workplace id. */
	// 職場ID
	private WorkplaceId workplaceId;

	/** The hierarchy code. */
	// 階層コード
	private HierarchyCode hierarchyCode;
}
