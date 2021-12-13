package nts.uk.screen.com.app.command.sys.auth.role;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.app.command.employmentrole.DeleteEmploymentRoleCmd;
import nts.uk.ctx.at.auth.app.command.employmentrole.DeleteEmploymentRoleCmdHandler;
import nts.uk.ctx.sys.auth.app.command.role.DeleteRoleCommand;
import nts.uk.ctx.sys.auth.app.command.role.DeleteRoleCommandHandler;
import nts.uk.ctx.sys.auth.app.command.wplmanagementauthority.DeleteWorkPlaceAuthorityCmd;
import nts.uk.ctx.sys.auth.app.command.wplmanagementauthority.DeleteWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.DeleteRoleByRoleTiesCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.DeleteRoleByRoleTiesCommandHanndler;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeleteRoleCas005CommandHandler extends CommandHandler<DeleteRoleCas005Command> {

	@Inject
	private DeleteRoleCommandHandler deleteRoleCommandHandler;
	
	@Inject
	private DeleteRoleByRoleTiesCommandHanndler deleteRoleByRoleTiesCommandHanndler;
	
	@Inject
	private DeleteEmploymentRoleCmdHandler deleteEmploymentRoleCmdHandler;
	
	@Inject
	private DeleteWorkPlaceAuthorityCmdHandler deleteWorkPlaceAuthorityCmdHandler;
	
	@Inject
	private RoleRepository repo;
	
	@Inject
	private RoleSetRepository repoRoleSet;
	
	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepository;
	
	@Inject
	private DefaultRoleSetRepository defaultRoleSetRepository;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteRoleCas005Command> context) {
		String roleId = context.getCommand().getRoleId();
		String companyId = AppContexts.user().companyId();
		Optional<Role> checkData = repo.findByRoleId(roleId);
		if(checkData.isPresent()) {
			int assignAtr = repo.findByRoleId(roleId).get().getAssignAtr().value;
			//if roleSet 一般
			if(assignAtr == RoleAtr.GENERAL.value) {
				List<RoleSet> listRoleset = repoRoleSet.findByemploymentRoleId(roleId);
				DefaultRoleSet rolesetDefault = defaultRoleSetRepository.findByCompanyId(companyId).get();
				boolean isSetAsDefault = listRoleset.stream().anyMatch(c -> c.getRoleSetCd().equals(rolesetDefault.getRoleSetCd()));
				if(isSetAsDefault) {
					throw new BusinessException("Msg_586");
				}
			}else {
				List<RoleIndividualGrant> roleIndivi = roleIndividualGrantRepository.findByRoleId(roleId);
				if(!roleIndivi.isEmpty()) {
					throw new BusinessException("Msg_584");
				}	
			}
			//delete Role
			DeleteRoleCommand deleteRoleCommand = new DeleteRoleCommand(
					roleId
					);
			deleteRoleCommandHandler.handle(deleteRoleCommand);
			
			//delete RoleByRoleTies
			if(assignAtr != RoleAtr.GENERAL.value ) {
				DeleteRoleByRoleTiesCommand DeleteRoleByRoleTiesCommand = new  DeleteRoleByRoleTiesCommand(
						roleId
						);
				deleteRoleByRoleTiesCommandHanndler.handle(DeleteRoleByRoleTiesCommand);	
			}else {
				List<RoleSet> listRoleset = repoRoleSet.findByCidEmploymentRoleId(companyId, roleId);
				for(RoleSet roleSet : listRoleset) {
					roleSet.setEmploymentRoleId();
					repoRoleSet.update(roleSet);
				}
			}
			//delete EmploymentRole
			DeleteEmploymentRoleCmd deleteEmploymentRoleCmd = new DeleteEmploymentRoleCmd();
			deleteEmploymentRoleCmd.setCompanyId(companyId);
			deleteEmploymentRoleCmd.setRoleId(roleId);
			
			deleteEmploymentRoleCmdHandler.handle(deleteEmploymentRoleCmd);
			// Comment note ea: 2021/5/24の発注にこの部分処理をとばす。
//			//delete WorkPlaceAuthority
//			DeleteWorkPlaceAuthorityCmd deleteWorkPlaceAuthorityCmd = new DeleteWorkPlaceAuthorityCmd();
//			deleteWorkPlaceAuthorityCmd.setCompanyId(companyId);
//			deleteWorkPlaceAuthorityCmd.setRoleId(roleId);
//			deleteWorkPlaceAuthorityCmdHandler.handle(deleteWorkPlaceAuthorityCmd);
		}
		
	}

}
