package nts.uk.ctx.at.record.app.command.kdw.kdw006.l;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistory;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * Command: 履歴を更新する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.L：履歴の選択.メニュー別OCD.履歴を更新する
 * 
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateHistoryCommandHandler extends CommandHandler<AddHistoryCommand> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;

	@Override
	protected void handle(CommandHandlerContext<AddHistoryCommand> context) {
		AddHistoryCommand command = context.getCommand();
		List<DateHistoryItem> dateHistoryItems = new ArrayList<>();
		List<TaskSupInfoChoicesHistory> taskSupInfoChoicesHistory = taskSupInfoChoicesHistoryRepo
				.getAll(AppContexts.user().companyId());

		taskSupInfoChoicesHistory.forEach(f -> {
			if (f.getItemId() == Integer.parseInt(command.getItemId())) {
				dateHistoryItems.addAll(f.getDateHistoryItems());
			}
		});

		Optional<TaskSupInfoChoicesHistory> domain = taskSupInfoChoicesHistoryRepo.get(command.getHistoryId(),
				Integer.valueOf(command.getItemId()));

		if (!dateHistoryItems.isEmpty() && dateHistoryItems.size() > 1) {
			if (domain.isPresent()) {
				if (dateHistoryItems.get(1).start().after(command.getStartDate()) || dateHistoryItems.get(1).start().equals(command.getStartDate())) {
					throw new BusinessException("Msg_127");
				} else {
					List<DateHistoryItem> historysUpdateAfter = new ArrayList<>();
					List<DateHistoryItem> historysUpdate = new ArrayList<>();

					historysUpdateAfter.add(new DateHistoryItem(dateHistoryItems.get(1).identifier(),
							new DatePeriod(dateHistoryItems.get(1).start(), command.getStartDate().addDays(-1))));
					historysUpdate.add(new DateHistoryItem(command.getHistoryId(),
							new DatePeriod(command.getStartDate(), command.getEndDate())));

					TaskSupInfoChoicesHistory domainUpdateAfter = new TaskSupInfoChoicesHistory(
							Integer.parseInt(command.getItemId()), historysUpdateAfter);
					TaskSupInfoChoicesHistory domainUpdate = new TaskSupInfoChoicesHistory(
							Integer.parseInt(command.getItemId()), historysUpdate);

					this.taskSupInfoChoicesHistoryRepo.update(domainUpdateAfter);
					this.taskSupInfoChoicesHistoryRepo.update(domainUpdate);
				}
			}
		} else {
			if (domain.isPresent()) {
				List<DateHistoryItem> historysUpdate = new ArrayList<>();
				historysUpdate.add(new DateHistoryItem(command.getHistoryId(),
						new DatePeriod(command.getStartDate(), command.getEndDate())));
				TaskSupInfoChoicesHistory result = new TaskSupInfoChoicesHistory(domain.get().getItemId(),
						historysUpdate);
				this.taskSupInfoChoicesHistoryRepo.update(result);
			}
		}
	}
}
