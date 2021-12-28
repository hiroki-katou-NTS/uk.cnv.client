package nts.uk.screen.at.app.command.kdw.kdw003.g;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskCode;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSel;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskItem;

/**
 * 履歴を変更する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業初期選択設定.App.履歴を変更する
 * @author quytb
 *
 */
@Stateless
public class UpdateHistCommandHandler extends CommandHandler<HistCommandUpdate>{
	@Inject
	TaskInitialSelHistRepo taskInitialSelHistRepo;

	@Override
	protected void handle(CommandHandlerContext<HistCommandUpdate> context) {
		HistCommandUpdate command = context.getCommand();
		String employeeId = command.getEmployeeId();
		
		TaskItem taskItem = new TaskItem(Optional.ofNullable(new TaskCode(command.getLstTask().get(0))),
				Optional.ofNullable(new TaskCode(command.getLstTask().get(1))),
				Optional.ofNullable(new TaskCode(command.getLstTask().get(2))),
				Optional.ofNullable(new TaskCode(command.getLstTask().get(3))),
				Optional.ofNullable(new TaskCode(command.getLstTask().get(4))));

//		TaskInitialSel taskInitialSel = new TaskInitialSel(employeeId, command.toOldDatePeriod(), taskItem);
		Optional<TaskInitialSelHist> optional = this.taskInitialSelHistRepo.getById(employeeId);
		if (!optional.isPresent()) {
			return;
		}

		TaskInitialSelHist taskInitialSelHist = optional.get();
		taskInitialSelHist.changeHistory(taskItem, command.toDatePeriod(), command.toCurrentStartDate());

		this.taskInitialSelHistRepo.update(taskInitialSelHist);
	}
}
