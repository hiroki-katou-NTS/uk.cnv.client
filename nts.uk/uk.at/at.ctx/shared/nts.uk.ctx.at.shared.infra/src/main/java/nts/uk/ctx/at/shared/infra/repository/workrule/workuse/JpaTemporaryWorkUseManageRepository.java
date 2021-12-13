package nts.uk.ctx.at.shared.infra.repository.workrule.workuse;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManage;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository;
import nts.uk.ctx.at.shared.infra.entity.workusage.KshmtTemporaryMng;
import nts.uk.ctx.at.shared.infra.entity.workusage.KshstTempWkUseManagePK;

/**
 * The Class JpaTemporaryWorkUseManageRepository.
 * @author HoangNDH
 */
@Stateless
public class JpaTemporaryWorkUseManageRepository extends JpaRepository implements TemporaryWorkUseManageRepository {

	/**
	 * Convert to domain.
	 *
	 * @param setting the setting
	 * @return the flex work set
	 */
	public TemporaryWorkUseManage convertToDomain(KshmtTemporaryMng setting) {
		return TemporaryWorkUseManage.createFromJavaType(setting.getId().getCid(), BooleanUtils.toInteger(setting.isUseClassification()));
	}
	
	/**
	 * Convert to db type.
	 *
	 * @param setting the setting
	 * @return the kshst flex work setting
	 */
	public KshmtTemporaryMng convertToDbType(TemporaryWorkUseManage setting) {
		KshmtTemporaryMng entity = new KshmtTemporaryMng();
		KshstTempWkUseManagePK primaryKey = new KshstTempWkUseManagePK();
		primaryKey.setCid(setting.getCompanyId().v());
		entity.setId(primaryKey);
		entity.setUseClassification(BooleanUtils.toBoolean(setting.getUseClassification().value));
		return entity;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository#findByCid(java.lang.String)
	 */
	@Override
	public Optional<TemporaryWorkUseManage> findByCid(String companyId) {
		return this.queryProxy()
				.find(new KshstTempWkUseManagePK(companyId), KshmtTemporaryMng.class)
				.map(x -> convertToDomain(x));

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository#insert(nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManage)
	 */
	@Override
	public void insert(TemporaryWorkUseManage managementSetting) {
		KshmtTemporaryMng setting = convertToDbType(managementSetting);
		this.commandProxy().insert(setting);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository#update(nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManage)
	 */
	@Override
	public void update(TemporaryWorkUseManage managementSetting) {
		KshmtTemporaryMng setting = convertToDbType(managementSetting);
		this.commandProxy().update(setting);
	}

}
