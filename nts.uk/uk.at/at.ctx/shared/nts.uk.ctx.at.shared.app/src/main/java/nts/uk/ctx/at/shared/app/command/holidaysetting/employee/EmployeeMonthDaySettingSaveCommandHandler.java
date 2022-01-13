package nts.uk.ctx.at.shared.app.command.holidaysetting.employee;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.EmployeeMonthDaySettingRepository;

/**
 * The Class EmployeeMonthDaySettingSaveCommandHandler.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmployeeMonthDaySettingSaveCommandHandler extends CommandHandler<EmployeeMonthDaySettingSaveCommand> {
	
	/** The repository. */
	@Inject
	private EmployeeMonthDaySettingRepository repository;

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<EmployeeMonthDaySettingSaveCommand> context) {
		
		// Get Command
		EmployeeMonthDaySettingSaveCommand command = context.getCommand();
		
		// convert to domain
		EmployeeMonthDaySetting domain = new EmployeeMonthDaySetting(command);
		
		// save data
		this.repository.remove(domain);
		this.repository.add(domain);
	}

}
