package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ChangeConfirmedService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;

/**
 * 確定状態を変更する
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定.App
 * @author hoangnd
 *
 */
@Stateless
@Transactional
public class ChangeConfirmedStateCommandHandler extends CommandHandler<List<ChangeConfirmedStateCommand>>{

	@Inject
	private WorkScheduleRepository workScheduleRepository;
	
	@Override
	protected void handle(CommandHandlerContext<List<ChangeConfirmedStateCommand>> context) {
		List<ChangeConfirmedStateCommand> commands = context.getCommand();
		
		for (ChangeConfirmedStateCommand command : commands) {
			
			// 1 変更する(@Require, 社員ID, 年月日, boolean)
			AtomTask at = ChangeConfirmedService.change(
					new Require(workScheduleRepository),
					command.getSid(),
					command.getYmd(),
					command.isConfirmed);
			// 2
			transaction.execute(() -> {
				at.run();
			});
		}
		
	}

	@AllArgsConstructor
	public class Require implements ChangeConfirmedService.Require {
		
		@Inject
		private WorkScheduleRepository workScheduleRepository;

		@Override
		public Optional<WorkSchedule> getWorkSchedule(String sid, GeneralDate ymd) {
			return workScheduleRepository.get(sid, ymd);
		}

		@Override
		public void update(WorkSchedule workSchedule) {
			workScheduleRepository.updateConfirmedState(workSchedule);
			
		}
		
	}
	
	
}
