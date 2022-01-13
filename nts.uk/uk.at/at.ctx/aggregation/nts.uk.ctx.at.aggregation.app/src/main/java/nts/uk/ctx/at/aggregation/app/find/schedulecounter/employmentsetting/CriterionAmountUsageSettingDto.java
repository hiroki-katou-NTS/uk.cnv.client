package nts.uk.ctx.at.aggregation.app.find.schedulecounter.employmentsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CriterionAmountUsageSettingDto {
	/** 会社ID */
	private String cid;

	/** 雇用利用区分 */
	private int employmentUse;
	
	public static CriterionAmountUsageSettingDto fromDomain(CriterionAmountUsageSetting domain) {
		
		return new CriterionAmountUsageSettingDto(domain.getCid(), domain.getEmploymentUse().value);
	}
}
