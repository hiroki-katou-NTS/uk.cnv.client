package nts.uk.ctx.at.auth.app.command.initswitchsetting;

import lombok.Data;

@Data
public class DeleteInitDisplayPeriodSwitchSetCmd {
	/** 会社ID */
	private String companyId;
	
	/** ロールID */
	private String roleId;
}
