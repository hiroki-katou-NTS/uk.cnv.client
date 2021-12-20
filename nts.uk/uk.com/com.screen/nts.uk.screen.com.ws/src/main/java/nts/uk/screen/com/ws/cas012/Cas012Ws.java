package nts.uk.screen.com.ws.cas012;


import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.auth.app.find.grant.roleindividual.dto.EmployeeBasicInfoDto;
import nts.uk.ctx.sys.auth.dom.adapter.company.CompanyImport;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.screen.com.app.find.cas012.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Path("screen/com/cas012")
@Produces("application/json")
public class Cas012Ws extends WebService {
    @Inject
    private GetListOfCompaniesScreenQuery getListOfCompaniesScreenQuery;
    @Inject
    private SearchForRolePersonalizedGrantScreenQuery grantScreenQuery;
    @Inject
    private SelectIndividualRoleGrantScreenQuery roleGrantScreenQuery;
    @Inject
    private GetEmployeeListScreenQuery getEmployeeListScreenQuery;


    @POST
    @Path("a/get-data-init")
    public Cas012Dto getDataForStartUp() {
        List<EnumConstant> enumRoleType = EnumAdaptor.convertToValueNameList(RoleType.class, RoleType.SYSTEM_MANAGER, RoleType.COMPANY_MANAGER);
        List<CompanyImport> companyImportList = getListOfCompaniesScreenQuery.getListCompany();
        return new Cas012Dto(enumRoleType, companyImportList);
    }

    @POST
    @Path("a/get-employee")
    public List<Cas012aDto> getListEmployee(CidAndRoleTypeParams params) {
        return grantScreenQuery.getListPersonalizedGrant(params.getRoleType(), params.getCid());
    }

    @POST
    @Path("a/get-employee-info")
    public WplAndJobInfoDto getEmployeeInfo(EmployeeInfoParam params) {
        return roleGrantScreenQuery.getPersonalPromotion(params.getCid(), params.getSid());
    }

    @POST
    @Path("b/get-employee-info/{cid}")
    public List<EmployeeBasicInfoDto> getEmployeeInfo(@PathParam("cid") String cid) {
        return getEmployeeListScreenQuery.getListEmployee(cid);
    }

}