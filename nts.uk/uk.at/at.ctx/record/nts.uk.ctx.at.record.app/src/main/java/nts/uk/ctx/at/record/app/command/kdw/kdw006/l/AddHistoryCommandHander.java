package nts.uk.ctx.at.record.app.command.kdw.kdw006.l;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistory;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * Command: 履歴を追加する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.L：履歴の選択.メニュー別OCD.履歴を追加する
 * 
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AddHistoryCommandHander extends CommandHandlerWithResult<AddHistoryCommand, String> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;

	@Override
	protected String handle(CommandHandlerContext<AddHistoryCommand> context) {
		AddHistoryCommand command = context.getCommand();
		List<DateHistoryItem> dateHistoryItems = new ArrayList<>();
		List<TaskSupInfoChoicesHistory> taskSupInfoChoicesHistory = taskSupInfoChoicesHistoryRepo
				.getAll(AppContexts.user().companyId());

		taskSupInfoChoicesHistory.forEach(f -> {
			if (f.getItemId() == Integer.parseInt(command.getItemId())) {
				dateHistoryItems.addAll(f.getDateHistoryItems());
			}
		});

		if (!dateHistoryItems.isEmpty()) {
			if (dateHistoryItems.get(0).start().after(command.getStartDate()) || dateHistoryItems.get(0).start().equals(command.getStartDate())) {
				throw new BusinessException("Msg_102");
			} else {
				List<DateHistoryItem> historysUpdate = new ArrayList<>();
				List<DateHistoryItem> historysAdd = new ArrayList<>();
				
				historysUpdate.add(new DateHistoryItem(dateHistoryItems.get(0).identifier(),
						new DatePeriod(dateHistoryItems.get(0).start(), command.getStartDate().addDays(-1))));
				historysAdd.add(new DateHistoryItem(command.getHistoryId(),
						new DatePeriod(command.getStartDate(), command.getEndDate())));

				TaskSupInfoChoicesHistory domainUpdate = new TaskSupInfoChoicesHistory(
						Integer.parseInt(command.getItemId()), historysUpdate);
				TaskSupInfoChoicesHistory domainAdd = new TaskSupInfoChoicesHistory(
						Integer.parseInt(command.getItemId()), historysAdd);

				this.taskSupInfoChoicesHistoryRepo.update(domainUpdate);
				this.taskSupInfoChoicesHistoryRepo.insert(domainAdd);
			}
		} else {
			List<DateHistoryItem> historys = new ArrayList<>();
			historys.add(new DateHistoryItem(command.getHistoryId(),
					new DatePeriod(command.getStartDate(), command.getEndDate())));

			TaskSupInfoChoicesHistory domain = new TaskSupInfoChoicesHistory(Integer.parseInt(command.getItemId()),
					historys);

			this.taskSupInfoChoicesHistoryRepo.insert(domain);
		}
		return null;
	}
}
