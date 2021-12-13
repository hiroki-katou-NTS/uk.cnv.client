package nts.uk.screen.com.ws.cas014;


import nts.uk.screen.com.app.find.cas014.Cas014Dto;
import nts.uk.screen.com.app.find.cas014.GetDataInitCas014ScreenQuery;
import nts.uk.screen.com.app.find.cas014.GetIndividualRollSetGrantScreenQuery;
import nts.uk.screen.com.app.find.cas014.RoleSetGrantedPersonDto;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("screen/com/cas014")
@Produces("application/json")
public class Cas014Ws {

    @Inject
    private GetDataInitCas014ScreenQuery getDataInitCas014ScreenQuery;

    @Inject
    private GetIndividualRollSetGrantScreenQuery getIndividualRollSetGrantScreenQuery;


    @POST
    @Path("get-role-set-grandted-person/{sid}")
    public RoleSetGrantedPersonDto getDetailRoleSet(@PathParam("sid") String sid) {
        return getIndividualRollSetGrantScreenQuery.getIndividualRollSetGrant(sid);
    }

    @POST
    @Path("get-data-init")
    public Cas014Dto getDataForStartUp() {
        return getDataInitCas014ScreenQuery.getDataInitScreen();
    }
}
