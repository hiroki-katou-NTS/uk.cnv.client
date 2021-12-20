package nts.uk.ctx.at.shared.dom.specialholiday.periodinformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 特別休暇の有効期限
 *
 * @author tanlv
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecialVacationDeadline {
	/** 月数 */
	private SpecialVacationMonths months;

	/** 年数 */
	private SpecialVacationYears years;

	public static SpecialVacationDeadline createFromJavaType(int months, int years) {
		return new SpecialVacationDeadline(new SpecialVacationMonths(months), new SpecialVacationYears(years));
	}
	
	/**
	 * 期限日を計算する
	 * @param grantDate
	 * @return
	 */
	public GeneralDate calcDeadline(GeneralDate grantDate){
		return grantDate.addYears(this.getYears().v()).addMonths(this.getMonths().v()).addDays(-1);
	}
}
