package nts.uk.ctx.at.aggregation.app.find.schedulecounter.budget.laborcost;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;

/**
 * 
 * @author HieuLt
 *
 */
@Value
public class DailyLaborBudgetDto {
	private TargetOrgIdenInforDto targetOrgIdenInforDto;
	
	private  GeneralDate ymd;
	
	private int amount;
	
	public static DailyLaborBudgetDto fromDomain(LaborCostBudget domain){
	
		return new DailyLaborBudgetDto(TargetOrgIdenInforDto.toDto(domain.getTargetOrg()),
				domain.getYmd(),
				domain.getAmount().v());
	}
	
}
