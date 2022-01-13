package nts.uk.ctx.at.aggregation.infra.repository.schedulecounter.criterion;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSetting;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;
import nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion.KagmtCriterionMoneyUseage;

/**
 * 
 * @author TU-TK
 *
 */
@Stateless
public class JpaCriterionAmountUsageSettingRepository extends JpaRepository implements CriterionAmountUsageSettingRepository {

	private static final String GET_BY_CID = "SELECT a FROM KagmtCriterionMoneyUseage a WHERE a.companyId = :companyId ";

	@Override
	public void insert(CriterionAmountUsageSetting usageSetting) {
		this.commandProxy().insert(KagmtCriterionMoneyUseage.toEntity(usageSetting));
	}

	@Override
	public void update(CriterionAmountUsageSetting usageSetting) {
		KagmtCriterionMoneyUseage dataOld = this.queryProxy()
				.query(GET_BY_CID, KagmtCriterionMoneyUseage.class)
				.setParameter("companyId", usageSetting.getCid()).getSingle().get();
		KagmtCriterionMoneyUseage dataNew = KagmtCriterionMoneyUseage.toEntity(usageSetting);
		dataOld.employmentUse = dataNew.employmentUse;
		this.commandProxy().update(dataOld);
	}

	@Override
	public boolean exist(String cid) {
		return this.get(cid).isPresent();
	}

	@Override
	public Optional<CriterionAmountUsageSetting> get(String cid) {
		Optional<KagmtCriterionMoneyUseage> result = this.queryProxy()
				.query(GET_BY_CID, KagmtCriterionMoneyUseage.class).setParameter("companyId", cid).getSingle();
		if (!result.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(result.get().toDomain());
	}

}
