package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.schedule.monthly;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.FixedExtractionSMonCon;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.FixedExtractionSMonConRepository;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixCondDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.monthly.KscdtScheFixCondMonth;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.monthly.KscdtScheFixCondMonthPk;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class JpaFixedExtractionSMonConRepository extends JpaRepository implements FixedExtractionSMonConRepository {
	private static final String SELECT_BASIC = "SELECT a FROM KscdtScheFixCondMonth a";
	private static final String BY_CONTRACT_COMPANY = " WHERE a.pk.cid = :companyId AND a.contractCd = :contractCode";
	private static final String BY_ERAL_CHECK_ID = " AND a.pk.cid = :companyId AND a.contractCd = :contractCode AND a.pk.checkId = :eralCheckIds";
	
    @Override
    public List<FixedExtractionSMonCon> getAll() {
        List<KscdtScheFixCondMonth> entities = new ArrayList<>();
        return null;
    }

	@Override
	public void add(String contractCode, String companyId, FixedExtractionSMonCon domain) {
		this.commandProxy().insert(toEntity(contractCode, companyId, domain));
	}

	@Override
	public void update(String contractCode, String companyId, FixedExtractionSMonCon domain) {
		KscdtScheFixCondMonthPk pk = new KscdtScheFixCondMonthPk(companyId, domain.getErrorAlarmWorkplaceId(), domain.getFixedCheckSMonItems().value);
		Optional<KscdtScheFixCondMonth> entityOpt = this.queryProxy().find(pk, KscdtScheFixCondMonth.class);
		KscdtScheFixCondMonth entity = entityOpt.get();
		entity.useAtr = domain.isUseAtr();
		entity.condMsg = domain.getMessageDisp().get().v();
		
		this.commandProxy().update(entity);
	}

	@Override
	public void delete(String contractCode, String companyId, String erAlCheckIds) {
		List<KscdtScheFixCondMonth> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheFixCondMonth.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", erAlCheckIds)
				.getList();
		this.commandProxy().removeAll(entities);
	}
	
	/**
	 * Convert from domain to entity KscdtScheFixCondMonth
	 * @param contractCode
	 * @param companyId
	 * @param domain
	 * @return
	 */
	private KscdtScheFixCondMonth toEntity(String contractCode, String companyId, FixedExtractionSMonCon domain) {
		KscdtScheFixCondMonthPk pk = new KscdtScheFixCondMonthPk(companyId, domain.getErrorAlarmWorkplaceId(), domain.getFixedCheckSMonItems().value);
		KscdtScheFixCondMonth entity = new KscdtScheFixCondMonth(pk, domain.isUseAtr(), domain.getMessageDisp().get().v());
		entity.setContractCd(contractCode);
		return entity;
	}

	@Override
	public List<FixedExtractionSMonCon> getScheFixCond(String contractCode, String companyId, String eralCheckIds) {
		List<KscdtScheFixCondMonth> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheFixCondMonth.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.getList();
        return entities.stream().map(item -> item.toDomain()).collect(Collectors.toList());
	}
}
