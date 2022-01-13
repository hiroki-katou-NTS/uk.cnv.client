/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.holidaysetting.employee;

import lombok.Data;

/**
 * The Class EmployeeMonthDaySettingRemoveCommand.
 */
@Data
public class EmployeeMonthDaySettingRemoveCommand {
	/** The year. */
	private int year;
	
	
	/** The employee id. */
	private String employeeId;
	
	private int startMonth;
}
