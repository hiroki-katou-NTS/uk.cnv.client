package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;

@AllArgsConstructor
@Getter
public class BreakTimeOfDailyAttdCommand {

	// 時間帯
	private List<BreakTimeSheetCommand> breakTimeSheets;

	public BreakTimeOfDailyAttd toDomain() {

		return new BreakTimeOfDailyAttd(
				this.getBreakTimeSheets().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}
}
