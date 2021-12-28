package nts.uk.ctx.at.shared.app.command.holidaysetting.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Instantiates a new company month day setting remove command.
 */
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMonthDaySettingRemoveCommand {
	
	/** The year. */
	private int year;
	
	private int startMonth;
}
