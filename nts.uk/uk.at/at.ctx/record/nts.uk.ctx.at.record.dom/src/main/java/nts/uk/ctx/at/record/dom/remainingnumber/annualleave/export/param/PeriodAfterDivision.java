package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param;


import lombok.Getter;
import lombok.Setter;
import nts.arc.time.calendar.period.DatePeriod;
@Getter
@Setter
public class PeriodAfterDivision {

	//当月以前の期間
	private DatePeriod periodBeforeTheMonth;

	//当月以降の期間
	private DatePeriod periodAfterTheMonth;
	
	
	public PeriodAfterDivision(DatePeriod beforePeriod, DatePeriod afterPeriod) {
		this.periodBeforeTheMonth = beforePeriod;
		this .periodAfterTheMonth = afterPeriod;
	}
	
}
