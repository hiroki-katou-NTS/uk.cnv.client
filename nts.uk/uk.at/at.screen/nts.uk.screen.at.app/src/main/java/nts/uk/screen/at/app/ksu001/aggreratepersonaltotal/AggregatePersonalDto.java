package nts.uk.screen.at.app.ksu001.aggreratepersonaltotal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.start.EstimatedSalaryMapDto;
import nts.uk.screen.at.app.ksu001.start.TotalTimesMapDto;
import nts.uk.screen.at.app.ksu001.start.TotalTimesMapDtoList;
import nts.uk.screen.at.app.ksu001.start.WorkMapDto;
import nts.uk.screen.at.app.ksu001.start.WorkMapDtoList;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 個人計集計結果　←集計内容によって情報が異なる
 * @author hoangnd
 *
 */
public class AggregatePersonalDto {
	
	// Map<社員ID, 想定給与額>
	public Map<String, EstimatedSalaryDto> estimatedSalary = new HashMap<String, EstimatedSalaryDto>();
	
	// Map<社員ID, Map<回数集計, BigDecimal>>
	public Map<String, Map<TotalTimesDto, BigDecimal>> timeCount = new HashMap<String, Map<TotalTimesDto, BigDecimal>>();
	
	
	// Map<社員ID, Map<集計対象の勤怠時間, BigDecimal>>
	public Map<String, Map<Integer, BigDecimal>> workHours = new HashMap<String, Map<Integer, BigDecimal>>();

	
	public List<EstimatedSalaryMapDto> convertEstimatedSalary() {
		
		return this.estimatedSalary
			.entrySet()
			.stream()
			.map(x -> new EstimatedSalaryMapDto(
									x.getKey(),
									x.getValue().getSalary(),
									x.getValue().getCriterion(),
									x.getValue().getBackground()
									)
			)
			.collect(Collectors.toList());
		
		
	}
	
	public List<TotalTimesMapDtoList> convertTimeCount() {
		
		
		
		return this.timeCount
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
					e -> e.getKey(),
					e -> e.getValue()
						  .entrySet()
						  .stream()
						  .map(x -> new TotalTimesMapDto(
								  x.getKey().getTotalCountNo(),
								  x.getKey().getTotalTimesName(),
								  x.getValue()
								  ))
						  .collect(Collectors.toList())
					)
			)
			.entrySet()
			.stream()
			.map(x -> new TotalTimesMapDtoList(x.getKey(), x.getValue()))
			.collect(Collectors.toList());
		
		
	}
	
	public List<WorkMapDtoList> convertWorkhours() {
		
		return this.workHours
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new WorkMapDto(
									  x.getKey(),
									  x.getValue()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new WorkMapDtoList(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
	}
}
