package nts.uk.ctx.at.record.ac.workrecord;

import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.TaskImport;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.WorkMasterAdapter;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.TaskPubExport;
import nts.uk.ctx.at.shared.pub.workmanagement.workmaster.WorkMasterPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class WorkMasterAdapterImpl implements WorkMasterAdapter {
    @Inject
    private WorkMasterPub workMasterPub;

    @Override
    public List<TaskImport> getTaskList(String cid, Integer taskFrameNo, List<String> codes) {
        List<TaskPubExport> taskExportList = workMasterPub.getListTask(cid, taskFrameNo, codes);

        return taskExportList.stream().map(t -> new TaskImport(
                t.getCode(),
                t.getTaskFrameNo(),
                t.getDisplayInfo().getTaskName()
        )).sorted(Comparator.comparing(TaskImport::getCode)).collect(Collectors.toList());
    }
}
