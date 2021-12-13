/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinshift;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;

/**
 * @author laitv
 */
@Data
@NoArgsConstructor
public class ShiftMasterMapWithWorkStyle {
	public String companyId;
	public String shiftMasterName;
	public String shiftMasterCode;
	public String color;
	public String remark;
	public String importCode;
	public String workTypeCode;
	public String workTimeCode;
	public String workStyle;  // 出勤休日区分 (co truong hơp workStyle = null nên ko để kiểu int được) class WorkStyle

	public ShiftMasterMapWithWorkStyle(ShiftMaster domain, String workStyle) {
		this.companyId = domain.getCompanyId();
		this.shiftMasterCode = domain.getShiftMasterCode().v();
		ShiftMasterDisInfor info = domain.getDisplayInfor();
		this.shiftMasterName = info.getName().v();
		this.color = info.getColor().v();
		this.remark = info.getRemarks().isPresent() ? info.getRemarks().get().v() : null;
		this.importCode = domain.getImportCode().map(importCode -> importCode.v()).orElse(null);
		this.workTypeCode = domain.getWorkTypeCode() == null ? null : domain.getWorkTypeCode().toString().toString();
		this.workTimeCode = domain.getWorkTimeCode() == null ? null : domain.getWorkTimeCode().toString().toString();
		this.workStyle = workStyle;
	}
	
	public ShiftMasterMapWithWorkStyle(ShiftMasterDto dto, String workStyle) {
		this.companyId = dto.getCompanyId();
		this.shiftMasterCode = dto.getShiftMasterCode();
		this.shiftMasterName = dto.getShiftMasterName();
		this.color = dto.getColor();
		this.remark = dto.getRemark();
		this.workTypeCode = dto.getWorkTypeCode();
		this.workTimeCode = dto.getWorkTimeCode();
		this.workStyle = workStyle;
	}
}
