package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.command.PeregDeleteCommandHandler;

@Stateless
public class DeleteEmployeeUnitPriceCommandHandler extends CommandHandler<DeleteEmployeeUnitPriceCommand>
	implements PeregDeleteCommandHandler<DeleteEmployeeUnitPriceCommand> {
	
	@Inject
	private EmployeeUnitPriceHistoryRepository eupHistRepo;
	
	@Inject
	private EmployeeUnitPriceHistoryItemRepository eupHistItemRepo;

	@Override
	public String targetCategoryCd() {
		return "CS00097";
	}

	@Override
	public Class<?> commandClass() {
		return DeleteEmployeeUnitPriceCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<DeleteEmployeeUnitPriceCommand> context) {
		DeleteEmployeeUnitPriceCommand command = context.getCommand();
		String cid = AppContexts.user().companyId();
		
		Optional<EmployeeUnitPriceHistory> eupHistOp = eupHistRepo.getBySid(cid, command.getEmployeeId());
		
		if (!eupHistOp.isPresent()){
			throw new RuntimeException("Invalid EmployeeUnitPriceHistory"); 
		}
		
		Optional<DateHistoryItem> itemToBeDeleted = eupHistOp.get().getHistoryItems().stream()
                .filter(h -> h.identifier().equals(command.getHistId()))
                .findFirst();
		
		if (!itemToBeDeleted.isPresent()){
			throw new RuntimeException("Invalid EmployeeUnitPriceHistory");
		}
		
		eupHistOp.get().remove(itemToBeDeleted.get());
		
		eupHistRepo.delete(cid, command.getEmployeeId(), command.getHistId());
		
		if (eupHistOp.get().getHistoryItems().size() > 0) {
			List<DateHistoryItem> histItemList = eupHistOp.get().getHistoryItems();
			Comparator<DateHistoryItem> compareByStartDate = 
    				(DateHistoryItem hist1, DateHistoryItem hist2) 
    				-> hist1.start().compareTo(hist2.start());
    		Collections.sort(histItemList, compareByStartDate);
			
			DateHistoryItem itemToBeUpdated = histItemList.get(histItemList.size() - 1);
			GeneralDate endDate = GeneralDate.max();
			DateHistoryItem itemUpdate = new DateHistoryItem(itemToBeUpdated.identifier(), new DatePeriod(itemToBeUpdated.start(), endDate));
			eupHistRepo.update(itemUpdate);
		}
		
		eupHistItemRepo.delete(cid, command.getEmployeeId(), command.getHistId());
	}

}
