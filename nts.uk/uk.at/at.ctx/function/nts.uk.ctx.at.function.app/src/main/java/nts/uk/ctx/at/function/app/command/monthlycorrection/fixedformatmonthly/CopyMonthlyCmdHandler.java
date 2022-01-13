package nts.uk.ctx.at.function.app.command.monthlycorrection.fixedformatmonthly;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.function.dom.monthlycorrection.fixedformatmonthly.MonthlyRecordWorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CopyMonthlyCmdHandler extends CommandHandler<CopyMonthlyCmd> {

	@Inject MonthlyRecordWorkTypeRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<CopyMonthlyCmd> context) {
		
		String companyId = AppContexts.user().companyId();
		CopyMonthlyCmd command = context.getCommand();
		
		repository.copy(companyId, command.getBusinessTypeCode(), command.getListBusinessTypeCode());
	}

}
