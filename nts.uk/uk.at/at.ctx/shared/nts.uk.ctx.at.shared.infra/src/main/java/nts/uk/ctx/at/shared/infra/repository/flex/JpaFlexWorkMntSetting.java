package nts.uk.ctx.at.shared.infra.repository.flex;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkMntSetRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkSet;
import nts.uk.ctx.at.shared.infra.entity.flex.KshmtFlexMng;
import nts.uk.ctx.at.shared.infra.entity.flex.KshstFlexWorkSettingPK;

/**
 * The Class JpaFlexWorkMntSetting.
 * @author HoangNDH
 */
@Stateless
public class JpaFlexWorkMntSetting extends JpaRepository implements FlexWorkMntSetRepository {

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.flex.FlexWorkMntSetRepository#find(java.lang.String)
	 */
	@Override
	public Optional<FlexWorkSet> find(String companyId) {
		return this.queryProxy()
				.find(new KshstFlexWorkSettingPK(companyId), KshmtFlexMng.class)
				.map(x -> convertToDomain(x));
	}
	
	/**
	 * Convert to domain.
	 *
	 * @param setting the setting
	 * @return the flex work set
	 */
	public FlexWorkSet convertToDomain(KshmtFlexMng setting) {
		return FlexWorkSet.createFromJavaType(setting.getId().getCid(), BooleanUtils.toInteger(setting.isManageFlexWork()));
	}
	
	/**
	 * Convert to db type.
	 *
	 * @param setting the setting
	 * @return the kshst flex work setting
	 */
	public KshmtFlexMng convertToDbType(FlexWorkSet setting) {
		KshmtFlexMng entity = new KshmtFlexMng();
		KshstFlexWorkSettingPK primaryKey = new KshstFlexWorkSettingPK();
		primaryKey.setCid(setting.getCompanyId().v());
		entity.setId(primaryKey);
		entity.setManageFlexWork(BooleanUtils.toBoolean(setting.getUseFlexWorkSetting().value));
		return entity;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.flex.FlexWorkMntSetRepository#add(nts.uk.ctx.at.shared.dom.flex.FlexWorkSet)
	 */
	@Override
	public void add(FlexWorkSet flexWorkSetting) {
		KshmtFlexMng setting = convertToDbType(flexWorkSetting);
		this.commandProxy().insert(setting);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.flex.FlexWorkMntSetRepository#update(nts.uk.ctx.at.shared.dom.flex.FlexWorkSet)
	 */
	@Override
	public void update(FlexWorkSet flexWorkSetting) {
		Optional<KshmtFlexMng> optEntity = this.queryProxy().find(new KshstFlexWorkSettingPK(flexWorkSetting.getCompanyId().v()), KshmtFlexMng.class);
		
		KshmtFlexMng setting;
		if (optEntity.isPresent()) {
			setting = optEntity.get();
			setting.setManageFlexWork(BooleanUtils.toBoolean(flexWorkSetting.getUseFlexWorkSetting().value));
		}
		else {
			setting = convertToDbType(flexWorkSetting);
		}
				
		this.commandProxy().update(setting);
	}
	
}
