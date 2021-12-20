package nts.uk.ctx.sys.env.ac.mailnoticeset.maildestination;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.pub.contact.EmployeeContactObject;
import nts.uk.ctx.bs.employee.pub.contact.EmployeeContactPub;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;

@Stateless
public class EmployeeContactInfoAdapterImpl implements EmployeeContactInfoAdapter {

	@Inject
	private EmployeeContactPub employeeContactPub;

	@Override
	public List<EmployeeMailAddressImport> find(List<String> sids) {
		// 1. <call>(社員IDList): List<社員連絡先>
		List<EmployeeContactObject> employeeContacts = this.employeeContactPub.getList(sids);

		// 2. create()
		return employeeContacts
				.stream().map(data -> new EmployeeMailAddressImport(data.getSid(),
						Optional.ofNullable(data.getMailAddress()).map(MailAddress::new), 
						Optional.ofNullable(data.getPhoneMailAddress()).map(MailAddress::new)))
				.collect(Collectors.toList());
	}
}
