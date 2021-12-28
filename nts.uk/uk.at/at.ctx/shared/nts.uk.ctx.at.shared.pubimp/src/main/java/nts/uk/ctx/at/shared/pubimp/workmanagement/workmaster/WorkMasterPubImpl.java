package nts.uk.ctx.at.shared.pubimp.workmanagement.workmaster;

import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.ExternalCorporationInfoPubExport;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.TaskDisplayInfoPubExport;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.TaskPubExport;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.WorkMasterPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Stateless
public class WorkMasterPubImpl implements WorkMasterPub {
    @Inject
    private TaskingRepository taskRepo;

    @Override
    public List<TaskPubExport> getListTask(String cid, Integer taskFrameNo, List<String> codes) {
        val lstTask = taskRepo.getListTask(cid, taskFrameNo, codes);
        if (lstTask.isEmpty()) return Collections.emptyList();

        return toTaskPubExport(lstTask);
    }

    private List<TaskPubExport> toTaskPubExport(List<Task> taskList) {
        return taskList.stream().map(e ->
                new TaskPubExport(
                        e.getCode().v(),
                        e.getTaskFrameNo().v(),
                        new ExternalCorporationInfoPubExport(
                                e.getCooperationInfo().getExternalCode1().isPresent() ? e.getCooperationInfo().getExternalCode1().get().v() : null,
                                e.getCooperationInfo().getExternalCode2().isPresent() ? e.getCooperationInfo().getExternalCode2().get().v() : null,
                                e.getCooperationInfo().getExternalCode3().isPresent() ? e.getCooperationInfo().getExternalCode3().get().v() : null,
                                e.getCooperationInfo().getExternalCode4().isPresent() ? e.getCooperationInfo().getExternalCode4().get().v() : null,
                                e.getCooperationInfo().getExternalCode5().isPresent() ? e.getCooperationInfo().getExternalCode5().get().v() : null
                        ),
                        e.getChildTaskList().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()),
                        e.getExpirationDate(),
                        new TaskDisplayInfoPubExport(
                                e.getDisplayInfo().getTaskName().v(),
                                e.getDisplayInfo().getTaskAbName().v(),
                                e.getDisplayInfo().getColor().isPresent() ? e.getDisplayInfo().getColor().get().v() : null,
                                e.getDisplayInfo().getTaskNote().isPresent() ? e.getDisplayInfo().getTaskNote().get().v() : null
                        )
                )
        ).collect(Collectors.toList());
    }
}
