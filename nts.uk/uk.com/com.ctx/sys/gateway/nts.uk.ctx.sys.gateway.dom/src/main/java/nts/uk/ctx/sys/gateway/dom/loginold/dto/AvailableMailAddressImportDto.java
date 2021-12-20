package nts.uk.ctx.sys.gateway.dom.loginold.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AvailableMailAddressImportDto {
	
	/**
	 * 会社メールアドレス
	 */
	private String companyMailAddress;
	
	/**
	 * 会社携帯メールアドレス
	 */
	private String companyMobileMailAddress;
	
	/**
	 * 個人メールアドレス
	 */
	private String personalMailAddress;
	
	/**
	 * 個人携帯メールアドレス
	 */
	private String personalMobileMailAddress;
}
