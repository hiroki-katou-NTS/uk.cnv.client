package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * ValueObject 補足情報の数値項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class SuppInfoNumItem implements DomainObject {
	
	/** 補足情報NO: 作業補足情報NO */
	private SuppInfoNo suppInfoNo;
	
    /** 補足数値: 作業補足数値 */
	private SuppNumValue suppNumValue;
	
}
