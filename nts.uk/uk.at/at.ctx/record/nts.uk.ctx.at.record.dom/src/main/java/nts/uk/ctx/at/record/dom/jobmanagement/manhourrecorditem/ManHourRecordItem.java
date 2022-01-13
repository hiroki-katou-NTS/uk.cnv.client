package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.工数実績項目
 * AR: 工数実績項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class ManHourRecordItem extends AggregateRoot {
	
	/** 項目ID*/
	private final int itemId;
	
	/** 名称*/
	private String name;
	
	/** フォーマット設定に表示する*/
	private final UseAtr useAtr;
}
