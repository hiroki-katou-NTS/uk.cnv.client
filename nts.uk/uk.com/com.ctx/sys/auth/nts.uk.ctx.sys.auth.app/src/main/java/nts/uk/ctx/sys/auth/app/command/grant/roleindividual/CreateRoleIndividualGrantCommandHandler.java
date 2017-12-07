package nts.uk.ctx.sys.auth.app.command.grant.roleindividual;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.user.User;
import nts.uk.ctx.sys.auth.dom.user.UserRepository;


@Stateless
public class CreateRoleIndividualGrantCommandHandler extends CommandHandlerWithResult<CreateRoleIndividualGrantCommand, CreateRoleIndividualGrantCommandResult> {

	@Inject
	private RoleRepository roleRepository;
	
	@Inject
	private RoleIndividualGrantRepository roleIndividualGrantRepo;

	@Inject
	private UserRepository userRepo;

	@Override
	protected CreateRoleIndividualGrantCommandResult handle(CommandHandlerContext<CreateRoleIndividualGrantCommand> context) {
		CreateRoleIndividualGrantCommand command = context.getCommand();
		
		Optional<RoleIndividualGrant> roleIndividualGrant = roleIndividualGrantRepo.findByUserCompanyRoleType(command.getUserID(), command.getCompanyID(), command.getRoleType());
		if (roleIndividualGrant.isPresent()) {
			throw new BusinessException("Msg_3");
		}
		if (command.getUserID() == null) {
			throw new BusinessException("Msg_218");
		}
		
		Role uniqueRole = roleRepository.findByType(command.getRoleType()).get(0);
		
		// ドメインモデル「ロール個人別付与」を新規登録する | Register a domain model "Role individual grant"
		RoleIndividualGrant domain = command.toDomain(uniqueRole.getRoleId());
		roleIndividualGrantRepo.add(domain);

		if (command.isSetRoleAdminFlag() == true) {
			RoleIndividualGrant roleIndiGrantSys = RoleIndividualGrant.createFromJavaType(
					command.getUserID(),
					uniqueRole.getRoleId(),
					command.getDecisionCompanyID(),
					command.getRoleType(),
					command.getStartValidPeriod(),
					command.getEndValidPeriod());
			// ドメインモデル「ロール個人別付与」を新規登録する | Register a domain model "Role individual grant"
			roleIndividualGrantRepo.add(roleIndiGrantSys);
		}
		
		Optional<User> user = userRepo.getByUserID(command.getUserID());
		if (user.get().isDefaultUser() == true) {
			user.get().setExpirationDate(command.getEndValidPeriod());
		}
		
		return new CreateRoleIndividualGrantCommandResult(domain.getCompanyId(), domain.getUserId(), domain.getRoleType().value);
	}
}
