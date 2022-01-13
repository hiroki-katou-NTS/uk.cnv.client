package nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;
import nts.uk.shr.com.context.AppContexts;

import java.util.Optional;

@Stateless
public class DeleteRoleByRoleTiesCommandHanndler extends CommandHandler<DeleteRoleByRoleTiesCommand> {

	@Inject
	private RoleByRoleTiesRepository repo;
	@Override
	protected void handle(CommandHandlerContext<DeleteRoleByRoleTiesCommand> context) {
		String roleId = context.getCommand().getRoleId();
		Optional<RoleByRoleTies> checkData = repo.getByRoleIdAndCompanyId(roleId, AppContexts.user().companyId());

		if(checkData.isPresent()) {
			repo.deleteRoleByRoleTies(roleId,AppContexts.user().companyId());

		} else {
			throw new BusinessException("K co du lieu ");
		}
	}

}
