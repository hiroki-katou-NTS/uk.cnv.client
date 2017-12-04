package nts.uk.ctx.at.auth.ws.wplmanagementauthority;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.CreateWorkPlaceAuthorityCmd;
import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.CreateWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.DeleteWorkPlaceAuthorityCmd;
import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.DeleteWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.UpdateWorkPlaceAuthorityCmd;
import nts.uk.ctx.at.auth.app.command.wplmanagementauthority.UpdateWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.at.auth.app.find.wplmanagementauthority.WorkPlaceAuthorityFinder;
import nts.uk.ctx.at.auth.app.find.wplmanagementauthority.dto.InputWorkPlaceAuthority;
import nts.uk.ctx.at.auth.app.find.wplmanagementauthority.dto.WorkPlaceAuthorityDto;

@Path("at/auth/workplace/wplmanagementauthority/WorkPlaceAuthority")
@Produces(MediaType.APPLICATION_JSON)
public class WorkPlaceAuthorityWebservice {
	
	@Inject
	private  WorkPlaceAuthorityFinder finder;
	
	@Inject
	private  CreateWorkPlaceAuthorityCmdHandler addWorkPlaceAuthority;
	
	@Inject
	private  UpdateWorkPlaceAuthorityCmdHandler updateWorkPlaceAuthority;
	
	@Inject
	private  DeleteWorkPlaceAuthorityCmdHandler deleteWorkPlaceAuthority;
	
	/** finder */
	//get all WorkPlaceAuthority
	@POST
	@Path("getallWorkplaceauthority")
	public List<WorkPlaceAuthorityDto> getAllWorkPlaceAuthority(){
		 List<WorkPlaceAuthorityDto> data = this.finder.getAllWorkPlaceAuthority();
		 return data;
	}
	//get all WorkPlaceAuthority by role id
	@POST
	@Path("getallWorkplaceauthoritybyid/{roleId}")
	public List<WorkPlaceAuthorityDto> getAllWorkPlaceAuthorityById(@PathParam("roleId") String roleId ){
		 List<WorkPlaceAuthorityDto> data = this.finder.getAllWorkPlaceAuthorityByRoleId(roleId);
		 return data;
	}
	//get WorkPlaceAuthority by id
	@POST
	@Path("getWorkplaceauthoritybyid")
	public WorkPlaceAuthorityDto getWorkPlaceAuthorityById(InputWorkPlaceAuthority inputWorkPlaceAuthority ){
		 WorkPlaceAuthorityDto data = this.finder.getWorkPlaceAuthorityById(inputWorkPlaceAuthority);
		 return data;
	}
	/** handler*/
	
	//get WorkPlaceAuthority by id
	@POST
	@Path("addworkplaceauthority")
	public void addWorkPlaceAuthority(CreateWorkPlaceAuthorityCmd command ){
		 this.addWorkPlaceAuthority.handle(command);
	}
	
	//get WorkPlaceAuthority by id
	@POST
	@Path("updateworkplaceauthority")
	public void updateWorkPlaceAuthority(UpdateWorkPlaceAuthorityCmd command ){
		 this.updateWorkPlaceAuthority.handle(command);
	}
		
	//get WorkPlaceAuthority by id
	@POST
	@Path("deleteworkplaceauthority")
	public void deleteWorkPlaceAuthority(DeleteWorkPlaceAuthorityCmd command ){
		 this.deleteWorkPlaceAuthority.handle(command);
	}
	
}
