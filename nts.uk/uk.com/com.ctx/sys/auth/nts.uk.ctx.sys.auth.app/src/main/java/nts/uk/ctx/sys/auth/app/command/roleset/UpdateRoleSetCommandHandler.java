/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.command.roleset;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.service.RoleSetService;
import nts.uk.shr.com.context.AppContexts;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
* The Class UpdateRoleSetCommandHandler.
* @author HieuNV
*/
@Stateless
public class UpdateRoleSetCommandHandler extends CommandHandlerWithResult<RoleSetCommand, String> {

    @Inject
    private RoleSetService roleSetService;

    @Inject
    private RoleSetRepository roleSetRepository;


    @Override
    protected String handle(CommandHandlerContext<RoleSetCommand> context) {
        RoleSetCommand command = context.getCommand();
        String companyId = AppContexts.user().companyId();
        if (!StringUtils.isNoneEmpty(companyId)) {
            return null;
        }
        String roleSetCd = command.getRoleSetCd();
        String roleSetName = command.getRoleSetName();
        String attendanceRoleId = command.getEmploymentRoleId();
        String personInfoRoleId = command.getPersonInfRoleId();
        RoleSet roleSetDom = RoleSet.create(
                companyId,
                roleSetCd,
                roleSetName,
                Optional.ofNullable(attendanceRoleId),
                Optional.ofNullable(personInfoRoleId));

        Optional<RoleSet> optionalRoleSet = roleSetRepository.findByRoleSetCdAndCompanyId(roleSetCd,companyId);
        if(!optionalRoleSet.isPresent()){
            roleSetService.registerRoleSet(roleSetDom);
        }else {
            roleSetService.updateRoleSet(roleSetDom);
        }
        return command.getRoleSetCd();
    }
}
