package nts.uk.ctx.at.function.app.command.alarm.extractionrange;

import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.EndDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.PreviousClassification;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.StartDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.EndSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.ExtractionPeriodDaily;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.DailyClosingDateSpecifiedMonth;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.StartSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.month.MonthlyClosingMonthSpecifiedMonth;

@Data
public class ExtractionPeriodDailyCommand {
	
	private String extractionId;
	
	private int extractionRange;
	
	private int strSpecify;

	private Integer strPreviousDay;

	private int strDay;

	private Integer strPreviousMonth;

	private Integer strMonth;

	private int endSpecify;

	private Integer endPreviousDay;

	private int endDay;

	private Integer endPreviousMonth;

	private Integer endMonth;
	
	public ExtractionPeriodDaily toDomain(){
		if(this.extractionId == null || this.extractionId.equals("")){
			this.extractionId = IdentifierUtil.randomUniqueId();
		}
		
		StartDate startDate = new StartDate(strSpecify);
		
		if (strSpecify == StartSpecify.DAYS.value) {
			startDate.setStartDay(
					EnumAdaptor.valueOf(strPreviousDay, PreviousClassification.class),
					strDay,
					strDay == 0
			);
		} else if (strSpecify == StartSpecify.MONTH.value) {
			startDate.setStartMonth(
					EnumAdaptor.valueOf(strPreviousMonth, PreviousClassification.class),
					strMonth,
					strMonth == MonthlyClosingMonthSpecifiedMonth.CURRENT_MONTH.value
			);
		}
		
		EndDate endDate = new EndDate(endSpecify);
		
		if (endSpecify == EndSpecify.DAYS.value) {
			endDate.setEndDay(
					EnumAdaptor.valueOf(endPreviousDay, PreviousClassification.class),
					endDay,
					endDay == 0);
		} else if (endSpecify == EndSpecify.MONTH.value) {
			endDate.setEndMonth(
					EnumAdaptor.valueOf(endPreviousMonth, PreviousClassification.class),
					endMonth,
					endMonth == MonthlyClosingMonthSpecifiedMonth.CURRENT_MONTH.value
			);
		}
		
		return new ExtractionPeriodDaily(extractionId, extractionRange, startDate, endDate);
	}
}
