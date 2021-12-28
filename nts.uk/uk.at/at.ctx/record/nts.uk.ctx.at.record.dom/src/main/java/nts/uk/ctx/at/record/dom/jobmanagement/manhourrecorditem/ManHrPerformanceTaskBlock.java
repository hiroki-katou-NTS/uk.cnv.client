package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * ValueObject: 工数実績作業ブロック
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数実績項目.工数実績作業ブロックを作成する.工数実績作業ブロック
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class ManHrPerformanceTaskBlock implements DomainObject {
	
	/** 時間帯 */
	private final TimeSpanForCalc caltimeSpan;
	
	/**	作業詳細*/
	private final List<ManHrTaskDetail> taskDetails;
}
