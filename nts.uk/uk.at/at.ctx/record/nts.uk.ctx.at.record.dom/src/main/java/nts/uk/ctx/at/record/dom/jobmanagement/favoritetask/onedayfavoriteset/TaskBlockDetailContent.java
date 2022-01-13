package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * ValueObject: 作業ブロック詳細内容
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.作業ブロック詳細内容
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class TaskBlockDetailContent implements DomainValue {
	
	/** 開始時刻*/
	private final TimeWithDayAttr startTime;
	
	/** 終了時刻*/
	private final TimeWithDayAttr endTime;
	
	/** 作業詳細*/
	private List<TaskContentForEachSupportFrame> taskContents;

}
