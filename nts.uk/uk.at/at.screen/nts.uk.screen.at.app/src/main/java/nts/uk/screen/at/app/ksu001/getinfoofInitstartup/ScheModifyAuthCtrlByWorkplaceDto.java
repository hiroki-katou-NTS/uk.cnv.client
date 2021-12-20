package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyAuthCtrlByWorkplace;

@AllArgsConstructor
public class ScheModifyAuthCtrlByWorkplaceDto {

	/** 会社ID */
	public String companyId;
	
	/** ロールID */
	public String roleId;
	
	/** 機能NO */
	public int functionNo;
	
	/** 利用できる */
	public boolean isAvailable;
	
	public static ScheModifyAuthCtrlByWorkplaceDto fromDomain(ScheModifyAuthCtrlByWorkplace domain) {
		
		return new ScheModifyAuthCtrlByWorkplaceDto(
				domain.getCompanyId(),
				domain.getRoleId(),
				domain.getFunctionNo(),
				domain.isAvailable()
				);
	}
}
