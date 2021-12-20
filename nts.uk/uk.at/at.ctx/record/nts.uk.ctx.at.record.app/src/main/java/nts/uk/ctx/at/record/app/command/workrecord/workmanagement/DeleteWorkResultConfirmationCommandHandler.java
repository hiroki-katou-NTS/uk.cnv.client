package nts.uk.ctx.at.record.app.command.workrecord.workmanagement;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.CancelConfirmationWorkResultsService;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResults;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResultsRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.App.作業実績の確認を解除する
 * @author tutt
 *
 */
@Stateless
public class DeleteWorkResultConfirmationCommandHandler extends CommandHandler<DeleteWorkResultConfirmCommand> {

	@Inject
	private ConfirmationWorkResultsRepository confirmationWorkResultsRepository;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteWorkResultConfirmCommand> context) {
		RequireImpl require = new RequireImpl(confirmationWorkResultsRepository);

		DeleteWorkResultConfirmCommand command = context.getCommand();
		
		//1: 解除する(require, 対象者, 対象日, 確認者):AtomTask
		Optional<AtomTask> atomOpt = CancelConfirmationWorkResultsService.check(require, command.getEmployeeId(), command.getDate(), command.getConfirmerId());
		
		if (atomOpt.isPresent()) {
			transaction.execute(() -> {
				atomOpt.get().run();
			});
		}
	}
	
	@AllArgsConstructor
	public class RequireImpl implements CancelConfirmationWorkResultsService.Require {

		private ConfirmationWorkResultsRepository confirmationWorkResultsRepo;

		@Override
		public Optional<ConfirmationWorkResults> get(String targetSid, GeneralDate targetYMD) {
			return confirmationWorkResultsRepo.get(targetSid, targetYMD);
		}

		@Override
		public void delete(ConfirmationWorkResults confirmationWorkResults) {
			confirmationWorkResultsRepo.delete(confirmationWorkResults);
		}

		@Override
		public void update(ConfirmationWorkResults confirmationWorkResults) {
			confirmationWorkResultsRepo.update(confirmationWorkResults);
		}
	}

}
