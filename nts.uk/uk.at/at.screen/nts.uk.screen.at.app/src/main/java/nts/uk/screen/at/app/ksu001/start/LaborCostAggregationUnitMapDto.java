package nts.uk.screen.at.app.ksu001.start;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LaborCostAggregationUnitMapDto {

	
	
	/** 集計単位  **/
	public int unit;
	/** 項目種類  **/
	public int itemType;
	
	public BigDecimal value;
}
