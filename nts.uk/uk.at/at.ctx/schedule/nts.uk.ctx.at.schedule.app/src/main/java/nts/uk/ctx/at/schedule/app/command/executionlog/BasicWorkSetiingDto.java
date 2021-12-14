package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.BasicWorkSetting;

@Data
@AllArgsConstructor
public class BasicWorkSetiingDto {
	private Optional<BasicWorkSetting> basicSet;
	private Optional<ScheduleErrorLog> scheduleErrorLog;
}
