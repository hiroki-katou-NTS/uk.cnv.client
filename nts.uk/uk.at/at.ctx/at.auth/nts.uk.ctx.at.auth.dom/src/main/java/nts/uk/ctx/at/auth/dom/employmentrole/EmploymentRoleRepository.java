package nts.uk.ctx.at.auth.dom.employmentrole;

import java.util.List;
import java.util.Optional;

public interface EmploymentRoleRepository {
	/**
	 * get*(会社ID)
	 * @param companyId 会社ID
	 * @return
	 */
	List<EmploymentRole> getAllByCompanyId(String companyId);
	/**
	 * get(ロールID )
	 * @param roleId ロールID
	 * @return
	 */
	Optional<EmploymentRole> getEmploymentRoleById(String roleId);
	
	/**
	 * insert(就業ロール )
	 * @param employmentRole 就業ロール
	 */
	void addEmploymentRole(EmploymentRole employmentRole );
	
	/**
	 * update(就業ロール )
	 * @param employmentRole 就業ロール
	 */
	void updateEmploymentRole(EmploymentRole employmentRole );
	
	/**
	 * delete(ロールID )
	 * @param roleId ロールID
	 */
	void deleteEmploymentRole(String roleId );
	
}
