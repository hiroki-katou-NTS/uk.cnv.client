package nts.uk.ctx.sys.portal.app.find.toppagesetting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageRoleSetting;

/**
 * The Class TopPageRoleSettingDto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopPageRoleSettingDto {
	
	/** The company id. */
	private String companyId;
	
	/** The role set code. */
	private String roleSetCode;
	
	/** The login menu code. */
	private String loginMenuCode;
	
	/** The top menu code. */
	private String topMenuCode;
	
	/** The menu classification. */
	private int menuClassification; 
	
	/** The system. */
	private int system;
	
	public static TopPageRoleSettingDto fromDomain(TopPageRoleSetting domain) {
		return TopPageRoleSettingDto.builder()
				.companyId(domain.getCompanyId())
				.roleSetCode(domain.getRoleSetCode().v())
				.loginMenuCode(domain.getMenuLogin().getLoginMenuCode().v())
				.menuClassification(domain.getMenuLogin().getMenuClassification().value)
				.system(domain.getMenuLogin().getSystem().value)
				.topMenuCode(domain.getTopMenuCode().v())
				.build();
	}

}
