package nts.uk.screen.com.app.find.user.information;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import nts.uk.screen.com.app.find.user.information.setting.MailFunctionDto;
import nts.uk.screen.com.app.find.user.information.setting.UserInformationUseMethodDto;

/**
 * DTO ユーザ情報の設定を取得する
 * @author admin
 *
 */
@Data
@Builder
public class UserInformationSettingDto {
	/**
	 * ユーザー情報の使用方法
	 */
	private UserInformationUseMethodDto userInformationUseMethodDto;
	
	/**
	 * メール機能
	 */
	private List<MailFunctionDto> mailFunctionDtos;
}
