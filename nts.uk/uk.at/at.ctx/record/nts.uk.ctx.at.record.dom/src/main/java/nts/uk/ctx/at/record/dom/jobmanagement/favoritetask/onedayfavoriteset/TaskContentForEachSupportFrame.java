package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * ValueObject: 応援枠ごとの作業内容
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.応援枠ごとの作業内容
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class TaskContentForEachSupportFrame implements DomainValue{
	
	/** 応援勤務枠No*/
	private final SupportFrameNo frameNo;
	
	/** 作業内容*/
	private final List<TaskContent> taskContent;
	
	/** 作業時間*/
	private final Optional<AttendanceTime> attendanceTime;
}
 