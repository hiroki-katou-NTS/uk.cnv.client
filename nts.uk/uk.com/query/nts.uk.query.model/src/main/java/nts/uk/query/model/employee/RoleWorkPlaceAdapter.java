/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.query.model.employee;

import java.util.List;

import nts.arc.time.GeneralDate;

/**
 * The Interface RoleWorkPlaceAdapter.
 */
public interface RoleWorkPlaceAdapter {
	
	/**
	 * Gets the work place id by employee reference range.
	 *
	 * @param baseDate the base date
	 * @param employeeReferenceRange the employee reference range
	 * @return the work place id by employee reference range
	 */
	List<String> getWorkPlaceIdByEmployeeReferenceRange(GeneralDate baseDate, Integer employeeReferenceRange);
}
