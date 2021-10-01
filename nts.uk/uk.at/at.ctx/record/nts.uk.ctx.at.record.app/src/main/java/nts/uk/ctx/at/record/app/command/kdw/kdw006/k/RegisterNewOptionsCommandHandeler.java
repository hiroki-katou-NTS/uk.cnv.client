package nts.uk.ctx.at.record.app.command.kdw.kdw006.k;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.ChoiceName;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.ExternalCode;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting.TaskSupInfoChoicesHistoryRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * Command: 選択肢を新規登録する
 * @author chungnt
 *
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterNewOptionsCommandHandeler  extends CommandHandler<RegisterNewOptionsCommand> {

	@Inject
	private TaskSupInfoChoicesHistoryRepository taskRepo;
	
	@Override
	protected void handle(CommandHandlerContext<RegisterNewOptionsCommand> context) {
		RegisterNewOptionsCommand command = context.getCommand();
		
		
		List<TaskSupInfoChoicesDetail> choicesDetails = this.taskRepo.get(command.getHistoryId());
		
		if (!choicesDetails.isEmpty()) {
			throw new BusinessException("Msg_3");
		}
		
		TaskSupInfoChoicesDetail domain = new TaskSupInfoChoicesDetail(command.getHistoryId(),
				command.getItemId(), 
				new ChoiceCode(command.getChoiceCode()), 
				new ChoiceName(command.getOptionName()), 
				command.getEternalCodeOfChoice() == null ? Optional.empty() : Optional.of(new ExternalCode(String.valueOf(command.getEternalCodeOfChoice()))));
		
		this.taskRepo.insert(domain);
	}

}
