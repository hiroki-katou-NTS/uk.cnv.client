package nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.uk.shr.com.color.ColorCode;

/**
 * 枠別の扱い
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.枠別の扱い
 * @author lan_lt
 *
 */
@Value
@AllArgsConstructor
public class HandlingOfCriterionAmountByNo implements DomainValue {

	/** 枠NO */
	private final CriterionAmountNo frameNo;

	/** 背景色 */
	private final ColorCode backgroundColor;

}
