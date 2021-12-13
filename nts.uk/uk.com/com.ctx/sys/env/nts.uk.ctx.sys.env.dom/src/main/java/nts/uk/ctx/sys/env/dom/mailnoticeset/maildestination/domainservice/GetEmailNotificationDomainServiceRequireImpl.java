package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice;

import java.util.Optional;

import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRepository;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRequireImpl;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalContactInfoAdapter;

public class GetEmailNotificationDomainServiceRequireImpl extends MailDestinationFunctionManageRequireImpl implements GetEmailNotificationDomainService.Require {

	private MailDestinationFunctionManageRepository repository;
	
	public GetEmailNotificationDomainServiceRequireImpl(EmployeeContactInfoAdapter employeeContactInfoAdapter,
			PersonalContactInfoAdapter personalContactInfoAdapter, MailDestinationFunctionManageRepository repository) {
		super(employeeContactInfoAdapter, personalContactInfoAdapter);
		this.repository = repository;
	}

	@Override
	public Optional<MailDestinationFunctionManage> findByFunctionId(String cid, int functionId) {
		return this.repository.findByFunctionId(cid, functionId);
	}
}
