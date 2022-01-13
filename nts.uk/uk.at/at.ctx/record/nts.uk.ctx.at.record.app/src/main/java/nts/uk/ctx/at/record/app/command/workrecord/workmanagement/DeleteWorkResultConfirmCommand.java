package nts.uk.ctx.at.record.app.command.workrecord.workmanagement;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 作業実績の確認を解除する
 * @author tutt
 *
 */
@Getter
@Setter
public class DeleteWorkResultConfirmCommand {
	
	//対象者
	private String employeeId;
	
	//対象日
	private GeneralDate date;
	
	//確認者
	private String confirmerId;
}
