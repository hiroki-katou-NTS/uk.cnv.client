package nts.uk.ctx.at.shared.infra.repository.workrule.deformed;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSetting;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository;
import nts.uk.ctx.at.shared.infra.entity.workrule.deformed.KshmtDeformedLaborMng;
import nts.uk.ctx.at.shared.infra.entity.workrule.deformed.KshstAggLaborSetPK;

/**
 * The Class JpaAggDeformedLaborSetting.
 *
 * @author HoangNDH
 */
@Stateless
public class JpaAggDeformedLaborSetting extends JpaRepository implements AggDeformedLaborSettingRepository {
	
//	private static final String FIND;
	
//	static {
//		StringBuilder builderString = new StringBuilder();
//		builderString.append("SELECT a ");
//		builderString.append("FROM KshmtDeformedLaborMng a ");
//		builderString.append("WHERE a.KshstAggLaborSetPK.companyId = :companyId ");
//		FIND = builderString.toString();
//	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository#findByCid(java.lang.String)
	 */
	@Override
	public Optional<AggDeformedLaborSetting> findByCid(String companyId) {
		return this.queryProxy()
				.find(new KshstAggLaborSetPK(companyId), KshmtDeformedLaborMng.class)
				.map(x -> convertToDomain(x));
	}

	private AggDeformedLaborSetting convertToDomain(KshmtDeformedLaborMng aggLaborSet) {
		return new AggDeformedLaborSetting(new CompanyId(aggLaborSet.getId().getCid()), 
				EnumAdaptor.valueOf(BooleanUtils.toInteger(aggLaborSet.isUseDeformLabor()), UseAtr.class));
	}

	/**
	 * To db type.
	 *
	 * @param aggLaborSet the agg labor set
	 * @return the kshst agg labor set
	 */
	private KshmtDeformedLaborMng toDbType (AggDeformedLaborSetting aggLaborSet) {
		KshmtDeformedLaborMng setting = new KshmtDeformedLaborMng();
		KshstAggLaborSetPK primaryKey = new KshstAggLaborSetPK();
		primaryKey.setCid(aggLaborSet.getCompanyId().v());
		setting.setId(primaryKey);
		setting.setUseDeformLabor(BooleanUtils.toBoolean(aggLaborSet.getUseDeformedLabor().value));
		return setting;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository#insert(nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSetting)
	 */
	@Override
	public void insert(AggDeformedLaborSetting aggLaborSet) {
		KshmtDeformedLaborMng setting = toDbType(aggLaborSet);
		this.commandProxy().insert(setting);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository#update(nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSetting)
	 */
	@Override
	public void update(AggDeformedLaborSetting aggLaborSet) {
		KshmtDeformedLaborMng setting = toDbType(aggLaborSet);
		this.commandProxy().update(setting);
	}

}
