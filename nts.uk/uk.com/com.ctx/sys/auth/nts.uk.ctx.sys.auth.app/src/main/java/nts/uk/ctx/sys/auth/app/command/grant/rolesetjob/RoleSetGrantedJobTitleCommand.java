package nts.uk.ctx.sys.auth.app.command.grant.rolesetjob;

import java.util.List;

import lombok.Value;

/**
 * 
 * @author HungTT
 *
 */

@Value
public class RoleSetGrantedJobTitleCommand {
	private List<RoleSetGrantedJobTitleDetailCommand> details;
}
