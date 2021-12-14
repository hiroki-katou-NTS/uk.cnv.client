package nts.uk.ctx.sys.auth.app.command.role;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;

@Stateless
public class AddRoleCommandHandler extends CommandHandlerWithResult<AddRoleCommand,String> {

	@Inject
	private RoleRepository repo;

	@Override
	protected String handle(CommandHandlerContext<AddRoleCommand> context) {
		AddRoleCommand role = context.getCommand();
		Role newRole = role.toDomain();
		Optional<Role> checkData = repo.findRoleByRoleCode(newRole.getCompanyId(),newRole.getRoleCode().v(),newRole.getRoleType().value);
		if(checkData.isPresent()) {
			throw new BusinessException("Msg_3");
		} else {
			repo.insert(newRole);
		}
		return newRole.getRoleId();
	}
	

}
