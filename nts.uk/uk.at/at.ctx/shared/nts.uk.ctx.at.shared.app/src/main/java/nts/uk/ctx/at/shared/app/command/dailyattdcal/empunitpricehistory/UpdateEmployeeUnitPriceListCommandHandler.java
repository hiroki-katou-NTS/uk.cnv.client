package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;

@Stateless
public class UpdateEmployeeUnitPriceListCommandHandler extends CommandHandlerWithResult<List<UpdateEmployeeUnitPriceCommand>, List<MyCustomizeException>>
	implements PeregUpdateListCommandHandler<UpdateEmployeeUnitPriceCommand>{

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
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<UpdateEmployeeUnitPriceCommand>> context) {
		List<UpdateEmployeeUnitPriceCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		List<String> sidErrorLst = new ArrayList<>();
		List<MyCustomizeException> errorExceptionLst = new ArrayList<>();
		// sidsPidsMap
		List<String> sids = command.stream().map(c -> c.getSId()).collect(Collectors.toList());

		Map<String, List<EmployeeUnitPriceHistory>> histBySidsMap = eupHistRepo.getBySidsAndCid(sids, cid)
				.stream().collect(Collectors.groupingBy(c -> c.getSid()));
		
		command.stream().forEach(c -> {
			try {
				// In case of date period are exist in the screen,
				if (c.getStartDate() != null) {
					List<EmployeeUnitPriceHistory> histBySidList = histBySidsMap.get(c.getSId());
					if (histBySidList != null && histBySidList.size() > 0) {
						Optional<DateHistoryItem> itemToBeUpdated = histBySidList.get(0).getHistoryItems().stream()
								.filter(h -> h.identifier().equals(c.getHistId())).findFirst();
						if (itemToBeUpdated.isPresent()) {
							GeneralDate endDate = c.getEndDate() != null ? c.getEndDate() : GeneralDate.max();
							histBySidList.get(0).changeSpan(itemToBeUpdated.get(), new DatePeriod(c.getStartDate(), endDate));
							eupHistRepo.update(itemToBeUpdated.get());
						} else {
							sidErrorLst.add(c.getSId());
							return;
						}
					} else {
						sidErrorLst.add(c.getSId());
						return;
					}
				}

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
				
				EmployeeUnitPriceHistoryItem eupHistItem = EmployeeUnitPriceHistoryItem.createSimpleFromJavaType(c.getSId(), c.getHistId(), unitPrices);
				eupHistItemRepo.update(eupHistItem);
			} catch (BusinessException e) {
				MyCustomizeException ex = new MyCustomizeException(e.getMessageId(), Arrays.asList(c.getSId()), "期間");
				errorExceptionLst.add(ex);
			}

		});
		
		if(!sidErrorLst.isEmpty()) {
			errorExceptionLst.add(new MyCustomizeException("invalid employmentHistory", sidErrorLst));
		}
		
		return errorExceptionLst;
	}

}
