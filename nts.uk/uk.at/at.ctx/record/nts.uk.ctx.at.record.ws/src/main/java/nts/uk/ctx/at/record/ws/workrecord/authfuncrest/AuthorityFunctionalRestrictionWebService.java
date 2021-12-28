/**
 * 
 */
package nts.uk.ctx.at.record.ws.workrecord.authfuncrest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.workrecord.authfuncrest.AuthFuncRestrictionCommand;
import nts.uk.ctx.at.record.app.command.workrecord.authfuncrest.AuthFuncRestrictionCommandHandler;
import nts.uk.ctx.at.record.app.command.workrecord.authfuncrest.CopyDailyPerformanceAuthorityCommand;
import nts.uk.ctx.at.record.app.command.workrecord.authfuncrest.CopyDailyPerformanceAuthorityCommandHandler;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.EmployeeRoleDto;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.EmploymentRoleFinder;
import nts.uk.ctx.at.record.app.find.workrecord.authfuncrest.FunctionalRestrictionWithAuthorityDto;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceAuthority;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformAuthorRepo;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformanceFunction;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.ctx.at.record.dom.workrecord.authormanage.DailyPerformFuncRepo;

/**
 * @author danpv
 *
 */
@Path("at/record/workrecord/authfuncrest/")
@Produces("application/json")
public class AuthorityFunctionalRestrictionWebService extends WebService {

	@Inject
	private DailyPerformFuncRepo dailyPerfFunctionRepo;

	@Inject
	private DailyPerformAuthorRepo dailyPerAuthRepo;

	@Inject
	private AuthFuncRestrictionCommandHandler authFuncRestHandler;
	
	@Inject
	private EmploymentRoleFinder employmentRoleFinder; 
	
	@Inject
	private CopyDailyPerformanceAuthorityCommandHandler copyHandler;

	@POST
	@Path("find-emp-roles")
	public List<EmployeeRoleDto> getEmployeeRoles() {
		return employmentRoleFinder.findEmploymentRoles();
	}

	@POST
	@Path("find/{roleId}")
	public List<FunctionalRestrictionWithAuthorityDto> findFuncRestWithAuthor(@PathParam("roleId") String roleId) {
		List<DailyPerformanceFunction> daiPerfFunctions = dailyPerfFunctionRepo.getAll();
		List<DailyPerformanceAuthority> daiPerAuthors = dailyPerAuthRepo.get(roleId);

		// function-No , function-name, function-description
		List<FunctionalRestrictionWithAuthorityDto> results = new ArrayList<>();
		daiPerfFunctions.forEach(
				dpFunc -> results.add(new FunctionalRestrictionWithAuthorityDto(dpFunc.getFunctionNo().v().intValue(),
						dpFunc.getDisplayName().v(), dpFunc.getDescription().v())));

		// function-availability
		results.forEach(dto -> {
			daiPerAuthors.forEach(dpAu -> {
				if (dto.getFunctionNo() == dpAu.getFunctionNo().v().intValue()) {
					dto.setAvailability(dpAu.isAvailability());
				}
			});
		});
		return results;
	}

	@POST
	@Path("register")
	public void registerAuthFuncRestriction(AuthFuncRestrictionCommand command) {
		authFuncRestHandler.handle(command);
	}
	
	@POST
	@Path("getRoleIds")
	public List<String> getRoleIds() {
		String cid = AppContexts.user().companyId();
		//ドメインモデル「勤務実績の権限」を取得する
		List<DailyPerformanceAuthority> daiPerAuthors = dailyPerAuthRepo.findByCid(cid);
		//取得した「勤務実績の権限．ロールID」を返す
		List<String> roleIds = daiPerAuthors.stream()
				.map(daiPerAuthor -> daiPerAuthor.getRoleID())
				.distinct()
				.collect(Collectors.toList());
		return roleIds;
	}
	
	@POST
	@Path("copyDaiPerfAuth")
	public void copyDaiPerfAuth(CopyDailyPerformanceAuthorityCommand command) {
		copyHandler.handle(command);
	}

}
