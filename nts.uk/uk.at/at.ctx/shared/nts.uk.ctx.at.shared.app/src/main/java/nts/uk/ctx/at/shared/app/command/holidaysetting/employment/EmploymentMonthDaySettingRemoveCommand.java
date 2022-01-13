package nts.uk.ctx.at.shared.app.command.holidaysetting.employment;

import lombok.Data;

/**
 * The Class EmploymentMonthDaySettingRemoveCommand.
 */
@Data
public class EmploymentMonthDaySettingRemoveCommand {
	/** The year. */
	private int year;
	
	/** The emp cd. */
	private String empCd;
	
	private int startMonth;
}
