package nts.uk.ctx.at.aggregation.ws.schedulecounter.budget.laborcost;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.personalcounter.RegisterPersonalCounterCommand;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KSU001RCommand {
	
	public int unit;
	
	public String startDate;
	
	public String endDate; 
	
	public String targetID;
}
