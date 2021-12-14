package nts.uk.screen.at.app.ksu001.getshiftpalette;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterDisInfor;

/**
 * @author laitv
 *
 */
@Data
@NoArgsConstructor
public class ShiftMasterDto {
	public String companyId;
	public String shiftMasterName;
	public String shiftMasterCode;
	public String color;
	public String remark;
	public String workTypeCode;
	public String workTimeCode;

	public ShiftMasterDto(ShiftMaster domain) {
		this.companyId = domain.getCompanyId();
		this.shiftMasterCode = domain.getShiftMasterCode().v();
		ShiftMasterDisInfor info = domain.getDisplayInfor();
		this.shiftMasterName = info.getName().v();
		this.color = info.getColor().v();
		this.remark = info.getRemarks().isPresent() ? info.getRemarks().get().v() : null;
		this.workTypeCode = domain.getWorkTypeCode().v();
		this.workTimeCode = domain.getWorkTimeCode() == null ? null : domain.getWorkTimeCode().v();
	}
}
