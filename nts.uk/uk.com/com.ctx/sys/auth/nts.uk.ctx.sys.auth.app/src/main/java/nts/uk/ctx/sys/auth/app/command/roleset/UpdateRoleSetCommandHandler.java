/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.command.roleset;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.roleset.ApprovalAuthority;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;

/**
* The Class UpdateRoleSetCommandHandler.
* @author HieuNV
*/
@Stateless
public class UpdateRoleSetCommandHandler extends CommandHandlerWithResult<RoleSetCommand, String> {

    @Inject
    private RoleSetService roleSetService;

    @Override
    protected String handle(CommandHandlerContext<RoleSetCommand> context) {
        RoleSetCommand command = context.getCommand();

        RoleSet roleSetDom = new RoleSet(command.getRoleSetCd()
                , command.getCompanyId()
                , command.getRoleSetName()
                , command.isApprovalAuthority() ? ApprovalAuthority.HasRight : ApprovalAuthority.HasntRight
                , command.getOfficeHelperRoleId()
                , command.getMyNumberRoleId()
                , command.getHumanResourceRoleId()
                , command.getPersonInfRoleId()
                , command.getEmploymentRoleId()
                , command.getSalaryRoleId());

        //アルゴリズム「更新登録」を実行する - Execute algorithm "update registration"
        this.roleSetService.updateRoleSet(roleSetDom);

        return roleSetDom.getRoleSetCd().v();
    }
}
