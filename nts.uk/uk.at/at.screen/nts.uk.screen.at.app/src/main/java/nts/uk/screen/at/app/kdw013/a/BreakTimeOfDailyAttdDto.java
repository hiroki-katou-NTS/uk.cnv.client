package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.BreakTimeSheetDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BreakTimeOfDailyAttdDto {

	// 時間帯
	private List<BreakTimeSheetDto> breakTimeSheets;

	public static BreakTimeOfDailyAttdDto fromDomain(BreakTimeOfDailyAttd domain) {
		return new BreakTimeOfDailyAttdDto(domain.getBreakTimeSheets().stream()
				.map(x -> BreakTimeSheetDto.fromDomain(x)).collect(Collectors.toList()));
	}

}
