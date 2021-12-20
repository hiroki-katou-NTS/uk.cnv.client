package nts.uk.ctx.bs.employee.app.command.workplace.affiliate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryIntermediate;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryService;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;
@Stateless
public class UpdateAffWorkplaceHistoryListCommandHandler extends CommandHandlerWithResult<List<UpdateAffWorkplaceHistoryCommand>, List<MyCustomizeException>>
implements PeregUpdateListCommandHandler<UpdateAffWorkplaceHistoryCommand> {
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
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<UpdateAffWorkplaceHistoryCommand>> context) {
		List<UpdateAffWorkplaceHistoryCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		List<String> sidErrorLst = new ArrayList<>();
		List<MyCustomizeException> errorExceptionLst = new ArrayList<>();
		List<AffWorkplaceHistoryItem> histItems = new ArrayList<>();
		List<AffWorkplaceHistoryIntermediate> affWorkplaceHistories = new ArrayList<>();
		Map<String, List<AffWorkplaceHistory>> existHistMap = new HashMap<>();
		// sidsPidsMap
		List<String> sids = command.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		UpdateAffWorkplaceHistoryCommand updateFirst = command.get(0);
		if(updateFirst != null) {
			Map<String, List<AffWorkplaceHistory>> existHistMaps = affWorkplaceHistoryRepository.getBySidsAndCid(cid, sids)
					.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
			existHistMap.putAll(existHistMaps);
		}

		command.stream().forEach(c -> {
			try {
				if (c.getStartDate() != null) {
					List<AffWorkplaceHistory> affWorkplaceHistoryLst = existHistMap.get(c.getEmployeeId());
					if (affWorkplaceHistoryLst != null) {
						AffWorkplaceHistory affWorkplaceHistory = affWorkplaceHistoryLst.get(0);

						Optional<DateHistoryItem> itemToBeUpdate = affWorkplaceHistory.getHistoryItems().stream()
								.filter(h -> h.identifier().equals(c.getHistoryId())).findFirst();
						if (!itemToBeUpdate.isPresent()) {
							sidErrorLst.add(c.getEmployeeId());
							return;
						} else {
							affWorkplaceHistory.changeSpan(itemToBeUpdate.get(), new DatePeriod(c.getStartDate(),
									c.getEndDate() != null ? c.getEndDate() : ConstantUtils.maxDate()));
							affWorkplaceHistories.add(
									new AffWorkplaceHistoryIntermediate(affWorkplaceHistory, itemToBeUpdate.get()));
						}
					} else {
						sidErrorLst.add(c.getEmployeeId());
						return;
					}
				}
				AffWorkplaceHistoryItem histItem = AffWorkplaceHistoryItem.createFromJavaType(c.getHistoryId(),
						c.getEmployeeId(), c.getWorkplaceId());
				histItems.add(histItem);
			} catch (BusinessException e) {
				MyCustomizeException ex = new MyCustomizeException(e.getMessageId(), Arrays.asList(c.getEmployeeId()), "期間");
				errorExceptionLst.add(ex);
			}

		});
		if(!affWorkplaceHistories.isEmpty()) {
			affWorkplaceHistoryService.updateAll(affWorkplaceHistories);
		}
		
		if(!histItems.isEmpty()) {
			affWorkplaceHistoryItemRepository.updateAll(histItems);
		}
		
		if (sidErrorLst.size() > 0) {
			errorExceptionLst.add(new MyCustomizeException("invalid AffWorkplaceHistory", sidErrorLst));
		}
		return errorExceptionLst;
	}

}
