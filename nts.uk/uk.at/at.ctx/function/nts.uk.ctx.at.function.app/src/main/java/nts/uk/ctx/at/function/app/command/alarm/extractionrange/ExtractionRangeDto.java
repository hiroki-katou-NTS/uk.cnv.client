package nts.uk.ctx.at.function.app.command.alarm.extractionrange;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.EndDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.PreviousClassification;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.StartDate;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.EndSpecify;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.ExtractionPeriodDaily;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.daily.StartSpecify;

public class ExtractionRangeDto {
	
	private String extractionId;
	
	private int extractionRange;
	
	private int strSpecify;

	private Integer strPreviousDay;

	private Integer strDay;

	private Integer strPreviousMonth;

	private Integer strMonth;

	private int endSpecify;

	private Integer endPreviousDay;

	private Integer endDay;

	private Integer endPreviousMonth;

	private Integer endMonth;
	
	public ExtractionPeriodDaily toDomain(){
		StartDate startDate = new StartDate(strSpecify);
		
		if(strSpecify == StartSpecify.DAYS.value){
			startDate.setStartDay(EnumAdaptor.valueOf(strPreviousDay, PreviousClassification.class), (int)strDay, (int)strDay==0?true:false);
		}else if(strSpecify == StartSpecify.MONTH.value){
			startDate.setStartMonth(EnumAdaptor.valueOf(strPreviousMonth, PreviousClassification.class), (int)strMonth, (int)strMonth==0?true:false);	
		}
		
		EndDate endDate = new EndDate(endSpecify);
		
		if(endSpecify == EndSpecify.DAYS.value){
			endDate.setEndDay(EnumAdaptor.valueOf(endPreviousDay, PreviousClassification.class), (int)endDay, (int)endDay==0?true:false);
		}else if(endSpecify == EndSpecify.MONTH.value){
			endDate.setEndMonth(EnumAdaptor.valueOf(endPreviousMonth, PreviousClassification.class), (int)endMonth, (int)endMonth==0?true:false);
		}
		return new ExtractionPeriodDaily(extractionId, extractionRange, startDate, endDate);
	}
}
