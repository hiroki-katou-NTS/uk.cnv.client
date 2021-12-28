package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;

@AllArgsConstructor
@Getter
public class ShortTimeOfDailyAttdCommand {
	private List<ShortWorkingTimeSheetCommand> shortWorkingTimeSheets;

	public ShortTimeOfDailyAttd toDomain() {
		return new ShortTimeOfDailyAttd(
				this.getShortWorkingTimeSheets().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}
}
