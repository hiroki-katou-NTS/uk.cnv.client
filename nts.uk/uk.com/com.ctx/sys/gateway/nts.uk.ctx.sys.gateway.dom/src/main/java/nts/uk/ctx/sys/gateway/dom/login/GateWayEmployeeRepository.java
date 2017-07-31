/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.gateway.dom.login;

import java.util.Optional;

/**
 * The Interface EmployeeRepository.
 */
public interface GateWayEmployeeRepository {

	/**
	 * Gets the by employee code.
	 *
	 * @param employeeCode the employee code
	 * @return the by employee code
	 */
	Optional<Employee> getByEmployeeCode(String employeeCode);
}
