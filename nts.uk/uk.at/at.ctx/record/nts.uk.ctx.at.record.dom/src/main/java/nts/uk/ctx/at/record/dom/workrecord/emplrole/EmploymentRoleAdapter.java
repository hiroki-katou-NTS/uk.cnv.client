package nts.uk.ctx.at.record.dom.workrecord.emplrole;

import java.util.List;

import nts.uk.ctx.at.auth.dom.employmentrole.EmploymentRole;

public interface EmploymentRoleAdapter {

	List<EmploymentRole> getEmploymentRoleByCompany(String companyId);
}
