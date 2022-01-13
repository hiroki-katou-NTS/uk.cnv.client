package nts.uk.ctx.at.schedule.app.command.budget.premium;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.app.command.budget.premium.command.UpdateHistPersonCostCalculationCommand;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.*;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;


@Stateless
@Transactional
public class UpdateHistPersonCostCalculationCommandHandler extends CommandHandler<UpdateHistPersonCostCalculationCommand> {
    @Inject
    PersonCostCalculationDomainService service;

    @Override
    protected void handle(CommandHandlerContext<UpdateHistPersonCostCalculationCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        val priceRounding = command.getPersonCostRoundingSetting().getUnitPriceRounding();
        val roundingOfPremium = new UnitPriceRoundingSetting(EnumAdaptor.valueOf(priceRounding, UnitPriceRounding.class));
        val unit = command.getPersonCostRoundingSetting().getUnit();
        val rounding = command.getPersonCostRoundingSetting().getRounding();
        val amountRoundingSetting = new AmountRoundingSetting(AmountUnit.valueOf(unit), AmountRounding.valueOf(rounding));
        val roundingSetting = new PersonCostRoundingSetting(roundingOfPremium, amountRoundingSetting);
        val cid = AppContexts.user().companyId();
        val unitPrice = command.getUnitPrice();

        Optional<UnitPrice> optUnitPrice = unitPrice != null ? Optional.of(EnumAdaptor.valueOf(unitPrice, UnitPrice.class))
                : Optional.empty();
        val premiumSettings = new ArrayList<PremiumSetting>();
        if (command.getPremiumSets() != null && !command.getPremiumSets().isEmpty()) {
            premiumSettings.addAll(command.getPremiumSets().stream().map(e -> new PremiumSetting(
                    cid,
                    command.getHistoryID(),
                    EnumAdaptor.valueOf(e.getID(), ExtraTimeItemNo.class),
                    new PremiumRate(e.getRate()),
                    EnumAdaptor.valueOf(e.getUnitPrice(), UnitPrice.class),
                    e.getAttendanceItems()
            )).collect(Collectors.toList()));
        }
        val workingHoursUnitPrice = unitPrice != null ? unitPrice : command.getWorkingHoursUnitPrice();

        PersonCostCalculation domain = new PersonCostCalculation(
                roundingSetting,
                cid,
                new Remarks(command.getMemo()),
                premiumSettings,
                optUnitPrice,
                EnumAdaptor.valueOf(command.getHowToSetUnitPrice(), HowToSetUnitPrice.class),
                EnumAdaptor.valueOf(workingHoursUnitPrice, UnitPrice.class),
                command.getHistoryID()
        );
        service.updateHistPersonCalculation(domain, command.getHistoryID(), command.getStartDate());

    }
}
