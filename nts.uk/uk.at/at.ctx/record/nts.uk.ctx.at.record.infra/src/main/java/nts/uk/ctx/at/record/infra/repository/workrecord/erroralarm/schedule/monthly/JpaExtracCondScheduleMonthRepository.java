package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.schedule.monthly;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareRange;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.TimeCheckCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.DayCheckCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.ExtractionCondScheduleMonth;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.ExtractionCondScheduleMonthRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.MonCheckItemType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.PublicHolidayCheckCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.ScheduleMonRemainCheckCond;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRangePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSingle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSinglePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixed;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixedPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.monthly.KscdtScheAnyCondMonth;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.monthly.KscdtScheAnyCondMonthPk;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class JpaExtracCondScheduleMonthRepository  extends JpaRepository implements ExtractionCondScheduleMonthRepository {
	private static final String SELECT_BASIC = "SELECT a FROM KscdtScheAnyCondMonth a";
	private static final String BY_CONTRACT_COMPANY = " WHERE a.pk.cid = :companyId AND a.contractCd = :contractCode";
	private static final String BY_ERAL_CHECK_ID = " AND a.pk.cid = :companyId AND a.contractCd = :contractCode AND a.pk.checkId = :eralCheckIds";
	private static final String ORDER_BY_NO = " ORDER BY a.pk.sortBy";
	private static final String SELECT_COMPARE_RANGE = "SELECT a FROM KrcstErAlCompareRange a WHERE a.krcstEralCompareRangePK.conditionGroupId = :checkId";
	private static final String SELECT_COMPARE_RANGE_SINGLE = "SELECT a FROM KrcstErAlCompareSingle a WHERE a.krcstEralCompareSinglePK.conditionGroupId = :checkId";
	private static final String SELECT_COMPARE_RANGE_SINGLE_FIXED = "SELECT a FROM KrcstErAlSingleFixed a WHERE a.krcstEralSingleFixedPK.conditionGroupId = :checkId";
	private static final String BY_COMPARE_RANGE_NO = " AND a.krcstEralCompareRangePK.atdItemConNo = :atdItemConNo ";
	private static final String BY_COMPARE_RANGE_SINGLE_NO = " AND a.krcstEralCompareSinglePK.atdItemConNo = :atdItemConNo ";
	private static final String BY_COMPARE_RANGE_SINGLE_FIXED_NO = " AND a.krcstEralSingleFixedPK.atdItemConNo = :atdItemConNo ";
	
    @Override
    public List<ExtractionCondScheduleMonth> getAll() {
        List<KscdtScheAnyCondMonth> entities = new ArrayList<>();
        return null;
    }
    
    @Override
	public List<ExtractionCondScheduleMonth> getScheAnyCond(String contractCode, String companyId,
			String eralCheckIds) {
		List<KscdtScheAnyCondMonth> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID + ORDER_BY_NO, KscdtScheAnyCondMonth.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.getList();
        List<ExtractionCondScheduleMonth> domain = new ArrayList<>();
    	for(KscdtScheAnyCondMonth item: entities) {
    		Optional<KrcstErAlCompareSingle> single = this.queryProxy().find(new KrcstErAlCompareSinglePK(eralCheckIds, item.pk.sortBy), KrcstErAlCompareSingle.class);
    		Optional<KrcstErAlSingleFixed> singleFixed = this.queryProxy().find(new KrcstErAlSingleFixedPK(eralCheckIds, item.pk.sortBy), KrcstErAlSingleFixed.class);
    		Optional<KrcstErAlCompareRange> range = this.queryProxy().find(new KrcstErAlCompareRangePK(eralCheckIds, item.pk.sortBy), KrcstErAlCompareRange.class);
    		domain.add(item.toDomain(
    				single.isPresent() ? single.get() : null, 
					singleFixed.isPresent() ? singleFixed.get() : null, 
    				range.isPresent() ? range.get() : null));
        }
    	
    	return domain;
	}
    
	@Override
	public void add(String contractCode, String companyId, ExtractionCondScheduleMonth domain) {
		KscdtScheAnyCondMonth entity = fromDomain(contractCode, companyId, domain);
		
		updateByCheckCondition(contractCode, companyId, domain, entity);
		
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(String contractCode, String companyId, ExtractionCondScheduleMonth domain) {
		KscdtScheAnyCondMonthPk pk = new KscdtScheAnyCondMonthPk(companyId, domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KscdtScheAnyCondMonth> entityOpt = this.queryProxy().find(pk, KscdtScheAnyCondMonth.class);
		
		KscdtScheAnyCondMonth entity = entityOpt.get();
		
		// remove all condition if change check item type
		if (entity.condType != domain.getCheckItemType().value) {
			removeCheckCondition(contractCode, companyId, entity.pk.checkId, entity.pk.sortBy);
		}
		
		entity.condName = domain.getName().v();
		entity.condMsg = domain.getErrorAlarmMessage() != null ? domain.getErrorAlarmMessage().get().v() : null;
		entity.useAtr = BooleanUtils.toInteger(domain.isUse());
		entity.condType = domain.getCheckItemType().value;
		
		updateByCheckCondition(contractCode, companyId, domain, entity);
		
		this.commandProxy().update(entity);
	}
	
	@Override
	public void delete(String contractCode, String companyId, String erAlCheckIds, int alarmNo) {
		removeCheckCondition(contractCode, companyId, erAlCheckIds, alarmNo);
		
		KscdtScheAnyCondMonthPk pk = new KscdtScheAnyCondMonthPk(companyId, erAlCheckIds, alarmNo);
		Optional<KscdtScheAnyCondMonth> entityOpt = this.queryProxy().find(pk, KscdtScheAnyCondMonth.class);
		if (!entityOpt.isPresent()) {
			return;
		}
		this.commandProxy().remove(entityOpt.get());
	}

	@Override
	public void delete(String contractCode, String companyId, String checkId) {
		List<KrcstErAlCompareRange> ranges = this.queryProxy().query(SELECT_COMPARE_RANGE, KrcstErAlCompareRange.class)
				.setParameter("checkId", checkId)
				.getList();
		if (!ranges.isEmpty()) {
			this.commandProxy().removeAll(ranges);
		}
		
		List<KrcstErAlCompareSingle> singleRanges = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE, KrcstErAlCompareSingle.class)
				.setParameter("checkId", checkId)
				.getList();
		if (!singleRanges.isEmpty()) {
			this.commandProxy().removeAll(singleRanges);
		}
		
		List<KrcstErAlSingleFixed> singleRangeFixeds = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE_FIXED, KrcstErAlSingleFixed.class)
				.setParameter("checkId", checkId)
				.getList();
		if (!singleRangeFixeds.isEmpty()) {
			this.commandProxy().removeAll(singleRangeFixeds);
		}
		
		List<KscdtScheAnyCondMonth> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheAnyCondMonth.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", checkId)
				.getList();
		if (!entities.isEmpty()) {
			this.commandProxy().removeAll(entities);
		}
	}
	
	private KscdtScheAnyCondMonth fromDomain(String contractCode, String companyId, ExtractionCondScheduleMonth domain) {
		KscdtScheAnyCondMonthPk pk = new KscdtScheAnyCondMonthPk(companyId, domain.getErrorAlarmId(), domain.getSortOrder());
		KscdtScheAnyCondMonth entity = new KscdtScheAnyCondMonth(
				pk, BooleanUtils.toInteger(domain.isUse()), domain.getName().v(), 
				domain.getCheckItemType().value,
				0,
				domain.getErrorAlarmMessage() != null ? domain.getErrorAlarmMessage().get().v() : "", 
				"");
		entity.setContractCd(contractCode);
		
		return entity;
	}
	
	private void updateByCheckCondition(String contractCode, String companyId, ExtractionCondScheduleMonth domain, KscdtScheAnyCondMonth entity) {
		MonCheckItemType checkItemType = domain.getCheckItemType();
		switch(checkItemType) {
			case CONTRAST:
				PublicHolidayCheckCond publicHolidayCheckCond = (PublicHolidayCheckCond)domain.getScheCheckConditions();
				entity.checkType = publicHolidayCheckCond.getTypeOfContrast().value;
				break;
			case TIME:
				TimeCheckCond timeCheckCond = (TimeCheckCond)domain.getScheCheckConditions();
				entity.checkType = timeCheckCond.getTypeOfTime().value;
				break;
			case NUMBER_DAYS:
				DayCheckCond dayCheckCond = (DayCheckCond)domain.getScheCheckConditions();
				entity.checkType = dayCheckCond.getTypeOfDays().value;
				break;
			case REMAIN_NUMBER:
				ScheduleMonRemainCheckCond scheduleMonRemainCheckCond = (ScheduleMonRemainCheckCond)domain.getScheCheckConditions();
				entity.checkType = scheduleMonRemainCheckCond.getTypeOfVacations().value;
				if (scheduleMonRemainCheckCond.getSpecialHolidayCode() != null && scheduleMonRemainCheckCond.getSpecialHolidayCode().isPresent()) {
					entity.speVacCd = scheduleMonRemainCheckCond.getSpecialHolidayCode().get().v().toString();
				}
				break;
			default:
				break;
		}
		
		updateErAlCompare(contractCode, companyId, domain);
	}
	
	/**
	 * The update for MonCheckItemType=Contrast
	 */
	private void updateErAlCompare(String contractCode, String companyId, ExtractionCondScheduleMonth domain) {		
		// 
		if (domain.getCheckConditions() == null) {
			removeCheckCondition(contractCode, companyId, domain.getErrorAlarmId(), domain.getSortOrder());
			return;
		}
		
		KrcstErAlCompareRangePK compareRangePK = new KrcstErAlCompareRangePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareRange> entityCompareRangeOpt = this.queryProxy().find(compareRangePK, KrcstErAlCompareRange.class);
		
		KrcstErAlCompareSinglePK compareSinglePK = new KrcstErAlCompareSinglePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareSingle> entityCompareRangeSingleOpt = this.queryProxy().find(compareSinglePK, KrcstErAlCompareSingle.class);
		KrcstErAlSingleFixedPK singleFixedPK = new KrcstErAlSingleFixedPK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlSingleFixed> erAlSingleFixedOpt = this.queryProxy().find(singleFixedPK, KrcstErAlSingleFixed.class);
		
		if (domain.getCheckConditions() instanceof CompareRange) {
			CompareRange compareRange = (CompareRange)domain.getCheckConditions();
			if (compareRange.getStartValue() == null && compareRange.getEndValue() == null) {
				return;
			}
			
			KrcstErAlCompareRange entityCompareRange = new KrcstErAlCompareRange(compareRangePK);
			if (entityCompareRangeOpt.isPresent()) {
				entityCompareRange = entityCompareRangeOpt.get();
			}
			
			entityCompareRange.compareAtr = compareRange.getCompareOperator().value;
			entityCompareRange.startValue = ((Double)compareRange.getStartValue()).doubleValue();
			entityCompareRange.endValue = ((Double)compareRange.getEndValue()).doubleValue();
			saveOrUpdate(entityCompareRange, entityCompareRangeOpt.isPresent());
			
			// remove compare range single if exist
			if (entityCompareRangeSingleOpt.isPresent()) {
				this.commandProxy().remove(entityCompareRangeSingleOpt.get());
			}
			if (erAlSingleFixedOpt.isPresent()) {
				this.commandProxy().remove(erAlSingleFixedOpt.get());
			}
		} else {
			CompareSingleValue compareSingleRange = (CompareSingleValue)domain.getCheckConditions();
			if (compareSingleRange.getValue() == null) {
				return;
			}
			
			KrcstErAlCompareSingle entityCompareRangeSingle = new KrcstErAlCompareSingle(compareSinglePK);
			if (entityCompareRangeSingleOpt.isPresent()) {
				entityCompareRangeSingle = entityCompareRangeSingleOpt.get();
			}
			
			entityCompareRangeSingle.compareAtr = compareSingleRange.getCompareOpertor().value;
			saveOrUpdate(entityCompareRangeSingle, entityCompareRangeSingleOpt.isPresent());
			
			KrcstErAlSingleFixed erAlSingleFixed = new KrcstErAlSingleFixed(singleFixedPK);
			if (erAlSingleFixedOpt.isPresent()) {
				erAlSingleFixed = erAlSingleFixedOpt.get();
			}
			
			erAlSingleFixed.fixedValue = ((Double)compareSingleRange.getValue()).doubleValue();
			saveOrUpdate(erAlSingleFixed, erAlSingleFixedOpt.isPresent());
			
			// remove compare range if exist
			if (entityCompareRangeOpt.isPresent()) {
				this.commandProxy().remove(entityCompareRangeOpt.get());
			}
		}
	}
	
	/**
	 * Remove check condition when change check type item or remove item
	 * @param contractCode
	 * @param companyId
	 * @param erAlCheckIds
	 * @param alarmNo
	 */
	private void removeCheckCondition(String contractCode, String companyId, String erAlCheckIds, int alarmNo) {
		List<KrcstErAlCompareRange> ranges = this.queryProxy().query(SELECT_COMPARE_RANGE + BY_COMPARE_RANGE_NO, KrcstErAlCompareRange.class)
				.setParameter("checkId", erAlCheckIds)
				.setParameter("atdItemConNo", alarmNo)
				.getList();
		if (!ranges.isEmpty()) {
			this.commandProxy().removeAll(ranges);
			this.getEntityManager().flush();
		}
		
		List<KrcstErAlCompareSingle> singleRanges = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE + BY_COMPARE_RANGE_SINGLE_NO, KrcstErAlCompareSingle.class)
				.setParameter("checkId", erAlCheckIds)
				.setParameter("atdItemConNo", alarmNo)
				.getList();
		if (!singleRanges.isEmpty()) {
			this.commandProxy().removeAll(singleRanges);
			this.getEntityManager().flush();
		}
		
		List<KrcstErAlSingleFixed> singleRangeFixeds = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE_FIXED + BY_COMPARE_RANGE_SINGLE_FIXED_NO, KrcstErAlSingleFixed.class)
				.setParameter("checkId", erAlCheckIds)
				.setParameter("atdItemConNo", alarmNo)
				.getList();
		if (!singleRangeFixeds.isEmpty()) {
			this.commandProxy().removeAll(singleRangeFixeds);
			this.getEntityManager().flush();
		}
	}
	
	private void saveOrUpdate(Object entity, boolean isUpdate) {
		if (isUpdate) {
			this.commandProxy().update(entity);
		} else {
			this.commandProxy().insert(entity);
		}
	}
}
