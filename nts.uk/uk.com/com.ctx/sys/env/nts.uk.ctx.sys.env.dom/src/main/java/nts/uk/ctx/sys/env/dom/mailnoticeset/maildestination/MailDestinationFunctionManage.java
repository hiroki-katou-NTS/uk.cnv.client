package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.メール送信先機能管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class MailDestinationFunctionManage extends AggregateRoot {

	/**
	 * 機能ID
	 */
	private final FunctionType functionId;

	/**
	 * 会社メールアドレスを利用する
	 */
	private NotUseAtr useCompanyMailAddress;

	/**
	 * 会社携帯メールアドレスを利用する
	 */
	private NotUseAtr useCompanyMobileMailAddress;

	/**
	 * 個人メールアドレスを利用する
	 */
	private NotUseAtr usePersonalMailAddress;

	/**
	 * 個人携帯メールアドレスを利用する
	 */
	private NotUseAtr usePersonalMobileMailAddress;

	/**
	 * [1] 利用できるメールアドレスを求める
	 * 
	 * @param require
	 * @param sids    List<社員ID>
	 * @return List<利用できるメールアドレス>
	 */
	public List<AvailableMailAddress> getAvailableMailAddresses(Require require, List<String> sids) {
		boolean isUseCompany = this.useCompanyMailAddress.isUse() || this.useCompanyMobileMailAddress.isUse();
		boolean isUsePersonal = this.usePersonalMailAddress.isUse() || this.usePersonalMobileMailAddress.isUse();

		List<EmployeeMailAddressImport> employeeMailAddresses = isUseCompany ? require.getEmployeeContactInfo(sids)
				: Collections.emptyList();
		List<PersonalMailAddressImport> personalMailAddresses = isUsePersonal ? require.getPersonalContactInfo(sids)
				: Collections.emptyList();

		return sids.stream().map(sid -> {
			Optional<EmployeeMailAddressImport> employeeMailAddress = employeeMailAddresses.stream()
					.filter(data -> data.getSid().equals(sid)).findFirst();
			Optional<PersonalMailAddressImport> personalMailAddress = personalMailAddresses.stream()
					.filter(data -> data.getSid().equals(sid)).findFirst();
			return this.summarizeAvailableMails(sid, employeeMailAddress.orElse(null),
					personalMailAddress.orElse(null));
		}).collect(Collectors.toList());
	}

	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * 
	 * @param sid                 社員ID
	 * @param employeeMailAddress 社員のメールアドレス
	 * @param personalMailAddress 個人のメールアドレス
	 * @return 個人のメールアドレス
	 */
	private AvailableMailAddress summarizeAvailableMails(String sid, EmployeeMailAddressImport employeeMailAddress,
			PersonalMailAddressImport personalMailAddress) {
		Optional<MailAddress> optCompanyMailAddress = Optional.empty();
		Optional<MailAddress> optCompanyMobileMailAddress = Optional.empty();
		Optional<MailAddress> optPersonalMailAddress = Optional.empty();
		Optional<MailAddress> optPersonalMobileMailAddress = Optional.empty();

		if (employeeMailAddress != null) {
			if (this.useCompanyMailAddress.isUse()) {
				optCompanyMailAddress = employeeMailAddress.getCompanyMailAddress();
			}
			if (this.useCompanyMobileMailAddress.isUse()) {
				optCompanyMobileMailAddress = employeeMailAddress.getCompanyMobileMailAddress();
			}
		}

		if (personalMailAddress != null) {
			if (this.usePersonalMailAddress.isUse()) {
				optPersonalMailAddress = personalMailAddress.getPersonalMailAddress();
			}
			if (this.usePersonalMobileMailAddress.isUse()) {
				optPersonalMobileMailAddress = personalMailAddress.getPersonalMobileMailAddress();
			}
		}

		return new AvailableMailAddress(sid, optCompanyMailAddress, optCompanyMobileMailAddress, optPersonalMailAddress,
				optPersonalMobileMailAddress);
	}

	public interface Require {
		// [R-1] 社員IDListから社員連絡先を取得
		List<EmployeeMailAddressImport> getEmployeeContactInfo(List<String> sids);

		// [R-2]社員IDListから個人連絡先を取得
		List<PersonalMailAddressImport> getPersonalContactInfo(List<String> sids);
	}
}
