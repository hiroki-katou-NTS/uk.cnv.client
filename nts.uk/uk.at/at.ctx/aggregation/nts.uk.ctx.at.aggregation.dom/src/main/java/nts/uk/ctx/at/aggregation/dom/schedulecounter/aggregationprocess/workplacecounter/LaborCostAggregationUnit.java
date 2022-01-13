package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;

/**
 * 職場計の人件費の集計単位
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.人件費・時間.職場計の人件費項目種類
 * @author lan_lt
 *
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LaborCostAggregationUnit {
	
	/** 集計単位  **/
	private AggregationUnitOfLaborCosts unit;
	
	/** 項目種類  **/
	private LaborCostItemType itemType;
	
}
