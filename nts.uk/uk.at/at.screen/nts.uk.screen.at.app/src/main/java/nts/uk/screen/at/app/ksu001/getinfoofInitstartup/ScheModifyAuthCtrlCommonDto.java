package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.schedule.dom.displaysetting.authcontrol.ScheModifyAuthCtrlCommon;

@AllArgsConstructor
public class ScheModifyAuthCtrlCommonDto {
	/** 会社ID */
	public  String companyId;
	
	/** ロールID */
	public  String roleId;
	
	/** 機能NO */
	public  int functionNo;
	
	/** 利用できる */
	public boolean isAvailable;
	
	public static ScheModifyAuthCtrlCommonDto fromDomain(ScheModifyAuthCtrlCommon domain) {
		return new ScheModifyAuthCtrlCommonDto(
				domain.getCompanyId(),
				domain.getRoleId(),
				domain.getFunctionNo(),
				domain.isAvailable()
				);
	}
}
