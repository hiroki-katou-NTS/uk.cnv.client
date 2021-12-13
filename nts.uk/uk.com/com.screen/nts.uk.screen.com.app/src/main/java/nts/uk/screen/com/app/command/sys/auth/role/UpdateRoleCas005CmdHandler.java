package nts.uk.screen.com.app.command.sys.auth.role;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.app.command.employmentrole.UpdateEmploymentRoleCmd;
import nts.uk.ctx.at.auth.app.command.employmentrole.UpdateEmploymentRoleCmdHandler;
import nts.uk.ctx.sys.auth.app.command.role.UpdateRoleCommand;
import nts.uk.ctx.sys.auth.app.command.role.UpdateRoleCommandHandler;
//import nts.uk.ctx.sys.auth.app.command.wplmanagementauthority.UpdateWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.RoleByRoleTiesCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.UpdateRoleByRoleTiesCommandHandler;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class UpdateRoleCas005CmdHandler extends CommandHandler<RoleCas005Command> {

    @Inject
    private UpdateRoleCommandHandler updateRoleCommandHandler;

    @Inject
    private UpdateRoleByRoleTiesCommandHandler updateRoleByRoleTiesCommandHandler;

    @Inject
    private UpdateEmploymentRoleCmdHandler updateEmploymentRoleCmdHandler;

//    @Inject
//    private UpdateWorkPlaceAuthorityCmdHandler updateWorkPlaceAuthorityCmdHandler;

    @Override
    protected void handle(CommandHandlerContext<RoleCas005Command> context) {
        RoleCas005Command data = context.getCommand();
        //update Role
        UpdateRoleCommand updateRoleCommand = new UpdateRoleCommand(
                data.getRoleId(),
                data.getRoleCode(),
                data.getRoleType(),
                data.getEmployeeReferenceRange(),
                data.getName(),
                data.getContractCode(),
                data.getAssignAtr(),
                AppContexts.user().companyId(),
                data.getApprovalAuthority() != null && (data.getApprovalAuthority() == 1)

        );
        updateRoleCommandHandler.handle(updateRoleCommand);
        //update RoleByRoleTies
        if (data.getAssignAtr() == RoleAtr.INCHARGE.value) {
            RoleByRoleTiesCommand roleByRoleTiesCommand = new RoleByRoleTiesCommand(
                    data.getRoleId(),
                    data.getWebMenuCd()
            );
            updateRoleByRoleTiesCommandHandler.handle(roleByRoleTiesCommand);
        }
        //update EmploymentRole
        UpdateEmploymentRoleCmd updateEmploymentRoleCmd = new UpdateEmploymentRoleCmd(
                data.getRoleId(),
                AppContexts.user().companyId(),
                NotUseAtr.NOT_USE
        );
        updateEmploymentRoleCmdHandler.handle(updateEmploymentRoleCmd);

//		//update WorkPlaceAuthority Note Ea: 2021/5/24の発注にこの部分処理をとばす。
//		for(WorkPlaceAuthorityCommand workPlaceAuthorityCommand :data.getListWorkPlaceAuthority()) {
//			UpdateWorkPlaceAuthorityCmd updateWorkPlaceAuthorityCmd = new UpdateWorkPlaceAuthorityCmd(
//					data.getRoleId(),
//					AppContexts.user().companyId(),
//					workPlaceAuthorityCommand.getFunctionNo(),
//					workPlaceAuthorityCommand.isAvailability()
//					);
//			updateWorkPlaceAuthorityCmdHandler.handle(updateWorkPlaceAuthorityCmd);
//		}
    }

}
