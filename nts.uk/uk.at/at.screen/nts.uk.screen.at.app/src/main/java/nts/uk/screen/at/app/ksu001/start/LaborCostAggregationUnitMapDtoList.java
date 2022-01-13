package nts.uk.screen.at.app.ksu001.start;


import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class LaborCostAggregationUnitMapDtoList {
	
	public GeneralDate date;
	
	public List<LaborCostAggregationUnitMapDto> laborCostAndTime = Collections.emptyList();
}
