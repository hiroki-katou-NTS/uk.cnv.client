package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecificDateAttrOfDailyAttdDto {
	// 特定日区分
	private List<SpecificDateAttrSheetDto> specificDateAttrSheets;

	public static SpecificDateAttrOfDailyAttdDto fromDomain(Optional<SpecificDateAttrOfDailyAttd> domain) {
		return domain
				.map(x -> new SpecificDateAttrOfDailyAttdDto(x.getSpecificDateAttrSheets().stream()
						.map(sas -> SpecificDateAttrSheetDto.fromDomain(sas)).collect(Collectors.toList())))
				.orElse(null);
	}
}
