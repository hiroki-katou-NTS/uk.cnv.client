package nts.uk.ctx.workflow.ws.approvermanagement.setting;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.ctx.workflow.app.command.approvermanagement.setting.JobAssignSettingCommand;
import nts.uk.ctx.workflow.app.command.approvermanagement.setting.UpdateJobAssignSettingCommandHandler;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.JobAssignSettingDto;
import nts.uk.ctx.workflow.app.find.approvermanagement.setting.JobAssignSettingFinder;

@Path("job/assign/setting")
@Produces("application/json")
public class JobAssignSettingWebService {
	
	@Inject 
	private JobAssignSettingFinder jobFinder;
	
	@Inject
	private UpdateJobAssignSettingCommandHandler update;
	
	@POST
	@Path("getjob")
	public JobAssignSettingDto getJob(){
		return this.jobFinder.findApp();
	}
	
	@POST
	@Path("update")
	public void updateJob(JobAssignSettingCommand command){
		this.update.handle(command);
	}
}
