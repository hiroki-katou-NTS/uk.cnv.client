package nts.uk.ctx.at.shared.ws.scherec.monthlyattditem;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.MonthlyItemControlByAuthCmd;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.MonthlyItemControlByAuthCopyCmd;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.UpdateMonthlyItemControlByAuthCmdHandler;
import nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem.CopyMonthlyItemControlByAuthCmdHandler;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.MonthlyItemControlByAuthDto;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.MonthlyItemControlByAuthFinder;

@Path("at/shared/scherec/monthlyattditem/auth")
@Produces("application/json")
public class MonthlyItemControlByAuthorityWS extends WebService {
	
	@Inject
	private MonthlyItemControlByAuthFinder finder;
	
	@Inject
	private UpdateMonthlyItemControlByAuthCmdHandler monthlyHandler;
	
	@Inject
	private CopyMonthlyItemControlByAuthCmdHandler copyMonthlyHandler;
	
	@POST
	@Path("getmonthlyattd/{roleid}")
	public MonthlyItemControlByAuthDto getMonthlyAttdItemByRoleID(@PathParam("roleid") String roleID) {
		MonthlyItemControlByAuthDto data = finder.getMonthlyItemControlByRoleID(roleID);
		return data;
	}
	
	@POST
	@Path("updatemonthlyattd")
	public void updateMonthlyAttd(MonthlyItemControlByAuthCmd command) {
		this.monthlyHandler.handle(command);
	}
	
	@POST
	@Path("copymonthlyattd")
	public void copyMonthlyAttd(MonthlyItemControlByAuthCopyCmd command) {
		this.copyMonthlyHandler.handle(command);
	}
	
	@POST
	@Path("getmonthlyrolesbycid")
	public List<String> getMonthlytRolesByCid() {
		return this.finder.getMonthlytRolesByCid();
	}

}
