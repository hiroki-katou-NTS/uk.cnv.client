package nts.uk.ctx.at.shared.ws.scherec.monthlyattditem;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.ControlOfMonthlyCmd;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.UpdateControlOfMonthlyCmdHandler;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.UpdateMonthlyAttendanceItemCommandHandler;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyDto;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyFinder;

@Path("at/shared/scherec/monthly")
@Produces("application/json")
public class ControlOfMonthlyWs extends WebService {
	
	@Inject
	private UpdateControlOfMonthlyCmdHandler handler;
	
	@Inject
	private UpdateMonthlyAttendanceItemCommandHandler updateMonthlyAttendanceItemCommandHandler;

	@Inject
	private ControlOfMonthlyFinder finder;

	@POST
	@Path("findById/{id}")
	public ControlOfMonthlyDto getListDailyAttdItem(@PathParam("id") int id) {
		return finder.getControlOfAttendanceItem(id);
	}

	@POST
	@Path("update")
	public void update(ControlOfMonthlyCmd command) {
		this.handler.handle(command);
		this.updateMonthlyAttendanceItemCommandHandler.handle(command.getUpdateMonthlyAttendanceItemCommand());
	}
}
