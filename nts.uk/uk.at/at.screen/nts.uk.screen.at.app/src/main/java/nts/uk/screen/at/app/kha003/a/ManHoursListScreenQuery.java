package nts.uk.screen.at.app.kha003.a;

import lombok.val;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.operationsettings.TaskOperationMethod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.operationsettings.TaskOperationSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.screen.at.app.kha003.ManHourInfoDto;
import nts.uk.screen.at.app.kha003.ManHoursDto;
import nts.uk.screen.at.app.kha003.TaskFrameSettingDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ScreenQuery: 工数集計一覧の取得する
 */
@Stateless
public class ManHoursListScreenQuery {
    @Inject
    private ManHourSummaryTableFormatRepository manHourRepo;

    @Inject
    private TaskOperationSettingRepository taskOperationRepo;

    @Inject
    private TaskFrameUsageSettingRepository taskFrameUsageRepo;

    public ManHoursDto getManHoursList() {
        List<TaskFrameSettingDto> taskFrameSettings = new ArrayList<>();
        String companyId = AppContexts.user().companyId();

        val lstManHourSummaryTable = this.manHourRepo.getAll(companyId);
        val manHoursSummaryTables = lstManHourSummaryTable.stream().map(item -> new ManHourInfoDto(
                item.getCode().v(), item.getName().v())).collect(Collectors.toList());

        val taskSetting = this.taskOperationRepo.getTasksOperationSetting(companyId);
        if (taskSetting.isPresent() && taskSetting.get().getTaskOperationMethod().value == TaskOperationMethod.USED_IN_ACHIEVENTS.value) {
            val taskFrameUsage = this.taskFrameUsageRepo.getWorkFrameUsageSetting(companyId);
            if (Objects.nonNull(taskFrameUsage) && !taskFrameUsage.getFrameSettingList().isEmpty()) {
                taskFrameSettings = taskFrameUsage.getFrameSettingList().stream().map(x -> new TaskFrameSettingDto(
                        x.getTaskFrameNo().v(),
                        x.getTaskFrameName().v(),
                        x.getUseAtr().value)).collect(Collectors.toList());
            }
        }

        return new ManHoursDto(manHoursSummaryTables, taskFrameSettings);
    }
}
