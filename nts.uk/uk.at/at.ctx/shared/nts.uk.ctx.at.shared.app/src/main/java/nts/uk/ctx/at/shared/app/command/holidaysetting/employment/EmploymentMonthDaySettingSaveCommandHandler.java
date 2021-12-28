package nts.uk.ctx.at.shared.app.command.holidaysetting.employment;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment.EmploymentMonthDaySettingRepository;

/**
 * The Class EmploymentMonthDaySettingSaveCommandHandler.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmploymentMonthDaySettingSaveCommandHandler extends CommandHandler<EmploymentMonthDaySettingSaveCommand> {
	
	/** The repository. */
	@Inject
	private EmploymentMonthDaySettingRepository repository;

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<EmploymentMonthDaySettingSaveCommand> context) {
		// Get Command
		EmploymentMonthDaySettingSaveCommand command = context.getCommand();
		
		// convert to domain
		EmploymentMonthDaySetting domain = new EmploymentMonthDaySetting(command);

		// save data
		this.repository.remove(domain);
		this.repository.add(domain);
	}

}
