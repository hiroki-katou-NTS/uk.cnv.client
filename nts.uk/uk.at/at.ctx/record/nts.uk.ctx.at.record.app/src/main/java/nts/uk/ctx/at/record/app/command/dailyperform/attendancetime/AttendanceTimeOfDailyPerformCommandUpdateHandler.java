package nts.uk.ctx.at.record.app.command.dailyperform.attendancetime;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;

@Stateless
public class AttendanceTimeOfDailyPerformCommandUpdateHandler
		extends CommandFacade<AttendanceTimeOfDailyPerformCommand> {

	@Inject
	private DailyRecordAdUpService adUpRepo;

	@Override
	protected void handle(CommandHandlerContext<AttendanceTimeOfDailyPerformCommand> context) {
		AttendanceTimeOfDailyPerformCommand command = context.getCommand();
		if (command.getData().isPresent()) {
			adUpRepo.adUpAttendanceTime(command.toDomain());
		}
	}

}
