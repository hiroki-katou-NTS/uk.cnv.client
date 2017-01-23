package nts.uk.ctx.pr.core.app.command.rule.employment.unitprice;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistory;
import nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceHistoryRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class UpdateUnitPriceHistoryCommandHandler extends CommandHandler<UpdateUnitPriceHistoryCommand> {

	@Inject
	private UnitPriceHistoryRepository unitPriceHistoryRepository;
	
	@Override
	@Transactional
	protected void handle(CommandHandlerContext<UpdateUnitPriceHistoryCommand> context) {
		String companyCode = AppContexts.user().companyCode();
		UnitPriceHistory unitPriceHistory = context.getCommand().toDomain(companyCode);
		unitPriceHistoryRepository.update(unitPriceHistory);

	}

}
