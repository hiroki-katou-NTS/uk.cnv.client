package nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking;

import java.util.Optional;

public interface RoleByRoleTiesRepository {
	
	Optional<RoleByRoleTies> getByRoleIdAndCompanyId(String roleId, String companyId);
	
	void insertRoleByRoleTies(RoleByRoleTies roleByRoleTies);

	void updateRoleByRoleTies(RoleByRoleTies roleByRoleTies);

	void deleteRoleByRoleTies(String roleId, String companyId);

}

