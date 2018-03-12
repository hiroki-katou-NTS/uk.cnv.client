package nts.uk.ctx.at.schedule.pub.applicationreflectprocess.applicationsmanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
@AllArgsConstructor
@Setter
@Getter
public class GoBackDirectlyReflectParamDto {
	/**	社員ID */
	private String employeeId;
	/**	年月日 */
	private GeneralDate datePara;
	/**	 振出・休出時反映する区分*/
	private OutsetBreakReflectScheAtrPub outsetBreakReflectAtr; 
	/**	直行直帰申請 */
	private ApplicationGobackScheInforDto appInfor;
	/**直行直帰申請 の　時刻の反映：　固定（開始）	 */
	private ApplyTimeAtrPub applyTimeAtr;
}
