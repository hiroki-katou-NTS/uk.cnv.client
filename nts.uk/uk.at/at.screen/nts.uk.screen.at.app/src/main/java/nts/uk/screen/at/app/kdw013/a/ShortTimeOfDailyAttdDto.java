package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.find.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheetDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShortTimeOfDailyAttdDto {
	private List<ShortWorkingTimeSheetDto> shortWorkingTimeSheets;

	public static ShortTimeOfDailyAttdDto fromDomain(Optional<ShortTimeOfDailyAttd> shortTime) {

		return shortTime.map(x -> new ShortTimeOfDailyAttdDto(x.getShortWorkingTimeSheets().stream()
				.map(sw -> ShortWorkingTimeSheetDto.fromDomain(sw)).collect(Collectors.toList()))).orElse(null);
	}
}
