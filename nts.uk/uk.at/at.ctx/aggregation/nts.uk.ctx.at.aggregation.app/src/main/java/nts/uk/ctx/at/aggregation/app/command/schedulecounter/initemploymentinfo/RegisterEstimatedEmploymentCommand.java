package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo.CriterionAmountByNoCommand;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterEstimatedEmploymentCommand {
	
	private String employmentCode;
	
	private List<CriterionAmountByNoCommand> months = Collections.emptyList();
	
	private List<CriterionAmountByNoCommand> years = Collections.emptyList();
}
