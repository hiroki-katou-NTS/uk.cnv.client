package nts.uk.ctx.at.shared.infra.repository.bonuspay;

import java.math.BigDecimal;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.BPUnitUseSettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BPUnitUseSetting;
import nts.uk.ctx.at.shared.infra.entity.bonuspay.KbpstBPUnitUseSetting;
import nts.uk.ctx.at.shared.infra.entity.bonuspay.KbpstBPUnitUseSettingPK;

@Stateless
public class JpaBPUnitUseSettingRepository extends JpaRepository implements BPUnitUseSettingRepository {
	private final String SELECT_BY_COMPANYID = "SELECT c FROM KBPSTBPUnitUseSetting c WHERE c.KBPSTBPUnitUseSetting.companyId = :companyId";

	@Override
	public Optional<BPUnitUseSetting> getSetting(String companyId) {
		Optional<KbpstBPUnitUseSetting> kbpstBPUnitUseSetting = this.queryProxy()
				.query(SELECT_BY_COMPANYID, KbpstBPUnitUseSetting.class).setParameter("companyId", companyId)
				.getSingle();
		return Optional.ofNullable(this.toUnitUseSettingDomain(kbpstBPUnitUseSetting.get()));
	}

	@Override
	public void updateSetting(BPUnitUseSetting setting) {

		this.commandProxy().update(this.toUnitUseSettingEntity(setting));
	}

	private KbpstBPUnitUseSetting toUnitUseSettingEntity(BPUnitUseSetting bPUnitUseSetting) {
		return new KbpstBPUnitUseSetting(new KbpstBPUnitUseSettingPK(bPUnitUseSetting.getCompanyId().toString()),
				new BigDecimal(bPUnitUseSetting.getWorkplaceUseAtr().value),
				new BigDecimal(bPUnitUseSetting.getPersonalUseAtr().value),
				new BigDecimal(bPUnitUseSetting.getWorkingTimesheetUseAtr().value));
	}

	@Override
	public void insertSetting(BPUnitUseSetting setting) {
		this.commandProxy().insert(this.toUnitUseSettingEntity(setting));
	}

	private BPUnitUseSetting toUnitUseSettingDomain(KbpstBPUnitUseSetting kbpstBPUnitUseSetting) {
		return BPUnitUseSetting.createFromJavaType(kbpstBPUnitUseSetting.companyId.companyId,
				kbpstBPUnitUseSetting.workplaceUseAtr.intValue(), kbpstBPUnitUseSetting.personalUseAtr.intValue(),
				kbpstBPUnitUseSetting.workingTimesheetUseAtr.intValue());
	}
	

}
