package nts.uk.ctx.at.record.app.command.kdw.kdw006.k;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * 選択肢を削除する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業補足情報項目設定.App.選択肢を削除する
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DeleteTheChoiceCommandHandler extends CommandHandler<DeleteTheChoiceCommand> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskRepo;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteTheChoiceCommand> context) {
		DeleteTheChoiceCommand command = context.getCommand();
		
		this.taskRepo.delete(command.getHistoryId(), new ChoiceCode(command.getChoiceCode()));
		
	}

}
