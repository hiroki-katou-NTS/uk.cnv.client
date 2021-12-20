package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import java.util.List;

import lombok.AllArgsConstructor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.EmployeeMailAddressImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalContactInfoAdapter;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter.PersonalMailAddressImport;

@AllArgsConstructor
public class MailDestinationFunctionManageRequireImpl implements MailDestinationFunctionManage.Require {

	private EmployeeContactInfoAdapter employeeContactInfoAdapter;
	private PersonalContactInfoAdapter personalContactInfoAdapter;
	
	@Override
	public List<EmployeeMailAddressImport> getEmployeeContactInfo(List<String> sids) {
		return this.employeeContactInfoAdapter.find(sids);
	}

	@Override
	public List<PersonalMailAddressImport> getPersonalContactInfo(List<String> sids) {
		return this.personalContactInfoAdapter.find(sids);
	}

}
