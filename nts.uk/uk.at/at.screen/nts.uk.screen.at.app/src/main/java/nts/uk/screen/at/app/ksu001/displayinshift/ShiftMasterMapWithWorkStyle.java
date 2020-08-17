/**
 * 
 */
package nts.uk.screen.at.app.ksu001.displayinshift;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;

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
	public String workTypeCode;
	public String workTimeCode;
	public String workStyle;  // 出勤休日区分 (co truong hơp workStyle = null nên ko để kiểu int được)

	public ShiftMasterMapWithWorkStyle(ShiftMaster domain, String workStyle) {
		this.companyId = domain.getCompanyId();
		this.shiftMasterCode = domain.getShiftMasterCode().v();
		ShiftMasterDisInfor info = domain.getDisplayInfor();
		this.shiftMasterName = info.getName().v();
		this.color = info.getColor().v();
		this.remark = info.getRemarks().isPresent() ? info.getRemarks().get().v() : null;
		this.workTypeCode = domain.getWorkTypeCode().v();
		this.workTimeCode = domain.getWorkTimeCode() == null ? null : domain.getWorkTimeCode().v();
		this.workStyle = workStyle;
	}
}
