/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.app.command.sys.auth.roleset;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.sys.auth.app.command.roleset.AddOrUpdateDefaultRoleSetCommandHandler;
import nts.uk.ctx.sys.auth.app.command.roleset.AddRoleSetCommandHandler;
import nts.uk.ctx.sys.auth.app.command.roleset.DefaultRoleSetCommand;
import nts.uk.ctx.sys.auth.app.command.roleset.RoleSetCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.WebMenuCommand;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.AddRoleSetLinkWebMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.webmenu.webmenulinking.RoleSetLinkWebMenuCommand;
import nts.uk.shr.com.context.AppContexts;

/**
* The Class RegisterRoleSetCommandBaseHandler.
* @author HieuNV
*/
@Stateless
@javax.transaction.Transactional
public class RegisterRoleSetCommandBaseHandler extends CommandHandlerWithResult<RoleSetCommandBase, String> {

    @Inject
    private AddRoleSetCommandHandler addRoleSetCommandHandler;

    @Inject
    private AddRoleSetLinkWebMenuCommandHandler addRoleSetLinkWebMenuCommandHandler;

    @Inject
    private AddOrUpdateDefaultRoleSetCommandHandler addOrUpdateDefaultRoleSetCommandHandler;

    /**
     * アルゴリズム「新規登録」を実行する - Execute the algorithm "new registration"
     */
    @Override
    protected String handle(CommandHandlerContext<RoleSetCommandBase> context) {
        RoleSetCommandBase command = context.getCommand();

        //get companyid
        String companyId = AppContexts.user().companyId();
        //build Role Set
        RoleSetCommand roleSetCommand = new RoleSetCommand(
                command.getRoleSetCd()
                , companyId
                , command.getRoleSetName()
                , command.getPersonInfRoleId()
                , command.getEmploymentRoleId());
        // register Role Set
        this.addRoleSetCommandHandler.handle(roleSetCommand);
        val isDefaultRoleSet = command.getDefaultRoleSet();
        if(isDefaultRoleSet){
            addOrUpdateDefaultRoleSetCommandHandler
                    .handle(new DefaultRoleSetCommand(roleSetCommand.getRoleSetCd()));
        }
        // build Role Set Link Web Menu command
        List<WebMenuCommand> listWebMenus = command.getWebMenus();
        List<String> listWebMenuCds = !CollectionUtil.isEmpty(listWebMenus) ?
                listWebMenus.stream().map(WebMenuCommand::getWebMenuCode).collect(Collectors.toList())
                : new ArrayList<String>();
        RoleSetLinkWebMenuCommand roleSetLinkWebMenuCommand = new RoleSetLinkWebMenuCommand(
                command.getRoleSetCd()
                , companyId
                , listWebMenuCds);
        // register Role Set Link Web Menu
        this.addRoleSetLinkWebMenuCommandHandler.handle(roleSetLinkWebMenuCommand);
        return command.getRoleSetCd();
    }
}
