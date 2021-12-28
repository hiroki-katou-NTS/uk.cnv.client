package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;

/**
 * ValueObject: 工数実績作業詳細
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.日別勤怠(Work)から工数実績項目に変換する.工数実績作業詳細
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class ManHrTaskDetail extends DomainObject {
	
	/** 工数項目リスト*/
	private List<TaskItemValue> taskItemValues;
	
	/** 応援勤務枠No*/
	private SupportFrameNo supNo;
}
