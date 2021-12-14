package nts.uk.screen.com.app.command.sys.auth.personrole.delete;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.pereg.dom.roles.functionauth.authsetting.PersonInfoAuthorityRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleService;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRoleRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeletePersonRoleCommandHandler extends CommandHandler<DeletePersonRoleCommand> {
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private PersonRoleRepository personRoleRepo;
	
	@Inject
	private RoleSetRepository roleSetRepo;
	
	@Inject
	private RoleSetService roleSetService;
	
	@Inject
	private PersonInfoAuthorityRepository personInfoAuthRepo;

	@Override
	protected void handle(CommandHandlerContext<DeletePersonRoleCommand> context) {
		final DeletePersonRoleCommand command = context.getCommand();
		String roleId = command.getRoleId();
		String companyId = AppContexts.user().companyId();
		
		//ロール
		roleService.removeRole(roleId);

		//個人情報のロール
		personRoleRepo.remove(roleId);

		if (command.getAssignAtr() == RoleAtr.GENERAL.value) {
			
			List<RoleSet> roleSets = roleSetRepo.findByCompanyIdAndPersonRole(companyId, roleId);
			if (!roleSets.isEmpty()) {
				roleSets.forEach(rs -> {
					rs.removePersonInfRole();
					roleSetService.updateRoleSet(rs);
				});

			}
		}
		
		//個人情報の権限
		personInfoAuthRepo.delete(companyId, roleId);
	}
	
	public void checkRoleUse(String roleId) {
		roleService.checksUseRole(roleId);
	}

}
