package nts.uk.ctx.sys.env.dom.mailnoticeset.company;

import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * Dto ユーザー情報の使用方法
 */
@Getter
@Builder
public class UserInfoUseMethodDto implements UserInformationUseMethod.MementoSetter, UserInformationUseMethod.MementoGetter {
	/**
	 * お知らせの利用
	 */
	private Integer useOfNotice;

	/**
	 * パスワードの利用
	 */
	private Integer useOfPassword;

	/**
	 * プロフィールの利用
	 */
	private Integer useOfProfile;

	/**
	 * 言語の利用
	 */
	private Integer useOfLanguage;

	/**
	 * 会社ID
	 */
	private String companyId;

	/**
	 * 連絡先情報の設定
	 */
	private SettingContactInformationDto settingContactInformationDto;

	@Override
	public void setSettingContactInformation(SettingContactInformation settingContactInformation) {
		this.settingContactInformationDto = SettingContactInformationDto.builder()
				.companyMobilePhone(settingContactInformation.getCompanyMobilePhone() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getCompanyMobilePhone().getContactUsageSetting().value)
						.updatable(settingContactInformation.getCompanyMobilePhone().getUpdatable().isPresent() ?
								settingContactInformation.getCompanyMobilePhone().getUpdatable().get().value : null)
						.build())
				.companyEmailAddress(settingContactInformation.getCompanyEmailAddress() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getCompanyEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getCompanyEmailAddress().getUpdatable().isPresent() ?
								settingContactInformation.getCompanyEmailAddress().getUpdatable().get().value : null)
						.build())
				.companyMobileEmailAddress(settingContactInformation.getCompanyMobileEmailAddress() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getCompanyMobileEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getCompanyMobileEmailAddress().getUpdatable().isPresent() ?
								settingContactInformation.getCompanyMobileEmailAddress().getUpdatable().get().value : null)
						.build())
				.personalMobilePhone(settingContactInformation.getPersonalMobilePhone() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getPersonalMobilePhone().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalMobilePhone().getUpdatable().isPresent() ?
								settingContactInformation.getPersonalMobilePhone().getUpdatable().get().value : null)
						.build())
				.personalEmailAddress(settingContactInformation.getPersonalEmailAddress() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getPersonalEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalEmailAddress().getUpdatable().isPresent() ?
								settingContactInformation.getPersonalEmailAddress().getUpdatable().get().value : null)
						.build())
				.personalMobileEmailAddress(settingContactInformation.getPersonalMobileEmailAddress() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getPersonalMobileEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalMobileEmailAddress().getUpdatable().isPresent() ?
								settingContactInformation.getPersonalMobileEmailAddress().getUpdatable().get().value : null)
						.build())
				.dialInNumber(settingContactInformation.getDialInNumber() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getDialInNumber().getContactUsageSetting().value)
						.updatable(settingContactInformation.getDialInNumber().getUpdatable().isPresent() ?
								settingContactInformation.getDialInNumber().getUpdatable().get().value : null)
						.build())
				.extensionNumber(settingContactInformation.getExtensionNumber() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getExtensionNumber().getContactUsageSetting().value)
						.updatable(settingContactInformation.getExtensionNumber().getUpdatable().isPresent() ?
								settingContactInformation.getExtensionNumber().getUpdatable().get().value : null)
						.build())
				.emergencyNumber1(settingContactInformation.getEmergencyNumber1() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getEmergencyNumber1().getContactUsageSetting().value)
						.updatable(settingContactInformation.getEmergencyNumber1().getUpdatable().isPresent() ?
								settingContactInformation.getEmergencyNumber1().getUpdatable().get().value : null)
						.build())
				.emergencyNumber2(settingContactInformation.getEmergencyNumber2() == null ? null : ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getEmergencyNumber2().getContactUsageSetting().value)
						.updatable(settingContactInformation.getEmergencyNumber2().getUpdatable().isPresent() ?
								settingContactInformation.getEmergencyNumber2().getUpdatable().get().value : null)
						.build())
				.otherContacts(settingContactInformation.getOtherContacts().stream()
						.map(o -> OtherContactDto.builder()
								.no(o.getNo())
								.contactName(o.getContactName().v())
								.contactUsageSetting(o.getContactUsageSetting().value)
								.build())
						.collect(Collectors.toList()))
				.build();
	}

	@Override
	public SettingContactInformation getSettingContactInformation() {
		if (settingContactInformationDto == null) {
			return null;
		}
		return SettingContactInformation.builder()
				.companyEmailAddress(settingContactInformationDto.getCompanyEmailAddress() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getCompanyEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getCompanyEmailAddress().getUpdatable())))
						.build())
				.companyMobilePhone(settingContactInformationDto.getCompanyMobilePhone() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getCompanyMobilePhone().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getCompanyMobilePhone().getUpdatable())))
						.build())
				.companyMobileEmailAddress(settingContactInformationDto.getCompanyMobileEmailAddress() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getCompanyMobileEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getCompanyMobileEmailAddress().getUpdatable())))
						.build())
				.personalEmailAddress(settingContactInformationDto.getPersonalEmailAddress() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getPersonalEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getPersonalEmailAddress().getUpdatable())))
						.build())
				.personalMobilePhone(settingContactInformationDto.getPersonalMobilePhone() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getPersonalMobilePhone().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getPersonalMobilePhone().getUpdatable())))
						.build())
				.personalMobileEmailAddress(settingContactInformationDto.getPersonalMobileEmailAddress() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getPersonalMobileEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getPersonalMobileEmailAddress().getUpdatable())))
						.build())
				.dialInNumber(settingContactInformationDto.getDialInNumber() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getDialInNumber().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getDialInNumber().getUpdatable())))
						.build())
				.extensionNumber(settingContactInformationDto.getExtensionNumber() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getExtensionNumber().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getExtensionNumber().getUpdatable())))
						.build())
				.emergencyNumber1(settingContactInformationDto.getEmergencyNumber1() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getEmergencyNumber1().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getEmergencyNumber1().getUpdatable())))
						.build())
				.emergencyNumber2(settingContactInformationDto.getEmergencyNumber2() == null ? null : ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto.getEmergencyNumber2().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.settingContactInformationDto.getEmergencyNumber2().getUpdatable())))
						.build())
				.otherContacts(this.settingContactInformationDto.getOtherContacts() != null
					? this.settingContactInformationDto.getOtherContacts().stream()
						.map(o -> OtherContact.builder()
								.no(o.getNo())
								.contactName(new ContactName(o.getContactName()))
								.contactUsageSetting(ContactUsageSetting.valueOf(o.getContactUsageSetting()))
								.build())
						.collect(Collectors.toList())
					: null)
				.build();
	}

	@Override
	public void setUseOfNotice(Integer useOfNotice) {
		this.useOfNotice = useOfNotice;
	}

	@Override
	public void setUseOfPassword(Integer useOfPassword) {
		this.useOfPassword = useOfPassword;
	}

	@Override
	public void setUseOfProfile(Integer useOfProfile) {
		this.useOfProfile = useOfProfile;
	}

	@Override
	public void setUseOfLanguage(Integer useOfLanguage) {
		this.useOfLanguage = useOfLanguage;
	}

	@Override
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public Integer getUseOfNotice() {
		return this.useOfNotice;
	}

	public Integer getUseOfPassword() {
		return this.useOfPassword;
	}

	public Integer getUseOfProfile() {
		return this.useOfProfile;
	}

	public Integer getUseOfLanguage() {
		return this.useOfLanguage;
	}

	public String getCompanyId() {
		return this.companyId;
	}

}