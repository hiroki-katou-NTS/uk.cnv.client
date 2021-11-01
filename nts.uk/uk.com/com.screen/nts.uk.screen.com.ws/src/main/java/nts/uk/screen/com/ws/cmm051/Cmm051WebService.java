package nts.uk.screen.com.ws.cmm051;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.screen.com.app.find.cmm051.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("com/screen/cmm051")
@Produces("application/json")
public class Cmm051WebService extends WebService {
    @Inject
    private GetDataInitCmm051ScreenQuery initCmm051ScreenQuery;
    @Inject
    private GetWorkplaceManagerScreenQuery getWorkplaceManagerScreenQuery;

    @Inject
    private GetEmployeeInfoAndWorkplaceInfoScreenQuery employeeModeScreenQuery;

    @Path("get-data-init")
    @POST
    public Cmm051InitDto getDataInit(){
        return initCmm051ScreenQuery.getDataInit();
    }

    @Path("get-wpl-manager")
    @POST
    public List<WorkplaceManager> getListWplMn(WplParamDto param){
        return getWorkplaceManagerScreenQuery.getListWplInfo(param.getWorkplaceId(),param.getSid());
    }

    @Path("get-data-init/employee-mode")
    @POST
    public EmployeeInfoAndWorkplaceInfoDto getListInformation(List<String> sids){
       return employeeModeScreenQuery.getListInformation(sids);
    }
}
