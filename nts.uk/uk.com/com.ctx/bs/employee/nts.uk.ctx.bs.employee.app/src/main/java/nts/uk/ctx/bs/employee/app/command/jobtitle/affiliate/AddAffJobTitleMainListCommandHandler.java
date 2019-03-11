package nts.uk.ctx.bs.employee.app.command.jobtitle.affiliate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistory;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItem;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.affiliate.AffJobTitleHistoryService;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;
@Stateless
public class AddAffJobTitleMainListCommandHandler extends CommandHandlerWithResult<List<AddAffJobTitleMainCommand>, List<PeregAddCommandResult>>
implements PeregAddListCommandHandler<AddAffJobTitleMainCommand>{
	@Inject
	private AffJobTitleHistoryRepository affJobTitleHistoryRepository;
	
	@Inject
	private AffJobTitleHistoryItemRepository affJobTitleHistoryItemRepository;
	
	@Inject 
	private AffJobTitleHistoryService affJobTitleHistoryService;
	@Override
	public String targetCategoryCd() {
		return "CS00016";
	}

	@Override
	public Class<?> commandClass() {
		return AddAffJobTitleMainCommand.class;
	}

	@Override
	protected List<PeregAddCommandResult> handle(CommandHandlerContext<List<AddAffJobTitleMainCommand>> context) {
		List<AddAffJobTitleMainCommand> command = context.getCommand();
		String cid = AppContexts.user().companyId();
		List<String> sids = command.parallelStream().map(c -> c.getSid()).collect(Collectors.toList());
		List<AffJobTitleHistoryItem> histItems = new ArrayList<>();
		List<AffJobTitleHistory> affJobTitleHistoryLst = new ArrayList<>();
		List<PeregAddCommandResult> result = new ArrayList<>();
		Map<String, List<AffJobTitleHistory>> existHistMap = affJobTitleHistoryRepository.getListBySids(cid, sids)
				.parallelStream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));

		command.parallelStream().forEach(c -> {
			String histId = IdentifierUtil.randomUniqueId();
			List<AffJobTitleHistory> affJobTitleHistory = existHistMap.get(c.getSid());
			AffJobTitleHistory itemtoBeAdded = new AffJobTitleHistory(cid, c.getSid(), new ArrayList<>());
			DateHistoryItem dateItem = new DateHistoryItem(histId,
					new DatePeriod(c.getStartDate() != null ? c.getStartDate() : ConstantUtils.minDate(),
							c.getEndDate() != null ? c.getEndDate() : ConstantUtils.maxDate()));
			if (affJobTitleHistory != null) {
				itemtoBeAdded = affJobTitleHistory.get(0);
			}
			itemtoBeAdded.add(dateItem);
			affJobTitleHistoryLst.add(itemtoBeAdded);
			AffJobTitleHistoryItem histItem = AffJobTitleHistoryItem.createFromJavaType(histId, c.getSid(),
					c.getJobTitleId(), c.getNote());
			histItems.add(histItem);
			result.add(new PeregAddCommandResult(histId));
		});
		if (!affJobTitleHistoryLst.isEmpty()) {
			affJobTitleHistoryService.addAll(affJobTitleHistoryLst);
		}
		if (!histItems.isEmpty()) {
			affJobTitleHistoryItemRepository.addAll(histItems);
		}

		return result;
	}

}
