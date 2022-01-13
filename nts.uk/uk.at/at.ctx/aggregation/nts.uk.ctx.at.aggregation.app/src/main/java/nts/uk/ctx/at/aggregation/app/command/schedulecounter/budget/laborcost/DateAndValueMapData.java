package nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost;

import lombok.Data;

@Data
public class DateAndValueMapData {
	/**  年月日（項目）. */
	private String date;

	/**  値（項目）. */
	private String value;
}
