package nts.uk.ctx.at.shared.app.find.worktime.worktimeset.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.UseATR;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkTimeResultDto {
    private Integer allCheckStatus;
    private List<WorkTimeDto> allWorkHours;
    private List<WorkTimeDto> availableWorkingHours;
    private List<WorkTimeDto> workingHoursByWorkplace;
    private String companyID;
    private Integer useATR;
}
