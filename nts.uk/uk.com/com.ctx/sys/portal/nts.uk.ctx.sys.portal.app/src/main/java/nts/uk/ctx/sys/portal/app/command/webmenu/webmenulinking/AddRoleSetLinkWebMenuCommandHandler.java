/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.service.RoleSetLinkWebMenuService;

/**
* The Class AddRoleSetLinkWebMenuCommandHandler.
* @author HieuNV
*/
@Stateless
public class AddRoleSetLinkWebMenuCommandHandler extends CommandHandlerWithResult<RoleSetLinkWebMenuCommand, String> {

    @Inject
    private RoleSetLinkWebMenuService roleSetLinkWebMenuService;

    @Override
    protected String handle(CommandHandlerContext<RoleSetLinkWebMenuCommand> context) {
        RoleSetLinkWebMenuCommand command = context.getCommand();

        this.roleSetLinkWebMenuService.executeUpdate(
                command.getCompanyId()
                , command.getRoleSetCd()
                , command.getWebMenuCds());
        return command.getRoleSetCd();
    }
}
