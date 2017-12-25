package nts.uk.ctx.at.function.dom.alarm.extractionrange;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.SpecifyStartDate;

/**
 * @author thanhpv
 * 終了日
 */

@Getter
@Setter
public class EndDate {

	/**前・先区分*/
	private SpecifyStartDate specifyStartDate;
	
	/** Months*/
	// 月数: 締め日指定月数
	private int months;
	
	/** Make it the day*/
	// 当日とする
	private boolean makeToDay;

	public EndDate(SpecifyStartDate specifyStartDate, int months, boolean makeToDay) {
		super();
		this.specifyStartDate = specifyStartDate;
		this.months = months;
		this.makeToDay = makeToDay;
	}	
}
