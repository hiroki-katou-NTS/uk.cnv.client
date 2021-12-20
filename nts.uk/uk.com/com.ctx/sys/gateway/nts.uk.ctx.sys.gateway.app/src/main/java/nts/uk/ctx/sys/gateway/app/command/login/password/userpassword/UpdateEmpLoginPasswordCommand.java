package nts.uk.ctx.sys.gateway.app.command.login.password.userpassword;

import lombok.Data;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.PeregPersonId;

@Data
public class UpdateEmpLoginPasswordCommand {
	
	/* 個人ID */
	@PeregPersonId
	private String personId;
	
	/* 社員ID */
	@PeregEmployeeId
	private String employeeId;
	
	/* パスワード */
	@PeregItem("IS01108")
	private String password;
}
