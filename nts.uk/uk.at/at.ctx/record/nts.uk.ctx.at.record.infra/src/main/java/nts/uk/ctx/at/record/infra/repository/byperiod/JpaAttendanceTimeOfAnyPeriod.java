package nts.uk.ctx.at.record.infra.repository.byperiod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAggrHdwkTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAggrOverTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAnyItemValue;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAttendanceTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAttendanceTimePK;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpExcoutTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpTimeTotalcount;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpDaysAbsence;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpAggrSpecDays;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.workdays.KrcdtAnpAggrSpvcDays;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrBnspyTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrDivgTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpTimeGoout;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpAggrPremTime;
import nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime.KrcdtAnpMedicalTime;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriod;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriodKey;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateItemNo;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;

/**
 * ????????????????????????????????????????????????????????????
 * @author shuichi_ishida
 */
@Stateless
public class JpaAttendanceTimeOfAnyPeriod extends JpaRepository implements AttendanceTimeOfAnyPeriodRepository {

	private static final String WHERE_PK = "WHERE a.PK.employeeId = :employeeId "
			+ "AND a.PK.frameCode = :frameCode ";
	
	private static final String FIND_BY_EMPLOYEES = "SELECT a FROM KrcdtAnpAttendanceTime a "
			+ "WHERE a.PK.employeeId IN :employeeIds "
			+ "AND a.PK.frameCode = :frameCode ";
	
	private static final List<String> DELETE_TABLES = Arrays.asList(
			"DELETE FROM KrcdtAnpAttendanceTime a ",
			"DELETE FROM KrcdtAnpAggrOverTime a ",
			"DELETE FROM KrcdtAnpAggrHdwkTime a ",
			"DELETE FROM KrcdtAnpDaysAbsence a ",
			"DELETE FROM KrcdtAnpAggrSpecDays a ",
			"DELETE FROM KrcdtAnpAggrSpvcDays a ",
			"DELETE FROM KrcdtAnpAggrBnspyTime a ",
			"DELETE FROM KrcdtAnpAggrDivgTime a ",
			"DELETE FROM KrcdtAnpTimeGoout a ",
			"DELETE FROM KrcdtAnpAggrPremTime a ",
			"DELETE FROM KrcdtAnpMedicalTime a ",
			"DELETE FROM KrcdtAnpExcoutTime a ",
			"DELETE FROM KrcdtAnpTimeTotalcount a ",
			"DELETE FROM KrcdtAnpAnyItemValue a ");

	/** ?????? */
	@Override
	public Optional<AttendanceTimeOfAnyPeriod> find(String employeeId, String frameCode) {
		
		return this.queryProxy()
				.find(new KrcdtAnpAttendanceTimePK(employeeId, frameCode), KrcdtAnpAttendanceTime.class)
				.map(c -> c.toDomain());
	}

	/** ??????????????????ID???????????? */
	@Override
	public List<AttendanceTimeOfAnyPeriod> findBySids(List<String> employeeIds, String frameCode) {
		
		List<KrcdtAnpAttendanceTime> results = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			results.addAll(this.queryProxy().query(FIND_BY_EMPLOYEES, KrcdtAnpAttendanceTime.class)
					.setParameter("employeeIds", splitData)
					.setParameter("frameCode", frameCode)
					.getList());
		});
		return results.stream().map(c -> c.toDomain()).collect(Collectors.toList());
	}
	
	/** ????????????????????? */
	@Override
	public void persistAndUpdate(AttendanceTimeOfAnyPeriod domain){

		// ??????
		val key = new KrcdtAnpAttendanceTimePK(
				domain.getEmployeeId(),
				domain.getAnyAggrFrameCode().v());
		val domainKey = new AttendanceTimeOfAnyPeriodKey(
				domain.getEmployeeId(),
				domain.getAnyAggrFrameCode());

		// ????????????
		val monthlyCalculation = domain.getMonthlyAggregation();
		
		// ??????????????????????????????????????????????????????
		boolean isNeedPersist = false;
		KrcdtAnpAttendanceTime entity = this.getEntityManager().find(KrcdtAnpAttendanceTime.class, key);
		if (entity == null){
			isNeedPersist = true;
			entity = new KrcdtAnpAttendanceTime();
			entity.fromDomainForPersist(domain);
		}
		else entity.fromDomainForUpdate(domain);

		// ????????????????????????????????????????????????
		val aggregateTime = monthlyCalculation.getAggregateTime();
		val aggrOverTimeMap = aggregateTime.getOverTime().getAggregateOverTimeMap();
		if (entity.krcdtAnpAggrOverTimes == null) entity.krcdtAnpAggrOverTimes = new ArrayList<>();
		val entityAggrOverTimeList = entity.krcdtAnpAggrOverTimes;
		entityAggrOverTimeList.removeIf(
				a -> {return !aggrOverTimeMap.containsKey(new OverTimeFrameNo(a.PK.overTimeFrameNo));} );
		for (val aggrOverTime : aggrOverTimeMap.values()){
			KrcdtAnpAggrOverTime entityAggrOverTime = new KrcdtAnpAggrOverTime();
			val entityAggrOverTimeOpt = entityAggrOverTimeList.stream()
					.filter(c -> c.PK.overTimeFrameNo == aggrOverTime.getOverTimeFrameNo().v()).findFirst();
			if (entityAggrOverTimeOpt.isPresent()){
				entityAggrOverTime = entityAggrOverTimeOpt.get();
				entityAggrOverTime.fromDomainForUpdate(aggrOverTime);
			}
			else {
				entityAggrOverTime.fromDomainForPersist(domainKey, aggrOverTime);
				entityAggrOverTimeList.add(entityAggrOverTime);
			}
		}
		
		// ???????????????????????????????????????????????????
		val aggrHolidayWorkTimeMap = aggregateTime.getHolidayWorkTime().getAggregateHolidayWorkTimeMap();
		if (entity.krcdtAnpAggrHdwkTimes == null) entity.krcdtAnpAggrHdwkTimes = new ArrayList<>();
		val entityAggrHdwkTimeList = entity.krcdtAnpAggrHdwkTimes;
		entityAggrHdwkTimeList.removeIf(
				a -> {return !aggrHolidayWorkTimeMap.containsKey(new HolidayWorkFrameNo(a.PK.holidayWorkFrameNo));} );
		for (val aggrHolidayWorkTime : aggrHolidayWorkTimeMap.values()){
			KrcdtAnpAggrHdwkTime entityAggrHdwkTime = new KrcdtAnpAggrHdwkTime();
			val entityAggrHdwkTimeOpt = entityAggrHdwkTimeList.stream()
					.filter(c -> c.PK.holidayWorkFrameNo == aggrHolidayWorkTime.getHolidayWorkFrameNo().v()).findFirst();
			if (entityAggrHdwkTimeOpt.isPresent()){
				entityAggrHdwkTime = entityAggrHdwkTimeOpt.get();
				entityAggrHdwkTime.fromDomainForUpdate(aggrHolidayWorkTime);
			}
			else {
				entityAggrHdwkTime.fromDomainForPersist(domainKey, aggrHolidayWorkTime);
				entityAggrHdwkTimeList.add(entityAggrHdwkTime);
			}
		}
		
		// ?????????????????????????????????
		val excessOutsideTimeMap = domain.getExcessOutside().getExcessOutsideItems();
		if (entity.krcdtAnpExcoutTime == null) entity.krcdtAnpExcoutTime = new ArrayList<>();
		val entityExcoutTimeList = entity.krcdtAnpExcoutTime;
		entityExcoutTimeList.removeIf(
				a -> {return !excessOutsideTimeMap.containsKey(a.PK.breakdownNo);} );
		for (val breakdown : excessOutsideTimeMap.values()){
			KrcdtAnpExcoutTime entityExcoutTime = new KrcdtAnpExcoutTime();
			val entityExcoutTimeOpt = entityExcoutTimeList.stream()
					.filter(c -> c.PK.breakdownNo == breakdown.getBreakdownNo()).findFirst();
			if (entityExcoutTimeOpt.isPresent()){
				entityExcoutTime = entityExcoutTimeOpt.get();
				entityExcoutTime.fromDomainForUpdate(breakdown);
			}
			else {
				entityExcoutTime.fromDomainForPersist(domainKey, breakdown);
				entityExcoutTimeList.add(entityExcoutTime);
			}
		}
		
		// ??????????????????????????????????????????
		val vtWorkDays = domain.getVerticalTotal().getWorkDays();
		val absenceDaysMap = vtWorkDays.getAbsenceDays().getAbsenceDaysList();
		if (entity.krcdtAnpAggrAbsnDays == null) entity.krcdtAnpAggrAbsnDays = new ArrayList<>();
		val entityAggrAbsnDaysList = entity.krcdtAnpAggrAbsnDays;
		entityAggrAbsnDaysList.removeIf(a -> {return !absenceDaysMap.containsKey(a.PK.absenceFrameNo);} );
		for (val absenceDays : absenceDaysMap.values()){
			KrcdtAnpDaysAbsence entityAggrAbsnDays = new KrcdtAnpDaysAbsence();
			val entityAggrAbsnDaysOpt = entityAggrAbsnDaysList.stream()
					.filter(c -> c.PK.absenceFrameNo == absenceDays.getAbsenceFrameNo()).findFirst();
			if (entityAggrAbsnDaysOpt.isPresent()){
				entityAggrAbsnDays = entityAggrAbsnDaysOpt.get();
				entityAggrAbsnDays.fromDomainForUpdate(absenceDays);
			}
			else {
				entityAggrAbsnDays.fromDomainForPersist(domainKey, absenceDays);
				entityAggrAbsnDaysList.add(entityAggrAbsnDays);
			}
		}
		
		// ??????????????????????????????????????????
		val specificDaysMap = vtWorkDays.getSpecificDays().getSpecificDays();
		if (entity.krcdtAnpAggrSpecDays == null) entity.krcdtAnpAggrSpecDays = new ArrayList<>();
		val entityAggrSpecDaysList = entity.krcdtAnpAggrSpecDays;
		entityAggrSpecDaysList.removeIf(
				a -> {return !specificDaysMap.containsKey(new SpecificDateItemNo(a.PK.specificDayItemNo));} );
		for (val specificDays : specificDaysMap.values()){
			KrcdtAnpAggrSpecDays entityAggrSpecDays = new KrcdtAnpAggrSpecDays();
			val entityAggrSpecDaysOpt = entityAggrSpecDaysList.stream()
					.filter(c -> c.PK.specificDayItemNo == specificDays.getSpecificDayItemNo().v()).findFirst();
			if (entityAggrSpecDaysOpt.isPresent()){
				entityAggrSpecDays = entityAggrSpecDaysOpt.get();
				entityAggrSpecDays.fromDomainForUpdate(specificDays);
			}
			else {
				entityAggrSpecDays.fromDomainForPersist(domainKey, specificDays);
				entityAggrSpecDaysList.add(entityAggrSpecDays);
			}
		}
		
		// ????????????????????????????????????????????????
		val spcVactDaysMap = vtWorkDays.getSpecialVacationDays().getSpcVacationDaysList();
		if (entity.krcdtAnpAggrSpvcDays == null) entity.krcdtAnpAggrSpvcDays = new ArrayList<>();
		val entityAggrSpvcDaysList = entity.krcdtAnpAggrSpvcDays;
		entityAggrSpvcDaysList.removeIf(a -> {return !spcVactDaysMap.containsKey(a.PK.specialVacationFrameNo);} );
		for (val spcVactDays : spcVactDaysMap.values()){
			KrcdtAnpAggrSpvcDays entityAggrSpvcDays = new KrcdtAnpAggrSpvcDays();
			val entityAggrSpvcDaysOpt = entityAggrSpvcDaysList.stream()
					.filter(c -> c.PK.specialVacationFrameNo == spcVactDays.getSpcVacationFrameNo()).findFirst();
			if (entityAggrSpvcDaysOpt.isPresent()){
				entityAggrSpvcDays = entityAggrSpvcDaysOpt.get();
				entityAggrSpvcDays.fromDomainForUpdate(spcVactDays);
			}
			else {
				entityAggrSpvcDays.fromDomainForPersist(domainKey, spcVactDays);
				entityAggrSpvcDaysList.add(entityAggrSpvcDays);
			}
		}
		
		// ??????????????????????????????????????????
		val vtWorkTime = domain.getVerticalTotal().getWorkTime();
		val bonusPayTimeMap = vtWorkTime.getBonusPayTime().getBonusPayTime();
		if (entity.krcdtAnpAggrBnspyTime == null) entity.krcdtAnpAggrBnspyTime = new ArrayList<>();
		val entityAggrBnspyTimeList = entity.krcdtAnpAggrBnspyTime;
		entityAggrBnspyTimeList.removeIf(a -> {return !bonusPayTimeMap.containsKey(a.PK.bonusPayFrameNo);} );
		for (val bonusPayTime : bonusPayTimeMap.values()){
			KrcdtAnpAggrBnspyTime entityAggrBnspyTime = new KrcdtAnpAggrBnspyTime();
			val entityAggrBnspyTimeOpt = entityAggrBnspyTimeList.stream()
					.filter(c -> c.PK.bonusPayFrameNo == bonusPayTime.getBonusPayFrameNo()).findFirst();
			if (entityAggrBnspyTimeOpt.isPresent()){
				entityAggrBnspyTime = entityAggrBnspyTimeOpt.get();
				entityAggrBnspyTime.fromDomainForUpdate(bonusPayTime);
			}
			else {
				entityAggrBnspyTime.fromDomainForPersist(domainKey, bonusPayTime);
				entityAggrBnspyTimeList.add(entityAggrBnspyTime);
			}
		}
		
		// ??????????????????????????????????????????
		val divergenceTimeMap = vtWorkTime.getDivergenceTime().getDivergenceTimeList();
		if (entity.krcdtAnpAggrDivgTime == null) entity.krcdtAnpAggrDivgTime = new ArrayList<>();
		val entityAggrDivgTimeList = entity.krcdtAnpAggrDivgTime;
		entityAggrDivgTimeList.removeIf(a -> {return !divergenceTimeMap.containsKey(a.PK.divergenceTimeNo);} );
		for (val divergenceTime : divergenceTimeMap.values()){
			KrcdtAnpAggrDivgTime entityAggrDivgTime = new KrcdtAnpAggrDivgTime();
			val entityAggrDivgTimeOpt = entityAggrDivgTimeList.stream()
					.filter(c -> c.PK.divergenceTimeNo == divergenceTime.getDivergenceTimeNo()).findFirst();
			if (entityAggrDivgTimeOpt.isPresent()){
				entityAggrDivgTime = entityAggrDivgTimeOpt.get();
				entityAggrDivgTime.fromDomainForUpdate(divergenceTime);
			}
			else {
				entityAggrDivgTime.fromDomainForPersist(domainKey, divergenceTime);
				entityAggrDivgTimeList.add(entityAggrDivgTime);
			}
		}
		
		// ????????????????????????????????????
		val goOutMap = vtWorkTime.getGoOut().getGoOuts();
		if (entity.krcdtAnpAggrGoout == null) entity.krcdtAnpAggrGoout = new ArrayList<>();
		val entityAggrGooutList = entity.krcdtAnpAggrGoout;
		entityAggrGooutList.removeIf(
				a -> {return !goOutMap.containsKey(EnumAdaptor.valueOf(a.PK.goOutReason, GoingOutReason.class));} );
		for (val goOut : goOutMap.values()){
			KrcdtAnpTimeGoout entityAggrGoout = new KrcdtAnpTimeGoout();
			val entityAggrGooutOpt = entityAggrGooutList.stream()
					.filter(c -> c.PK.goOutReason == goOut.getGoOutReason().value).findFirst();
			if (entityAggrGooutOpt.isPresent()){
				entityAggrGoout = entityAggrGooutOpt.get();
				entityAggrGoout.fromDomainForUpdate(goOut);
			}
			else {
				entityAggrGoout.fromDomainForPersist(domainKey, goOut);
				entityAggrGooutList.add(entityAggrGoout);
			}
		}
		
		// ??????????????????????????????????????????
		val premiumTimeMap = vtWorkTime.getPremiumTime().getPremiumTime();
		if (entity.krcdtAnpAggrPremTime == null) entity.krcdtAnpAggrPremTime = new ArrayList<>();
		val entityAggrPremTimeList = entity.krcdtAnpAggrPremTime;
		entityAggrPremTimeList.removeIf(a -> {return !premiumTimeMap.containsKey(a.PK.premiumTimeItemNo);} );
		for (val premiumTime : premiumTimeMap.values()){
			KrcdtAnpAggrPremTime entityAggrPremTime = new KrcdtAnpAggrPremTime();
			val entityAggrPremTimeOpt = entityAggrPremTimeList.stream()
					.filter(c -> c.PK.premiumTimeItemNo == premiumTime.getPremiumTimeItemNo().value).findFirst();
			if (entityAggrPremTimeOpt.isPresent()){
				entityAggrPremTime = entityAggrPremTimeOpt.get();
				entityAggrPremTime.fromDomainForUpdate(premiumTime);
			}
			else {
				entityAggrPremTime.fromDomainForPersist(domainKey, premiumTime);
				entityAggrPremTimeList.add(entityAggrPremTime);
			}
		}
		
		// ???????????????????????????????????????????????????
		val medicalTimeMap = vtWorkTime.getMedicalTime();
		if (entity.krcdtAnpMedicalTime == null) entity.krcdtAnpMedicalTime = new ArrayList<>();
		val entityMedicalTimeList = entity.krcdtAnpMedicalTime;
		entityMedicalTimeList.removeIf(
				a -> {return !medicalTimeMap.containsKey(EnumAdaptor.valueOf(a.PK.dayNightAtr, WorkTimeNightShift.class));} );
		for (val medicalTime : medicalTimeMap.values()){
			KrcdtAnpMedicalTime entityMedicalTime = new KrcdtAnpMedicalTime();
			val entityMedicalTimeOpt = entityMedicalTimeList.stream()
					.filter(c -> c.PK.dayNightAtr == medicalTime.getDayNightAtr().value).findFirst();
			if (entityMedicalTimeOpt.isPresent()){
				entityMedicalTime = entityMedicalTimeOpt.get();
				entityMedicalTime.fromDomainForUpdate(medicalTime);
			}
			else {
				entityMedicalTime.fromDomainForPersist(domainKey, medicalTime);
				entityMedicalTimeList.add(entityMedicalTime);
			}
		}
		
		// ???????????????????????????
		val totalCountMap = domain.getTotalCount().getTotalCountList();
		if (entity.krcdtAnpTotalTimes == null) entity.krcdtAnpTotalTimes = new ArrayList<>();
		val entityTotalTimesList = entity.krcdtAnpTotalTimes;
		entityTotalTimesList.removeIf(a -> {return !totalCountMap.containsKey(a.PK.totalTimesNo);} );
		for (val totalCount : totalCountMap.values()){
			KrcdtAnpTimeTotalcount entityTotalTimes = new KrcdtAnpTimeTotalcount();
			val entityTotalTimesOpt = entityTotalTimesList.stream()
					.filter(c -> c.PK.totalTimesNo == totalCount.getTotalCountNo()).findFirst();
			if (entityTotalTimesOpt.isPresent()){
				entityTotalTimes = entityTotalTimesOpt.get();
				entityTotalTimes.fromDomainForUpdate(totalCount);
			}
			else {
				entityTotalTimes.fromDomainForPersist(domainKey, totalCount);
				entityTotalTimesList.add(entityTotalTimes);
			}
		}
		
		// ??????????????????????????????
		val anyItemValueMap = domain.getAnyItem().getAnyItemValues();
		if (entity.krcdtAnpAnyItemValue == null) entity.krcdtAnpAnyItemValue = new ArrayList<>();
		val entityAnyItemValueList = entity.krcdtAnpAnyItemValue;
		entityAnyItemValueList.removeIf(a -> {return !anyItemValueMap.containsKey(a.PK.anyItemId);} );
		for (val anyItemValue : anyItemValueMap.values()){
			KrcdtAnpAnyItemValue entityAnyItemValue = new KrcdtAnpAnyItemValue();
			val entityAnyItemValueOpt = entityAnyItemValueList.stream()
					.filter(c -> c.PK.anyItemId == anyItemValue.getAnyItemNo()).findFirst();
			if (entityAnyItemValueOpt.isPresent()){
				entityAnyItemValue = entityAnyItemValueOpt.get();
				entityAnyItemValue.fromDomainForUpdate(anyItemValue);
			}
			else {
				entityAnyItemValue.fromDomainForPersist(domainKey, anyItemValue);
				entityAnyItemValueList.add(entityAnyItemValue);
			}
		}
		
		// ???????????????????????????????????????
		if (isNeedPersist) this.getEntityManager().persist(entity);
	}
	
	/** ?????? */
	@Override
	public void remove(String employeeId, String frameCode) {
		
		for (val deleteTable : DELETE_TABLES){
			this.getEntityManager().createQuery(deleteTable + WHERE_PK)
					.setParameter("employeeId", employeeId)
					.setParameter("frameCode", frameCode)
					.executeUpdate();
		}
	}
}
