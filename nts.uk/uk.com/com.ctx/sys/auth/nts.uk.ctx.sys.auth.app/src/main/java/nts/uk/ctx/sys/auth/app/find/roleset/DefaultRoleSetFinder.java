/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.app.find.roleset;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.DefaultRoleSetRepository;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSet;
import nts.uk.ctx.sys.auth.dom.roleset.RoleSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
* The Class DefaultRoleSetFinder.
* @author HieuNV
*/
@Stateless
public class DefaultRoleSetFinder {

    @Inject
    private DefaultRoleSetRepository defaultRoleSetRepository;

    @Inject
    private RoleSetRepository roleSetRepository;

    /**
     * Get a Default Role Set by company id
     * @param
     * @return
     */
    public DefaultRoleSetDto find() {
        // get domain role set
        Optional<DefaultRoleSet> defaultRoleSetOpt = defaultRoleSetRepository.find(AppContexts.user().companyId());
        
        return buildDefaultRoleSetDto(defaultRoleSetOpt);            
    }


    /**
     * Get all Default Role Set by company id
     * @return
     */
    public DefaultRoleSetDto findByCompanyId() {
        // get domain role set
        Optional<DefaultRoleSet> defaultRoleSetOpt =  this.defaultRoleSetRepository.findByCompanyId(AppContexts.user().companyId());
        return buildDefaultRoleSetDto(defaultRoleSetOpt);
    }

    /**
     * Build Default Role Set DTO from Role set
     * @param defaultRoleSetOpt
     * @return
     */
    private DefaultRoleSetDto buildDefaultRoleSetDto(Optional<DefaultRoleSet> defaultRoleSetOpt) {
        if (!defaultRoleSetOpt.isPresent()) {
            return null;
        }
        Optional<RoleSet> roleSetOpt = roleSetRepository.findByRoleSetCdAndCompanyId(
                defaultRoleSetOpt.get().getRoleSetCd().v(), defaultRoleSetOpt.get().getCompanyId());
        if (!roleSetOpt.isPresent()) {
            return null;
        }
        return new DefaultRoleSetDto(roleSetOpt.get().getRoleSetCd().v(), roleSetOpt.get().getRoleSetName().v());
    }

}
