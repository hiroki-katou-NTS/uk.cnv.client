package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.request.app.find.application.overtime.AnyItemValueDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnyItemValueOfDailyAttdDto {
	private List<AnyItemValueDto> items;

	public static AnyItemValueOfDailyAttdDto fromDomain(AnyItemValueOfDailyAttd domain) {

		return new AnyItemValueOfDailyAttdDto(
				domain.getItems().stream().map(item -> AnyItemValueDto.fromDomain(item)).collect(Collectors.toList()));
	}
}
