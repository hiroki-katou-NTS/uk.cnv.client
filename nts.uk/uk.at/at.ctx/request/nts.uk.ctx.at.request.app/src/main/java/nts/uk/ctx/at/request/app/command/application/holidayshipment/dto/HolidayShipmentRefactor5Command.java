package nts.uk.ctx.at.request.app.command.application.holidayshipment.dto;

import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.dto.DisplayInforWhenStarting;

@NoArgsConstructor
@Setter
public class HolidayShipmentRefactor5Command {

	/** 振休申請 */
	public AbsenceLeaveAppCmd abs;
	/** 振出申請 */
	public RecruitmentAppCmd rec;
	/** 振休振出申請起動時の表示情報  */
	public DisplayInforWhenStarting displayInforWhenStarting;
	/** 代行申請か */
	public boolean represent;
	
	public boolean existAbs() {
		return this.abs != null;
	}
	
	public boolean existRec() {
		return this.rec != null;
	}
}
