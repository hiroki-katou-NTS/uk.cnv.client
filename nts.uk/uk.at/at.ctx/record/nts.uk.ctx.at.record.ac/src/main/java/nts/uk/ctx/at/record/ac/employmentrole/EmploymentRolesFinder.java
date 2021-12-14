package nts.uk.ctx.at.record.ac.employmentrole;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;
import nts.uk.ctx.at.auth.pub.employmentrole.EmploymentRolePub;
import nts.uk.ctx.at.record.dom.workrecord.emplrole.EmploymentRoleAdapter;

@Stateless
public class EmploymentRolesFinder implements EmploymentRoleAdapter {

	@Inject
	private EmploymentRolePub employmentRolePub;

	@Override
	public List<EmploymentRole> getEmploymentRoleByCompany(String companyId) {
		return employmentRolePub.getAllByCompanyId(companyId).stream().map(item -> {
			return new EmploymentRole(item.getCompanyId(), item.getRoleId());
		}).collect(Collectors.toList());
	}

}
