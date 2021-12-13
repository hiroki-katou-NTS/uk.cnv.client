package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import java.util.ArrayList;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryService;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddAffWorkplaceHistoryCommandHandler extends CommandHandlerWithResult<AddAffWorkplaceHistoryCommand,PeregAddCommandResult>
	implements PeregAddCommandHandler<AddAffWorkplaceHistoryCommand>{
	
	@Inject
	private AffWorkplaceHistoryRepository affWorkplaceHistoryRepository;
	
	@Inject
	private AffWorkplaceHistoryItemRepository affWorkplaceHistoryItemRepository;
	
	@Inject 
	private AffWorkplaceHistoryService affWorkplaceHistoryService;
	
	@Override
	public String targetCategoryCd() {
		return "CS00017";
	}

	@Override
	public Class<?> commandClass() {
		return AddAffWorkplaceHistoryCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddAffWorkplaceHistoryCommand> context) {
		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		
		String newHistID = IdentifierUtil.randomUniqueId();
		DateHistoryItem dateItem = new DateHistoryItem(newHistID, new DatePeriod(command.getStartDate()!=null?command.getStartDate():ConstantUtils.minDate(), command.getEndDate()!= null? command.getEndDate():  ConstantUtils.maxDate()));
		Optional<AffWorkplaceHistory> existHist = affWorkplaceHistoryRepository.getByEmployeeId(companyId, command.getEmployeeId());
		
		AffWorkplaceHistory itemtoBeAdded = new AffWorkplaceHistory(companyId, command.getEmployeeId(),new ArrayList<>());
		// In case of exist history of this employee
		if (existHist.isPresent()){
			itemtoBeAdded = existHist.get();
		}
		
		itemtoBeAdded.add(dateItem);
		
		affWorkplaceHistoryService.add(itemtoBeAdded);
		
		AffWorkplaceHistoryItem histItem = AffWorkplaceHistoryItem.createFromJavaType(newHistID, command.getEmployeeId(), command.getWorkplaceId());
		affWorkplaceHistoryItemRepository.add(histItem);
		
		return new PeregAddCommandResult(newHistID);
	}
	
}
