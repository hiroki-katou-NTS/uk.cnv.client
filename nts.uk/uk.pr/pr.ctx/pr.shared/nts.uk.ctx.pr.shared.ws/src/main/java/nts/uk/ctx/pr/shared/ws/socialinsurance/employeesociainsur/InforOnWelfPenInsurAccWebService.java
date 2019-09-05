package nts.uk.ctx.pr.shared.ws.socialinsurance.employeesociainsur;


import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pr.shared.app.command.socialinsurance.employeesociainsur.emphealinsurbeneinfo.AddEmpBasicPenNumInforCommandHandler;
import nts.uk.ctx.pr.shared.app.command.socialinsurance.employeesociainsur.emphealinsurbeneinfo.CredentialAcquisitionInfoCommand;
import nts.uk.ctx.pr.shared.app.find.socialinsurance.employeesociainsur.emphealinsurbeneinfo.*;
import nts.uk.ctx.pr.shared.dom.adapter.query.person.PersonInfoExportAdapter;
import nts.uk.ctx.pr.shared.dom.adapter.query.person.PersonInfomationAdapter;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.EmpBasicPenNumInfor;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.MultiEmpWorkInfo;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.SocialInsurAcquisiInfor;
import nts.uk.shr.com.context.AppContexts;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Optional;

@Path("shared/employeesociainsur")
@Produces("application/json")
public class InforOnWelfPenInsurAccWebService extends WebService{

    @Inject
    private PersonInfomationAdapter adapter;

    @Inject
    private AddEmpBasicPenNumInforCommandHandler commandHandler;

    @Inject
    private SocialInsurAcquisiInforFinder socialInsurAcquisiInforFinder;

    @Inject
    private MultiEmpWorkInfoFinder multiEmpWorkInfoFinder;

    @Inject
    private EmpBasicPenNumInforFinder empBasicPenNumInforFinder;


    @POST
    @Path("getPersonInfo/{empID}")
    public PersonInfoExportAdapter getPersonInfo(@PathParam("empID")String empID){
        return adapter.getPersonInfo(empID);
    }

    @POST
    @Path("add")
    public void add(CredentialAcquisitionInfoCommand command){
        commandHandler.handle(command);
    }


    @POST
    @Path("getSocialInsurAcquisiInforById/{empID}")
    public SocialInsurAcquisiInforDto getSocialInsurAcquisiInforById(@PathParam("empID")String empID){
        String cid = AppContexts.user().companyId();
        Optional<SocialInsurAcquisiInfor> socialInsurAcquisiInfor = socialInsurAcquisiInforFinder.getSocialInsurAcquisiInforById(cid, empID);

        if(socialInsurAcquisiInfor.isPresent()){
            return SocialInsurAcquisiInforDto.fromDomain(socialInsurAcquisiInfor.get());
        }

        return null;
    }

    @POST
    @Path("getMultiEmpWorkInfoById/{empID}")
    public MultiEmpWorkInfoDto getMultiEmpWorkInfoById(@PathParam("empID")String empID){
        String cid = AppContexts.user().companyId();
        Optional<MultiEmpWorkInfo> multiEmpWorkInfo = multiEmpWorkInfoFinder.getMultiEmpWorkInfoById(empID);

        if(multiEmpWorkInfo.isPresent()){
            return MultiEmpWorkInfoDto.fromDomain(multiEmpWorkInfo.get());
        }

        return null;
    }


    @POST
    @Path("getEmpBasicPenNumInforById/{empID}")
    public EmpBasicPenNumInforDto getEmpBasicPenNumInforById(@PathParam("empID")String empID){
        Optional<EmpBasicPenNumInfor> empBasicPenNumInfor = empBasicPenNumInforFinder.getEmpBasicPenNumInforById(empID);

        if(empBasicPenNumInfor.isPresent()){
            return EmpBasicPenNumInforDto.fromDomain(empBasicPenNumInfor.get());
        }

        return null;
    }
}
