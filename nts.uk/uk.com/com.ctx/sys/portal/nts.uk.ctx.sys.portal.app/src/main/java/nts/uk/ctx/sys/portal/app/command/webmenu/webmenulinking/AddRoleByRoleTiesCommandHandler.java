package nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking;


import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.webmenu.WebMenuCode;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AddRoleByRoleTiesCommandHandler extends CommandHandler<RoleByRoleTiesCommand> {

	@Inject
	private RoleByRoleTiesRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<RoleByRoleTiesCommand> context) {
		RoleByRoleTiesCommand role = context.getCommand();
		RoleByRoleTies newRole = new RoleByRoleTies(role.getRoleId(),new WebMenuCode( role.getWebMenuCd()), AppContexts.user().companyId());
		repo.insertRoleByRoleTies(newRole);
	}

}
