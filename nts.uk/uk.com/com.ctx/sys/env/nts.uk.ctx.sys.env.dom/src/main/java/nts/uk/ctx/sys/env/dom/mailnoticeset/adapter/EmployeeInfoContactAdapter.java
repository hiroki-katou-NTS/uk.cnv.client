/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailnoticeset.adapter;

import java.util.List;

import nts.uk.ctx.sys.env.dom.mailnoticeset.dto.EmployeeInfoContactImport;

/**
 * The Interface EmployeeInfoContactAdapter.
 */
public interface EmployeeInfoContactAdapter {

	/**
	 * Gets the list contact.
	 *
	 * @param employeeIds the employee ids
	 * @return the list contact
	 */
	List<EmployeeInfoContactImport> getListContact(List<String> employeeIds);

	/**
	 * Register.
	 *
	 * @param employee the employee
	 */
	void register(EmployeeInfoContactImport employee);
}
