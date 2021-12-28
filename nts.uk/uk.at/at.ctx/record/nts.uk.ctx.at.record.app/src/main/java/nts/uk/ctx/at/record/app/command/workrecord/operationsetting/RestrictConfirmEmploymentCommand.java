package nts.uk.ctx.at.record.app.command.workrecord.operationsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestrictConfirmEmploymentCommand {

	/**
	 * 会社ID
	 */
	private String companyID;
	
	/**
	 * 就業確定を行う
	 */
	private boolean confirmEmployment;
	
	private Long version;
}
