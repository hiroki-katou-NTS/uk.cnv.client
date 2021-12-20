package nts.uk.screen.com.app.command.sys.auth.role;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.app.command.employmentrole.CreateEmploymentRoleCmd;
import nts.uk.ctx.at.auth.app.command.employmentrole.CreateEmploymentRoleCmdHandler;
import nts.uk.ctx.sys.auth.app.command.role.AddRoleCommand;
import nts.uk.ctx.sys.auth.app.command.role.AddRoleCommandHandler;
//import nts.uk.ctx.sys.auth.app.command.wplmanagementauthority.CreateWorkPlaceAuthorityCmdHandler;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.AddRoleByRoleTiesCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.RoleByRoleTiesCommand;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CreateRoleCas005CmdHandler extends CommandHandler<RoleCas005Command> {

    @Inject
    private AddRoleCommandHandler addRoleCommandHandler;

    @Inject
    private AddRoleByRoleTiesCommandHandler addRoleByRoleTiesCommandHandler;

    @Inject
    private CreateEmploymentRoleCmdHandler createEmploymentRoleCmdHandler;

//    @Inject
//    private CreateWorkPlaceAuthorityCmdHandler createWorkPlaceAuthorityCmdHandler;

    @Override
    protected void handle(CommandHandlerContext<RoleCas005Command> context) {
        RoleCas005Command data = context.getCommand();
        //insert Role
        AddRoleCommand addRoleCommand = new AddRoleCommand(
                data.getRoleId(),
                data.getRoleCode(),
                data.getRoleType(),
                data.getEmployeeReferenceRange(),
                data.getName(),
                data.getContractCode(),
                data.getAssignAtr(),
                data.getCompanyId(),
                data.getApprovalAuthority() != null && (data.getApprovalAuthority() == 1)

        );
        data.setRoleId(addRoleCommandHandler.handle(addRoleCommand));
        if (!data.getRoleId().isEmpty()) {
            //insert  RoleByRoleTies
            if (data.getAssignAtr() == RoleAtr.INCHARGE.value) {
                RoleByRoleTiesCommand roleByRoleTiesCommand = new RoleByRoleTiesCommand(
                        data.getRoleId(),
                        data.getWebMenuCd()
                );
                addRoleByRoleTiesCommandHandler.handle(roleByRoleTiesCommand);
            }
            //insert EmploymentRole
            CreateEmploymentRoleCmd createEmploymentRoleCmd = new CreateEmploymentRoleCmd(
                    data.getRoleId(),
                    AppContexts.user().companyId(),
                    NotUseAtr.NOT_USE
            );
            createEmploymentRoleCmdHandler.handle(createEmploymentRoleCmd);
        }

// Note Ea: 2021/5/24の発注にこの部分処理をとばす。
//			//insert WorkPlaceAuthority
//			for(WorkPlaceAuthorityCommand workPlaceAuthorityCommand :data.getListWorkPlaceAuthority()) {
//				CreateWorkPlaceAuthorityCmd createWorkPlaceAuthorityCmd = new CreateWorkPlaceAuthorityCmd(
//						data.getRoleId(),
//						AppContexts.user().companyId(),
//						workPlaceAuthorityCommand.getFunctionNo(),
//						workPlaceAuthorityCommand.isAvailability()
//						);
//				createWorkPlaceAuthorityCmdHandler.handle(createWorkPlaceAuthorityCmd);
//			}

    }

}
