package nts.uk.ctx.at.shared.ws.scherec.dailyattendanceitem;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.CopyDailyItemControlByAuthCmdHandler;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.DailyAttendanceItemAuthorityCmd;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.DailyAttendanceItemAuthorityCmdHandler;
import nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem.DailyItemControlByAuthCopyCmd;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.DailyAttendanceItemAuthorityDto;
import nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem.DailyAttendanceItemAuthorityFinder;

@Path("at/shared/scherec/dailyattditem/auth")
@Produces("application/json")
public class DailyAttendanceItemAuthorityWS extends WebService {

	@Inject
	private DailyAttendanceItemAuthorityFinder finder;
	
	@Inject
	private DailyAttendanceItemAuthorityCmdHandler dailyAttdHandler;
	
	@Inject
	private CopyDailyItemControlByAuthCmdHandler copyDailylyHandler;
	
	
	@POST
	@Path("getdailyattd/{roleid}")
	public DailyAttendanceItemAuthorityDto getDailyAttdItemByRoleID(@PathParam("roleid") String roleID) {
		DailyAttendanceItemAuthorityDto data = finder.getDailyAttdItemByRoleID(roleID);
		return data;
	}
	
	@POST
	@Path("updatedailyattd")
	public void updateDailyAttd(DailyAttendanceItemAuthorityCmd command) {
		this.dailyAttdHandler.handle(command);
	}
	
	@POST
	@Path("getdailyrolesbycid")
	public List<String> getDailytRolesByCid() {
		return this.finder.getDailytRolesByCid();
	}
	
	@POST
	@Path("copydailyattd")
	public void copyMonthlyAttd(DailyItemControlByAuthCopyCmd command) {
		this.copyDailylyHandler.handle(command);
	}

}
