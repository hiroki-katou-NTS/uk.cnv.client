package nts.uk.screen.com.ws.cas011;

import nts.uk.screen.com.app.find.cas011.roleset.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("screen/com/cas011")
@Produces("application/json")
public class Cas011Ws {
    @Inject
    private GetListOfRoleSetScreenQuery getListOfRoleSetScreenQuery;

    @Inject
    private GetDetailedRoleSetInformationScreenQuery informationScreenQuery;

    @Inject
    private GetInitialStartupInforCas011ScreenQuery startupInforCas011ScreenQuery;

    // Update CAS011
    @POST
    @Path("get-role-set-and-default")
    public RoleSetAndRoleDefaultDto getRoleSetAndRoleDefault() {
        return getListOfRoleSetScreenQuery.getListRoleSet();
    }


    @POST
    @Path("get-detail-role-set/{rolesetcd}")
    public DetailedRoleSetInformationDto getDetailRoleSet(@PathParam("rolesetcd") String rolesetcd) {
        return informationScreenQuery.getDetailRoleSet(rolesetcd);
    }

    @POST
    @Path("get-data-init")
    public Cas011Dto getDataForStartUp() {
        return startupInforCas011ScreenQuery.getDataForStartUp();
    }
}
