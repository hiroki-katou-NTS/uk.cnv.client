package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CompanyInfoRegisterCommand {
	private List<CriterionAmountByNoCommand> months;
	
	private List<CriterionAmountByNoCommand> years;
	
	private List<HandlingOfCriterionAmountByNoComand> handlings;	
}
