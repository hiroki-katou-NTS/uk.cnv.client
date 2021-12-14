package nts.uk.screen.at.app.ksu003.start.dto;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.TargetOrgIdenInforDto;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

@Value
public class DisplayWorkInfoParam {
	
	/** 対象組織：対象組織識別情報 */
	private TargetOrgIdenInforDto targetOrg;
	
	/** 社員リスト：List<社員ID> */
	private List<String> lstEmpId;
	
	/** ・抽出日：年月日 */
	private String date;
}
