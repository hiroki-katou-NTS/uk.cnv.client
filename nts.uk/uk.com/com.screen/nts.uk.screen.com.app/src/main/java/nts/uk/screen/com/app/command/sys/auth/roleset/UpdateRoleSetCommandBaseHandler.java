/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.app.command.sys.auth.roleset;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.app.command.roleset.AddOrUpdateDefaultRoleSetCommandHandler;
import nts.uk.ctx.sys.auth.app.command.roleset.DefaultRoleSetCommand;
import nts.uk.ctx.sys.auth.app.command.roleset.RoleSetCommand;
import nts.uk.ctx.sys.auth.app.command.roleset.UpdateRoleSetCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.WebMenuCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.RoleSetLinkWebMenuCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.UpdateRoleSetLinkWebMenuCommandHandler;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class UpdateRoleSetCommandBaseHandler.
 *
 * @author HieuNV
 */
@Stateless
@javax.transaction.Transactional
public class UpdateRoleSetCommandBaseHandler extends CommandHandlerWithResult<RoleSetCommandBase, String> {

    @Inject
    private UpdateRoleSetCommandHandler updateRoleSetCommandHandler;

    @Inject
    private UpdateRoleSetLinkWebMenuCommandHandler updateRoleSetLinkWebMenuCommandHandler;

    @Inject
    private AddOrUpdateDefaultRoleSetCommandHandler addOrUpdateDefaultRoleSetCommandHandler;


    /**
     * //アルゴリズム「更新登録」を実行する
     */
    @Override
    protected String handle(CommandHandlerContext<RoleSetCommandBase> context) {
        RoleSetCommandBase command = context.getCommand();

        //build Role Set command
        RoleSetCommand roleSetCommand = new RoleSetCommand(
                command.getRoleSetCd()
                , command.getCompanyId()
                , command.getRoleSetName()
                , command.getPersonInfRoleId()
                , command.getEmploymentRoleId());
        // update Role Set base
        this.updateRoleSetCommandHandler.handle(roleSetCommand);

        // build Role Set Link Web Menu command
        List<WebMenuCommand> listWebMenus = command.getWebMenus();
        List<String> listWebMenuCds = !CollectionUtil.isEmpty(listWebMenus) ?
                listWebMenus.stream().map(WebMenuCommand::getWebMenuCode).collect(Collectors.toList()) : new ArrayList<String>();
        RoleSetLinkWebMenuCommand roleSetLinkWebMenuCommand = new RoleSetLinkWebMenuCommand(
                command.getRoleSetCd()
                , command.getCompanyId()
                , listWebMenuCds);
        //update Role Set Link Web Menu
        this.updateRoleSetLinkWebMenuCommandHandler.handle(roleSetLinkWebMenuCommand);
        val isDefaultRoleSet = command.getDefaultRoleSet();
        if (isDefaultRoleSet) {
            addOrUpdateDefaultRoleSetCommandHandler.handle(new DefaultRoleSetCommand(roleSetCommand.getRoleSetCd()));
        }
        return command.getRoleSetCd();
    }
}
