package nts.uk.ctx.at.record.app.command.dailyperform.ouen;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;

@Stateless
public class OuenWorkTimeOfDailyCommandUpdateHandler extends CommandFacade<OuenWorkTimeOfDailyCommand> {

	@Inject
	private DailyRecordAdUpService adUpRepo;

	@Override
	protected void handle(CommandHandlerContext<OuenWorkTimeOfDailyCommand> context) {
		adUpRepo.adUpOuenWorkTime(context.getCommand().getData());
	}
}
