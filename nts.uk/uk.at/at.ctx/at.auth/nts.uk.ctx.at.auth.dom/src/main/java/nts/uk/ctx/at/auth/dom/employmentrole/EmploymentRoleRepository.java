package nts.uk.ctx.at.auth.dom.employmentrole;

import java.util.List;
import java.util.Optional;

public interface EmploymentRoleRepository {
	
	List<EmploymentRole> getAllByCompanyId(String companyId);
	
	List<EmploymentRole> getListEmploymentRole(String companyId);
	
	Optional<EmploymentRole> getEmploymentRoleById(String companyId,String roleId);
	
	void addEmploymentRole(EmploymentRole employmentRole );
	
	void updateEmploymentRole(EmploymentRole employmentRole );
	
	void deleteEmploymentRole(String companyId,String roleId );
	
}
