package nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * 枠別の目安金額
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.枠別の目安金額
 * @author lan_lt
 *
 */
@Value
@AllArgsConstructor
public class CriterionAmountByNo implements DomainObject {

	/** 枠NO */
	private final CriterionAmountNo frameNo;

	/** 目安金額 */
	private final CriterionAmountValue amount;

}
