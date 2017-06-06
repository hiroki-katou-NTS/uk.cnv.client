/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.ws.vacation.setting.nursingleave;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.vacation.setting.nursingleave.command.NursingLeaveCommand;
import nts.uk.ctx.at.shared.app.vacation.setting.nursingleave.command.NursingLeaveCommandHandler;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;

@Path("ctx/at/share/vacation/setting/nursingleave/")
@Produces("application/json")
public class NursingLeaveWs extends WebService {
    
    @Inject
    private NursingLeaveCommandHandler nursingHandler;
    
    /**
     * Find manage distinct.
     *
     * @return the list
     */
    @POST
    @Path("find/managedistinct")
    public List<EnumConstant> findManageDistinct() {
        return EnumAdaptor.convertToValueNameList(ManageDistinct.class);
    }
    
    /**
     * Save.
     *
     * @param command the command
     */
    @POST
    @Path("save")
    public void save(NursingLeaveCommand command) {
        this.nursingHandler.handle(command);
    }

    /**
     * Find by company id.
     *
     * @return the annual paid leave setting find dto
     */
//    @POST
//    @Path("find/setting")
//    public AnnualPaidLeaveSettingFindDto findByCompanyId() {
//        return this.annualFinder.findByCompanyId();
//    }
}
