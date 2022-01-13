/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.command.roleset;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * The Class AddRoleSetCommandHandler.
 *
 * @author HieuNV
 */
@Stateless
public class AddRoleSetCommandHandler extends CommandHandlerWithResult<RoleSetCommand, String> {

    @Inject
    private RoleSetService roleSetService;
    @Override
    protected String handle(CommandHandlerContext<RoleSetCommand> context) {
        RoleSetCommand command = context.getCommand();
        String companyId = AppContexts.user().companyId();
        RoleSet roleSetDom = RoleSet.create(companyId
                , command.getRoleSetCd()
                , command.getRoleSetName()
                , Optional.ofNullable(command.getEmploymentRoleId())
                , Optional.ofNullable(command.getPersonInfRoleId()));

        //アルゴリズム「新規登録」を実行する - Execute the algorithm "new registration"
        roleSetService.registerRoleSet(roleSetDom);
        return command.getRoleSetCd();
    }
}
