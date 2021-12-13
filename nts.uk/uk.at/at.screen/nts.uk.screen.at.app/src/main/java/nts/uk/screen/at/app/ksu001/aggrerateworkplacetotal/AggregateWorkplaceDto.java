package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.TotalTimesDto;
import nts.uk.screen.at.app.ksu001.start.ExternalBudgetMapDto;
import nts.uk.screen.at.app.ksu001.start.ExternalBudgetMapDtoList;
import nts.uk.screen.at.app.ksu001.start.LaborCostAggregationUnitMapDto;
import nts.uk.screen.at.app.ksu001.start.LaborCostAggregationUnitMapDtoList;
import nts.uk.screen.at.app.ksu001.start.NumberOfPeopleByEachWorkMethodMapDto;
import nts.uk.screen.at.app.ksu001.start.TotalTimesWpMapDto;
import nts.uk.screen.at.app.ksu001.start.TotalTimesWpMapDtoList;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregateWorkplaceDto {

	// Map<年月日, Map<職場計の人件費の集計単位, BigDecimal>>
	public Map<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>> laborCostAndTime = new HashMap<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>>();
	
	// Map<年月日, Map<回数集計, BigDecimal>>
	public Map<GeneralDate, Map<TotalTimesDto, BigDecimal>> timeCount = new HashMap<GeneralDate, Map<TotalTimesDto, BigDecimal>>();
	
	// Map<年月日, Map<(雇用マスタ or 分類マスタ or 職位情報), BigDecimal>>
	public AggregateNumberPeopleDto aggrerateNumberPeople = new AggregateNumberPeopleDto();
	
	// Map<年月日, Map<外部予算実績項目, 外部予算実績値>>
	public Map<GeneralDate, Map<ExternalBudgetDto, String>> externalBudget = new HashMap<GeneralDate, Map<ExternalBudgetDto, String>>();
	
	// Map<年月日, List<勤務方法別の人数<T>>>
	public Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>> peopleMethod = new HashMap<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>>();
	
	
	public List<LaborCostAggregationUnitMapDtoList> convertLaborCostAndTime() {
		
		return this.laborCostAndTime
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new LaborCostAggregationUnitMapDto(
									  x.getKey().unit,
									  x.getKey().itemType,
									  x.getValue()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new LaborCostAggregationUnitMapDtoList(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
	}
	
	public List<TotalTimesWpMapDtoList> convertTimeCount() {
		return this.timeCount
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new TotalTimesWpMapDto(
									  x.getKey().totalCountNo,
									  x.getKey().totalTimesName,
									  x.getValue()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new TotalTimesWpMapDtoList(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
	}
	
	public List<ExternalBudgetMapDtoList> convertExternalBudget() {
		
		return this.externalBudget
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> e.getValue()
							  .entrySet()
							  .stream()
							  .map(x -> new ExternalBudgetMapDto(
									  x.getKey().getExternalBudgetCode(),
									  x.getKey().getExternalBudgetName(),
									  x.getValue(),
									  x.getKey().getBudgetAtr(),
									  x.getKey().getUnitAtr()
									  ))
							  .collect(Collectors.toList())
						)
				)
				.entrySet()
				.stream()
				.map(x -> new ExternalBudgetMapDtoList(x.getKey(), x.getValue()))
				.collect(Collectors.toList());
	}
	
	public List<NumberOfPeopleByEachWorkMethodMapDto> convertPeopleMethod() {
		
		return this.peopleMethod
					.entrySet()
					.stream()
					.map(x -> new NumberOfPeopleByEachWorkMethodMapDto(x.getKey(), x.getValue()))
					.collect(Collectors.toList());
					
	}
}
