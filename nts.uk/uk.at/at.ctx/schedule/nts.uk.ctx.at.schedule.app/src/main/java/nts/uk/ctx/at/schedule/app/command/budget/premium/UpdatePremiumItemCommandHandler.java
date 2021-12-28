package nts.uk.ctx.at.schedule.app.command.budget.premium;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.UpdatePremiumItemCommand;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.UseAttribute;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Doan Duy Hung
 */
@Stateless
@Transactional
public class UpdatePremiumItemCommandHandler extends CommandHandler<List<UpdatePremiumItemCommand>> {

    @Inject
    private PremiumItemRepository premiumItemRepository;

    @Override
    protected void handle(CommandHandlerContext<List<UpdatePremiumItemCommand>> context) {
        String companyID = AppContexts.user().companyId();
        List<UpdatePremiumItemCommand> commands = context.getCommand();

        // Check all PremiumItem useAtr, if all is not use throw Exception;
        int allUse = 0;
        for (UpdatePremiumItemCommand command : commands) {
            allUse += command.getUseAtr();
        }
        if (allUse == 0) throw new BusinessException("Msg_66");

        //update PremiumItem
        for (UpdatePremiumItemCommand command : commands) {
            if (command.isChange()) {
                this.premiumItemRepository.update(
                        new PremiumItem(
                                companyID,
                                ExtraTimeItemNo.valueOf(command.getDisplayNumber()),
                                new PremiumName(command.getName()),
                                EnumAdaptor.valueOf(command.getUseAtr(), UseAttribute.class)
                        )
                );
            }
        }
    }

}
