package nts.uk.ctx.at.function.app.export.holidaysremaining;

import lombok.Value;

@Value
public class HolidaysRemainingOutputConditionQuery {
	private String startMonth;
	private String endMonth;
	private String layOutId;
	private int pageBreak;
	private String baseDate;
	private int closureId;
	private String title;
}
