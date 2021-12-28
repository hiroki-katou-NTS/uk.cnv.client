package nts.uk.ctx.at.record.dom.daily.timegroup;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.日別実績の作業時間帯グループ.日別実績の作業時間帯グループ
 * AR: 日別実績の作業時間帯グループ
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class TaskTimeGroup extends AggregateRoot {
	
	/** 社員ID */
	private final String sId;
	
	/** 年月日 */
	private final GeneralDate date;
	
	/** 時間帯リスト */
	private List<TaskTimeZone> timezones;

}
