package nts.uk.ctx.at.record.app.command.dailyperformanceformat;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatDailyRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.repository.BusinessTypeFormatMonthlyRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author dungbn
 *
 */
@Stateless
public class CopybusinessTypeDailyCommandHandler extends CommandHandler<CopybusinessTypeDailyCommand> {

	@Inject
	private BusinessTypeFormatDailyRepository repositoryDaily;
	
	@Inject
	private BusinessTypeFormatMonthlyRepository repositoryMonthly;
	
	@Override
	protected void handle(CommandHandlerContext<CopybusinessTypeDailyCommand> context) {
		
		CopybusinessTypeDailyCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		
		repositoryDaily.copy(companyId, command.getBusinessTypeCode(), command.getListBusinessTypeCode());
		repositoryMonthly.copy(companyId, command.getBusinessTypeCode(), command.getListBusinessTypeCode());
	}
}
