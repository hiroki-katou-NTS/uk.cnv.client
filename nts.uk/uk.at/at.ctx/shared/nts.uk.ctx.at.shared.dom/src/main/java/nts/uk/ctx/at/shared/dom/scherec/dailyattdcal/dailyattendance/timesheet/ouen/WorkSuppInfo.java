package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/** UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別時間帯別勤怠.応援実績.時間帯.作業補足情報.作業補足情報
 * ValueObject 作業補足情報
 * 
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class WorkSuppInfo implements DomainObject {

	/** 補足時間情報: List<補足情報の時間項目> */
	private List<SuppInfoTimeItem> suppInfoTimeItems;
	
	/** 補足数値情報: List<補足情報の数値項目> */
	private List<SuppInfoNumItem> suppInfoNumItems;
	
	/** 補足コメント情報: List<補足情報のコメント項目> */
	private List<SuppInfoCommentItem> suppInfoCommentItems;
	
	/** 補足選択項目情報: List<補足情報の選択項目> */
	private List<SuppInfoSelectionItem> suppInfoSelectionItems;
	
}