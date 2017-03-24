package nts.uk.ctx.pr.core.app.command.retirement.payitem;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EnumType;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.retirement.payitem.IndicatorCategory;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItem;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemCode;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemEnglishName;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemFullName;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemName;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemPrintName;
import nts.uk.ctx.pr.core.dom.retirement.payitem.RetirementPayItemRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.primitive.Memo;
/**
 * update retirement payment item if item exist
 * @author Doan Duy Hung
 * 
 */
@Stateless
public class UpdateRetirementPayItemCommandHandler extends CommandHandler<UpdateRetirementPayItemCommand>{
	@Inject
	private RetirementPayItemRepository retirementPayItemRepository;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateRetirementPayItemCommand> context) {
		// get company code
		String companyCode = AppContexts.user().companyCode();
		
		// convert item to domain
		UpdateRetirementPayItemCommand command = context.getCommand();
		RetirementPayItem retirementPayItem = new RetirementPayItem(
				companyCode, 
				EnumAdaptor.valueOf(command.category, IndicatorCategory.class), 
				new RetirementPayItemCode(command.itemCode), 
				new RetirementPayItemName(command.itemName), 
				new RetirementPayItemPrintName(command.printName), 
				new RetirementPayItemEnglishName(command.englishName), 
				new RetirementPayItemFullName(command.fullName), 
				new Memo(command.memo));
		
		// validate input item
		retirementPayItem.validate();
		Optional<RetirementPayItem> optionalRetirementPayItem = this.retirementPayItemRepository.findByKey(
				companyCode, 
				EnumAdaptor.valueOf(command.category, IndicatorCategory.class), 
				new RetirementPayItemCode(command.itemCode));
		if(!optionalRetirementPayItem.isPresent()) {
			throw new BusinessException("ER010");
		}
		
		// update retirement payment item
		this.retirementPayItemRepository.update(retirementPayItem);
		
	}
}
