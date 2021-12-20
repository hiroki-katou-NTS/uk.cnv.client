package nts.uk.ctx.at.record.app.command.workrecord.workmanagement;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.CheckWorkPerformanceService;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResults;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResultsRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.App.作業実績を確認する
 * 
 * @author tutt
 *
 */
@Stateless
public class AddWorkRecodConfirmationCommandHandler
		extends CommandHandler<AddWorkRecodConfirmationCommand> {

	@Inject
	private ConfirmationWorkResultsRepository confirmationWorkResultsRepository;

	@Override
	protected void handle(CommandHandlerContext<AddWorkRecodConfirmationCommand> context) {
		RequireImpl require = new RequireImpl(confirmationWorkResultsRepository);

		AddWorkRecodConfirmationCommand command = context.getCommand();

		AtomTask atom = CheckWorkPerformanceService.check(require, command.getEmployeeId(), command.getDate(),
				command.getConfirmerId());
		
		transaction.execute(() -> {
			// 2:persist
			atom.run();
		});
	}

	@AllArgsConstructor
	public class RequireImpl implements CheckWorkPerformanceService.Require {

		private ConfirmationWorkResultsRepository confirmationWorkResultsRepo;

		public Optional<ConfirmationWorkResults> get(String targetSid, GeneralDate targetYMD) {
			return confirmationWorkResultsRepo.get(targetSid, targetYMD);
		}

		@Override
		public void insert(ConfirmationWorkResults confirmationWorkResults) {
			confirmationWorkResultsRepo.insert(confirmationWorkResults);
		}

		@Override
		public void update(ConfirmationWorkResults confirmationWorkResults) {
			confirmationWorkResultsRepo.update(confirmationWorkResults);
		}

	}
}
