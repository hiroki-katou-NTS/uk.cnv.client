package nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.grantnumber;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//特別休暇付与数
public class SpecialLeaveGrantNumber {
	//日数
	public DayNumberOfGrant dayNumberOfGrant;
	//時間
	public Optional<TimeOfGrant> timeOfGrant;
	
}
