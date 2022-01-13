package nts.uk.ctx.at.auth.app.find.employmentrole.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitDisplayPeriodSwitchSetDataDto {

	/** 会社ID **/
	private String companyID;
	
	/** ロールID **/
	private String roleID;
	
	/** 日数 **/
	private int day;

	public InitDisplayPeriodSwitchSetDataDto(String companyID, String roleID, int day) {
		this.companyID = companyID;
		this.roleID = roleID;
		this.day = day;
	}
}
