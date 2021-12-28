package nts.uk.ctx.at.shared.ws.scherec.dailyattendanceitem;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.ControlOfAttendanceItemsCmd;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.UpdateControlOfAttendanceItemsCmdHandler;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.UpdateDailyAttendanceItemCommandHandler;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.ControlOfAttendanceItemsDto;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.ControlOfAttendanceItemsFinder;

@Path("at/shared/scherec/daily")
@Produces("application/json")
public class ControlOfDailyWS extends WebService {
	@Inject
	private UpdateControlOfAttendanceItemsCmdHandler uAttendanceItemsCmdHandler;

	@Inject
	private ControlOfAttendanceItemsFinder cOfAttendanceItemsFinder;
	
	@Inject
	private UpdateDailyAttendanceItemCommandHandler updateDailyAttendanceItemCommandHandler;

	@POST
	@Path("findById/{id}")
	public ControlOfAttendanceItemsDto getListDailyAttdItem(@PathParam("id") int id) {
		return cOfAttendanceItemsFinder.getControlOfAttendanceItem(id);
	}

	@POST
	@Path("update")
	public void update(ControlOfAttendanceItemsCmd command) {
		this.uAttendanceItemsCmdHandler.handle(command);
		this.updateDailyAttendanceItemCommandHandler.handle(command.getUpdateDailyAttendanceItemCommand());
	}
}
