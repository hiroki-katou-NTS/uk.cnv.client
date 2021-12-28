package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * ValueObject 補足情報の時間項目
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class SuppInfoTimeItem implements DomainObject {

	/** 補足情報NO: 作業補足情報NO */
	private SuppInfoNo suppInfoNo;
	
	/** 補足時間 */
	private AttendanceTime attTime;
}
 