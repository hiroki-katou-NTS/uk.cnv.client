package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutingTimeOfDailyAttdDto {
	private List<OutingTimeSheetDto> outingTimeSheets = new ArrayList<>();

	public static OutingTimeOfDailyAttdDto fromDomain(Optional<OutingTimeOfDailyAttd> domain) {

		return domain.map(od -> new OutingTimeOfDailyAttdDto(od.getOutingTimeSheets().stream()
				.map(ots -> OutingTimeSheetDto.fromDomain(ots)).collect(Collectors.toList()))).orElse(null);
	}
}
