package nts.uk.ctx.at.record.dom.daily.timegroup;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * ValueObject: 作業時間帯
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.日別実績の作業時間帯グループ.作業時間帯
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class TaskTimeZone implements DomainValue {

	/** 時間帯 */
	private final TimeSpanForCalc caltimeSpan;

	/** 対象応援勤務枠 */
	private final List<SupportFrameNo> supNos;
}
