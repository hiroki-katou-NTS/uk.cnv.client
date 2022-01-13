/**
 * 
 */
package nts.uk.ctx.at.auth.infra.repository.initswitchsetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSet;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSetRepo;
import nts.uk.ctx.at.auth.infra.entity.initswitchsetting.KacmtDispPeriodSwitch;
import nts.uk.ctx.at.auth.infra.entity.initswitchsetting.KacmtDispPeriodSwitchPK;

/**
 * @author hieult
 *
 */
@Stateless
public class JpaInitDisplayPeriodSwitchSetRepo extends JpaRepository implements InitDisplayPeriodSwitchSetRepo{

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSetRepo#findByKey(java.lang.String, java.lang.String)
	 */
	private static final String GET_BY_KEY = "SELECT e "
			+ " FROM KacmtDispPeriodSwitch e"
			+ " WHERE e.kacmtDispPeriodSwitchPK.companyID = :companyID"
			+ " AND e.kacmtDispPeriodSwitchPK.roleID = :roleID";
	
	private static final String GET_BY_CID = "SELECT e "
			+ " FROM KacmtDispPeriodSwitch e"
			+ " WHERE e.kacmtDispPeriodSwitchPK.companyID = :companyID";
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<InitDisplayPeriodSwitchSet> findByKey(String companyID, String roleID) {
		
		return this.queryProxy().query(GET_BY_KEY, KacmtDispPeriodSwitch.class)
				.setParameter("companyID", companyID)
				.setParameter("roleID", roleID)
				.getSingle(c -> c.toDomain());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void save(InitDisplayPeriodSwitchSet domain) {
		Optional<KacmtDispPeriodSwitch> optEntity = this.queryProxy().query(GET_BY_KEY, KacmtDispPeriodSwitch.class)
				.setParameter("companyID", domain.getCompanyID())
				.setParameter("roleID", domain.getRoleID())
				.getSingle();
		if (optEntity.isPresent()) {
			KacmtDispPeriodSwitch entity = optEntity.get();
			entity.setDay(domain.getDay());
			this.commandProxy().update(entity);
			return;
		}
		
		KacmtDispPeriodSwitch entity = KacmtDispPeriodSwitch.toEntity(domain);
		this.commandProxy().insert(entity);
		
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteByRoleAndCompany(String cid, String roleID) {
		KacmtDispPeriodSwitchPK pk = new KacmtDispPeriodSwitchPK(cid, roleID);
		this.commandProxy().remove(KacmtDispPeriodSwitch.class, pk);
	}

	@Override
	public List<InitDisplayPeriodSwitchSet> findByCid(String companyId) {
		return this.queryProxy().query(GET_BY_CID, KacmtDispPeriodSwitch.class)
				.setParameter("companyID", companyId)
				.getList(KacmtDispPeriodSwitch::toDomain);
	}

}
