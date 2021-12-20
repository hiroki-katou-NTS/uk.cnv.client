package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SpecialHolidayInfor {
	/**
	 * ・特別休暇情報一覧
	 */
	private GrantDaysInfor grantDaysInfor;
	/**
	 * 期限日
	 */
	private Optional<GeneralDate> deadlineDate;
	
	
	
	public SpecialHolidayInfor(NextSpecialLeaveGrant nextGrant){
		this.grantDaysInfor = new GrantDaysInfor(nextGrant.getGrantDate(),nextGrant.getErrorFlg(),nextGrant.getGrantDays().v());
		this.deadlineDate = Optional.of(nextGrant.getDeadLine());
	}
	
}
