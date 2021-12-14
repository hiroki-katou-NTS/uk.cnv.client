package nts.uk.ctx.sys.portal.infra.repository.webmenu.webmenulinking;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTies;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleByRoleTiesRepository;
import nts.uk.ctx.sys.portal.infra.entity.webmenu.webmenulinking.SptmtRoleByRoleTies;

@Stateless
public class JpaRoleByRoleTiesRepository extends JpaRepository implements  RoleByRoleTiesRepository {

	private static final String  GET_ROLE_BY_ROLE_TIES_BY_CODE = "SELECT c FROM SptmtRoleByRoleTies c "
			+ " WHERE c.roleId  = :roleId ";
	
	@Override
	public void insertRoleByRoleTies(RoleByRoleTies roleByRoleTies) {
		this.commandProxy().insert(SptmtRoleByRoleTies.toEntity(roleByRoleTies));
		
	}

	@Override
	public void updateRoleByRoleTies(RoleByRoleTies roleByRoleTies) {
		SptmtRoleByRoleTies dataUpdate = SptmtRoleByRoleTies.toEntity(roleByRoleTies);
		SptmtRoleByRoleTies newData = this.queryProxy().find(dataUpdate.roleId, SptmtRoleByRoleTies.class).get();
		newData.setWebMenuCd(dataUpdate.webMenuCd);
		
	}

	@Override
	public void deleteRoleByRoleTies(String roleId) {
		this.commandProxy().remove(SptmtRoleByRoleTies.class,roleId);
	}

	@Override
	public Optional<RoleByRoleTies> getRoleByRoleTiesById(String roleId) {
		Optional<RoleByRoleTies> data = this.queryProxy().query(GET_ROLE_BY_ROLE_TIES_BY_CODE,SptmtRoleByRoleTies.class)
				.setParameter("roleId", roleId)
				.getSingle(c->c.toDomain());
		return data;
	}
	
	@Override
	public Optional<RoleByRoleTies> getByRoleIdAndCompanyId(String roleId, String companyId) {
		Optional<RoleByRoleTies> data = this.queryProxy().query(GET_ROLE_BY_ROLE_TIES_BY_CODE 
				+ "AND (c.companyId = :companyId OR c.companyId = '000000000000-0000')", SptmtRoleByRoleTies.class)
				.setParameter("roleId", roleId)
				.setParameter("companyId", companyId)
				.getSingle(c -> c.toDomain());
		
		return data;
	}




}
