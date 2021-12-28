package nts.uk.ctx.sys.portal.infra.repository.toppagesetting;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageRoleSetting;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageRoleSettingRepository;
import nts.uk.ctx.sys.portal.infra.entity.toppagesetting.SptmtTopPageRoleSet;
import nts.uk.ctx.sys.portal.infra.entity.toppagesetting.SptmtTopPageRoleSetPK;

/**
 * The Class JpaTopPageRoleSettingRepository.
 */
@Stateless
public class JpaTopPageRoleSettingRepository extends JpaRepository implements TopPageRoleSettingRepository {
	
	/** The Constant SEL. */
	private static final String SEL = "SELECT c FROM SptmtTopPageRoleSet c ";
	
	/** The Constant SELECT_BY_CID. */
	private static final String SELECT_BY_CID = SEL + "WHERE c.pk.companyId = :companyId";
	
	/** The Constant SELECT_BY_ROLE_SET_CD. */
	private static final String SELECT_BY_ROLE_SET_CD = SEL + "WHERE c.pk.companyId = :companyId "
			+ " AND c.pk.roleSetCode = :roleSetCode";
	
	/**
	 * Insert.
	 *
	 * @param domain the domain
	 * @param companyId the company id
	 * @param contractCd the contract cd
	 */
	@Override
	public void insert(TopPageRoleSetting domain, String companyId, String contractCd) {
		this.commandProxy().insert(this.toEntity(domain, companyId, contractCd));
	}

	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @param companyId the company id
	 * @param contractCd the contract cd
	 * @return the sptmt top page role set
	 */
	private SptmtTopPageRoleSet toEntity(TopPageRoleSetting domain, String companyId, String contractCd) {
		SptmtTopPageRoleSet entity = new SptmtTopPageRoleSet();
		entity.setContractCd(contractCd);
		entity.setCompanyId(companyId);
		domain.setMemento(entity);
		return entity;
	}

	/**
	 * Update.
	 *
	 * @param domain the domain
	 * @param companyId the company id
	 * @param contractCd the contract cd
	 */
	@Override
	public void update(TopPageRoleSetting domain, String companyId, String contractCd) {
		SptmtTopPageRoleSet oldData = this.queryProxy()
				.query(SELECT_BY_ROLE_SET_CD, SptmtTopPageRoleSet.class)
				.setParameter("companyId", companyId)
				.setParameter("roleSetCode", domain.getRoleSetCode())
				.getSingle().orElse(new SptmtTopPageRoleSet());
		SptmtTopPageRoleSet newData = this.toEntity(domain, companyId, contractCd);
		oldData.setLoginMenuCode(newData.getLoginMenuCode());
		oldData.setTopMenuCode(newData.getTopMenuCode());
		oldData.setSystem(newData.getSystem());
		oldData.setMenuClassification(newData.getMenuClassification());
		this.commandProxy().update(oldData);
	}

	/**
	 * Delete.
	 *
	 * @param domain the domain
	 * @param companyId the company id
	 */
	@Override
	public void delete(TopPageRoleSetting domain, String companyId) {
		SptmtTopPageRoleSetPK pk = new SptmtTopPageRoleSetPK(
				companyId,
				domain.getRoleSetCode().v());
		this.commandProxy().remove(SptmtTopPageRoleSet.class, pk);
	}

	/**
	 * Gets the by company id.
	 *
	 * @param companyId the company id
	 * @return the by company id
	 */
	@Override
	public List<TopPageRoleSetting> getByCompanyId(String companyId) {
		return this.queryProxy()
			.query(SELECT_BY_CID, SptmtTopPageRoleSet.class)
			.setParameter("companyId", companyId)
			.getList(TopPageRoleSetting::createFromMemento);
	}

	/**
	 * Gets the by company id and role set code.
	 *
	 * @param companyId the company id
	 * @param roleSetCode the role set code
	 * @return the by company id and role set code
	 */
	@Override
	public Optional<TopPageRoleSetting> getByCompanyIdAndRoleSetCode(String companyId, String roleSetCode) {
		return this.queryProxy()
			.query(SELECT_BY_ROLE_SET_CD, SptmtTopPageRoleSet.class)
			.setParameter("companyId", companyId)
			.setParameter("roleSetCode", roleSetCode)
			.getSingle(TopPageRoleSetting::createFromMemento);
	}

}
