package nts.uk.ctx.exio.infra.repository.exo.authset;

import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSet;
import nts.uk.ctx.exio.dom.exo.authset.ExOutCtgAuthSetRepository;
import nts.uk.ctx.exio.infra.entity.exo.authset.OiomtExOutCtgAuthSet;
import nts.uk.shr.infra.permit.data.JpaAvailablityPermissionRepositoryBase;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class JpaExOutCtgAuthSetRepository
		extends JpaAvailablityPermissionRepositoryBase<ExOutCtgAuthSet, OiomtExOutCtgAuthSet>
		implements ExOutCtgAuthSetRepository {
	private static final String SELECT_BY_ROLE_ID = "SELECT f FROM OiomtExOutCtgAuthSet f"
			+ " WHERE f.pk.companyId =:cid AND f.pk.roleId =:roleId ";
	private static final String DELETE_BY_ROLE_ID = "DELETE from OiomtExOutCtgAuthSet f "
			+ " WHERE f.pk.companyId =:cid AND f.pk.roleId = :roleId ";

	@Override
	protected Class<OiomtExOutCtgAuthSet> getEntityClass() {
		return OiomtExOutCtgAuthSet.class;
	}

	@Override
	protected OiomtExOutCtgAuthSet createEmptyEntity() {
		return new OiomtExOutCtgAuthSet();
	}

	@Override
	public List<ExOutCtgAuthSet> findByCidAndRoleId(String cid, String roleId) {
		return this.queryProxy().query(SELECT_BY_ROLE_ID, OiomtExOutCtgAuthSet.class)
				.setParameter("cid", cid)
				.setParameter("roleId", roleId)
				.getList(OiomtExOutCtgAuthSet::toDomain);
	}

	@Override
	public void delete(String cid, String roleId) {
		this.getEntityManager().createQuery(DELETE_BY_ROLE_ID)
				.setParameter("cid", cid)
				.setParameter("roleId", roleId)
				.executeUpdate();
		this.getEntityManager().flush();
	}
}
