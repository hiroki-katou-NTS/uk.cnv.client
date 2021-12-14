package nts.uk.ctx.at.shared.dom.remainingnumber.work;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.HolidayType;

/**
 * 
 * @author sonnlb
 *
 *         時間休暇使用時間詳細
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacationUsageTimeDetail {
	/**
	 * 休暇種類
	 */
	private HolidayType holidayType;
	/**
	 * 時間
	 */
	private Integer times = 0;

	/** 特別休暇コード */
	private Optional<Integer> specialHolidayCode;
	
	
	public VacationUsageTimeDetail clone(){
		VacationUsageTimeDetail clone = new VacationUsageTimeDetail();
		clone.setHolidayType(this.holidayType);
		clone.setTimes(this.times);
		clone.setSpecialHolidayCode(this.specialHolidayCode);
		
		return clone;
	}
}
