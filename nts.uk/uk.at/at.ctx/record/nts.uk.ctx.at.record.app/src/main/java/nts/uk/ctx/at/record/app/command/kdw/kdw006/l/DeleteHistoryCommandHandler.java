package nts.uk.ctx.at.record.app.command.kdw.kdw006.l;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistory;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * Command: 履歴を削除する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW006_前準備.L：履歴の選択.メニュー別OCD.履歴を削除する
 * 
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DeleteHistoryCommandHandler extends CommandHandler<DeleteHistoryCommand> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskSupInfoChoicesHistoryRepo;

	@Override
	protected void handle(CommandHandlerContext<DeleteHistoryCommand> context) {
		DeleteHistoryCommand command = context.getCommand();
		List<DateHistoryItem> dateHistoryItems = new ArrayList<>();
		List<TaskSupInfoChoicesHistory> taskSupInfoChoicesHistory = taskSupInfoChoicesHistoryRepo
				.getAll(AppContexts.user().companyId());

		taskSupInfoChoicesHistory.forEach(f -> {
			if (f.getItemId() == Integer.parseInt(command.getItemId())) {
				dateHistoryItems.addAll(f.getDateHistoryItems());
			}
		});
		List<TaskSupInfoChoicesDetail> details = this.taskSupInfoChoicesHistoryRepo.get(command.getHistoryId());

		if (!details.isEmpty()) {
			throw new BusinessException("Msg_2306");
		}

		if (!dateHistoryItems.isEmpty()) {
			if (dateHistoryItems.size() <= 1) {
				this.taskSupInfoChoicesHistoryRepo.delete(command.getHistoryId());
			} else {
				List<DateHistoryItem> historysUpdate = new ArrayList<>();
				historysUpdate.add(new DateHistoryItem(dateHistoryItems.get(1).identifier(),
						new DatePeriod(dateHistoryItems.get(1).start(), GeneralDate.ymd(9999, 12, 31))));

				TaskSupInfoChoicesHistory domainAdd = new TaskSupInfoChoicesHistory(
						Integer.parseInt(command.getItemId()), historysUpdate);

				this.taskSupInfoChoicesHistoryRepo.update(domainAdd);
				this.taskSupInfoChoicesHistoryRepo.delete(command.getHistoryId());
			}
		}

		if (!details.isEmpty()) {
			throw new BusinessException("Msg_2306");
		}
	}
}
