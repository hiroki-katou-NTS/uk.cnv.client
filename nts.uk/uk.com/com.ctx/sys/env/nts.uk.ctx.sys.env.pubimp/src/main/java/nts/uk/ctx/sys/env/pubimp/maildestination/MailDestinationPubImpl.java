package nts.uk.ctx.sys.env.pubimp.maildestination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.env.dom.contact.EmployeeContactAdapter;
import nts.uk.ctx.sys.env.dom.contact.EmployeeContactObjectImport;
import nts.uk.ctx.sys.env.dom.contact.PersonContactObjectOfEmployeeImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.EmailClassification;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.EmailDestinationFunction;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethodRepository;
import nts.uk.ctx.sys.env.pub.maildestination.IMailDestinationPub;
import nts.uk.ctx.sys.env.pub.maildestination.MailDestination;

/**
 * @author sonnlb
 *
 */
@Stateless
public class MailDestinationPubImpl implements IMailDestinationPub {

	@Inject
	private UserInformationUseMethodRepository userInformationUseMethodRepository;
	@Inject
	private EmployeeContactAdapter empContactAdapter;

	@Override
	public List<MailDestination> getEmpEmailAddress(String cID, List<String> sIDs, Integer functionID) {

		List<MailDestination> emailAddress = new ArrayList<>();
		/**
		 * パラメータ.社員ID(List)をメール送信一覧に追加する
		 */
		sIDs.forEach(sID -> {
			emailAddress.add(new MailDestination(sID, Collections.emptyList()));
		});
		/**
		 * ドメインモデル「ユーザー情報の使用方法」を取得する
		 */
		Optional<UserInformationUseMethod> userInformationUseMethodOpt = userInformationUseMethodRepository
				.findByCId(cID);

		/**
		 * 取得件数０件
		 */
		if (!userInformationUseMethodOpt.isPresent()
				|| !userInformationUseMethodOpt.get().getSettingContactInformation().isPresent()) {
			return emailAddress;
		}
		/**
		 * 取得件数１件以上
		 */
		UserInformationUseMethod userInformationUseMethod = userInformationUseMethodOpt.get();

		/**
		 * 取得した「ユーザー情報の使用方法」kから、使えるメールの判断する
		 */
		boolean companyEmailAddressFlag = false;
		boolean personalEmailAddressFlag = false;
		boolean companyMobileEmailAddressFlag = false;
		boolean personalMobileEmailAddressFlag = false;
		if (!userInformationUseMethod.getEmailDestinationFunctions().isEmpty()) {
			for (EmailDestinationFunction e : userInformationUseMethod.getEmailDestinationFunctions()) {
				if (e.getEmailClassification() == EmailClassification.COMPANY_EMAIL_ADDRESS
						&& e.getFunctionIds().stream().anyMatch(id -> id.v() == functionID) //#115050
						&& userInformationUseMethod.getSettingContactInformation().get().getCompanyEmailAddress()
								.getContactUsageSetting().value != 0) {
					companyEmailAddressFlag = true;
				}
				if (e.getEmailClassification() == EmailClassification.PERSONAL_EMAIL_ADDRESS
						&& e.getFunctionIds().stream().anyMatch(id -> id.v() == functionID) //#115050
						&& userInformationUseMethod.getSettingContactInformation().get().getPersonalEmailAddress()
								.getContactUsageSetting().value != 0) {
					personalEmailAddressFlag = true;
				}
				if (e.getEmailClassification() == EmailClassification.COMPANY_MOBILE_EMAIL_ADDRESS
						&& e.getFunctionIds().stream().anyMatch(id -> id.v() == functionID) //#115050
						&& userInformationUseMethod.getSettingContactInformation().get().getCompanyMobileEmailAddress()
								.getContactUsageSetting().value != 0) {
					companyMobileEmailAddressFlag = true;
				}
				if (e.getEmailClassification() == EmailClassification.PERSONAL_MOBILE_EMAIL_ADDRESS
						&& e.getFunctionIds().stream().anyMatch(id -> id.v() == functionID) //#115050
						&& userInformationUseMethod.getSettingContactInformation().get().getPersonalMobileEmailAddress()
								.getContactUsageSetting().value != 0) {
					personalMobileEmailAddressFlag = true;
				}
			}
		}

		/**
		 * 「会社メールアドレスFlag」と「会社携帯メールアドレスFlag」をチェックする 両方がfalse場合
		 */
		if (!companyEmailAddressFlag && !companyMobileEmailAddressFlag) {
			/**
			 * 「個人メールアドレスFlag」と「個人携帯メールアドレスFlag」をチェックする
			 */
			if (!personalEmailAddressFlag && !personalMobileEmailAddressFlag) {
				/**
				 * パラメータ.社員ID(List)をメール送信一覧を渡す
				 */
				return emailAddress;
			}
				/**
				 * その他 Imported(環境)「個人連絡先」を取得する RequestList 420
				 */
				List<PersonContactObjectOfEmployeeImport> personContacts = empContactAdapter.getListOfEmployees(sIDs);

				if (personalEmailAddressFlag) { //#115050
					sIDs.forEach(sID -> {
						Optional<MailDestination> mailDestinationOpt = emailAddress.stream()
								.filter(mailDestination -> mailDestination.getEmployeeID().equals(sID)).findFirst();

						mailDestinationOpt.ifPresent(mailDestination -> {
							Optional<PersonContactObjectOfEmployeeImport> perContactOpt = personContacts.stream()
									.filter(perContact -> perContact.getPersonId().equals(sID)).findFirst();

							perContactOpt.ifPresent(item -> {
								mailDestination.addOutGoingMails(item.getMailAdress());
							});
						});
					});
				}
				
				if (personalMobileEmailAddressFlag) { //#115050
					sIDs.forEach(sID -> {
						Optional<MailDestination> mailDestinationOpt = emailAddress.stream()
								.filter(mailDestination -> mailDestination.getEmployeeID().equals(sID)).findFirst();

						mailDestinationOpt.ifPresent(mailDestination -> {
							Optional<PersonContactObjectOfEmployeeImport> perContactOpt = personContacts.stream()
									.filter(perContact -> perContact.getPersonId().equals(sID)).findFirst();

							perContactOpt.ifPresent(item -> {
								mailDestination.addOutGoingMails(item.getMobileMailAdress());
							});
						});
					});
				}
			
		} else {
			/**
			 * その他 Imported(環境)「社員連絡先」を取得する RequestList 378
			 */
			List<EmployeeContactObjectImport> empContacts = empContactAdapter.getList(sIDs);

			if (companyEmailAddressFlag) { //#115050
				sIDs.forEach(sID -> {
					Optional<MailDestination> mailDestinationOpt = emailAddress.stream()
							.filter(mailDestination -> mailDestination.getEmployeeID().equals(sID)).findFirst();

					mailDestinationOpt.ifPresent(mailDestination -> {
						Optional<EmployeeContactObjectImport> empContactOpt = empContacts.stream()
								.filter(empContact -> empContact.getSid().equals(sID)).findFirst();

						empContactOpt.ifPresent(item -> mailDestination.addOutGoingMails(item.getMailAddress()));
					});
				});
			}

			if (companyMobileEmailAddressFlag) { //#115050
				sIDs.forEach(sID -> {
					Optional<MailDestination> mailDestinationOpt = emailAddress.stream()
							.filter(mailDestination -> mailDestination.getEmployeeID().equals(sID)).findFirst();

					mailDestinationOpt.ifPresent(mailDestination -> {
						Optional<EmployeeContactObjectImport> empContactOpt = empContacts.stream()
								.filter(empContact -> empContact.getSid().equals(sID)).findFirst();

						empContactOpt.ifPresent(item -> {
							mailDestination.addOutGoingMails(item.getPhoneMailAddress());
						});
					});
				});
			}
		}

		return emailAddress;
	}
}
