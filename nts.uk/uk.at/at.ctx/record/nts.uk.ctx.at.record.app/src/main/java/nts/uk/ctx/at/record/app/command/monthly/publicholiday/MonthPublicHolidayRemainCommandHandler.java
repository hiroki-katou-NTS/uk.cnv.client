package nts.uk.ctx.at.record.app.command.monthly.publicholiday;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.app.util.attendanceitem.CommandFacade;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonthRepository;
@Stateless
public class MonthPublicHolidayRemainCommandHandler extends CommandFacade<MonthPublicHolidayRemainCommand> {

	@Inject
	private PublicHolidayRemNumEachMonthRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<MonthPublicHolidayRemainCommand> context) {
		if(context.getCommand().getData().isHaveData()) {
			repo.persistAndUpdate(context.getCommand().toDomain());
		
		}
	}
	

}
