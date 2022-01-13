package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.command.PeregUpdateCommandHandler;

@Stateless
public class UpdateEmployeeUnitPriceCommandHandler extends CommandHandler<UpdateEmployeeUnitPriceCommand>
	implements PeregUpdateCommandHandler<UpdateEmployeeUnitPriceCommand> {
	
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
		return UpdateEmployeeUnitPriceCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<UpdateEmployeeUnitPriceCommand> context) {
		UpdateEmployeeUnitPriceCommand command = context.getCommand();
		String cid = AppContexts.user().companyId();
		if (command.getStartDate() != null) {
			Optional<EmployeeUnitPriceHistory> eupHistOp = eupHistRepo.getBySid(cid, command.getSId());
			if (!eupHistOp.isPresent()) {
				throw new RuntimeException("Invalid EmployeeUnitPriceHistory");
			}
			
			Optional<DateHistoryItem> itemToBeUpdated = eupHistOp.get().getHistoryItems().stream()
					.filter(h -> h.identifier().equals(command.getHistId())).findFirst();
			
			if (!itemToBeUpdated.isPresent()) {
				throw new RuntimeException("Invalid EmployeeUnitPriceHistory");
			}
			
			GeneralDate endDate = command.getEndDate() != null ? command.getEndDate() : GeneralDate.max();
			eupHistOp.get().changeSpan(itemToBeUpdated.get(), new DatePeriod(command.getStartDate(), endDate));
			eupHistRepo.update(itemToBeUpdated.get());
		}
		
		List<UnitPricePerNumber> unitPrices = new ArrayList<UnitPricePerNumber>();
		
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(0, command.getUnitPrice1() != null ? command.getUnitPrice1().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(1, command.getUnitPrice2() != null ? command.getUnitPrice2().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(2, command.getUnitPrice3() != null ? command.getUnitPrice3().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(3, command.getUnitPrice4() != null ? command.getUnitPrice4().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(4, command.getUnitPrice5() != null ? command.getUnitPrice5().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(5, command.getUnitPrice6() != null ? command.getUnitPrice6().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(6, command.getUnitPrice7() != null ? command.getUnitPrice7().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(7, command.getUnitPrice8() != null ? command.getUnitPrice8().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(8, command.getUnitPrice9() != null ? command.getUnitPrice9().intValue() : 0));
		unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(9, command.getUnitPrice10() != null ? command.getUnitPrice10().intValue() : 0));
		
		EmployeeUnitPriceHistoryItem eupHistItem = EmployeeUnitPriceHistoryItem.createSimpleFromJavaType(command.getSId(), command.getHistId(), unitPrices);
		eupHistItemRepo.update(eupHistItem);
	}

}
