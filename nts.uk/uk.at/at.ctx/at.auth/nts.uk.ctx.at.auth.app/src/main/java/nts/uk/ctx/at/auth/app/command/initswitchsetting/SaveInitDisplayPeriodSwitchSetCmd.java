package nts.uk.ctx.at.auth.app.command.initswitchsetting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSet;

@Getter
@Setter
@NoArgsConstructor
public class SaveInitDisplayPeriodSwitchSetCmd {
	/** 会社ID */
	private String companyId;
	
	/** ロールID */
	private String roleId;

	/** 日数 **/
	private int day;
	
	public InitDisplayPeriodSwitchSet toDomain() {
		return new InitDisplayPeriodSwitchSet(companyId, roleId, day);
	}
}
