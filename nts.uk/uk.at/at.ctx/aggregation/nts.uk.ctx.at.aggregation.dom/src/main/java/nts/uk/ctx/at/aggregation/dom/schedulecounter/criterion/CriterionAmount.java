package nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;

/**
 * 目安金額
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.目安金額
 * @author lan_lt
 *
 */
@Value
@AllArgsConstructor
public class CriterionAmount implements DomainValue {

	/** 年間目安金額 */
	private final CriterionAmountList yearly;

	/** 月度目安金額 */
	private final CriterionAmountList monthly;

}
