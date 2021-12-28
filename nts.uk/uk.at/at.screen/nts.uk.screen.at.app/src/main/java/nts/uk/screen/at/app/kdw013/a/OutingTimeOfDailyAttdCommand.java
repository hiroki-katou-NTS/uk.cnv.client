package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;

@AllArgsConstructor
@Getter
public class OutingTimeOfDailyAttdCommand {
	private List<OutingTimeSheetCommand> outingTimeSheets = new ArrayList<>();

	public OutingTimeOfDailyAttd toDomain() {
		return new OutingTimeOfDailyAttd(
				this.getOutingTimeSheets().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}

}
