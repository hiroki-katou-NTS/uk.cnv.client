package nts.uk.ctx.at.aggregation.app.find.schedulecounter.budget.laborcost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetOrgIdenInforDto {
	private int unit;

	private String workplaceId;

	private String workplaceGroupId;

	public TargetOrgIdenInfor convertFromDomain() {
		if (this.unit == TargetOrganizationUnit.WORKPLACE.value) {
			return TargetOrgIdenInfor.creatIdentifiWorkplace(this.workplaceId);
		}
		return TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(this.workplaceGroupId);
	}

	public static TargetOrgIdenInforDto toDto(TargetOrgIdenInfor domain) {
		return new TargetOrgIdenInforDto(domain.getUnit().value,
				domain.getWorkplaceId().isPresent() ? domain.getWorkplaceId().get() : "",
				domain.getWorkplaceGroupId().isPresent() ? domain.getWorkplaceGroupId().get() : "");
	}
}
