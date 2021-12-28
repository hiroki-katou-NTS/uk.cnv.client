package nts.uk.ctx.at.schedule.app.command.budget.premium;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.DeleteLaborCalculationSettingCommand;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculationDomainService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * @author Doan Duy Hung
 */

@Stateless
@Transactional
public class DeletePersonCostCalculationCommandHandler extends CommandHandler<DeleteLaborCalculationSettingCommand> {

    @Inject
    private PersonCostCalculationDomainService personCostCalculationDomainService;

    @Override
    protected void handle(CommandHandlerContext<DeleteLaborCalculationSettingCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        val cid = AppContexts.user().companyId();
        val histId = command.getHistoryID();
        personCostCalculationDomainService.deletePersonCostCalculation(cid, histId);
    }

}
