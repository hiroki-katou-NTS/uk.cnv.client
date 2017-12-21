package nts.uk.ctx.bs.employee.app.command.holidaysetting.employment;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.bs.employee.dom.common.CompanyId;
import nts.uk.ctx.bs.employee.dom.holidaysetting.common.Year;
import nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySetting;
import nts.uk.ctx.bs.employee.dom.holidaysetting.employment.EmploymentMonthDaySettingRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class EmploymentMonthDaySettingSaveCommandHandler.
 */
@Stateless
public class EmploymentMonthDaySettingSaveCommandHandler extends CommandHandler<EmploymentMonthDaySettingSaveCommand> {
	
	/** The repository. */
	@Inject
	private EmploymentMonthDaySettingRepository repository;

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<EmploymentMonthDaySettingSaveCommand> context) {
		// Get Company Id
		String companyId = AppContexts.user().companyId();
		
		// Get Command
		EmploymentMonthDaySettingSaveCommand command = context.getCommand();
		
		// convert to domain
		EmploymentMonthDaySetting domain = new EmploymentMonthDaySetting(command);
		
		Optional<EmploymentMonthDaySetting> optional = this.repository.findByYear(new CompanyId(companyId), command.getEmploymentCode(), new Year(command.getYear()));
	
		// save data
		if(optional.isPresent()){
			this.repository.update(domain);
		}
		this.repository.add(domain);
	}

}
