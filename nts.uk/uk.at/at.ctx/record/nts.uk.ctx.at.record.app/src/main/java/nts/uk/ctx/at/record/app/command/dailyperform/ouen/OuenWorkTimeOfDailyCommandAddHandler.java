package nts.uk.ctx.at.record.app.command.dailyperform.ouen;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;

@Stateless
public class OuenWorkTimeOfDailyCommandAddHandler extends CommandFacade<OuenWorkTimeOfDailyCommand> {
	
	@Inject
	private OuenWorkTimeOfDailyRepo repo;

	@Override
	protected void handle(CommandHandlerContext<OuenWorkTimeOfDailyCommand> context) {
		context.getCommand().getData().ifPresent(d -> {
			repo.insert(d);
		});
	}
}
