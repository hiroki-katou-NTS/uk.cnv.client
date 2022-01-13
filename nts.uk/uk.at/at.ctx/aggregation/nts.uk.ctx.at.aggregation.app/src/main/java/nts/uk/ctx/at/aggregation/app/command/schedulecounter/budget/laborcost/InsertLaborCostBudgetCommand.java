package nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author HieuLt
 *
 */
@Data
@AllArgsConstructor
public class InsertLaborCostBudgetCommand {

	private int unit;
	private String targetID;
	/*private GeneralDate ymd;
	private int amount;*/
	//Map<年月日, 値>: Map
	private Map<GeneralDate, String> lstmap;

}
