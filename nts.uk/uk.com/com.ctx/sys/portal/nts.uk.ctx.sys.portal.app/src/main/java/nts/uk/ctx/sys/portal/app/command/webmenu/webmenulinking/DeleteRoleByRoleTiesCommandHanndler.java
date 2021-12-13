package nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;

@Stateless
public class DeleteRoleByRoleTiesCommandHanndler extends CommandHandler<DeleteRoleByRoleTiesCommand> {

	@Inject
	private RoleByRoleTiesRepository repo;
	@Override
	protected void handle(CommandHandlerContext<DeleteRoleByRoleTiesCommand> context) {
		String roleId = context.getCommand().getRoleId();
//TODO　修正お願いいたします。	
/*		Optional<RoleByRoleTies> checkData = repo.getRoleByRoleTiesById(roleId);

		if(checkData.isPresent()) {
			repo.deleteRoleByRoleTies(roleId);
			
		} else {
			throw new BusinessException("K co du lieu ");
		}
*/
	}

}
