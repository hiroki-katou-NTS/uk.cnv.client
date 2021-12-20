package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregatePersonalMapDto {

	// Map<社員ID, 想定給与額>
	public List<EstimatedSalaryMapDto> estimatedSalary = Collections.emptyList();
	
	// Map<社員ID, Map<回数集計, BigDecimal>>
	public List<TotalTimesMapDtoList> timeCount = Collections.emptyList();
	
	// Map<社員ID, Map<集計対象の勤怠時間, BigDecimal>>
	public List<WorkMapDtoList> workHours = Collections.emptyList();
	
	
	public static AggregatePersonalMapDto convertMap(AggregatePersonalDto dto) {
		return new AggregatePersonalMapDto(
				dto.convertEstimatedSalary(),
				dto.convertTimeCount(),
				dto.convertWorkhours()
				);
	}
}
