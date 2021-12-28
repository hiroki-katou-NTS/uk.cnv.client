package nts.uk.ctx.at.record.app.command.workrecord.authfuncrest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CopyDailyPerformanceAuthorityCommand {
	
	private String selectedRole;

	private List<String> targetRoleList;
}
