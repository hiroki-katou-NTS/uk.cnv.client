/**
 * 
 */
package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * AR: 応援の同一打刻の判断基準
 * path: UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.打刻管理.応援
 * @author laitv
 *
 */

@Getter
public class JudgmentCriteriaSameStampOfSupport implements DomainAggregate {

	// 会社ID
	private final String cid;

	// 同一打刻とみなす範囲
	private final RangeRegardedSupportStamp sameStampRanceInMinutes;

	// 最大応援回数
	private final MaximumNumberOfSupport supportMaxFrame;
	
	// [C-1] 応援の同一打刻の判断基準を作成する	
	public JudgmentCriteriaSameStampOfSupport(String cid,
			RangeRegardedSupportStamp sameStampRanceInMinutes,MaximumNumberOfSupport supportMaxFrame) {
		super();
		this.cid = cid;
		this.sameStampRanceInMinutes = sameStampRanceInMinutes;
		this.supportMaxFrame = supportMaxFrame;
	}
	
	/**
	 * [1] 同一と認識すべきの打刻か
	 * path : UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.打刻管理.応援.応援の同一打刻の判断基準.同一と認識すべきの打刻か
	 * 「パラメータ」 
	 * ・基準打刻：時刻（日区分付き） standardStamp
	 * ・対象打刻：時刻（日区分付き） targetStamp
	 * 「Output」 ・Boolean
	 */
	public boolean checkStampRecognizedAsSame(TimeWithDayAttr standardStamp, TimeWithDayAttr targetStamp) {
		
		/** 同一の打刻化を確認する */
		return Math.abs(standardStamp.valueAsMinutes() - targetStamp.valueAsMinutes()) <= this.sameStampRanceInMinutes.v();
	}
}
