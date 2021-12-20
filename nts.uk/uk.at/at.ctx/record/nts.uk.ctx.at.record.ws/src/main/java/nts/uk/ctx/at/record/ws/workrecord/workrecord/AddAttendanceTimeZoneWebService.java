package nts.uk.ctx.at.record.ws.workrecord.workrecord;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.record.app.command.workrecord.workrecord.AddAttendanceTimeZoneCommand;
import nts.uk.ctx.at.record.app.command.workrecord.workrecord.RegisterWorkManHoursCommandHandler;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 
 * @author tutt
 *
 */
@Path("at/record/workrecord/workrecord/")
@Produces("application/json")
public class AddAttendanceTimeZoneWebService {
	
	@Inject
	private RegisterWorkManHoursCommandHandler handler;
	
	@POST
	@Path("register") 
	public List<IntegrationOfDaily> registerAttendanceTimeZone(AddAttendanceTimeZoneCommand command) {
		return handler.handle(command);
	}
}
