package nts.uk.ctx.at.request.dom.setting.company.request.applicationsetting.apptypesetting;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.request.dom.setting.company.request.applicationsetting.displaysetting.DisplayAtr;

/**
 * 申請理由表示
 * @author Doan Duy Hung
 *
 */
@Getter
public class DisplayReason extends DomainObject {
	
	/**
	 * 休暇申請の種類
	 */
	private HolidayAppType typeOfLeaveApp;
	
	/**
	 * 定型理由の表示
	 */
	private DisplayAtr displayFixedReason;
	
	/**
	 * 申請理由の表示
	 */
	private DisplayAtr displayAppReason;
}
