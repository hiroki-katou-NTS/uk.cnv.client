package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.kdw013.command.TaskTimeZoneCommand;
import nts.uk.screen.at.app.kdw013.command.TimeSpanForCalcCommand;

@Getter
@AllArgsConstructor
public class WorkDetailCommand {
	
	/** 年月日 */
	private GeneralDate date;

	/** List<作業詳細> */
	private List<WorkDetailsParamCommand> lstWorkDetailsParamCommand;

	public List<TaskTimeZoneCommand> toTimeZones() {
		
		return lstWorkDetailsParamCommand.stream()
				.map(wdp -> new TaskTimeZoneCommand(
						new TimeSpanForCalcCommand(wdp.getTimeZone().getStart(), wdp.getTimeZone().getEnd()),
						wdp.getSupportFrameNos()))
				.collect(Collectors.toList());
	}

}
