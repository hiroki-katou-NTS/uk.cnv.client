package nts.uk.ctx.at.record.ws.workrecord.attendanceitemprepare;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.shr.com.context.AppContexts;

@Path("at/record/workrecord/attendanceitemprepare/")
@Produces("application/json")
public class AttendanceItemPrepareWebService {

	@POST 
	@Path("start")
	public boolean start() {
		boolean ootsuka = AppContexts.optionLicense().customize().ootsuka();
		
		return ootsuka;
	}
}
