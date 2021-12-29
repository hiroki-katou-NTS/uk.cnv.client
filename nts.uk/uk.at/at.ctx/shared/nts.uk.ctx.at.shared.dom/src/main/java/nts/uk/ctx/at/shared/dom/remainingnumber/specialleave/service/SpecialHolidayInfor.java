package nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.specialholiday.export.NextSpecialLeaveGrant;
@AllArgsConstructor
@Setter
@Getter
/**
 * 特別休暇情報
 * @author hayata_maekawa
 *
 */
public class SpecialHolidayInfor {
	/**
	 * 付与日数
	 */
	private GrantDaysInfor grantDaysInfor;
	/**
	 * 期限日
	 */
	private Optional<GeneralDate> deadlineDate;
	
	
	//次回特休付与から作成
	public SpecialHolidayInfor(NextSpecialLeaveGrant nextGrant){
		this.grantDaysInfor = new GrantDaysInfor(nextGrant.getGrantDate(),nextGrant.getGrantDays().v());
		this.deadlineDate = Optional.of(nextGrant.getDeadLine());
	}
	
}
