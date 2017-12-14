package nts.uk.ctx.bs.employee.app.command.employment.history;

import java.util.ArrayList;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistory;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryService;
import nts.uk.ctx.bs.person.dom.person.common.ConstantUtils;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryItem;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.employment.history.EmploymentHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddEmploymentHistoryCommandHandler
		extends CommandHandlerWithResult<AddEmploymentHistoryCommand, PeregAddCommandResult>
		implements PeregAddCommandHandler<AddEmploymentHistoryCommand> {

	@Inject
	private EmploymentHistoryRepository employmentHistoryRepository;
	@Inject
	private EmploymentHistoryItemRepository employmentHistoryItemRepository;

	@Inject
	private EmploymentHistoryService employmentHistoryService;

	@Override
	public String targetCategoryCd() {
		return "CS00014";
	}

	@Override
	public Class<?> commandClass() {
		return AddEmploymentHistoryCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddEmploymentHistoryCommand> context) {
		val command = context.getCommand();
		String newHistID = IdentifierUtil.randomUniqueId();
		String companyId = AppContexts.user().companyId();
		DateHistoryItem dateItem = new DateHistoryItem(newHistID,
				new DatePeriod(command.getStartDate(), command.getEndDate()!= null? command.getEndDate():  GeneralDate.fromString(ConstantUtils.MAX_DATE, ConstantUtils.FORMAT_DATE_YYYYMMDD)));

		Optional<EmploymentHistory> histBySid = employmentHistoryRepository.getByEmployeeId(command.getEmployeeId());

		EmploymentHistory itemtoBeAdded = new EmploymentHistory(companyId, command.getEmployeeId(), new ArrayList<>());
		if (histBySid.isPresent()) {
			itemtoBeAdded = histBySid.get();
		}

		itemtoBeAdded.add(dateItem);

		employmentHistoryService.add(itemtoBeAdded);

		EmploymentHistoryItem histItem = EmploymentHistoryItem.createFromJavaType(newHistID, command.getEmployeeId(),
				command.getEmploymentCode(),
				command.getSalarySegment() != null ? command.getSalarySegment().intValue() : 0);
		employmentHistoryItemRepository.add(histItem);

		return new PeregAddCommandResult(newHistID);
	}

}
