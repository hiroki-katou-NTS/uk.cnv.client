package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryService;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.PeregUpdateCommandHandler;

@Stateless
public class UpdateAffWorkplaceHistoryCommandHandler extends CommandHandler<UpdateAffWorkplaceHistoryCommand>
	implements PeregUpdateCommandHandler<UpdateAffWorkplaceHistoryCommand>{
	
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
		return UpdateAffWorkplaceHistoryCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<UpdateAffWorkplaceHistoryCommand> context) {
		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		// In case of date period are exist in the screen
		if (command.getStartDate() != null){
			Optional<AffWorkplaceHistory> existHist = affWorkplaceHistoryRepository.getByEmployeeId(companyId, command.getEmployeeId());
			
			if (!existHist.isPresent()){
				throw new RuntimeException("invalid AffWorkplaceHistory"); 
			}
				
			Optional<DateHistoryItem> itemToBeUpdate = existHist.get().getHistoryItems().stream()
	                .filter(h -> h.identifier().equals(command.getHistoryId()))
	                .findFirst();
			
			if (!itemToBeUpdate.isPresent()){
				throw new RuntimeException("invalid AffWorkplaceHistory");
			}
			existHist.get().changeSpan(itemToBeUpdate.get(), new DatePeriod(command.getStartDate(), command.getEndDate()!= null? command.getEndDate():  ConstantUtils.maxDate()));
			
			affWorkplaceHistoryService.update(existHist.get(), itemToBeUpdate.get());
		}
		AffWorkplaceHistoryItem histItem = AffWorkplaceHistoryItem.createFromJavaType(command.getHistoryId(), command.getEmployeeId(), command.getWorkplaceId());
		affWorkplaceHistoryItemRepository.update(histItem);
	}
	
}
