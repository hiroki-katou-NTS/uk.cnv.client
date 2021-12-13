package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryService;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;
@Stateless
public class AddAffWorkplaceHistoryListCommandHandler extends CommandHandlerWithResult<List<AddAffWorkplaceHistoryCommand>, List<MyCustomizeException>>
implements PeregAddListCommandHandler<AddAffWorkplaceHistoryCommand>{
	
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
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<AddAffWorkplaceHistoryCommand>> context) {
		List<AddAffWorkplaceHistoryCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		Map<String, String> recordIds = new HashMap<>();
		List<MyCustomizeException> result = new ArrayList<>();
		List<AffWorkplaceHistoryItem> histItems = new ArrayList<>();
		List<AffWorkplaceHistory> affJobTitleHistoryLst = new ArrayList<>();
		List<String> sids = command.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		
		Map<String, List<AffWorkplaceHistory>> existHistMap = affWorkplaceHistoryRepository.getBySidsAndCid(cid, sids)
				.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		command.stream().forEach(c -> {
			try {
			String histId = IdentifierUtil.randomUniqueId();
			List<AffWorkplaceHistory> affJobTitleHistory = existHistMap.get(c.getEmployeeId());
			AffWorkplaceHistory itemtoBeAdded = new AffWorkplaceHistory(cid, c.getEmployeeId(), new ArrayList<>());
			DateHistoryItem dateItem = new DateHistoryItem(histId,
					new DatePeriod(c.getStartDate() != null ? c.getStartDate() : ConstantUtils.minDate(),
							c.getEndDate() != null ? c.getEndDate() : ConstantUtils.maxDate()));
			if (affJobTitleHistory != null) {
				itemtoBeAdded = affJobTitleHistory.get(0);
			}
			itemtoBeAdded.add(dateItem);
			affJobTitleHistoryLst.add(itemtoBeAdded);
			AffWorkplaceHistoryItem histItem =AffWorkplaceHistoryItem.createFromJavaType(histId,  c.getEmployeeId(), c.getWorkplaceId());
			histItems.add(histItem);
			recordIds.put(c.getEmployeeId(), histId);
			}catch(BusinessException e) {
				MyCustomizeException ex = new MyCustomizeException(e.getMessageId(), Arrays.asList(c.getEmployeeId()), "期間");
				result.add(ex);
			}
		});
		
		if(!affJobTitleHistoryLst.isEmpty()) {
			affWorkplaceHistoryService.addAll(affJobTitleHistoryLst);
		}
		
		if(!histItems.isEmpty()) {
			affWorkplaceHistoryItemRepository.addAll(histItems);
		}
		
		if(!recordIds.isEmpty()) {
			result.add(new MyCustomizeException("NOERROR", recordIds));
		}
		
		return result;
	}

}
