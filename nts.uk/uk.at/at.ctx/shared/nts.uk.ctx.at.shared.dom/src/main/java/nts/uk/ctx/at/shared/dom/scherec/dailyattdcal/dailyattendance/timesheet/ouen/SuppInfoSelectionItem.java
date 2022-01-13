package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * ValueObect 補足情報の選択項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class SuppInfoSelectionItem implements DomainObject {
	
	/** 補足情報の選択項目NO: 作業補足情報NO	*/
	private SuppInfoNo suppInfoSelectionNo;
	
	/** 補足選択肢コード: 作業補足情報の選択肢コード */
	private ChoiceCode choiceCode;
}
