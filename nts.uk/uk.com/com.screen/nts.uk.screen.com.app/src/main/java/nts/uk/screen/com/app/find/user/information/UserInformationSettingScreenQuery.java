package nts.uk.screen.com.app.find.user.information;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethodRepository;
import nts.uk.screen.com.app.find.user.information.setting.ContactSettingDto;
import nts.uk.screen.com.app.find.user.information.setting.OtherContactDto;
import nts.uk.screen.com.app.find.user.information.setting.SettingContactInformationDto;
import nts.uk.screen.com.app.find.user.information.setting.UserInformationUseMethodDto;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class UserInformationSettingScreenQuery {
	@Inject
    private UserInformationUseMethodRepository userInformationUseMethodrepository;

	/**
	 * ユーザ情報の設定を取得する
	 * @return
	 */
	public UserInformationSettingDto getUserInformationSettings() {
		String loginCid = AppContexts.user().companyId();
		
		/**
		 * Step get(会社ID): ユーザ情報の利用方法
		 */
		Optional<UserInformationUseMethod> userInformationUseMethod = this.userInformationUseMethodrepository.findByCId(loginCid);
		UserInformationUseMethodDto userInformationUseMethodDto = UserInformationUseMethodDto.builder().build();
		
		if(!userInformationUseMethod.isPresent()) {

			List<OtherContactDto> otherContacts = new ArrayList<>();
			otherContacts.add(OtherContactDto.builder()
					.no(1)
					.contactUsageSetting(0)
					.contactName("")
					.build());
			otherContacts.add(OtherContactDto.builder()
					.no(2)
					.contactUsageSetting(0)
					.contactName("")
					.build());
			otherContacts.add(OtherContactDto.builder()
					.no(3)
					.contactUsageSetting(0)
					.contactName("")
					.build());
			otherContacts.add(OtherContactDto.builder()
					.no(4)
					.contactUsageSetting(0)
					.contactName("")
					.build());
			otherContacts.add(OtherContactDto.builder()
					.no(5)
					.contactUsageSetting(0)
					.contactName("")
					.build());
			
			SettingContactInformationDto settingContactInformation = SettingContactInformationDto.builder()
					.dialInNumber(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.companyEmailAddress(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.companyMobileEmailAddress(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.companyMobilePhone(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.personalEmailAddress(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.personalMobileEmailAddress(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.personalMobilePhone(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.extensionNumber(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.emergencyNumber1(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.emergencyNumber2(ContactSettingDto.builder()
							.contactUsageSetting(0)
							.updatable(0)
							.build())
					.otherContacts(otherContacts)
					.build();

			userInformationUseMethodDto.setCompanyId(loginCid);
			userInformationUseMethodDto.setSettingContactInformationDto(settingContactInformation);
			userInformationUseMethodDto.setUseOfLanguage(0); //#114200
			userInformationUseMethodDto.setUseOfNotice(0); //#114200
			userInformationUseMethodDto.setUseOfPassword(0); //#114200
			userInformationUseMethodDto.setUseOfProfile(0); //#114200
		}
		userInformationUseMethod.ifPresent(method -> method.setMemento(userInformationUseMethodDto));
		
		return UserInformationSettingDto.builder()
				.userInformationUseMethodDto(userInformationUseMethodDto)
				.build();
	}
}
