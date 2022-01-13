package nts.uk.ctx.at.shared.app.command.holidaysetting.company;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company.CompanyMonthDaySettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class CompanyMonthDaySettingRemoveCommandHandler.
 */
@Stateless
public class CompanyMonthDaySettingRemoveCommandHandler extends CommandHandler<CompanyMonthDaySettingRemoveCommand> {
	
	/** The repository. */
	@Inject
	private CompanyMonthDaySettingRepository repository;
	
	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<CompanyMonthDaySettingRemoveCommand> context) {
		
		// Get Company Id
		String companyId = AppContexts.user().companyId();
		
		// Get command
		CompanyMonthDaySettingRemoveCommand command = context.getCommand();
		
		// Remove
		this.repository.remove(new CompanyId(companyId), new Year(command.getYear()), command.getStartMonth());
	}

}
