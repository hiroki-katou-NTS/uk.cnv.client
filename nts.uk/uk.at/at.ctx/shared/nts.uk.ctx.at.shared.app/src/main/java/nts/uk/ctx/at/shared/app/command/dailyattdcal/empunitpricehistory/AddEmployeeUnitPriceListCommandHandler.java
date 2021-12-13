package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;

@Stateless
public class AddEmployeeUnitPriceListCommandHandler extends CommandHandlerWithResult<List<AddEmployeeUnitPriceCommand>, List<MyCustomizeException>>
	implements PeregAddListCommandHandler<AddEmployeeUnitPriceCommand>{
	
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
		return AddEmployeeUnitPriceCommand.class;
	}

	@Override
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<AddEmployeeUnitPriceCommand>> context) {
		List<AddEmployeeUnitPriceCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		Map<String, String> recordIds = new HashMap<>();
		List<MyCustomizeException> result = new ArrayList<>();
		
		List<String> sids = command.stream().map(c -> c.getSId()).collect(Collectors.toList());
		
		Map<String, List<EmployeeUnitPriceHistory>> histBySidsMap = eupHistRepo.getBySidsAndCid(sids, cid)
				.stream().collect(Collectors.groupingBy(c -> c.getSid()));
		
		command.stream().forEach(c -> {
			try {
				String newHistId = IdentifierUtil.randomUniqueId();
				GeneralDate startDate = c.getStartDate() != null ? c.getStartDate() : GeneralDate.min();
				
				List<EmployeeUnitPriceHistory> histBySidList = histBySidsMap.get(c.getSId());
				EmployeeUnitPriceHistory eupHist = new EmployeeUnitPriceHistory(c.getSId(), new ArrayList<DateHistoryItem>());
				if (histBySidList != null && histBySidList.size() > 0) {
					eupHist = histBySidList.get(0);
				}
				
				if (eupHist.getHistoryItems().size() > 0) {
					List<DateHistoryItem> histItemList = eupHist.getHistoryItems();
					Comparator<DateHistoryItem> compareByStartDate = 
		    				(DateHistoryItem hist1, DateHistoryItem hist2) 
		    				-> hist1.start().compareTo(hist2.start());
		    		Collections.sort(histItemList, compareByStartDate);
					
					DateHistoryItem itemToBeUpdated = histItemList.get(histItemList.size() - 1);
					GeneralDate endDate = startDate.addDays(-1);
					DateHistoryItem itemUpdate = new DateHistoryItem(itemToBeUpdated.identifier(), new DatePeriod(itemToBeUpdated.start(), endDate));
					eupHistRepo.update(itemUpdate);
				}
				
				GeneralDate endDate = c.getEndDate() != null ? c.getEndDate() : GeneralDate.max();
				DateHistoryItem itemToBeAdded = new DateHistoryItem(newHistId, new DatePeriod(startDate, endDate));
				eupHist.add(itemToBeAdded);
				
				eupHistRepo.add(c.getSId(), itemToBeAdded);

				List<UnitPricePerNumber> unitPrices = new ArrayList<UnitPricePerNumber>();
				
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(0, c.getUnitPrice1() != null ? c.getUnitPrice1().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(1, c.getUnitPrice2() != null ? c.getUnitPrice2().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(2, c.getUnitPrice3() != null ? c.getUnitPrice3().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(3, c.getUnitPrice4() != null ? c.getUnitPrice4().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(4, c.getUnitPrice5() != null ? c.getUnitPrice5().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(5, c.getUnitPrice6() != null ? c.getUnitPrice6().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(6, c.getUnitPrice7() != null ? c.getUnitPrice7().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(7, c.getUnitPrice8() != null ? c.getUnitPrice8().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(8, c.getUnitPrice9() != null ? c.getUnitPrice9().intValue() : 0));
				unitPrices.add(UnitPricePerNumber.createSimpleFromJavaType(9, c.getUnitPrice10() != null ? c.getUnitPrice10().intValue() : 0));
				
				EmployeeUnitPriceHistoryItem eupHistItem = EmployeeUnitPriceHistoryItem.createSimpleFromJavaType(c.getSId(), newHistId, unitPrices);
				
				eupHistItemRepo.add(eupHistItem);
				
				recordIds.put(c.getSId(), newHistId);
				
			}catch(BusinessException e) {
				MyCustomizeException ex = new MyCustomizeException(e.getMessageId(), Arrays.asList(c.getSId()),"期間");
				result.add(ex);
			}
		});
		
		if(!recordIds.isEmpty()) {
			result.add(new MyCustomizeException("NOERROR", recordIds));
		}
		
		return result;
	}

}
