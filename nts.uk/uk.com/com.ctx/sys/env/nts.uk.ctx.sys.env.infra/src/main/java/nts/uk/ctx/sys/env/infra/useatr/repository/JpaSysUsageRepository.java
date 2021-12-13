package nts.uk.ctx.sys.env.infra.useatr.repository;

import java.util.Optional;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.env.dom.useatr.SysUsageRepository;
import nts.uk.ctx.sys.env.dom.useatr.SysUsageSet;
import nts.uk.ctx.sys.env.infra.useatr.entity.SacmtSysUsageSet;
import nts.uk.ctx.sys.env.infra.useatr.entity.SacmtSysUsageSetPK;
@Stateless
public class JpaSysUsageRepository extends JpaRepository implements SysUsageRepository{
	/**
	 * convert from SacmtSysUsageSet entity to SysUsageSet domain
	 * @param entity
	 * @return
	 * author: Hoang Yen
	 */
	private static SysUsageSet toDomainSys(SacmtSysUsageSet entity){
		SysUsageSet domain = SysUsageSet.createFromJavaType(entity.sacmtSysUsageSetPK.companyId,
															entity.jinji,
															entity.shugyo, entity.kyuyo);
		return domain;
	}
	
	/**
	 * convert from SysUsageSet domain to SacmtSysUsageSet entity 
	 * @param domain
	 * @return
	 * author: Hoang Yen
	 */
	private static SacmtSysUsageSet toEntitySys(SysUsageSet domain){
		val entity = new SacmtSysUsageSet();
		entity.sacmtSysUsageSetPK = new SacmtSysUsageSetPK(domain.getCompanyId());
		entity.jinji = domain.getJinji().value;
		entity.shugyo = domain.getShugyo().value;
		entity.kyuyo = domain.getKyuyo().value;
		return entity;
	}

	/**
	 * find SysUsageSet
	 * @param companyId
	 * @return
	 * author: Hoang Yen
	 */
	@Override
	public Optional<SysUsageSet> findUsageSet(String companyId) {
		val pk = new SacmtSysUsageSetPK(companyId);
		return this.queryProxy().find(pk, SacmtSysUsageSet.class).map(c -> toDomainSys(c));
	}

	/**
	 * update UsageSet
	 * @param sysUsageSet
	 * author: Hoang Yen
	 */
	@Override
	public void updateUsageSet(SysUsageSet sysUsageSet) {
		SacmtSysUsageSet entity = toEntitySys(sysUsageSet);
		Optional<SacmtSysUsageSet> sacmtSysUsageSetOpt = this.queryProxy()
				.find(entity.sacmtSysUsageSetPK, SacmtSysUsageSet.class);
		if (!sacmtSysUsageSetOpt.isPresent()) {
			this.insertUsageSet(sysUsageSet);
			return;
		}
		SacmtSysUsageSet oldEntity = sacmtSysUsageSetOpt.get();
		oldEntity.jinji = entity.jinji;
		oldEntity.shugyo = entity.shugyo;
		oldEntity.kyuyo = entity.kyuyo;
		this.commandProxy().update(oldEntity);
	}

	/**
	 * insert UsageSet
	 * @param sysUsageSet
	 * author: Hoang Yen
	 */
	@Override
	public void insertUsageSet(SysUsageSet sysUsageSet) {
		SacmtSysUsageSet entity = toEntitySys(sysUsageSet);
		this.commandProxy().insert(entity);
	}

	/**
	 * delete a item
	 * author: Hoang Yen
	 */
	@Override
	public void deleteUsageSet(String companyId, String companyCode, String contractCd) {
		SacmtSysUsageSetPK sacmtSysUsageSetPK = new SacmtSysUsageSetPK(companyId);
		this.commandProxy().remove(SacmtSysUsageSet.class, sacmtSysUsageSetPK);
	}
	
}
