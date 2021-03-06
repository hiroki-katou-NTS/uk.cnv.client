package nts.uk.screen.at.ws.kmt.kmt001;

import lombok.val;
import nts.arc.layer.ws.WebService;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.shared.app.query.task.GetsTheChildTaskOfTheSpecifiedTask;
import nts.uk.screen.at.app.kmt001.*;
import nts.uk.screen.at.app.kmt009.ExternalCooperationInfoDto;
import nts.uk.screen.at.app.kmt009.TaskDisplayInfoDto;
import nts.uk.screen.at.app.query.kmt.kmt005.TaskFrameSettingDto;
import nts.uk.shr.com.context.AppContexts;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;


@Path("at/shared/scherec/workmanagement/work/kmt001")
@Produces("application/json")
public class KmtWebService extends WebService {

    @Inject
    private AcquireWorkListAndWorkDetailsDisplayed acquireWorkListAndWorkDetailsDisplayed;

    @Inject
    private InforAcquisitionProcessAtStartup inforAcquisitionProcessAtStartup;

    @Inject
    private GetsTheChildTaskOfTheSpecifiedTask getsTheChildTaskOfTheSpecifiedTask;

    @POST
    @Path("getlist")
    public TaskResultDto getData(KmtParamDto param) {
        return acquireWorkListAndWorkDetailsDisplayed.getData(param.getCid(), param.getFrameNo());
    }

    @POST
    @Path("init")
    public Kmt001InitDto getData() {
        return inforAcquisitionProcessAtStartup.getInforAcquisition();
    }

    @POST
    @Path("getlistchild")
    public List<KmtDto> getListTaskChild(KmtParamDto param) {
        val cid = AppContexts.user().companyId();
        val tk = getsTheChildTaskOfTheSpecifiedTask.getAllChildTask(cid, param.getFrameNo(), param.getCode());
        return tk.stream().map(e ->
                new KmtDto(
                        e.getCode().v(),
                        e.getTaskFrameNo().v(),
                        new ExternalCooperationInfoDto(
                                e.getCooperationInfo().getExternalCode1().isPresent() ? e.getCooperationInfo()
                                        .getExternalCode1().get().v() : null,
                                e.getCooperationInfo().getExternalCode2().isPresent() ? e.getCooperationInfo()
                                        .getExternalCode2().get().v() : null,
                                e.getCooperationInfo().getExternalCode3().isPresent() ? e.getCooperationInfo()
                                        .getExternalCode3().get().v() : null,
                                e.getCooperationInfo().getExternalCode4().isPresent() ? e.getCooperationInfo()
                                        .getExternalCode4().get().v() : null,
                                e.getCooperationInfo().getExternalCode5().isPresent() ? e.getCooperationInfo()
                                        .getExternalCode5().get().v() : null
                        ),
                        e.getChildTaskList().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()),
                        e.getExpirationDate().start(),
                        e.getExpirationDate().end(),
                        new TaskDisplayInfoDto(
                                e.getDisplayInfo().getTaskName().v(),
                                e.getDisplayInfo().getTaskAbName().v(),
                                e.getDisplayInfo().getColor().isPresent() ? e.getDisplayInfo().getColor()
                                        .get().v() : null,
                                e.getDisplayInfo().getTaskNote().isPresent() ? e.getDisplayInfo().getTaskNote()
                                        .get().v() : null
                        )
                )
        ).collect(Collectors.toList());
    }
}
