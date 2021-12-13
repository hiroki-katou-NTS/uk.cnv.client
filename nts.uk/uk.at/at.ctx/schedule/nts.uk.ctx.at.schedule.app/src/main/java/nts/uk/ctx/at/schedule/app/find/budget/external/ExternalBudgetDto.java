package nts.uk.ctx.at.schedule.app.find.budget.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudget;

@NoArgsConstructor
@AllArgsConstructor
@Data
/**
 * 外部予算実績項目
 * @author hoangnd
 *
 */
public class ExternalBudgetDto {
	private String externalBudgetCode;

	private String externalBudgetName;

	private int budgetAtr;

	private int unitAtr;

	public static ExternalBudgetDto fromDomain(ExternalBudget domain) {
		return new ExternalBudgetDto(domain.getExternalBudgetCd().v(), domain.getExternalBudgetName().v(),
			domain.getBudgetAtr().value, domain.getUnitAtr().value);
	}
	
	@Override
  	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    return externalBudgetCode == ((ExternalBudgetDto) o).externalBudgetCode
	    	&& externalBudgetName == ((ExternalBudgetDto) o).externalBudgetName
	    	&& budgetAtr == ((ExternalBudgetDto) o).budgetAtr
	    	&& unitAtr == ((ExternalBudgetDto) o).unitAtr;
	    	   
  }
}
