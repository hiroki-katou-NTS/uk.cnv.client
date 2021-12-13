package nts.uk.ctx.sys.auth.app.command.person.role;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleService;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRole;
import nts.uk.ctx.sys.auth.dom.role.personrole.PersonRoleRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@Transactional
public class SavePersonRoleCommandHandler extends CommandHandlerWithResult<SavePersonRoleCommand, String> {
	@Inject
	private PersonRoleRepository personRoleRepo;
	@Inject
	private RoleService roleService;

	@Override
	protected String handle(CommandHandlerContext<SavePersonRoleCommand> context) {
		final SavePersonRoleCommand command = context.getCommand();

		if (command.getCreateMode()) {
			return insertPersonInfoRole(command);
		} else {
			return updatePersonInfoRole(command);
		}

	}

	private String insertPersonInfoRole(SavePersonRoleCommand command) {
		Role role = command.toDomain(AppContexts.user().companyId(), AppContexts.user().contractCode());
		roleService.insertRole(role);

		PersonRole personRole = new PersonRole(
				role.getRoleId(), 
				AppContexts.user().companyId(), 
				command.getReferFutureDate());
		personRoleRepo.insert(personRole);

		return role.getRoleId();
	}

	private String updatePersonInfoRole(SavePersonRoleCommand command) {
		Role role = command.toDomain(AppContexts.user().companyId(), AppContexts.user().contractCode());
		roleService.updateRole(role);

		PersonRole personRole = new PersonRole(
				role.getRoleId(), 
				AppContexts.user().companyId(), 
				command.getReferFutureDate());
		personRoleRepo.update(personRole);

		return role.getRoleId();
	}
}
