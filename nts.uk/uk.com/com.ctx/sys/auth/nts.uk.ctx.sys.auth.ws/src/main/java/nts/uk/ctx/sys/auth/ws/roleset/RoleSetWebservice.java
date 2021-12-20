/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.ws.roleset;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.command.roleset.AddOrUpdateDefaultRoleSetCommandHandler;
import nts.uk.ctx.sys.auth.app.command.roleset.DefaultRoleSetCommand;
import nts.uk.ctx.sys.auth.app.find.roleset.DefaultRoleSetDto;
import nts.uk.ctx.sys.auth.app.find.roleset.DefaultRoleSetFinder;
import nts.uk.ctx.sys.auth.app.find.roleset.RoleSetDto;
import nts.uk.ctx.sys.auth.app.find.roleset.RoleSetFinder;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.WebMenuAdapter;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.WebMenuImport;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetLinkWebMenuAdapter;
import nts.uk.ctx.sys.auth.dom.roleset.webmenu.webmenulinking.RoleSetLinkWebMenuImport;
import nts.uk.shr.com.context.AppContexts;

/**
* The Class RoleSetWebservice.
* @author HieuNV
*/
@Path("ctx/sys/auth/roleset")
@Produces("application/json")
public class RoleSetWebservice extends WebService {
    @Inject
    private RoleSetFinder roleSetFinder;

    // Default RoleSet:
    @Inject
    private DefaultRoleSetFinder defaultRoleSetFinder;

    @Inject
    private AddOrUpdateDefaultRoleSetCommandHandler addOrUpdateDefaultRoleSetCommandHandler;

    // Web menu
    @Inject
    private WebMenuAdapter webMenuAdapter;

    // Role Set and Web menu link
    @Inject
    private RoleSetLinkWebMenuAdapter roleSetLinkWebMenuAdapter;


    /**
     * Get all role set by login user's company id
     * @return List<RoleSetDto>
     */
    @POST
    @Path("findallroleset")
    public List<RoleSetDto> getAllRolSet() {
        return this.roleSetFinder.findAll();
    }

    /**
     * Get role set by role set code and login user's company id
     * @param roleSetCd
     * @return RoleSetDto
     */
    @POST
    @Path("findroleset/{rolesetcd}")
    public RoleSetDto getRoleSet(@PathParam("rolesetcd") String roleSetCd) {
        return this.roleSetFinder.find(roleSetCd);
    }

    /**
     *  Get default role set by login user's companyId
     * @return
     */
    @POST
    @Path("finddefaultroleset")
    public DefaultRoleSetDto getCurrentDefaultRoleSet() {
        return this.defaultRoleSetFinder.findByCompanyId();
    }

    /**
     * Execute register a default role set
     * @param command
     * @return
     */
    @POST
    @Path("adddefaultroleset")
    public JavaTypeResult<String> addDefaultRoleSet(DefaultRoleSetCommand command) {
        return new JavaTypeResult<String>(this.addOrUpdateDefaultRoleSetCommandHandler.handle(command));
    }

    /**
     * Get list of web menu by login user's company id
     * @return
     */
    @POST
    @Path("findallwebmenu")
    public List<WebMenuImport> getAllWebMenu() {
        return this.webMenuAdapter.findByCompanyId();
    }

    /**
     * Get all role set link web menu by role set code and login user's company id
     * @param roleSetCd
     * @return
     */
    @POST
    @Path("findallrolesetwebmenu/{rolesetcd}")
    public List<RoleSetLinkWebMenuImport> getAllRoleSetWebMenu(@PathParam("roleSetCd") String roleSetCd) {
        return this.roleSetLinkWebMenuAdapter.findAllWebMenuByRoleSetCd(roleSetCd);
    }

    /**
     * Get company id of the login user
     * @return companyId
     */
    @POST
    @Path("companyidofloginuser")
    public JavaTypeResult<String> getCompanyIdOfLoginUser() {
        return new JavaTypeResult<String> (AppContexts.user().companyId());
    }

}
