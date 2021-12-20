package nts.uk.ctx.at.auth.app.command.employmentrole;


import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;

@Stateless
public class CreateEmploymentRoleCmdHandler extends CommandHandler<CreateEmploymentRoleCmd> {

	@Inject
	private EmploymentRoleRepository empRepo;

	@Override
	protected void handle(CommandHandlerContext<CreateEmploymentRoleCmd> context) {
		CreateEmploymentRoleCmd appCommand = context.getCommand();
		EmploymentRole employmentRole = appCommand.toDomain();
		empRepo.addEmploymentRole(employmentRole);
	}

}
