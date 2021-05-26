package nts.uk.screen.at.app.ksu001.start;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggrerateWorkplaceDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggregateWorkplaceMapDto {
	
	// Map<年月日, Map<職場計の人件費の集計単位, BigDecimal>>
	public List<LaborCostAggregationUnitMapDtoList> laborCostAndTime = Collections.emptyList();
	
	// Map<年月日, Map<回数集計, BigDecimal>>
	public List<TotalTimesWpMapDtoList> timeCount = Collections.emptyList();
	
	// Map<年月日, Map<(雇用マスタ or 分類マスタ or 職位情報), BigDecimal>>
	public AggregateNumberPeopleMapDto aggrerateNumberPeople;
	
	// Map<年月日, Map<外部予算実績項目, 外部予算実績値>>
	public List<ExternalBudgetMapDtoList> externalBudget = Collections.emptyList();
			
	
	public static AggregateWorkplaceMapDto convertMap(AggrerateWorkplaceDto dto) {
		
		
		return new AggregateWorkplaceMapDto(
				dto.convertLaborCostAndTime(),
				dto.convertTimeCount(),
				Optional.ofNullable(dto.getAggrerateNumberPeople())
					.map(x -> new AggregateNumberPeopleMapDto(
							x.convertEmployment(),
							x.convertClassification(),
							x.convertJobTitleInfo()
							))
					.orElse(null)
				,
				dto.convertExternalBudget()
				);
		
		
	}
}
