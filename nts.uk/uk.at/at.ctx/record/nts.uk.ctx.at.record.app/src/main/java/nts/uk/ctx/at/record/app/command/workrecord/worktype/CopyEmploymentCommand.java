package nts.uk.ctx.at.record.app.command.workrecord.worktype;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CopyEmploymentCommand {
	/**
	 * List target employment code 
	 */
	private List<String> targetEmploymentCodes;
	/**
	 * Selected employment code
	 */
	private String employmentCode;
}
