package nts.uk.ctx.sys.gateway.dom.loginold.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailableMailAddressImport {

	/**
	 * 社員ID
	 */
	private String sid;
	
	/**
	 * 会社メールアドレス
	 */
	private Optional<String> optCompanyMailAddress;
	
	/**
	 * 会社携帯メールアドレス
	 */
	private Optional<String> optCompanyMobileMailAddress;
	
	/**
	 * 個人メールアドレス
	 */
	private Optional<String> optPersonalMailAddress;
	
	/**
	 * 個人携帯メールアドレス
	 */
	private Optional<String> optPersonalMobileMailAddress;
}
