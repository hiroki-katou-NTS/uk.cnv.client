package nts.uk.ctx.at.shared.app.command.holidaysetting.company;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository;


/**
 * The Class CompanyMonthDaySettingSaveCommandHandler.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CompanyMonthDaySettingSaveCommandHandler extends CommandHandler<CompanyMonthDaySettingSaveCommand> {
	
	/** The repository. */
	@Inject
	private CompanyMonthDaySettingRepository repository;

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<CompanyMonthDaySettingSaveCommand> context) {
		
		// Get Command
		CompanyMonthDaySettingSaveCommand command = context.getCommand();
		
		// convert to domain
		CompanyMonthDaySetting domain = new CompanyMonthDaySetting(command);
		
		// save data
		this.repository.remove(domain);
		this.repository.add(domain);
	}

}
