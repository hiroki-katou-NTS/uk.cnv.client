package nts.uk.ctx.at.auth.app.command.employmentrole;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRoleRepository;

@Stateless
public class DeleteEmploymentRoleCmdHandler extends CommandHandler<DeleteEmploymentRoleCmd> {
	@Inject
	private EmploymentRoleRepository empRepo;

	@Override
	protected void handle(CommandHandlerContext<DeleteEmploymentRoleCmd> context) {
		DeleteEmploymentRoleCmd input = context.getCommand();
		//TODO EmploymentRoleRepositoryのメソッドが変更したので、修正お願いいたします。
/*		Optional<EmploymentRole> empRole =  empRepo.getEmploymentRoleById(input.getCompanyId(), input.getRoleId());
		if(empRole.isPresent()) {
			empRepo.deleteEmploymentRole(input.getCompanyId(), input.getRoleId());
		}*/
		
	}
}
