package nts.uk.ctx.at.record.ac.employmentrole;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.auth.pub.employmentrole.EmploymentRolePub;
import nts.uk.ctx.at.record.dom.workrecord.emplrole.EmployeeRole;
import nts.uk.ctx.at.record.dom.workrecord.emplrole.EmploymentRoleAdapter;
@Stateless
public class EmploymentRolesFinder implements EmploymentRoleAdapter {

	@Inject
	private EmploymentRolePub employmentRolePub;

	@Override
	public List<EmployeeRole> getEmploymentRoleByCompany(String companyId) {
		return employmentRolePub.getAllByCompanyId(companyId).stream().map(item -> {
			return new EmployeeRole(item.getCompanyId(), item.getRoleId());
		}).collect(Collectors.toList());
	}

}
