package nts.uk.screen.com.app.command.sys.auth.role;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CopyRoleCas005Command {
	
	private RoleCas005Command roleCas005Command;
	
	private boolean checkUpdate;

	public CopyRoleCas005Command(RoleCas005Command roleCas005Command,
			boolean checkUpdate) {
		super();
		this.roleCas005Command = roleCas005Command;
		this.checkUpdate = checkUpdate;
	}
}
