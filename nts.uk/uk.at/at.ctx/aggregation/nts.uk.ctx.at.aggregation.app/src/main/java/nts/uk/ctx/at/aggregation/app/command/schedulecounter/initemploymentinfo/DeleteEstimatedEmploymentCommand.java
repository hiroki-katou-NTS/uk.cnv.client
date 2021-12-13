package nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DeleteEstimatedEmploymentCommand {
	private String employmentCode;
}
