package nts.uk.ctx.sys.auth.ws.grant.cas012;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.command.cas012.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("ctx/sys/auth/grant/cas012")
@Produces("application/json")
public class Cas012GrantWs extends WebService {
    @Inject
    private Cas012DeleteCommandHandler deleteCommandHandler;
    @Inject
    private Cas012UpdateCommandHandler updateCommandHandler;

    @Inject
    private Cas012AddCommandHandler addCommandHandler;

    @Inject
    private Cas012CompanyAdDeleteCommandHandler companyAdDeleteCommandHandler;

    @Inject
    private Cas012CompanyAdUpdateCommandHandler companyAdUpdateCommandHandler;

    @Inject
    private Cas012CompanyAdRegisterCommandHandler companyAdRegisterCommandHandler;

    @POST
    @Path("a/delete")
    public void delete(Cas012DeleteCommand command) {
        deleteCommandHandler.handle(command);
    }

    @POST
    @Path("a/update")
    public void update(Cas012AddOrUpdateCommand command) {
        updateCommandHandler.handle(command);
    }

    @POST
    @Path("a/add")
    public void add(Cas012AddOrUpdateCommand command) {
        addCommandHandler.handle(command);
    }


    @POST
    @Path("a/sys/company/delete")
    public void delete(Cas012CompanyAdDeleteCommand command) {
        companyAdDeleteCommandHandler.handle(command);
    }

    @POST
    @Path("a/sys/company/update")
    public void update(Cas012CompanyAdRegisterOrUpdateCommand command) {
        companyAdUpdateCommandHandler.handle(command);
    }

    @POST
    @Path("a/sys/company/add")
    public void add(Cas012CompanyAdRegisterOrUpdateCommand command) {
        companyAdRegisterCommandHandler.handle(command);
    }


}
