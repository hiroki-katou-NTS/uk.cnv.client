package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.schedule.daily;


import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.FixedExtractSDailyConRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.FixedExtractionSDailyCon;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.FixedExtractionSDailyItems;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixCondDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixCondDayPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixItemDay;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class JpaFixedExtractSDailyConRepository extends JpaRepository implements FixedExtractSDailyConRepository {
	private static final String SELECT_BASIC = "SELECT a FROM KscdtScheFixCondDay a";
	private static final String BY_CONTRACT_COMPANY = " WHERE a.pk.cid = :companyId AND a.contractCd = :contractCode";
	private static final String BY_ERAL_CHECK_ID = " AND a.pk.checkId = :eralCheckIds";
	private static final String BY_USE_ATR = " AND a.useAtr = :useAtr";
	
    @Override
    public List<FixedExtractionSDailyItems> getAll() {
        List<KscdtScheFixItemDay> entities = new ArrayList<>();

        return null;
    }
    
    @Override
	public List<FixedExtractionSDailyCon> getScheFixCondDay(String contractCode, String companyId) {
    	List<KscdtScheFixCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY, KscdtScheFixCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.getList();
        return entities.stream().map(item -> item.toDomain()).collect(Collectors.toList());
	}
    
    @Override
	public List<FixedExtractionSDailyCon> getScheFixCondDay(String contractCode, String companyId, String eralCheckIds) {
    	List<KscdtScheFixCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheFixCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.getList();
        return entities.stream().map(item -> item.toDomain()).collect(Collectors.toList());
	}
    
    @Override
	public List<FixedExtractionSDailyCon> getScheFixCondDay(String contractCode, String companyId, String eralCheckIds,
			boolean isUse) {
    	List<KscdtScheFixCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID + BY_USE_ATR, KscdtScheFixCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.setParameter("useAtr", isUse)
				.getList();
        return entities.stream().map(item -> item.toDomain()).collect(Collectors.toList());
	}

	@Override
	public void add(String contractCode, String companyId, FixedExtractionSDailyCon domain) {
		this.commandProxy().insert(fromDomain(contractCode, companyId, domain));
	}

	@Override
	public void update(String contractCode, String companyId, FixedExtractionSDailyCon domain) {
		KscdtScheFixCondDayPk pk = new KscdtScheFixCondDayPk(companyId, domain.getErrorAlarmWorkplaceId(), domain.getFixedCheckDayItems().value);
		Optional<KscdtScheFixCondDay> entityOpt = this.queryProxy().find(pk, KscdtScheFixCondDay.class);
		if(entityOpt.isPresent()) {
			KscdtScheFixCondDay entity = entityOpt.get();
			entity.useAtr = domain.isUseAtr();
			entity.condMsg = domain.getMessageDisp().get().v();
			this.commandProxy().update(entity);
		} else {
			this.add(contractCode, companyId, domain);
		}
		
	}
	
	private KscdtScheFixCondDay fromDomain(String contractCode, String companyId, FixedExtractionSDailyCon domain) {
		KscdtScheFixCondDayPk pk = new KscdtScheFixCondDayPk(companyId, domain.getErrorAlarmWorkplaceId(), domain.getFixedCheckDayItems().value);
		KscdtScheFixCondDay entity = new KscdtScheFixCondDay(pk, domain.isUseAtr(), domain.getMessageDisp().get().v());
		entity.setContractCd(contractCode);
		return entity;
	}
	
	@Override
	public void delete(String contractCode, String companyId, String erAlCheckIds) {
		List<KscdtScheFixCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheFixCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", erAlCheckIds)
				.getList();
		this.commandProxy().removeAll(entities);
	}
}
