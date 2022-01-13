package nts.uk.ctx.at.record.app.find.calculationsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.record.app.find.workrecord.goout.OutManageDto;
import nts.uk.ctx.at.record.app.find.workrecord.temporarywork.ManageWorkTemporaryDto;

@Data
@AllArgsConstructor
public class UsageDataDto {

	/**
	 * 外出管理
	 */
	private OutManageDto outManage;
	
	/**
	 * 臨時勤務利用管理
	 */
	private int tempWorkUseManageAtr;
	
	/**
	 * 臨時勤務管理
	 */
	private ManageWorkTemporaryDto tempWorkManage;
}
