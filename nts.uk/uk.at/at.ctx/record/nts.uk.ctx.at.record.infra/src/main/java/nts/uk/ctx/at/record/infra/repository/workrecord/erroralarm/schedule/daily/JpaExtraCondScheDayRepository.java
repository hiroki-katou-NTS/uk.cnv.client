package nts.uk.ctx.at.record.infra.repository.workrecord.erroralarm.schedule.daily;


import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareRange;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.CondContinuousTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.CondContinuousTimeZone;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.CondContinuousWrkType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.CondTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.DaiCheckItemType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.ExtraCondScheDayRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.ExtractionCondScheduleDay;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.FixedExtractionSDailyCon;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.RangeToCheck;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.ScheduleCheckCond;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRange;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareRangePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSingle;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlCompareSinglePK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixed;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem.KrcstErAlSingleFixedPK;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheAnyCondDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheAnyCondDayPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheConDayWt;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheConDayWtime;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheCondDayWtPk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheCondDayWtimePk;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixCondDay;
import nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.schedule.daily.KscdtScheFixCondDayPk;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class JpaExtraCondScheDayRepository extends JpaRepository implements ExtraCondScheDayRepository {
	private static final String SELECT_BASIC = "SELECT a FROM KscdtScheAnyCondDay a";
	private static final String BY_CONTRACT_COMPANY = " WHERE a.pk.cid = :companyId AND a.contractCd = :contractCode";
	private static final String BY_ERAL_CHECK_ID = " AND a.pk.checkId = :eralCheckIds";
	private static final String BY_USE_ATTR = " AND a.useAtr = :useAtr";
	private static final String ORDER_BY_NO = " ORDER BY a.pk.sortBy";
	private static final String SELECT_WT = "SELECT a FROM KscdtScheConDayWt a WHERE a.pk.cid = :companyId AND a.pk.checkId = :checkId";
	private static final String BY_WT_NO = " AND a.pk.No = :no";
	private static final String SELECT_WTime = "SELECT a FROM KscdtScheConDayWtime a WHERE a.pk.cid = :companyId AND a.pk.checkId = :checkId";
	private static final String BY_WTime_NO = " AND a.pk.No = :no";
	private static final String SELECT_COMPARE_RANGE = "SELECT a FROM KrcstErAlCompareRange a WHERE a.krcstEralCompareRangePK.conditionGroupId = :checkId";
	private static final String SELECT_COMPARE_RANGE_SINGLE = "SELECT a FROM KrcstErAlCompareSingle a WHERE a.krcstEralCompareSinglePK.conditionGroupId = :checkId";
	private static final String SELECT_COMPARE_RANGE_SINGLE_FIXED = "SELECT a FROM KrcstErAlSingleFixed a WHERE a.krcstEralSingleFixedPK.conditionGroupId = :checkId";
	private static final String BY_COMPARE_RANGE_NO = " AND a.krcstEralCompareRangePK.atdItemConNo = :atdItemConNo ";
	private static final String BY_COMPARE_RANGE_SINGLE_NO = " AND a.krcstEralCompareSinglePK.atdItemConNo = :atdItemConNo ";
	private static final String BY_COMPARE_RANGE_SINGLE_FIXED_NO = " AND a.krcstEralSingleFixedPK.atdItemConNo = :atdItemConNo ";
	
    @Override
    public List<ExtractionCondScheduleDay> getAll(String cid) {
        List<KscdtScheAnyCondDay> entities = new ArrayList<>();

        return null;
    }
    
    @Override
	public List<ExtractionCondScheduleDay> getScheAnyCondDay(String contractCode, String companyId) {
    	List<KscdtScheAnyCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY, KscdtScheAnyCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.getList();
//        return entities.stream().map(item -> item.toDomain()).collect(Collectors.toList());
    	return new ArrayList<>();
	}
    
    @Override
	public List<ExtractionCondScheduleDay> getScheAnyCondDay(String contractCode, String companyId, String eralCheckIds) {
    	List<KscdtScheAnyCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID + ORDER_BY_NO, KscdtScheAnyCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.getList();
        List<ExtractionCondScheduleDay> domain = new ArrayList<>();
    	for(KscdtScheAnyCondDay item: entities) {
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
	public List<ExtractionCondScheduleDay> getScheAnyCondDay(String contractCode, String companyId, String eralCheckIds,
			boolean isUse) {
    	List<KscdtScheAnyCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID + BY_USE_ATTR + ORDER_BY_NO, KscdtScheAnyCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", eralCheckIds)
				.setParameter("useAtr", isUse)
				.getList();
        List<ExtractionCondScheduleDay> domain = new ArrayList<>();
    	for(KscdtScheAnyCondDay item: entities) {
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
	public void add(String contractCode, String companyId, ExtractionCondScheduleDay domain) {
		KscdtScheAnyCondDay entity = fromDomain(contractCode, companyId, domain);
		
		// 勤務種類 - pattern D
		if (DaiCheckItemType.TIME == domain.getCheckItemType()) {
			// pattern D
			CondTime time = (CondTime)domain.getScheduleCheckCond();
			if (time != null) {
				// work type
				updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), time.getWrkTypeCds());
			
				if (time.getCheckedCondition() instanceof CompareRange) {
					CompareRange compareRange = (CompareRange)time.getCheckedCondition();
					KrcstErAlCompareRange entityCompareRange = new KrcstErAlCompareRange(
							new KrcstErAlCompareRangePK(domain.getErrorAlarmId(), domain.getSortOrder()),
							compareRange.getCompareOperator().value,
							((Double)compareRange.getStartValue()).doubleValue(),
							((Double)compareRange.getEndValue()).doubleValue());
					this.commandProxy().insert(entityCompareRange);
				} else {
					CompareSingleValue compareSingleRange = (CompareSingleValue)time.getCheckedCondition();
					KrcstErAlCompareSingle entityCompareRange = new KrcstErAlCompareSingle(
							new KrcstErAlCompareSinglePK(domain.getErrorAlarmId(), domain.getSortOrder()),
							compareSingleRange.getCompareOpertor().value,
							compareSingleRange.getConditionType().value);
					this.commandProxy().insert(entityCompareRange);
					
					KrcstErAlSingleFixed erAlSingleFixed = new KrcstErAlSingleFixed(
							new KrcstErAlSingleFixedPK(domain.getErrorAlarmId(), domain.getSortOrder()),
							((Double)compareSingleRange.getValue()).doubleValue());
					this.commandProxy().insert(erAlSingleFixed);
				}
			}
		}
		
		// pattern G
		if (DaiCheckItemType.CONTINUOUS_TIME == domain.getCheckItemType()) {
			CondContinuousTime continuousTime = (CondContinuousTime)domain.getScheduleCheckCond();
			if (continuousTime != null) {
				entity.conPeriod = continuousTime.getPeriod().v();
				
				// work type
				updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTime.getWrkTypeCds());
				
				if (continuousTime.getCheckedCondition() instanceof CompareRange) {
					CompareRange compareRange = (CompareRange)continuousTime.getCheckedCondition();
					KrcstErAlCompareRange entityCompareRange = new KrcstErAlCompareRange(
							new KrcstErAlCompareRangePK(domain.getErrorAlarmId(), domain.getSortOrder()),
							compareRange.getCompareOperator().value,
							((Double)compareRange.getStartValue()).doubleValue(),
							((Double)compareRange.getEndValue()).doubleValue());
					this.commandProxy().insert(entityCompareRange);
				} else {
					CompareSingleValue compareSingleRange = (CompareSingleValue)continuousTime.getCheckedCondition();
					KrcstErAlCompareSingle entityCompareRange = new KrcstErAlCompareSingle(
							new KrcstErAlCompareSinglePK(domain.getErrorAlarmId(), domain.getSortOrder()),
							compareSingleRange.getCompareOpertor().value,
							compareSingleRange.getConditionType().value);
					this.commandProxy().insert(entityCompareRange);
					
					KrcstErAlSingleFixed erAlSingleFixed = new KrcstErAlSingleFixed(
							new KrcstErAlSingleFixedPK(domain.getErrorAlarmId(), domain.getSortOrder()),
							((Double)compareSingleRange.getValue()).doubleValue());
					this.commandProxy().insert(erAlSingleFixed);
				}
			}
		}
		
		// 連続時間帯の場合
		if (DaiCheckItemType.CONTINUOUS_TIMEZONE == domain.getCheckItemType()) {
			CondContinuousTimeZone continuousTimeZone = (CondContinuousTimeZone)domain.getScheduleCheckCond();
			
			entity.conPeriod = continuousTimeZone.getPeriod().v();
			
			// work type
			if (continuousTimeZone.getWrkTypeCds() != null && !continuousTimeZone.getWrkTypeCds().isEmpty()) {
				updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTimeZone.getWrkTypeCds());
			}
			
			// work time
			if (continuousTimeZone.getWrkTimeCds() != null && !continuousTimeZone.getWrkTimeCds().isEmpty()) {
				updateWorkTime(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTimeZone.getWrkTimeCds());
			}
		}
		
		// 連続勤務の場合
		if (DaiCheckItemType.CONTINUOUS_WORK == domain.getCheckItemType()) {
			CondContinuousWrkType continuousWorkType = (CondContinuousWrkType)domain.getScheduleCheckCond();
			
			entity.conPeriod = continuousWorkType.getPeriod().v();
			
			// work type
			updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousWorkType.getWrkTypeCds());
		}
		
				
		this.commandProxy().insert(entity);
	}

	@Override
	public void update(String contractCode, String companyId, ExtractionCondScheduleDay domain) {
		KscdtScheAnyCondDayPk pk = new KscdtScheAnyCondDayPk(companyId, domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KscdtScheAnyCondDay> entityOpt = this.queryProxy().find(pk, KscdtScheAnyCondDay.class);
		KscdtScheAnyCondDay entity = entityOpt.get();
		
		if (entity.checkType != domain.getCheckItemType().value) {
			removeCheckCondition(contractCode, companyId, entity.pk.checkId, entity.pk.sortBy);
		}
		
		entity.useAtr = domain.isUse();
		entity.condName = domain.getName().v();
		entity.message = domain.getErrorAlarmMessage() != null && domain.getErrorAlarmMessage().isPresent() ? domain.getErrorAlarmMessage().get().v() : null;
		entity.checkType = domain.getCheckItemType().value;
		entity.wrkTypeCondAtr = domain.getTargetWrkType().value;
		entity.wrkTimeCondAtr = domain.getTimeZoneTargetRange() != null ? domain.getTimeZoneTargetRange().value : null;
					
		// 勤務種類 - pattern D
		if (DaiCheckItemType.TIME == domain.getCheckItemType()) {
			updateByCheckItemTypeTime(contractCode, companyId, domain, entity);
		}
		
		// pattern G
		if (DaiCheckItemType.CONTINUOUS_TIME == domain.getCheckItemType()) {
			updateByCheckItemTypeContinuosTime(contractCode, companyId, domain, entity);
		}
		
		// 連続時間帯の場合
		if (DaiCheckItemType.CONTINUOUS_TIMEZONE == domain.getCheckItemType()) {
			CondContinuousTimeZone continuousTimeZone = (CondContinuousTimeZone)domain.getScheduleCheckCond();
			
			entity.conPeriod = continuousTimeZone.getPeriod().v();
			
			// work type
			if (continuousTimeZone.getWrkTypeCds() != null && !continuousTimeZone.getWrkTypeCds().isEmpty()) {
				updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTimeZone.getWrkTypeCds());
			}
			
			// work time
			if (continuousTimeZone.getWrkTimeCds() != null && !continuousTimeZone.getWrkTimeCds().isEmpty()) {
				updateWorkTime(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTimeZone.getWrkTimeCds());
			}
		}
		
		// 連続勤務の場合
		if (DaiCheckItemType.CONTINUOUS_WORK == domain.getCheckItemType()) {
			CondContinuousWrkType continuousWorkType = (CondContinuousWrkType)domain.getScheduleCheckCond();
			
			entity.conPeriod = continuousWorkType.getPeriod().v();
			
			// work type
			updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousWorkType.getWrkTypeCds());
		}
		
		this.commandProxy().update(entity);
	}
	
	/**
	 * The update for DaiCheckItemType=Time
	 */
	private void updateByCheckItemTypeTime(String contractCode, String companyId, ExtractionCondScheduleDay domain, KscdtScheAnyCondDay entity) {
		// pattern D
		CondTime time = (CondTime)domain.getScheduleCheckCond();
		if (time == null) {
			removeCheckCondition(contractCode, companyId, domain.getErrorAlarmId(), domain.getSortOrder());
			return;
		}
		
		// 
		updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), time.getWrkTypeCds());
		
		// 
		KrcstErAlCompareRangePK compareRangePK = new KrcstErAlCompareRangePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareRange> entityCompareRangeOpt = this.queryProxy().find(compareRangePK, KrcstErAlCompareRange.class);
		
		KrcstErAlCompareSinglePK compareSinglePK = new KrcstErAlCompareSinglePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareSingle> entityCompareRangeSingleOpt = this.queryProxy().find(compareSinglePK, KrcstErAlCompareSingle.class);
		KrcstErAlSingleFixedPK singleFixedPK = new KrcstErAlSingleFixedPK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlSingleFixed> erAlSingleFixedOpt = this.queryProxy().find(singleFixedPK, KrcstErAlSingleFixed.class);
		
		if (time.getCheckedCondition() instanceof CompareRange) {
			CompareRange compareRange = (CompareRange)time.getCheckedCondition();
			
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
			CompareSingleValue compareSingleRange = (CompareSingleValue)time.getCheckedCondition();
			
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
	 * The update for DaiCheckItemType=Time
	 */
	private void updateByCheckItemTypeContinuosTime(String contractCode, String companyId, ExtractionCondScheduleDay domain, KscdtScheAnyCondDay entity) {
		CondContinuousTime continuousTime = (CondContinuousTime)domain.getScheduleCheckCond();
		if (continuousTime == null) {
			removeCheckCondition(contractCode, companyId, domain.getErrorAlarmId(), domain.getSortOrder());
			return;
		}
		
		//
		entity.conPeriod = continuousTime.getPeriod().v();
		// 
		updateWorkType(companyId, domain.getErrorAlarmId(), domain.getSortOrder(), continuousTime.getWrkTypeCds());
		
		// 
		KrcstErAlCompareRangePK compareRangePK = new KrcstErAlCompareRangePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareRange> entityCompareRangeOpt = this.queryProxy().find(compareRangePK, KrcstErAlCompareRange.class);
		
		KrcstErAlCompareSinglePK compareSinglePK = new KrcstErAlCompareSinglePK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlCompareSingle> entityCompareRangeSingleOpt = this.queryProxy().find(compareSinglePK, KrcstErAlCompareSingle.class);
		KrcstErAlSingleFixedPK singleFixedPK = new KrcstErAlSingleFixedPK(domain.getErrorAlarmId(), domain.getSortOrder());
		Optional<KrcstErAlSingleFixed> erAlSingleFixedOpt = this.queryProxy().find(singleFixedPK, KrcstErAlSingleFixed.class);
		
		if (continuousTime.getCheckedCondition() instanceof CompareRange) {
			CompareRange compareRange = (CompareRange)continuousTime.getCheckedCondition();
			
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
			CompareSingleValue compareSingleRange = (CompareSingleValue)continuousTime.getCheckedCondition();
			
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
	
	private KscdtScheAnyCondDay fromDomain(String contractCode, String companyId, ExtractionCondScheduleDay domain) {
		KscdtScheAnyCondDayPk pk = new KscdtScheAnyCondDayPk(companyId, domain.getErrorAlarmId(), domain.getSortOrder());
		KscdtScheAnyCondDay entity = new KscdtScheAnyCondDay(
				pk, domain.getName().v(), domain.isUse(), 
				domain.getCheckItemType().value, 
				domain.getTargetWrkType().value, 0, 0, 
				domain.getTimeZoneTargetRange() != null ? domain.getTimeZoneTargetRange().value : null, "");
		entity.setContractCd(contractCode);
		if (domain.getErrorAlarmMessage() != null) {
			entity.message = domain.getErrorAlarmMessage().get().v();
		}
		
		return entity;
	}
	
	private void updateWorkTime(String companyId, String checkId, int no, List<String> workTimeCodes) {
		List<KscdtScheConDayWtime> workTimes = this.queryProxy().query(SELECT_WTime + BY_WTime_NO, KscdtScheConDayWtime.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.setParameter("no", no)
				.getList();
		
		for (String code: workTimeCodes) {
			if (StringUtils.isEmpty(code)) {
				continue;
			}
			KscdtScheCondDayWtimePk key = new KscdtScheCondDayWtimePk(companyId, checkId, no, code);
			if (!workTimes.stream().anyMatch(x -> x.pk.wrkTimeCd.equals(code))) {
				this.commandProxy().insert( new KscdtScheConDayWtime(key));
			}
		}
		
		for (KscdtScheConDayWtime item: workTimes) {
			if (!workTimeCodes.stream().anyMatch(x -> x.equals(item.pk.wrkTimeCd))) {
				this.commandProxy().remove(item);
				this.getEntityManager().flush();
			}
		}
	}
	
	private void updateWorkType(String companyId, String checkId, int no, List<String> workTypeCodes) {
		List<KscdtScheConDayWt> workTimes = this.queryProxy().query(SELECT_WT + BY_WT_NO, KscdtScheConDayWt.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.setParameter("no", no)
				.getList();
		
		for (String code: workTypeCodes) {
			if (StringUtils.isEmpty(code)) {
				continue;
			}
			KscdtScheCondDayWtPk key = new KscdtScheCondDayWtPk(companyId, checkId, no, code);
			if (!workTimes.stream().anyMatch(x -> x.pk.wrkTypeCd.equals(code))) {
				this.commandProxy().insert( new KscdtScheConDayWt(key));
			}
		}
		
		for (KscdtScheConDayWt item: workTimes) {
			if (!workTypeCodes.stream().anyMatch(x -> x.equals(item.pk.wrkTypeCd))) {
				this.commandProxy().remove(item);
				this.getEntityManager().flush();
			}
		}
	}

	@Override
	public void delete(String contractCode, String companyId, String erAlCheckIds) {
		List<KrcstErAlCompareRange> ranges = this.queryProxy().query(SELECT_COMPARE_RANGE, KrcstErAlCompareRange.class)
				.setParameter("checkId", erAlCheckIds)
				.getList();
		if (!ranges.isEmpty()) {
			this.commandProxy().removeAll(ranges);
		}
		
		List<KrcstErAlCompareSingle> singleRanges = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE, KrcstErAlCompareSingle.class)
				.setParameter("checkId", erAlCheckIds)
				.getList();
		if (!singleRanges.isEmpty()) {
			this.commandProxy().removeAll(singleRanges);
		}
		
		List<KrcstErAlSingleFixed> singleRangeFixeds = this.queryProxy().query(SELECT_COMPARE_RANGE_SINGLE_FIXED, KrcstErAlSingleFixed.class)
				.setParameter("checkId", erAlCheckIds)
				.getList();
		if (!singleRangeFixeds.isEmpty()) {
			this.commandProxy().removeAll(singleRangeFixeds);
		}
		
		removeWorkTypes(companyId, erAlCheckIds);
		removeWorkTimes(companyId, erAlCheckIds);
		
		List<KscdtScheAnyCondDay> entities = this.queryProxy().query(SELECT_BASIC + BY_CONTRACT_COMPANY + BY_ERAL_CHECK_ID, KscdtScheAnyCondDay.class)
				.setParameter("contractCode", contractCode)
				.setParameter("companyId", companyId)
				.setParameter("eralCheckIds", erAlCheckIds)
				.getList();
		if (!entities.isEmpty()) {
			this.commandProxy().removeAll(entities);
		}
	}

	@Override
	public void delete(String contractCode, String companyId, String erAlCheckIds, int alarmNo) {
		// Remove check condition
		removeCheckCondition(contractCode, companyId, erAlCheckIds, alarmNo);
		
		KscdtScheAnyCondDayPk pk = new KscdtScheAnyCondDayPk(companyId, erAlCheckIds, alarmNo);
		Optional<KscdtScheAnyCondDay> entityOpt = this.queryProxy().find(pk, KscdtScheAnyCondDay.class);
		if (!entityOpt.isPresent()) {
			return;
		}
		this.commandProxy().remove(entityOpt.get());
	}
	
	/**
	 * Insert or update entity
	 * @param entity
	 * @param isUpdate
	 */
	private void saveOrUpdate(Object entity, boolean isUpdate) {
		if (isUpdate) {
			this.commandProxy().update(entity);
		} else {
			this.commandProxy().insert(entity);
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
		
		removeWorkTypes(companyId, erAlCheckIds, alarmNo);
		removeWorkTimes(companyId, erAlCheckIds, alarmNo);
	}
	
	/**
	 * Remove all work type by no
	 * @param companyId
	 * @param checkId
	 * @param no
	 */
	private void removeWorkTypes(String companyId, String checkId, int no) {
		List<KscdtScheConDayWt> workTypes = this.queryProxy().query(SELECT_WT + BY_WT_NO, KscdtScheConDayWt.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.setParameter("no", no)
				.getList();
		if (workTypes.isEmpty()) {
			return;
		}
		this.commandProxy().removeAll(workTypes);
	}
	
	/**
	 * Remove all work type
	 * @param companyId
	 * @param checkId
	 * @param no
	 */
	private void removeWorkTypes(String companyId, String checkId) {
		List<KscdtScheConDayWt> workTypes = this.queryProxy().query(SELECT_WT, KscdtScheConDayWt.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.getList();
		if (workTypes.isEmpty()) {
			return;
		}
		this.commandProxy().removeAll(workTypes);
	}
	
	/**
	 * Remove all work time by no
	 * @param companyId
	 * @param checkId
	 */
	private void removeWorkTimes(String companyId, String checkId, int no) {
		List<KscdtScheConDayWtime> workTimes = this.queryProxy().query(SELECT_WTime + BY_WTime_NO, KscdtScheConDayWtime.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.setParameter("no", no)
				.getList();
		if (workTimes.isEmpty()) {
			return;
		}
		this.commandProxy().removeAll(workTimes);
	}
	
	/**
	 * Remove all work time
	 * @param companyId
	 * @param checkId
	 */
	private void removeWorkTimes(String companyId, String checkId) {
		List<KscdtScheConDayWtime> workTimes = this.queryProxy().query(SELECT_WTime, KscdtScheConDayWtime.class)
				.setParameter("companyId", companyId)
				.setParameter("checkId", checkId)
				.getList();
		if (workTimes.isEmpty()) {
			return;
		}
		this.commandProxy().removeAll(workTimes);
	}
}
