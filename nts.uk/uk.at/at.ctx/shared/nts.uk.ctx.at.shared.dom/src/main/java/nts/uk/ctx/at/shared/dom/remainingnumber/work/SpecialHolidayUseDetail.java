package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 特休使用明細
 * @author do_dt
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpecialHolidayUseDetail {
	/**
	 * 特別休暇コード
	 */
	private int specialHolidayCode;
	/**
	 * 日数
	 */
	private double days;
	
	public SpecialHolidayUseDetail clone(){
		SpecialHolidayUseDetail clone = new SpecialHolidayUseDetail();
		clone.setSpecialHolidayCode(this.specialHolidayCode);
		clone.setDays(this.days);
		
		return clone;
	}
	
}
