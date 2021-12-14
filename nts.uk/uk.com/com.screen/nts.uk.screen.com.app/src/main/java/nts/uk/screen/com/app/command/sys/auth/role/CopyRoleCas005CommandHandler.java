package nts.uk.screen.com.app.command.sys.auth.role;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CopyRoleCas005CommandHandler extends CommandHandler<CopyRoleCas005Command> {
	
	@Inject
	private RoleRepository repo;
	
	@Inject
	private UpdateRoleCas005CmdHandler updateRoleCas005CmdHandler;
	
	@Inject
	private CreateRoleCas005CmdHandler createRoleCas005CmdHandler;
	@Override
	protected void handle(CommandHandlerContext<CopyRoleCas005Command> context) {
		CopyRoleCas005Command data = context.getCommand();
		Optional<Role> role =  repo.findRoleByRoleCode(AppContexts.user().companyId(),data.getRoleCas005Command().getRoleCode(), data.getRoleCas005Command().getRoleType());
		
		//nếu có dữ liệu
		if(role.isPresent()) {
			String roleID = role.get().getRoleId();
			data.getRoleCas005Command().setRoleId(roleID);
			if(data.isCheckUpdate()) {
				updateRoleCas005CmdHandler.handle(data.getRoleCas005Command());
			}else {
				throw new BusinessException("Msg_3");
			}
		}else {
			createRoleCas005CmdHandler.handle(data.getRoleCas005Command());
		}
		
	}

}
