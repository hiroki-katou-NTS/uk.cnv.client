package nts.uk.screen.at.app.command.kdw.kdw003.g;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.CopyTaskInitialSelHisService;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHistRepo;

/**
 * 履歴を複写する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業初期選択設定.App.履歴を複写する
 * @author quytb
 *
 */
@Stateless
public class CopyHistCommandHandler extends CommandHandler<HistCommandCopy> {
	@Inject
	TaskInitialSelHistRepo taskInitialSelHistRepo;

	@Override
	protected void handle(CommandHandlerContext<HistCommandCopy> context) {		
		HistCommandCopy command = context.getCommand();
		CopyTaskInitialSelHistImpl require = new CopyTaskInitialSelHistImpl(taskInitialSelHistRepo);
		command.getEmpIdDes().stream().forEach(empId -> {
			AtomTask copy = CopyTaskInitialSelHisService.copy(require, command.getEmpIdSource(), empId);
			transaction.execute(() -> {
				copy.run();
			}); 
		});				
	}
	
	@AllArgsConstructor
	private static class CopyTaskInitialSelHistImpl implements CopyTaskInitialSelHisService.Require {
		
		private TaskInitialSelHistRepo taskInitialSelHistRepo;

		@Override
		public Optional<TaskInitialSelHist> getById(String empId) {
			return taskInitialSelHistRepo.getById(empId);
		}

		@Override
		public void insert(TaskInitialSelHist taskInitialSelHist) {
			taskInitialSelHistRepo.insert(taskInitialSelHist);
		}

		@Override
		public void delete(String empId) {
			taskInitialSelHistRepo.delete(empId);			
		}
	}
}
