package nts.uk.ctx.sys.env.ac.mailnoticeset.maildestination;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.pub.contact.EmployeeContactPub;
import nts.uk.ctx.bs.employee.pub.contact.PersonContactObjectOfEmployee;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;

@Stateless
public class PersonalContactInfoAdapterImpl implements PersonalContactInfoAdapter {

	@Inject
	private EmployeeContactPub employeeContactPub;

	@Override
	public List<PersonalMailAddressImport> find(List<String> sids) {
		// 1. <call>(社員IDList): List<List<社員の個人連絡先>>
		List<PersonContactObjectOfEmployee> personalContacts = this.employeeContactPub.getListOfEmployees(sids);

		// 2. create()
		return personalContacts.stream()
				.map(data -> new PersonalMailAddressImport(data.getEmployeeId(),
						Optional.ofNullable(data.getMailAdress()).map(MailAddress::new),
						Optional.ofNullable(data.getMobileMailAdress()).map(MailAddress::new)))
				.collect(Collectors.toList());
	}
}
