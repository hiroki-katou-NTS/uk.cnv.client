package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.dailyperformance.correction.dto.EmploymentDto;
import nts.uk.screen.at.app.ksu001.start.NumberPeopleMapDto;
import nts.uk.screen.at.app.ksu001.start.NumberPeopleMapDtoList;
/**
 * Map<年月日, Map<(雇用マスタ or 分類マスタ or 職位情報), BigDecimal>>
 * @author hoangnd
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class AggregateNumberPeopleDto {

	public Map<GeneralDate, Map<EmploymentDto, BigDecimal>> employment = new HashMap<GeneralDate, Map<EmploymentDto, BigDecimal>>();
	
	public Map<GeneralDate, Map<ClassificationDto, BigDecimal>> classification = new HashMap<GeneralDate, Map<ClassificationDto, BigDecimal>>();

	public Map<GeneralDate, Map<JobTitleInfoDto, BigDecimal>> jobTitleInfo = new HashMap<GeneralDate, Map<JobTitleInfoDto, BigDecimal>>();
	
	public List<NumberPeopleMapDtoList> convertEmployment() {
		return this.employment
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new NumberPeopleMapDto(
									  x.getKey().getEmploymentCode(),
									  x.getKey().getEmploymentName(),
									  x.getValue()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new NumberPeopleMapDtoList(x.getKey(), x.getValue().stream().sorted(Comparator.nullsLast(Comparator.comparing(y -> y.code))).collect(Collectors.toList())))
				.collect(Collectors.toList());
	}
	
	public List<NumberPeopleMapDtoList> convertClassification() {
		return this.classification
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new NumberPeopleMapDto(
									  x.getKey().getClassificationCode(),
									  x.getKey().getClassificationName(),
									  x.getValue()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new NumberPeopleMapDtoList(x.getKey(), x.getValue().stream().sorted(Comparator.nullsLast(Comparator.comparing(y -> y.code))).collect(Collectors.toList())))
				.collect(Collectors.toList());
	}
	
	public List<NumberPeopleMapDtoList> convertJobTitleInfo() {
		
		// sort condition
		Comparator<NumberPeopleMapDto> sortCond = Comparator.comparing(NumberPeopleMapDto::getRankCode,Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(NumberPeopleMapDto::getCode);
		
		List<NumberPeopleMapDtoList> rs = this.jobTitleInfo
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new NumberPeopleMapDto(
									  x.getKey().getJobTitleCode(),
									  x.getKey().getJobTitleName(),
									  x.getValue(),
									  x.getKey().getSequenceCode()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new NumberPeopleMapDtoList(x.getKey(), x.getValue().stream().sorted(sortCond).collect(Collectors.toList())))
				.collect(Collectors.toList());
		
		return rs;
	}
}
