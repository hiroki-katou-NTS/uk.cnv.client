package nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author HieuLt
 *
 */
@Data
public class InsertLaborCostBudgetData {

	private int unit;
	private String targetID;
	/** 年月日（項目） */
	private List<DateAndValueMapData> dateAndValues;
}
