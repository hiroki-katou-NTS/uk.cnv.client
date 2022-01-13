package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinputusagesetting;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSetting;
import nts.uk.ctx.at.record.dom.jobmanagement.usagesetting.ManHrInputUsageSettingRepository;

/**
 * 
 * @author tutt
 *
 */
@Stateless
public class JpaManHrInputUsageSettingRepository extends JpaRepository implements ManHrInputUsageSettingRepository {

	@Override
	public void insert(ManHrInputUsageSetting usageSetting) {
		this.commandProxy().insert(new KrcmtManHrUse(usageSetting));
	}

	@Override
	public void update(ManHrInputUsageSetting usageSetting) {
		this.commandProxy().update(new KrcmtManHrUse(usageSetting));
	}

	@Override
	public Optional<ManHrInputUsageSetting> get(String cId) {
		return this.queryProxy().find(cId, KrcmtManHrUse.class).map(u -> u.toDomain());
	}

}
