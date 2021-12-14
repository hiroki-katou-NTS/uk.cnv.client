package nts.uk.ctx.sys.env.app.find.mailnoticeset.setting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.sys.env.dom.mailnoticeset.FunctionId;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactUsageSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.EmailClassification;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.EmailDestinationFunction;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.OtherContact;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.SettingContactInformation;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * Dto ユーザー情報の使用方法
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInformationUseMethodDto implements UserInformationUseMethod.MementoSetter, UserInformationUseMethod.MementoGetter {
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
	 * メール送信先機能
	 */
	private List<EmailDestinationFunctionDto> emailDestinationFunctionDtos;

	/**
	 * 連絡先情報の設定
	 */
	private SettingContactInformationDto settingContactInformationDto;

	@Override
	public void setEmailDestinationFunctions(List<EmailDestinationFunction> emailDestinationFunctions) {
		this.emailDestinationFunctionDtos = emailDestinationFunctions.stream()
				.map(item -> EmailDestinationFunctionDto.builder()
						.emailClassification(item.getEmailClassification().value)
						.functionIds(
								item.getFunctionIds().stream().map(PrimitiveValueBase::v).collect(Collectors.toList()))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<EmailDestinationFunction> getEmailDestinationFunctions() {
		return this.emailDestinationFunctionDtos.stream()
				.map(item -> EmailDestinationFunction.builder()
						.emailClassification(EmailClassification.valueOf(item.getEmailClassification()))
						.functionIds(item.getFunctionIds().stream().map(FunctionId::new).collect(Collectors.toList()))
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void setSettingContactInformation(SettingContactInformation settingContactInformation) {
		this.settingContactInformationDto = SettingContactInformationDto.builder().companyMobilePhone(ContactSettingDto
				.builder()
				.contactUsageSetting(settingContactInformation.getCompanyMobilePhone().getContactUsageSetting().value)
				.updatable(settingContactInformation.getCompanyMobilePhone().getUpdatable().isPresent()
						? settingContactInformation.getCompanyMobilePhone().getUpdatable().get().value
						: null)
				.build())
				.companyEmailAddress(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getCompanyEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getCompanyEmailAddress().getUpdatable().isPresent()
								? settingContactInformation.getCompanyEmailAddress().getUpdatable().get().value
								: null)
						.build())
				.companyMobileEmailAddress(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getCompanyMobileEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getCompanyMobileEmailAddress().getUpdatable().isPresent()
								? settingContactInformation.getCompanyMobileEmailAddress().getUpdatable().get().value
								: null)
						.build())
				.personalMobilePhone(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getPersonalMobilePhone().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalMobilePhone().getUpdatable().isPresent()
								? settingContactInformation.getPersonalMobilePhone().getUpdatable().get().value
								: null)
						.build())
				.personalEmailAddress(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getPersonalEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalEmailAddress().getUpdatable().isPresent()
								? settingContactInformation.getPersonalEmailAddress().getUpdatable().get().value
								: null)
						.build())
				.personalMobileEmailAddress(ContactSettingDto.builder().contactUsageSetting(
						settingContactInformation.getPersonalMobileEmailAddress().getContactUsageSetting().value)
						.updatable(settingContactInformation.getPersonalMobileEmailAddress().getUpdatable().isPresent()
								? settingContactInformation.getPersonalMobileEmailAddress().getUpdatable().get().value
								: null)
						.build())
				.dialInNumber(ContactSettingDto.builder()
						.contactUsageSetting(settingContactInformation.getDialInNumber().getContactUsageSetting().value)
						.updatable(settingContactInformation.getDialInNumber().getUpdatable().isPresent()
								? settingContactInformation.getDialInNumber().getUpdatable().get().value
								: null)
						.build())
				.extensionNumber(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getExtensionNumber().getContactUsageSetting().value)
						.updatable(settingContactInformation.getExtensionNumber().getUpdatable().isPresent()
								? settingContactInformation.getExtensionNumber().getUpdatable().get().value
								: null)
						.build())
				.emergencyNumber1(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getEmergencyNumber1().getContactUsageSetting().value)
						.updatable(settingContactInformation.getEmergencyNumber1().getUpdatable().isPresent()
								? settingContactInformation.getEmergencyNumber1().getUpdatable().get().value
								: null)
						.build())
				.emergencyNumber2(ContactSettingDto.builder()
						.contactUsageSetting(
								settingContactInformation.getEmergencyNumber2().getContactUsageSetting().value)
						.updatable(settingContactInformation.getEmergencyNumber2().getUpdatable().isPresent()
								? settingContactInformation.getEmergencyNumber2().getUpdatable().get().value
								: null)
						.build())
				.otherContacts(settingContactInformation.getOtherContacts().stream()
						.map(o -> OtherContactDto.builder().no(o.getNo()).contactName(o.getContactName().v())
								.contactUsageSetting(o.getContactUsageSetting().value).build())
						.collect(Collectors.toList()))
				.build();
	}

	@Override
	public SettingContactInformation getSettingContactInformation() {
		return SettingContactInformation.builder().companyEmailAddress(ContactSetting.builder()
				.contactUsageSetting(ContactUsageSetting
						.valueOf(this.settingContactInformationDto.getCompanyEmailAddress().getContactUsageSetting()))
				.updatable(Optional.ofNullable(
						NotUseAtr.valueOf(this.settingContactInformationDto.getCompanyEmailAddress().getUpdatable())))
				.build())
				.companyMobilePhone(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getCompanyMobilePhone().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getCompanyMobilePhone().getUpdatable())))
						.build())
				.companyMobileEmailAddress(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto
								.getCompanyMobileEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(
								this.settingContactInformationDto.getCompanyMobileEmailAddress().getUpdatable())))
						.build())
				.personalEmailAddress(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getPersonalEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getPersonalEmailAddress().getUpdatable())))
						.build())
				.personalMobilePhone(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getPersonalMobilePhone().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getPersonalMobilePhone().getUpdatable())))
						.build())
				.personalMobileEmailAddress(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.settingContactInformationDto
								.getPersonalMobileEmailAddress().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(
								this.settingContactInformationDto.getPersonalMobileEmailAddress().getUpdatable())))
						.build())
				.dialInNumber(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting
								.valueOf(this.settingContactInformationDto.getDialInNumber().getContactUsageSetting()))
						.updatable(Optional.ofNullable(
								NotUseAtr.valueOf(this.settingContactInformationDto.getDialInNumber().getUpdatable())))
						.build())
				.extensionNumber(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getExtensionNumber().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getExtensionNumber().getUpdatable())))
						.build())
				.emergencyNumber1(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getEmergencyNumber1().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getEmergencyNumber1().getUpdatable())))
						.build())
				.emergencyNumber2(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(
								this.settingContactInformationDto.getEmergencyNumber2().getContactUsageSetting()))
						.updatable(Optional.ofNullable(NotUseAtr
								.valueOf(this.settingContactInformationDto.getEmergencyNumber2().getUpdatable())))
						.build())
				.otherContacts(this.settingContactInformationDto.getOtherContacts().stream()
						.map(o -> OtherContact.builder().no(o.getNo()).contactName(new ContactName(o.getContactName()))
								.contactUsageSetting(ContactUsageSetting.valueOf(o.getContactUsageSetting())).build())
						.collect(Collectors.toList()))
				.build();
	}
}
