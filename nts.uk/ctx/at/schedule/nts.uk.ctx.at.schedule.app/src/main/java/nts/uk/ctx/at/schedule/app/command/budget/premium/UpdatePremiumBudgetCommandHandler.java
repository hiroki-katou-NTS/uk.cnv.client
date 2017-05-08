package nts.uk.ctx.at.schedule.app.command.budget.premium;

import java.util.Collections;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.budget.premium.PersonCostCalculation;
import nts.uk.ctx.at.schedule.dom.budget.premium.PersonCostCalculationRepository;
import nts.uk.ctx.at.schedule.dom.budget.premium.UnitPrice;
import nts.uk.shr.com.primitive.Memo;

/**
 * 
 * @author Doan Duy Hung
 *
 */

@Stateless
@Transactional
public class UpdatePremiumBudgetCommandHandler extends CommandHandler<UpdatePremiumBudgetCommand>{

	@Inject
	private PersonCostCalculationRepository personCostCalculationRepository;
	
	@Override
	protected void handle(CommandHandlerContext<UpdatePremiumBudgetCommand> context) {
		UpdatePremiumBudgetCommand budgetCommand = context.getCommand();
		Optional<PersonCostCalculation> optional = this.personCostCalculationRepository.find(budgetCommand.getCID(), budgetCommand.getHID());
		if(!optional.isPresent()) throw new RuntimeException("Item do not exist");
		this.personCostCalculationRepository.update(
				new PersonCostCalculation(
						budgetCommand.getCID(), 
						budgetCommand.getHID(), 
						new Memo(budgetCommand.getMemo()), 
						EnumAdaptor.valueOf(budgetCommand.getUnitprice(), UnitPrice.class), 
						budgetCommand.getStartDate(), 
						budgetCommand.getEndDate(),
						Collections.EMPTY_LIST
				)
		);
		
	}

}
