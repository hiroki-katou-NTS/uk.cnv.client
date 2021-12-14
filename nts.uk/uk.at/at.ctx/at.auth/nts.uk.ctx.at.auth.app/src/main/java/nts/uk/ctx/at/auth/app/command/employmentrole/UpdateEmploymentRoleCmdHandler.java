package nts.uk.ctx.at.auth.app.command.employmentrole;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;
@Stateless
public class UpdateEmploymentRoleCmdHandler extends CommandHandler<UpdateEmploymentRoleCmd> {

	@Inject
	private EmploymentRoleRepository empRepo;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateEmploymentRoleCmd> context) {
		UpdateEmploymentRoleCmd appCommand =  context.getCommand();
		EmploymentRole empUpdate = appCommand.toDomain();
		Optional<EmploymentRole> empRole = empRepo.getEmploymentRoleById(empUpdate.getCompanyId(), empUpdate.getRoleId());
		if(empRole.isPresent()) {
			empRepo.updateEmploymentRole(empUpdate);
		}
	}
}
