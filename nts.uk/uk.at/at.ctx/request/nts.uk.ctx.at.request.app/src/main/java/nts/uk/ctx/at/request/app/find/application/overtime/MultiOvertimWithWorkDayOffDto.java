package nts.uk.ctx.at.request.app.find.application.overtime;

import lombok.Value;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.AppOverTimeDto;

import java.util.List;

@Value
public class MultiOvertimWithWorkDayOffDto {
    private AppOverTimeDto latestMultiOvertimeApp;
    private List<WorkdayoffFrameDto> dayOffWorkFrames;
}
