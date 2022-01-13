package nts.uk.ctx.sys.portal.app.find.toppagesetting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSetting;

/**
 * The Class TopPagePersonSettingCommandBase.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopPagePersonSettingDto {
	
	/** The employee id. */
	private String employeeId;
	
	/** The login menu code. */
	private String loginMenuCode;
	
	/** The top menu code. */
	private String topMenuCode;
	
	/** The menu classification. */
	private int menuClassification; 
	
	/** The system. */
	private int system;
	
	public static TopPagePersonSettingDto fromDomain(TopPagePersonSetting domain) {
		return TopPagePersonSettingDto.builder()
				.employeeId(domain.getEmployeeId())
				.loginMenuCode(domain.getMenuLogin().getLoginMenuCode().v())
				.menuClassification(domain.getMenuLogin().getMenuClassification().value)
				.system(domain.getMenuLogin().getSystem().value)
				.topMenuCode(domain.getTopMenuCode().v())
				.build();
	}

}
