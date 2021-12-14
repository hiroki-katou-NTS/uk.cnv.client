package nts.uk.ctx.sys.env.dom.mailnoticeset.company;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import java.util.List;
import java.util.Optional;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.会社.会社.ユーザー情報の使用方法
 */
@Getter
public class UserInformationUseMethod extends AggregateRoot {
	/**
	 * お知らせの利用
	 */
	private NotUseAtr useOfNotice;

	/**
	 * パスワードの利用
	 */
	private NotUseAtr useOfPassword;

	/**
	 * プロフィールの利用
	 */
	private NotUseAtr useOfProfile;

	/**
	 * 言語の利用
	 */
	private NotUseAtr useOfLanguage;

	/**
	 * 会社ID
	 */
	private String companyId;

	/**
	 * メール送信先機能
	 */
	private List<EmailDestinationFunction> emailDestinationFunctions;

	/**
	 * 連絡先情報の設定
	 */
	private Optional<SettingContactInformation> settingContactInformation;

	public static UserInformationUseMethod createFromMemento(MementoGetter memento) {
		UserInformationUseMethod domain = new UserInformationUseMethod();
		domain.getMemento(memento);
		return domain;
	}

	public void getMemento(MementoGetter memento) {
		this.companyId = memento.getCompanyId();
		this.emailDestinationFunctions = memento.getEmailDestinationFunctions();
		this.settingContactInformation = Optional.ofNullable(memento.getSettingContactInformation());
		this.useOfLanguage = NotUseAtr.valueOf(memento.getUseOfLanguage());
		this.useOfNotice = NotUseAtr.valueOf(memento.getUseOfNotice());
		this.useOfPassword = NotUseAtr.valueOf(memento.getUseOfPassword());
		this.useOfProfile = NotUseAtr.valueOf(memento.getUseOfProfile());
	}

	public void setMemento(MementoSetter memento) {
		memento.setCompanyId(this.companyId);
		memento.setEmailDestinationFunctions(this.emailDestinationFunctions);
		memento.setSettingContactInformation(this.settingContactInformation.orElse(null));
		memento.setUseOfLanguage(this.useOfLanguage.value);
		memento.setUseOfNotice(this.useOfNotice.value);
		memento.setUseOfPassword(this.useOfPassword.value);
		memento.setUseOfProfile(this.useOfProfile.value);
	}

	public interface MementoSetter {

		void setUseOfNotice(Integer useOfNotice);

		void setUseOfPassword(Integer useOfPassword);

		void setUseOfProfile(Integer useOfProfile);

		void setUseOfLanguage(Integer useOfLanguage);

		void setCompanyId(String companyId);

		void setEmailDestinationFunctions(List<EmailDestinationFunction> emailDestinationFunctions);

		void setSettingContactInformation(SettingContactInformation settingContactInformation);
	}

	public interface MementoGetter {
		Integer getUseOfNotice();

		Integer getUseOfPassword();

		Integer getUseOfProfile();

		Integer getUseOfLanguage();

		String getCompanyId();

		List<EmailDestinationFunction> getEmailDestinationFunctions();

		SettingContactInformation getSettingContactInformation();
	}
}
