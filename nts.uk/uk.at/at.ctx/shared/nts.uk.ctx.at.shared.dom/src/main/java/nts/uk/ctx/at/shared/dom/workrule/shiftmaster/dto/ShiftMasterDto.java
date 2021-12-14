package nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.i18n.I18NText;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;

/**
 * @author anhdt
 *
 */
@Data
@NoArgsConstructor
public class ShiftMasterDto {
	private String companyId;
	private String shiftMasterName;
	private String shiftMasterCode;
	private String color;
	private String remark;
	private String importCode;
	private String workTypeCd;
	private String workTypeName;
	private String workTimeCd;
	private String workTimeName;
	private String workTime1;
	private String workTime2;
	private String colorSmartphone;
	private String colorText;

	public ShiftMasterDto(ShiftMaster domain) {
		this.companyId = domain.getCompanyId();
		this.shiftMasterCode = domain.getShiftMasterCode().v();
		ShiftMasterDisInfor info = domain.getDisplayInfor();
		this.shiftMasterName = info.getName().v();
		this.color = info.getColor().v();
		this.remark = info.getRemarks().isPresent() ? info.getRemarks().get().v() : null;
		this.importCode = domain.getImportCode().isPresent() ? domain.getImportCode().get().v() : null;
	}

	public ShiftMasterDto(String companyId, String shiftMasterName, String shiftMaterCode, String color, String remark, String importCode,
			String workTypeCd, String workTypeName, String wtypecid, String workTimeCd, String workTimeName,
			String wtimecid) {
		this.companyId = companyId;
		this.shiftMasterName = shiftMasterName;
		this.shiftMasterCode = shiftMaterCode;
		this.color = color;
		this.remark = !StringUtils.isEmpty(remark) ? remark : "";
		this.workTypeCd = workTypeCd;
		this.workTypeName = !StringUtils.isEmpty(workTypeName) ? workTypeName : "";
		this.importCode = importCode;
		if (StringUtils.isEmpty(wtypecid)) {
			this.workTypeName = I18NText.getText("KSM015_28", workTypeCd, I18NText.getText("KSM015_29"));
		}

		this.workTimeCd = !StringUtils.isEmpty(workTimeCd) ? workTimeCd : "";
		this.workTimeName = !StringUtils.isEmpty(workTimeName) ? workTimeName : "";
		if (StringUtils.isEmpty(wtimecid) && !StringUtils.isEmpty(workTimeCd)) {
			this.workTimeName = I18NText.getText("KSM015_28", workTimeCd, I18NText.getText("KSM015_29"));
		}
		
		this.workTime1 = "";
		this.workTime2 = "";
	}
	
	public ShiftMasterDto(String companyId, String shiftMasterName, String shiftMaterCode, String color, String remark, String importCode,
			String workTypeCd, String workTypeName, String wtypecid, String workTimeCd, String workTimeName,
			String wtimecid,String colorSmartphone) {
		this.companyId = companyId;
		this.shiftMasterName = shiftMasterName;
		this.shiftMasterCode = shiftMaterCode;
		this.color = color;
		this.remark = !StringUtils.isEmpty(remark) ? remark : "";
		this.importCode = importCode;
		this.workTypeCd = workTypeCd;
		this.workTypeName = !StringUtils.isEmpty(workTypeName) ? workTypeName : "";
		if (StringUtils.isEmpty(wtypecid)) {
			this.workTypeName = I18NText.getText("KSM015_28", workTypeCd, I18NText.getText("KSM015_29"));
		}

		this.workTimeCd = !StringUtils.isEmpty(workTimeCd) ? workTimeCd : "";
		this.workTimeName = !StringUtils.isEmpty(workTimeName) ? workTimeName : "";
		if (StringUtils.isEmpty(wtimecid) && !StringUtils.isEmpty(workTimeCd)) {
			this.workTimeName = I18NText.getText("KSM015_28", workTimeCd, I18NText.getText("KSM015_29"));
		}
		
		this.workTime1 = "";
		this.workTime2 = "";
		this.colorSmartphone = colorSmartphone;
		this.colorText = "";
	}
	
	

}
