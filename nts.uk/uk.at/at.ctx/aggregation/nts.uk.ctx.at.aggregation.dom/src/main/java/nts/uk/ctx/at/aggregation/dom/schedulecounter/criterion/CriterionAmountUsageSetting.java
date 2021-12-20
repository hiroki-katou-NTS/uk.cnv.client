package nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 目安利用区分
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.目安金額.目安利用区分
 * @author lan_lt
 *
 */
@Getter
@AllArgsConstructor
public class CriterionAmountUsageSetting implements DomainAggregate {

	/** 会社ID */
	private final String cid;

	/** 雇用利用区分 */
	private NotUseAtr employmentUse;
	
	public void update(NotUseAtr employmentUse) {
		this.employmentUse = employmentUse;
	}

}