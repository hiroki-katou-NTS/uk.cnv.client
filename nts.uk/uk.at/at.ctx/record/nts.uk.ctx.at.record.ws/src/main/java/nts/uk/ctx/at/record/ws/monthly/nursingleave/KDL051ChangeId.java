package nts.uk.ctx.at.record.ws.monthly.nursingleave;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.record.app.find.monthly.nursingleave.ChildCareNusingLeaveFinder;
import nts.uk.ctx.at.record.app.find.monthly.nursingleave.dto.KDL051ProcessDto;

@Path("at/record/monthly/nursingleave/")
@Produces("application/json")
public class KDL051ChangeId {
	/** The finder. */
	@Inject
	ChildCareNusingLeaveFinder childCareNusingLeaveFinder;
	
	@Path("changeId/{employeeId}")
	@POST
	public KDL051ProcessDto getChildCareNusingLeave(@PathParam("employeeId") String employeeId) {
		return this.childCareNusingLeaveFinder.changeEmployee(employeeId);
	}
	
	@Path("changeIdKdl052/{employeeId}")
	@POST
	public KDL051ProcessDto getChildCareNusingLeaveKDL052(@PathParam("employeeId") String employeeId) {
		return this.childCareNusingLeaveFinder.changeEmployeeKDL052(employeeId);
	}
	

}
