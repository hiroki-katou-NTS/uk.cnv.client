package nts.uk.ctx.sys.env.pubimp.maildestination;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.AvailableMailAddress;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.FunctionType;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRepository;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice.GetEmailNotificationDomainService;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice.GetEmailNotificationDomainServiceRequireImpl;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice.MailAddressNotification;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.ctx.sys.env.pub.maildestination.AvailableMailAddressExport;
import nts.uk.ctx.sys.env.pub.maildestination.IMailDestinationPub;
import nts.uk.ctx.sys.env.pub.maildestination.MailAddressNotificationExport;
import nts.uk.ctx.sys.env.pub.maildestination.MailDestinationExport;
import nts.uk.ctx.sys.env.pub.maildestination.MailDestinationFunctionManageExport;
import nts.uk.ctx.sys.env.pub.maildestination.SentMailListExport;

@Stateless
public class MailDestinationPubImpl implements IMailDestinationPub {

	@Inject
	private MailDestinationFunctionManageRepository repository;

	@Inject
	private EmployeeContactInfoAdapter employeeContactInfoAdapter;

	@Inject
	private PersonalContactInfoAdapter personalContactInfoAdapter;

	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.Export.RQ397：社員のメールアドレスを取得する.アルゴリズム.アルゴリズム
	 */
	@Override
	public MailDestinationExport getEmployeeMails(String cid, List<String> sids, int functionId) {

		GetEmailNotificationDomainServiceRequireImpl require = new GetEmailNotificationDomainServiceRequireImpl(
				employeeContactInfoAdapter, personalContactInfoAdapter, repository);
		// 社員のメールアドレス通知を取得する
		MailAddressNotification mailAddressNotification = GetEmailNotificationDomainService.get(require, cid,
				EnumAdaptor.valueOf(functionId, FunctionType.class), sids);

		// メール送信先を作成して、渡す
		MailAddressNotificationExport export = this.convertToExport(mailAddressNotification);
		List<SentMailListExport> sentMailLists = mailAddressNotification.getMailAddresses().stream()
				.map(data -> new SentMailListExport(this.getTotalMails(data), data.getSid()))
				.collect(Collectors.toList());
		return new MailDestinationExport(export, sentMailLists);
	}

	private MailAddressNotificationExport convertToExport(MailAddressNotification domain) {
		List<AvailableMailAddressExport> mailAddresses = domain.getMailAddresses().stream()
				.map(data -> new AvailableMailAddressExport(data.getSid(),
						data.getOptCompanyMailAddress().map(MailAddress::v),
						data.getOptCompanyMobileMailAddress().map(MailAddress::v),
						data.getOptPersonalMailAddress().map(MailAddress::v),
						data.getOptPersonalMobileMailAddress().map(MailAddress::v)))
				.collect(Collectors.toList());
		Optional<MailDestinationFunctionManageExport> mailDestinationFunctionManage = domain
				.getMailDestinationFunctionManage()
				.map(data -> new MailDestinationFunctionManageExport(data.getFunctionId().value,
						data.getUseCompanyMailAddress().value, data.getUseCompanyMobileMailAddress().value,
						data.getUsePersonalMailAddress().value, data.getUsePersonalMobileMailAddress().value));
		return new MailAddressNotificationExport(mailAddresses, mailDestinationFunctionManage);
	}

	private List<String> getTotalMails(AvailableMailAddress availableMailAddress) {
		return Arrays
				.asList(availableMailAddress.getOptCompanyMailAddress().orElse(null),
						availableMailAddress.getOptCompanyMobileMailAddress().orElse(null),
						availableMailAddress.getOptPersonalMailAddress().orElse(null),
						availableMailAddress.getOptPersonalMobileMailAddress().orElse(null))
				.stream().filter(Objects::nonNull).map(MailAddress::v).collect(Collectors.toList());
	}
}
