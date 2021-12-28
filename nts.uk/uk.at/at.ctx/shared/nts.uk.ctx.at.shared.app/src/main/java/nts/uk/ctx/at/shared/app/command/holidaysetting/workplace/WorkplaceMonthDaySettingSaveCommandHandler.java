package nts.uk.ctx.at.shared.app.command.holidaysetting.workplace;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace.WorkplaceMonthDaySettingRepository;

/**
 * The Class WorkplaceMonthDaySettingSaveCommandHandler.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkplaceMonthDaySettingSaveCommandHandler extends CommandHandler<WorkplaceMonthDaySettingSaveCommand> {
	
	/** The repository. */
	@Inject
	private WorkplaceMonthDaySettingRepository repository;
	
	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<WorkplaceMonthDaySettingSaveCommand> context) {
		// Get Command
		WorkplaceMonthDaySettingSaveCommand command = context.getCommand();
		
		// convert to domain
		WorkplaceMonthDaySetting domain = new WorkplaceMonthDaySetting(command);

		// save data
		this.repository.remove(domain);
		this.repository.add(domain);
	}

}
