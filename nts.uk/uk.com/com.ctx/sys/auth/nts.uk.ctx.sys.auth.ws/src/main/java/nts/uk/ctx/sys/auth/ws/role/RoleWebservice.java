package nts.uk.ctx.sys.auth.ws.role;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.uk.ctx.sys.auth.app.query.role.DtoRole;
import nts.uk.ctx.sys.auth.app.query.role.GetRoleListQuery;
import nts.uk.ctx.sys.auth.app.query.role.RolesParam;
import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.command.person.role.RemovePersonRoleCommand;
import nts.uk.ctx.sys.auth.app.command.person.role.RemovePersonRoleCommandHandler;
import nts.uk.ctx.sys.auth.app.command.person.role.SavePersonRoleCommand;
import nts.uk.ctx.sys.auth.app.command.person.role.SavePersonRoleCommandHandler;
import nts.uk.ctx.sys.auth.app.find.person.role.GetRolesParam;
import nts.uk.ctx.sys.auth.app.find.person.role.PersonInformationRole;
import nts.uk.ctx.sys.auth.app.find.person.role.PersonInformationRoleFinder;
import nts.uk.ctx.sys.auth.app.find.person.role.dto.RoleDto;
import nts.uk.ctx.sys.auth.app.find.role.workplace.RoleWorkplaceIDFinder;
import nts.uk.ctx.sys.auth.app.find.role.workplace.RoleWorkplaceIDFinder.SystemType;
import nts.uk.ctx.sys.auth.app.find.role.workplace.WorkplaceParam;
import nts.uk.ctx.sys.auth.dom.adapter.persettingmenu.PermissionSettingMenuAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.persettingmenu.PermissionSettingMenuImport;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRole;
import nts.uk.shr.infra.i18n.resource.I18NResourcesForUK;

@Path("ctx/sys/auth/role")
@Produces("application/json")
public class RoleWebservice extends WebService {
	@Inject
	private RoleWorkplaceIDFinder roleWorkplaceIDFinder;
	@Inject
	private PersonInformationRoleFinder personInforRoleFinder;
	@Inject
	private SavePersonRoleCommandHandler savePersonRoleHandler;
	@Inject
	private RemovePersonRoleCommandHandler removePersonRoleHandler; 
	@Inject
	private I18NResourcesForUK i18n;
	@Inject
	private PermissionSettingMenuAdapter permissionSettingMenuAdapter;
	@Inject
	private GetRoleListQuery getRoleListQuery;
	
	@POST
	@Path("getlistrolebytype/{roleType}")
	public List<RoleDto> getListRoleByRoleType(@PathParam("roleType") int roleType) {
		return this.personInforRoleFinder.getListRoleByRoleType(roleType);
	}
	
	@POST
	@Path("get-list-roles")
	public List<RoleDto> getListRoles(GetRolesParam param) {
		return this.personInforRoleFinder.getListRoles(param);
	}

	@POST
	@Path("getrolebyroleid/{roleid}")
	public RoleDto getRoleByRoleId(@PathParam("roleid") String roleId) {
		return this.personInforRoleFinder.getRoleByRoleId(roleId);
	}

	@POST
	@Path("getrefrangebysystype/{systype}")
	public int getRefRangeByRoleId(@PathParam("systype") int sysType) {
		if (sysType == SystemType.ADMINISTRATOR.value) {
			return EmployeeReferenceRange.ALL_EMPLOYEE.value;
		}
		
		String roleId = this.roleWorkplaceIDFinder.findRoleIdBySystemType(sysType);
		if (Strings.isBlank(roleId)) {
			return EmployeeReferenceRange.ONLY_MYSELF.value;
		}
		
		return this.personInforRoleFinder.getRoleByRoleId(roleId).getEmployeeReferenceRange();
	}
	
	@POST
	@Path("get/rolename/by/roleids")
	public List<RoleDto> getRoleByListRoleId(List<String>  data) {
		return this.personInforRoleFinder.findByListRoleID(data);
	}

	
	@POST
	@Path("save/person/infor")
	public JavaTypeResult<String> savePersonInfo(SavePersonRoleCommand command){
		String roleId = savePersonRoleHandler.handle(command);
		return new JavaTypeResult<String>(roleId);
	}
	
	@POST
	@Path("remove/person/infor")
	public void removePersonInfo(RemovePersonRoleCommand command){
		removePersonRoleHandler.handle(command);
	}
	
	@POST
	@Path("find/person/infor")
	public List<PersonInformationRole> find(){
		return personInforRoleFinder.find();
	}
	@POST
	@Path("get/enum/reference/range")
	public List<EnumConstant> getEnumReferenceRange(){
		return EnumAdaptor.convertToValueNameList(EmployeeReferenceRange.class, i18n);
	}
	
	@POST
	@Path("find/person/role")
	public List<PersonRole> find(List<String> roleIds){
		return personInforRoleFinder.findByListRoleIds(roleIds);
	}
	
	@POST
	@Path("user/has/role/{roleType}")
	public Boolean userHasRoleType( @PathParam("roleType") int roleType){
		return 	personInforRoleFinder.userHasRoleType(roleType);
	}
	
	@POST
	@Path("per/setting/menu/{roleType}")
	public List<PermissionSettingMenuImport> perSettingMenu( @PathParam("roleType") int roleType){
		return 	permissionSettingMenuAdapter.findByRoleType(roleType);
	}
	
	@POST
	@Path("getListWorkplaceId")
	public List<String> findListWorkplaceId(WorkplaceParam param){
		return roleWorkplaceIDFinder.findListWorkplaceId(param);
	}
	@POST
	@Path("get-list-role-new")
	public List<DtoRole> getListRoles(RolesParam param) {
		return this.getRoleListQuery.getListRole(param);
	}
	
}
