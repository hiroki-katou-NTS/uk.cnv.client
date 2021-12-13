package nts.uk.ctx.sys.auth.ws.wkpmanager;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.command.cmm051.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("at/auth/cmm051/workplace/manager")
@Produces(MediaType.APPLICATION_JSON)
public class Cmm051WebService extends WebService {
    @Inject
    private AddNewWorkplaceManagerCommandHandler addNewCommandHandler;

    @Inject
    private RegisterWorkplaceManagerCommandHandler registerCommandHandler;

    @Inject
    private DeleteWorkplaceManagerCommandHandler deleteCommandHandler;

    @Inject
    private DeleteWorkplaceAdmHistoryCommandHandler deleteHistoryCommandHandler;

    @Path("add")
    @POST
    public void addNew(AddNewWorkplaceManagerCommand command) {
        addNewCommandHandler.handle(command);
    }

    @Path("register")
    @POST
    public void register(RegisterWorkplaceManagerCommand command) {
        registerCommandHandler.handle(command);
    }

    @Path("delete")
    @POST
    public void delete(DeleteWorkplaceManagerCommand command) {
        deleteCommandHandler.handle(command);
    }

    @Path("delete-history")
    @POST
    public void deleteHistory(DeleteWorkplaceAdmHistoryCommand command) {
        deleteHistoryCommandHandler.handle(command);
    }
}
