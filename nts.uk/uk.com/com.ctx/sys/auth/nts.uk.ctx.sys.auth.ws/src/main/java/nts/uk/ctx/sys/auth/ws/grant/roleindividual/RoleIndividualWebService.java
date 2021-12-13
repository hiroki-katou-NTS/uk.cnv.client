package nts.uk.ctx.sys.auth.ws.grant.roleindividual;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.jobtitle.AffJobTitleBasicExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.EmployeeJobHistExport;
import nts.uk.ctx.bs.employee.pub.jobtitle.affiliate.JobTitleHistoryExport;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.CreateRoleIndividualGrantCommand;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.CreateRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.CreateRoleIndividualGrantCommandResult;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.CreateSysRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.DeleteRoleIndividualGrantCommand;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.DeleteRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.DeleteSysRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.RoleIndividualCommand;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.UpdateRoleIndividualGrantCommand;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.UpdateRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.command.grant.roleindividual.UpdateSysRoleIndividualGrantCommandHandler;
import nts.uk.ctx.sys.auth.app.find.company.CompanyDto;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.Cas013aDto;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.RoleIndividualDto;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.RoleIndividualFinder;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.*;
import nts.uk.ctx.sys.auth.app.find.person.role.PersonInformationRoleFinder;
import nts.uk.ctx.sys.auth.app.find.person.role.dto.RoleDto;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceImport;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRole;
import nts.uk.shr.com.context.AppContexts;

@Path("ctx/sys/auth/grant/roleindividual")
@Produces("application/json")
public class RoleIndividualWebService extends WebService {

	@Inject
	private RoleIndividualFinder roleIndividualFinder;

	@Inject
	private CreateSysRoleIndividualGrantCommandHandler createHandler;
	
	@Inject
	private CreateRoleIndividualGrantCommandHandler createRoleGrantHandler;

	@Inject
	UpdateRoleIndividualGrantCommandHandler updateRoleGrant;
	
	@Inject
	private UpdateSysRoleIndividualGrantCommandHandler updateHandler;
	
	@Inject
	private DeleteSysRoleIndividualGrantCommandHandler deleteHandler;
	
	@Inject
	private DeleteRoleIndividualGrantCommandHandler deleteRoleGrant;
	
	@Inject
	private PersonInformationRoleFinder personInforRoleFinder;

	@POST
	@Path("findall")
	public RoleIndividualDto getAll(RoleIndividualCommand command) {
		return this.roleIndividualFinder.findByCompanyAndRoleType(command.getSelectedCompany(), command.getSelectedRoleType());
	}

	@POST
	@Path("getmetadata")
	public RoleIndividualGrantMetaDto getCompany() {
		return this.roleIndividualFinder.getCAS012Metadata();
	}

	@POST
	@Path("create")
	public CreateRoleIndividualGrantCommandResult create(CreateRoleIndividualGrantCommand command) {
		return this.createHandler.handle(command);
	}
	
	@POST
	@Path("update")
	public void update(UpdateRoleIndividualGrantCommand command) {
		updateHandler.handle(command);
	}

	@POST
	@Path("delete")
	public void delete(DeleteRoleIndividualGrantCommand command) {
		this.deleteHandler.handle(command);
	}

	@POST
	@Path("getRoleType")
	public List<RoleTypeDto> GetRoleType() {
		return this.roleIndividualFinder.getCAS013Metadata();
	}

	@POST
	@Path("getRoles/{roleType}")
	public List<RoleDto> GetRoleByRoleType(@PathParam("roleType") int roleType){
		return this.personInforRoleFinder.getListRoleByRoleType(roleType);
	}

	
	@POST
	@Path("getRoles/incharge/{roleType}")
	public List<RoleDto> GetRoleByRoleTypeIncharge(@PathParam("roleType") int roleType){
		return this.personInforRoleFinder.getListRoleByRoleTypeAtr(roleType,RoleAtr.INCHARGE.value);
	}
	
	@POST
	@Path("getRoleGrants")
	public List<Cas013aDto> GetRoleGrants(String Role){
		return this.roleIndividualFinder.getRoleGrants(Role);
	}

	@POST
	@Path("getRoleGrant")
	public RoleIndividualGrantDto GetRoleGrant(RoleIndividualGrantDto rDto){
		return this.roleIndividualFinder.getRoleGrant(rDto.getUserID(), rDto.getRoleID(),rDto.getCompanyID());
	}

	@POST
	@Path("insertRoleGrant")
	public JavaTypeResult<String> InsertRoleGrant(CreateRoleIndividualGrantCommand roleGrant){
		return new JavaTypeResult<String>(createRoleGrantHandler.InsertRoleGrant(roleGrant));
	}
	
	@POST
	@Path("upDateRoleGrant")
	public JavaTypeResult<String> UpDateRoleGrant(UpdateRoleIndividualGrantCommand roleGrant){
		return new JavaTypeResult<String>(updateRoleGrant.UpDateRoleGrant(roleGrant));
	}
	
	@POST
	@Path("deleteRoleGrant")
	public void DeleteRoleGrant(DeleteRoleIndividualGrantCommand roleGrant){
		this.deleteRoleGrant.deleteRoleGrant(roleGrant);
	}
	
	@POST
	@Path("get/futurerefpermit")
	public boolean getFutureDateRefPermit() {
		String roleId = AppContexts.user().roles().forPersonalInfo();
		return this.personInforRoleFinder.find(roleId).map(PersonRole::getReferFutureDate).orElse(false);
	}

    @POST
    @Path("getCompanyInfo")
    public CompanyInfo GetEmployy(String cid) {
	    return this.roleIndividualFinder.getCompanyInfo(cid);
	}

	@POST
	@Path("getWorkPlaceInfo")
	public WorkPlaceInfo GetWorkPlaceInfo(WorkPlaceDto dto){
		return this.roleIndividualFinder.GetWorkPlaceInfo(dto.getEmployeeID(),dto.getCid());
	}

	@POST
	@Path("getJobTitle")
	public JobTitle GetJobTitle(String employeeID){
		return this.roleIndividualFinder.GetJobTitle(employeeID);
	}

	@POST
	@Path("searchCompanyInfo")
	public CompanyImport searchCompanyInfo(String companyId) {
		return this.roleIndividualFinder.searchCompanyInfo(companyId);
	}
	@POST
	@Path("getCompanyList")
	public List<CompanyDto> getCompanyList() {
		return this.roleIndividualFinder.getCompanyList();
	}

	@POST
	@Path("getEmployeeList/{cid}")
	public List<EmployeeBasicInfoDto> getListEmployeeInfo(@PathParam("cid") String cid) {
		return this.roleIndividualFinder.getListEmployeeInfo(cid);
	}

}
