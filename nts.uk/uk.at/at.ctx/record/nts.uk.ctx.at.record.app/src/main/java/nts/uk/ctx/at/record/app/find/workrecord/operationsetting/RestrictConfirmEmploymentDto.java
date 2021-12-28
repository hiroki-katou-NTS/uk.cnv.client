package nts.uk.ctx.at.record.app.find.workrecord.operationsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;

/**
 * refactor 5
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.就業確定.就業確定の機能制限
 * @author huylq
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestrictConfirmEmploymentDto {
	/**
	 * 会社ID
	 */
	private String companyID;
	
	/**
	 * 就業確定を行う
	 */
	private boolean confirmEmployment;
	
	public static RestrictConfirmEmploymentDto fromDomain(RestrictConfirmEmployment domain) {
		if (domain == null) {
			return null;
		}
		
		return new RestrictConfirmEmploymentDto(domain.getCompanyID(), domain.isConfirmEmployment());
	}
}
