package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.LaborCostAggregationUnit;
/**
 * 
 * @author hoangnd
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class LaborCostAggregationUnitDto {
	/** 集計単位  **/
	public int unit;
	/** 項目種類  **/
	public int itemType;
	
	public static LaborCostAggregationUnitDto fromDomain(LaborCostAggregationUnit domain) {
		return new LaborCostAggregationUnitDto(domain.getUnit().value, domain.getItemType().value);
	}
}
