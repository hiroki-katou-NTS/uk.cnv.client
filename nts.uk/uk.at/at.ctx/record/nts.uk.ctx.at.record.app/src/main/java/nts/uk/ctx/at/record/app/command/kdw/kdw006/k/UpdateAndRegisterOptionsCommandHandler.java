package nts.uk.ctx.at.record.app.command.kdw.kdw006.k;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.ChoiceName;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.ExternalCode;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * Command: 選択肢を更新登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業補足情報項目設定.App.選択肢を更新登録する
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UpdateAndRegisterOptionsCommandHandler extends CommandHandler<RegisterNewOptionsCommand> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskRepo;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterNewOptionsCommand> context) {
		RegisterNewOptionsCommand command = context.getCommand();
		
		Optional<TaskSupInfoChoicesDetail> optdomain = this.taskRepo.get(command.getHistoryId(), command.getItemId(), new ChoiceCode(command.getChoiceCode()));
		
		if (optdomain.isPresent()) {
			TaskSupInfoChoicesDetail domain = new TaskSupInfoChoicesDetail(command.getHistoryId(), command.getItemId(),
					new ChoiceCode(command.getChoiceCode()), new ChoiceName(command.getOptionName()),
					command.getEternalCodeOfChoice().equals("") ? Optional.empty()
							: Optional.of(new ExternalCode(command.getEternalCodeOfChoice())));
			this.taskRepo.update(domain);
		}
	}

}
