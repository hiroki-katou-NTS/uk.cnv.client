package nts.uk.ctx.at.shared.dom.specialholiday;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;


/**
 * 次回特別休暇付与パラメータの付与基準日
 * @author hayata_maekawa
 *
 */

@AllArgsConstructor
@Getter
@Setter
public class NextSpecialHolidayGrantParameterGrantDate {
	
	/**	入社日 */
	private GeneralDate entryDate;
	
	/**	年休付与基準日 */
	private GeneralDate annualLeaveGrantDate;
	
	/**	特別休暇付与基準日 */
	private GeneralDate specialHolidayGrantDate;
	
	

}
