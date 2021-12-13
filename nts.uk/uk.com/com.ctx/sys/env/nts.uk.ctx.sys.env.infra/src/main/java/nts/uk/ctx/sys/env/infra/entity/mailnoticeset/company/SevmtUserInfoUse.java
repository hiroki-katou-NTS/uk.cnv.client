package nts.uk.ctx.sys.env.infra.entity.mailnoticeset.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactName;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactUsageSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.OtherContact;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.SettingContactInformation;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * Entity ユーザー情報の使用方法
 */
@Data
@Entity
@Table(name = "SEVMT_USER_INFO_USE")
@EqualsAndHashCode(callSuper = true)
public class SevmtUserInfoUse extends UkJpaEntity
		implements Serializable, UserInformationUseMethod.MementoGetter, UserInformationUseMethod.MementoSetter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "CID")
	public String cId;

	// column 排他バージョン
	@Column(name = "EXCLUS_VER")
	private long version;
	// column 契約コード
	@Basic(optional = false)
	@Column(name = "CONTRACT_CD")
	private String contractCd;
	/**
	 * プロフィールの利用
	 */
	@Basic(optional = false)
	@Column(name = "PROFILE_USE")
	private Integer useOfProfile;
	/**
	 * パスワードの利用
	 */
	@Basic(optional = false)
	@Column(name = "PASSWORD_USE")
	private Integer useOfPassword;
	/**
	 * 通知の利用
	 */
	@Basic(optional = false)
	@Column(name = "NOTIFICATION_USE")
	private Integer useOfNotice;
	/**
	 * 言語の利用
	 */
	@Basic(optional = false)
	@Column(name = "LANGUAGE_USE")
	private Integer useOfLanguage;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_NUMBER_COM_USE")
	private Integer phoneNumberComUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_NUMBER_COM_UP_ABLE")
	private Integer phoneNumberComUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_NUMBER_PS_USE")
	private Integer phoneNumberPsUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_NUMBER_PS_UP_ABLE")
	private Integer phoneNumberPsUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "URGENT_PHONE_NUMBER1_USE")
	private Integer urgentPhoneNumber1Use;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "URGENT_PHONE_NUMBER1_UP_ABLE")
	private Integer urgentPhoneNumber1Updatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "URGENT_PHONE_NUMBER2_USE")
	private Integer urgentPhoneNumber2Use;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "URGENT_PHONE_NUMBER2_UP_ABLE")
	private Integer urgentPhoneNumber2Updatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "DIAL_IN_NUMBER_USE")
	private Integer dialInNumberUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "DIAL_IN_NUMBER_UP_ABLE")
	private Integer dialInNumberUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "EXTENSION_NUMBER_USE")
	private Integer extensionNumberUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "EXTENSION_NUMBER_UP_ABLE")
	private Integer extensionNumberUpdatable;
	/**
	 * 会社メールアドレス
	 */
	@Basic(optional = true)
	@Column(name = "MAIL_COM_USE")
	private Integer mailComUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "MAIL_COM_UP_ABLE")
	private Integer mailComUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "MAIL_PS_USE")
	private Integer mailPsUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "MAIL_PS_UP_ABLE")
	private Integer mailPsUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_MAIL_COM_USE")
	private Integer phoneMailComUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_MAIL_COM_UP_ABLE")
	private Integer phoneMailComUpdatable;
	/**
	 * 連絡先利用設定
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_MAIL_PS_USE")
	private Integer phoneMailPsUse;
	/**
	 * 更新可能：（するしない区分）
	 */
	@Basic(optional = true)
	@Column(name = "PHONE_MAIL_PS_UP_ABLE")
	private Integer phoneMailPsUpdatable;
	/**
	 * 他の連絡先名1
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT1_NAME")
	private String otherContact1Name;
	/**
	 * 他の連絡先1利用設定
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT1_USE")
	private Integer otherContact1Use;
	/**
	 * 他の連絡先名2
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT2_NAME")
	private String otherContact2Name;
	/**
	 * 他の連絡先2利用設定
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT2_USE")
	private Integer otherContact2Use;
	/**
	 * 他の連絡先名3
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT3_NAME")
	private String otherContact3Name;
	/**
	 * 他の連絡先3利用設定
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT3_USE")
	private Integer otherContact3Use;
	/**
	 * 他の連絡先名4
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT4_NAME")
	private String otherContact4Name;
	/**
	 * 他の連絡先4利用設定
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT4_USE")
	private Integer otherContact4Use;
	/**
	 * 他の連絡先名5
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT5_NAME")
	private String otherContact5Name;
	/**
	 * 他の連絡先5利用設定
	 */
	@Basic(optional = true)
	@Column(name = "OTHER_CONTACT5_USE")
	private Integer otherContact5Use;

	@Override
	protected Object getKey() {
		return this.cId;
	}

	@Override
	public String getCompanyId() {
		return this.cId;
	}

	@Override
	public void setCompanyId(String companyId) {
		this.setCId(companyId);
	}

	@Override
	public void setSettingContactInformation(SettingContactInformation settingContactInformation) {
		List<OtherContact> otherContacts = settingContactInformation.getOtherContacts();
		ContactSetting dialInNumber = settingContactInformation.getDialInNumber();
		ContactSetting companyEmailAddress = settingContactInformation.getCompanyEmailAddress();
		ContactSetting companyMobileEmailAddress = settingContactInformation.getCompanyMobileEmailAddress();
		ContactSetting companyMobilePhone = settingContactInformation.getCompanyMobilePhone();
		ContactSetting personalEmailAddress = settingContactInformation.getPersonalEmailAddress();
		ContactSetting personalMobileEmailAddress = settingContactInformation.getPersonalMobileEmailAddress();
		ContactSetting personalPhoneNumber = settingContactInformation.getPersonalMobilePhone();
		ContactSetting extensionNumber = settingContactInformation.getExtensionNumber();
		ContactSetting emergencyNumber1 = settingContactInformation.getEmergencyNumber1();
		ContactSetting emergencyNumber2 = settingContactInformation.getEmergencyNumber2();
		this.mailComUse = companyEmailAddress.getContactUsageSetting().value;
		this.mailComUpdatable = companyEmailAddress.getUpdatable().isPresent()
				? companyEmailAddress.getUpdatable().get().value
				: null;
		this.phoneMailComUse = companyMobileEmailAddress.getContactUsageSetting().value;
		this.phoneMailComUpdatable = companyMobileEmailAddress.getUpdatable().isPresent()
				? companyMobileEmailAddress.getUpdatable().get().value
				: null;
		this.phoneNumberComUse = companyMobilePhone.getContactUsageSetting().value;
		this.phoneNumberComUpdatable = companyMobilePhone.getUpdatable().isPresent()
				? companyMobilePhone.getUpdatable().get().value
				: null;
		this.mailPsUse = personalEmailAddress.getContactUsageSetting().value;
		this.mailPsUpdatable = personalEmailAddress.getUpdatable().isPresent()
				? personalEmailAddress.getUpdatable().get().value
				: null;
		this.phoneMailPsUse = personalMobileEmailAddress.getContactUsageSetting().value;
		this.phoneMailPsUpdatable = personalMobileEmailAddress.getUpdatable().isPresent()
				? personalMobileEmailAddress.getUpdatable().get().value
				: null;
		this.phoneNumberPsUse = personalPhoneNumber.getContactUsageSetting().value;
		this.phoneNumberPsUpdatable = personalPhoneNumber.getUpdatable().isPresent()
				? personalPhoneNumber.getUpdatable().get().value
				: null;
		this.extensionNumberUse = extensionNumber.getContactUsageSetting().value;
		this.extensionNumberUpdatable = extensionNumber.getUpdatable().isPresent()
				? extensionNumber.getUpdatable().get().value
				: null;
		this.dialInNumberUse = dialInNumber.getContactUsageSetting().value;
		this.dialInNumberUpdatable = dialInNumber.getUpdatable().isPresent()
				? dialInNumber.getUpdatable().get().value
				: null;
		this.urgentPhoneNumber1Use = emergencyNumber1.getContactUsageSetting().value;
		this.urgentPhoneNumber1Updatable = emergencyNumber1.getUpdatable().isPresent()
				? emergencyNumber1.getUpdatable().get().value
				: null;
		this.urgentPhoneNumber2Use = emergencyNumber2.getContactUsageSetting().value;
		this.urgentPhoneNumber2Updatable = emergencyNumber2.getUpdatable().isPresent()
				? emergencyNumber2.getUpdatable().get().value
				: null;
		this.otherContact1Name = otherContacts.size() > 0 ? otherContacts.get(0).getContactName().v() : null;
		this.otherContact1Use = otherContacts.size() > 0 ? otherContacts.get(0).getContactUsageSetting().value : null;
		this.otherContact2Name = otherContacts.size() > 0 ? otherContacts.get(1).getContactName().v() : null;
		this.otherContact2Use = otherContacts.size() > 0 ? otherContacts.get(1).getContactUsageSetting().value : null;
		this.otherContact3Name = otherContacts.size() > 0 ? otherContacts.get(2).getContactName().v() : null;
		this.otherContact3Use = otherContacts.size() > 0 ? otherContacts.get(2).getContactUsageSetting().value : null;
		this.otherContact4Name = otherContacts.size() > 0 ? otherContacts.get(3).getContactName().v() : null;
		this.otherContact4Use = otherContacts.size() > 0 ? otherContacts.get(3).getContactUsageSetting().value : null;
		this.otherContact5Name = otherContacts.size() > 0 ? otherContacts.get(4).getContactName().v() : null;
		this.otherContact5Use = otherContacts.size() > 0 ? otherContacts.get(4).getContactUsageSetting().value : null;
	}

	@Override
	public SettingContactInformation getSettingContactInformation() {
		List<OtherContact> otherContacts = new ArrayList<>();
		otherContacts.add(OtherContact.builder().no(1).contactName(new ContactName(this.otherContact1Name))
				.contactUsageSetting(EnumAdaptor.valueOf(this.otherContact1Use, ContactUsageSetting.class)).build());
		otherContacts.add(OtherContact.builder().no(2).contactName(new ContactName(this.otherContact2Name))
				.contactUsageSetting(EnumAdaptor.valueOf(this.otherContact2Use, ContactUsageSetting.class)).build());
		otherContacts.add(OtherContact.builder().no(3).contactName(new ContactName(this.otherContact3Name))
				.contactUsageSetting(EnumAdaptor.valueOf(this.otherContact3Use, ContactUsageSetting.class)).build());
		otherContacts.add(OtherContact.builder().no(4).contactName(new ContactName(this.otherContact4Name))
				.contactUsageSetting(EnumAdaptor.valueOf(this.otherContact4Use, ContactUsageSetting.class)).build());
		otherContacts.add(OtherContact.builder().no(5).contactName(new ContactName(this.otherContact5Name))
				.contactUsageSetting(EnumAdaptor.valueOf(this.otherContact5Use, ContactUsageSetting.class)).build());
		return SettingContactInformation.builder()
				.companyEmailAddress(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.mailComUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.mailComUpdatable))).build())
				.companyMobileEmailAddress(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.phoneMailComUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.phoneMailComUpdatable))).build())
				.companyMobilePhone(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.phoneNumberComUse))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.phoneNumberComUpdatable))).build())
				.personalEmailAddress(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.mailPsUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.mailPsUpdatable))).build())
				.personalMobileEmailAddress(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.phoneMailPsUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.phoneMailPsUpdatable))).build())
				.personalMobilePhone(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.phoneNumberPsUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.phoneNumberPsUpdatable))).build())
				.dialInNumber(
						ContactSetting.builder().contactUsageSetting(ContactUsageSetting.valueOf(this.dialInNumberUse))
								.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.dialInNumberUpdatable))).build())
				.extensionNumber(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.extensionNumberUse))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.extensionNumberUpdatable))).build())
				.emergencyNumber1(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.urgentPhoneNumber1Use))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.urgentPhoneNumber1Updatable))).build())
				.emergencyNumber2(ContactSetting.builder()
						.contactUsageSetting(ContactUsageSetting.valueOf(this.urgentPhoneNumber2Use))
						.updatable(Optional.ofNullable(NotUseAtr.valueOf(this.urgentPhoneNumber2Updatable))).build())
				.otherContacts(otherContacts).build();
	}
}