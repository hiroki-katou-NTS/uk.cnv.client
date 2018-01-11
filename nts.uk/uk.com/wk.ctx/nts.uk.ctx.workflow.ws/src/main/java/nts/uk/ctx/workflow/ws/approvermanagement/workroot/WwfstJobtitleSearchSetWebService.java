package nts.uk.ctx.workflow.ws.approvermanagement.workroot;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.workflow.app.find.approvermanagement.workroot.JobtitleSearchSetDto;
import nts.uk.ctx.workflow.app.find.approvermanagement.workroot.JobtitleSearchSetFinder;

@Path("workflow/jobtitlesearchset/job")
@Produces("application/json")
public class WwfstJobtitleSearchSetWebService extends WebService{
	@Inject
	private JobtitleSearchSetFinder jobFinder;  
	
	@POST
	@Path("getbyId/{jobtitleId}")
	public JobtitleSearchSetDto getAllByCom(@PathParam("jobtitleId") String jobtitleId) {
		return this.jobFinder.getById(jobtitleId);
	}
}
