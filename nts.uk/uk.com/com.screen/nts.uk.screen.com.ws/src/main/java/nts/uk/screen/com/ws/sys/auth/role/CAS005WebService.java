/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.screen.com.ws.sys.auth.role;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.com.app.command.sys.auth.role.CreateRoleCas005CmdHandler;
import nts.uk.screen.com.app.command.sys.auth.role.DeleteRoleCas005Command;
import nts.uk.screen.com.app.command.sys.auth.role.DeleteRoleCas005CommandHandler;
import nts.uk.screen.com.app.command.sys.auth.role.RoleCas005Command;
import nts.uk.screen.com.app.command.sys.auth.role.UpdateRoleCas005CmdHandler;


@Path("screen/sys/auth/cas005")
@Produces("application/json")
public class CAS005WebService extends WebService {

    @Inject
    private CreateRoleCas005CmdHandler createRoleCas005CmdHandler;

    @Inject
    private UpdateRoleCas005CmdHandler updateRoleCas005CmdHandler;

    @Inject
    private DeleteRoleCas005CommandHandler deleteRoleCas005CommandHandler;
    
    /**
     * add new role 
     * @param command
     * @return
     */
    @POST
    @Path("addrolecas005")
    public void addRoleCas005(RoleCas005Command command) {
        this.createRoleCas005CmdHandler.handle(command);
    }

    /**
     * Execute update the role set. Include update RoleSet and RoleSetLinkWebMenu.
     * @param command
     */
    @POST
    @Path("updaterolecas005")
    public void updateRoleCas005(RoleCas005Command command) {
        this.updateRoleCas005CmdHandler.handle(command);
    }

    /**
     * Execute delete the role set. Include delete RoleSet and RoleSetLinkWebMenu.
     * @param command
     */
    @POST
    @Path("deleterolecas005")
    public void deleteRoleCas005(DeleteRoleCas005Command command) {
        this.deleteRoleCas005CommandHandler.handle(command);
    }
}
