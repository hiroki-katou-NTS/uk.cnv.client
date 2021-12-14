package nts.uk.ctx.at.shared.infra.repository.bonuspay;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.repository.BPTimeItemSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.infra.entity.bonuspay.KbpstBPTimeItemSetting;
import nts.uk.ctx.at.shared.infra.entity.bonuspay.KbpstBPTimeItemSettingPK;

@Stateless
public class JpaBPTimeItemSettingRepository extends JpaRepository implements BPTimeItemSettingRepository {
	private static final String SELECT_BPTIMEITEMSET_BY_COMPANYID = "SELECT c " + " FROM KbpstBPTimeItemSetting c "
			+ " JOIN  KbpstBonusPayTimeItem k "
			+ " ON c.kbpstBPTimeItemSettingPK.timeItemNo = k.kbpstBonusPayTimeItemPK.timeItemNo "
			+ " AND c.kbpstBPTimeItemSettingPK.timeItemTypeAtr = k.kbpstBonusPayTimeItemPK.timeItemTypeAtr "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = k.kbpstBonusPayTimeItemPK.companyId "
			+ " WHERE c.kbpstBPTimeItemSettingPK.timeItemTypeAtr = 0 " + " AND k.useAtr = 1 "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = :companyId "
			+ " ORDER BY k.kbpstBonusPayTimeItemPK.timeItemNo";

	private static final String SELECT_SPEC_BPTIMEITEMSET_BY_COMPANYID = "SELECT c " + " FROM KbpstBPTimeItemSetting c "
			+ " JOIN KbpstBonusPayTimeItem k "
			+ " ON c.kbpstBPTimeItemSettingPK.timeItemNo = k.kbpstBonusPayTimeItemPK.timeItemNo "
			+ " AND c.kbpstBPTimeItemSettingPK.timeItemTypeAtr = k.kbpstBonusPayTimeItemPK.timeItemTypeAtr "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = k.kbpstBonusPayTimeItemPK.companyId "
			+ " WHERE k.kbpstBonusPayTimeItemPK.timeItemTypeAtr = 1 " + " AND k.useAtr = 1 "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = :companyId "
			+ " ORDER BY k.kbpstBonusPayTimeItemPK.timeItemNo";
	
	private static final String SELECT_ALL_BPTIMEITEMSET_BY_COMPANYID = "SELECT c " + " FROM KbpstBPTimeItemSetting c "
			+ " JOIN KbpstBonusPayTimeItem k "
			+ " ON c.kbpstBPTimeItemSettingPK.timeItemNo = k.kbpstBonusPayTimeItemPK.timeItemNo "
			+ " AND c.kbpstBPTimeItemSettingPK.timeItemTypeAtr = k.kbpstBonusPayTimeItemPK.timeItemTypeAtr "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = k.kbpstBonusPayTimeItemPK.companyId "
			+ " WHERE k.useAtr = 1 "
			+ " AND c.kbpstBPTimeItemSettingPK.companyId = :companyId "
			+ " ORDER BY k.kbpstBonusPayTimeItemPK.timeItemNo";

	@Override
	public List<BPTimeItemSetting> getListSetting(String companyId) {
		return this.queryProxy().query(SELECT_BPTIMEITEMSET_BY_COMPANYID, KbpstBPTimeItemSetting.class)
				.setParameter("companyId", companyId).getList(x -> this.toBPTimeItemSettingDomain(x));
	}

	@Override
	public List<BPTimeItemSetting> getListSpecialSetting(String companyId) {
		return this.queryProxy().query(SELECT_SPEC_BPTIMEITEMSET_BY_COMPANYID, KbpstBPTimeItemSetting.class)
				.setParameter("companyId", companyId).getList(x -> this.toBPTimeItemSettingDomain(x));
	}
	
	@Override
	public List<BPTimeItemSetting> getListAllSetting(String companyId) {
		return this.queryProxy().query(SELECT_ALL_BPTIMEITEMSET_BY_COMPANYID, KbpstBPTimeItemSetting.class)
				.setParameter("companyId", companyId).getList(x -> this.toBPTimeItemSettingDomain(x));
	}

	@Override
	public void addListSetting(List<BPTimeItemSetting> lstSetting) {
		List<KbpstBPTimeItemSetting> lstKbpstBPTimeItemSetting = lstSetting.stream()
				.map(c -> toBPTimeItemSettingEntity(c)).collect(Collectors.toList());
		this.commandProxy().insertAll(lstKbpstBPTimeItemSetting);
	}

	@Override
	public void updateListSetting(List<BPTimeItemSetting> lstSetting) {
		lstSetting.forEach(c -> {
			Optional<KbpstBPTimeItemSetting> kbpstBPTimeItemSettingOptional = this.queryProxy()
					.find(new KbpstBPTimeItemSettingPK(c.getCompanyId().toString(), new BigDecimal(c.getTimeItemNo()), new BigDecimal(c.getTimeItemTypeAtr().value)),
							KbpstBPTimeItemSetting.class);
			if (kbpstBPTimeItemSettingOptional.isPresent()) {
				KbpstBPTimeItemSetting kbpstBPTimeItemSetting = kbpstBPTimeItemSettingOptional.get();
				kbpstBPTimeItemSetting.holidayCalSettingAtr = new BigDecimal(c.getHolidayCalSettingAtr().value);
				kbpstBPTimeItemSetting.overtimeCalSettingAtr = new BigDecimal(c.getOvertimeCalSettingAtr().value);
				kbpstBPTimeItemSetting.worktimeCalSettingAtr = new BigDecimal(c.getWorktimeCalSettingAtr().value);
				this.commandProxy().update(kbpstBPTimeItemSetting);
			} else {
				this.commandProxy().insert(c);
			}
		});
	}

	private BPTimeItemSetting toBPTimeItemSettingDomain(KbpstBPTimeItemSetting kbpstBPTimeItemSetting) {
		return BPTimeItemSetting.createFromJavaType(kbpstBPTimeItemSetting.kbpstBPTimeItemSettingPK.companyId,
				kbpstBPTimeItemSetting.kbpstBPTimeItemSettingPK.timeItemNo.intValue(),
				kbpstBPTimeItemSetting.holidayCalSettingAtr.intValue(),
				kbpstBPTimeItemSetting.overtimeCalSettingAtr.intValue(),
				kbpstBPTimeItemSetting.worktimeCalSettingAtr.intValue(),
				kbpstBPTimeItemSetting.kbpstBPTimeItemSettingPK.timeItemTypeAtr.intValue());

	}

	private KbpstBPTimeItemSetting toBPTimeItemSettingEntity(BPTimeItemSetting bpTimeItemSetting) {

		return new KbpstBPTimeItemSetting(
				new KbpstBPTimeItemSettingPK(bpTimeItemSetting.getCompanyId().toString(),
						new BigDecimal(bpTimeItemSetting.getTimeItemNo()),
						new BigDecimal(bpTimeItemSetting.getTimeItemTypeAtr().value)),
				new BigDecimal(bpTimeItemSetting.getHolidayCalSettingAtr().value),
				new BigDecimal(bpTimeItemSetting.getOvertimeCalSettingAtr().value),
				new BigDecimal(bpTimeItemSetting.getWorktimeCalSettingAtr().value));
	}

}
