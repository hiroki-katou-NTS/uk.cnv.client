package nts.uk.screen.com.app.find.user.information;

import lombok.Builder;
import lombok.Data;
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
}
