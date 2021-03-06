package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordServiceProc;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.emp.EmpFlexMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.flex.wkp.WkpFlexMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.emp.EmpDeforLaborMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.emp.EmpRegulaMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.wkp.WkpDeforLaborMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.calcmethod.calcmethod.other.wkp.WkpRegulaMonthActCalSet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.roundingset.RoundingSetOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.AggregateMethodOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.vtotalmethod.WorkDaysNumberOnLeaveCount;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.AggregateAttendanceTimeValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.AgreementTimeAggregateService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.AnyItemAggregateService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyOldDatas;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.editstate.EditStateOfMonthlyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flex.CalcFlexChangeDto;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.flex.ConditionCalcResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.reservation.ReservationOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.OutsideOTSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.holiday.SuperHD60HConMed;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.UsageUnitSetting;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSet.LaborWorkTypeAttr;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.monunit.MonthlyWorkTimeSetWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.defor.DeforLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeCom;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeEmp;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeWkp;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosurePeriod;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.difftimeset.DiffTimeWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/** ????????????????????????????????? */
public class AggregatePastMonthsService {

	/** ???????????? */
	public static List<AggregatePastMonthResult> aggregate(Require require, String sid, 
			GeneralDate aggrStartDate, List<IntegrationOfDaily> dailyRecords) {
		
		val cId = AppContexts.user().companyId();
		val cacheCarrier = new CacheCarrier();
		
		/** ?????????????????????????????????????????????????????? */
		val comSets = require.monAggrCompanySettings(cId);
		
		/** ??????????????????????????? */
		val aggrPeriods = AggregatePastMonthsPeriodService.calcPeriod(require, cacheCarrier, cId, sid, aggrStartDate);
		
		/** ???????????????????????????????????? */
		List<AggregatePastMonthResult> aggrResults = new ArrayList<>();
		
		aggrPeriods.forEach(ap -> {
			
			/** ????????????????????????????????????*/
			Optional<AttendanceTimeOfMonthly> attdTime = require.attendanceTimeOfMonthly(sid, ap.getYearMonth(), ap.getClosureId(), ap.getClosureDate());
			
			if(attdTime.isPresent()) {
				/** ????????????????????? */
				val result = aggrPastMonth(require, cacheCarrier, cId, sid, ap, dailyRecords, comSets, aggrResults);
				
				/** ??????????????????????????????Version???????????????*/
				result.getMonthlyAttdTime().setVersion(attdTime.get().getVersion());
				
				/** ?????????????????????????????????????????? */
				aggrResults.add(result);
			}
		});
		
		return aggrResults;
	}
	
	/** ????????????????????? */
	private static AggregatePastMonthResult aggrPastMonth(RequireM2 require, CacheCarrier cacheCarrier, String cid,
			 String sid, ClosurePeriod closurePeriod, List<IntegrationOfDaily> dailyRecords,
			 MonAggrCompanySettings companySets, List<AggregatePastMonthResult> aggrResults) {

		val ym = closurePeriod.getYearMonth();
		val closureId = closurePeriod.getClosureId();
		val closureDate = closurePeriod.getClosureDate();
		val period = closurePeriod.getPeriod();
		
		val empSets = require.monAggrEmployeeSettings(cacheCarrier, cid, sid, period);
		
		/** ???????????????Work???????????????????????? */
		val dailyWorks = MonthlyCalculatingDailys.loadData(require, sid, 
				period, Optional.of(dailyRecords), empSets);
		
		/** ????????????????????????????????????????????? */
		val workConditionItems = require.getWorkingConditionItemWithPeriod(cid, Arrays.asList(sid), period);
		workConditionItems.sort((c1, c2) -> c1.getDatePeriod().start().compareTo(c2.getDatePeriod().start()));
		
		/** ???????????????????????????????????????????????? */
		val monthlyOldDatas = MonthlyOldDatas.loadData(require, sid, ym, closureId, closurePeriod.getClosureDate());
		val editStates = require.monthEditStates(sid, ym, closureId, closurePeriod.getClosureDate());
		val converter = require.createMonthlyConverter();
		
		/** ?????????????????????????????????????????? */
		val attendanceTime = aggrMonthAttendanceTime(require, cacheCarrier, cid, sid, ym, period, closureId, 
				closureDate, companySets, empSets, dailyWorks, workConditionItems, monthlyOldDatas, editStates,
				converter, aggrResults);
		
		/** 36????????????????????? */
		val agreementTimeResults = AgreementTimeAggregateService.aggregate(require, cacheCarrier, cid, 
				sid, ym, closureId, closureDate, period, companySets, empSets, dailyWorks,  
				monthlyOldDatas, Optional.of(attendanceTime.getAttendanceTime().getMonthlyCalculation()));
		val agreementTimes = agreementTimeResults.stream().filter(c -> c.getAgreementTime().isPresent())
				.map(c -> c.getAgreementTime().get()).collect(Collectors.toList());
		
		/** ???????????????????????? */
		val anyItems = AnyItemAggregateService.aggregate(require, cacheCarrier, cid, sid, ym, closureId, 
				closureDate, period, companySets, empSets, dailyWorks, monthlyOldDatas, editStates, 
				attendanceTime.getAttendanceTimeWeeks(), Optional.ofNullable(attendanceTime.getAttendanceTime()));
		
		return AggregatePastMonthResult.builder()
										.monthlyAttdTime(attendanceTime.getAttendanceTime())
										.weeklyAttdTime(attendanceTime.getAttendanceTimeWeeks())
										.agreementTime(agreementTimes)
										.monthlyAnyItem(anyItems)
										.build();
	}
	
	/** ?????????????????????????????????????????? */
	private static AggregateAttendanceTimeValue aggrMonthAttendanceTime(RequireM3 require, CacheCarrier cacheCarrier,
			String cid, String sid, YearMonth ym, DatePeriod period, ClosureId closureId, 
			ClosureDate closureDate, MonAggrCompanySettings companySets, MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys dailyWorks, List<WorkingConditionItemWithPeriod> workCondition,
			MonthlyOldDatas monthlyOldDatas, List<EditStateOfMonthlyPerformance> editStates,
			MonthlyRecordToAttendanceItemConverter converter, List<AggregatePastMonthResult> aggrResults) {
		
		/** ???????????????????????????????????? */
		val workConGroup = mergeWorkCondition(workCondition, period);
		
		val aggrAttendanceTimes = workConGroup.stream().map(wc -> {
			/** ???????????????????????????????????? */
			AggregateAttendanceTimeValue aggrResult = new AggregateAttendanceTimeValue(sid, ym, closureId, closureDate, wc.getDatePeriod());
			List<AttendanceTimeOfWeekly> attendanceTimeWeeks = aggrResult.getAttendanceTime().aggregateAttendanceTime(
					createRequire(require, aggrResults), cacheCarrier, cid, wc.getDatePeriod(), wc.getWorkingConditionItem(), 
					companySets, employeeSets, dailyWorks, monthlyOldDatas, new HashMap<>());
			aggrResult.getAttendanceTimeWeeks().addAll(attendanceTimeWeeks);
			return aggrResult;
		}).collect(Collectors.toList());
		
		AggregateAttendanceTimeValue aggrAttendanceTime = aggrAttendanceTimes.stream().findFirst()
				.orElseGet(() -> new AggregateAttendanceTimeValue(sid, ym, closureId, closureDate, period));
		
		for (int idx = 1; idx < aggrAttendanceTimes.size(); idx++) {
			
			/** ?????????????????? */
			aggrAttendanceTime.sum(aggrAttendanceTimes.get(idx));
			
		}
		
		/** ??????????????????????????????????????? */
		val rounded = companySets.getRoundingSet().round(require, aggrAttendanceTime.getAttendanceTime());
		aggrAttendanceTime.setAttendanceTime(rounded);
		
		/** ??????????????????????????????????????? */
		revertAttendanceTime(monthlyOldDatas, editStates, converter, aggrAttendanceTime);
		
		/** ??????????????????????????????????????????????????????????????? */
		aggrAttendanceTime.getAttendanceTime().recalcSomeItem();
		
		/** ??????????????????????????????????????? */
		revertAttendanceTime(monthlyOldDatas, editStates, converter, aggrAttendanceTime);
		
		return aggrAttendanceTime;
	}

	/** ??????????????????????????????????????? */
	private static void revertAttendanceTime(MonthlyOldDatas monthlyOldDatas, List<EditStateOfMonthlyPerformance> editStates,
			MonthlyRecordToAttendanceItemConverter converter, AggregateAttendanceTimeValue aggrAttendanceTime) {

		if (!monthlyOldDatas.getAttendanceTime().isPresent()) return;
		
		val itemIds = editStates.stream().map(c -> c.getAttendanceItemId()).collect(Collectors.toList());
		converter.withAttendanceTime(monthlyOldDatas.getAttendanceTime().get());
		val oldData = converter.convert(itemIds);
		converter.withAttendanceTime(aggrAttendanceTime.getAttendanceTime());
		converter.merge(oldData);
		aggrAttendanceTime.setAttendanceTime(converter.toAttendanceTime().get());
	}
	
	/** ???????????????????????????????????? */
	private static List<WorkingConditionItemWithPeriod> mergeWorkCondition(List<WorkingConditionItemWithPeriod> workCondition, DatePeriod period) {
		
		/** ????????????????????????????????? */
		GeneralDate startDate = period.start();
		
		/** ???????????????????????????????????? */
		List<WorkingConditionItemWithPeriod> workConditions = new ArrayList<>();
		
		while(startDate.beforeOrEquals(period.end())) {
			
			val currentDate = startDate;
			/** ????????????????????????????????????????????????????????? */
			val currentWc = workCondition.stream().filter(c -> c.getDatePeriod().contains(currentDate)).findFirst().orElse(null);
			
			if (currentWc == null) return workConditions;

			/** ??????????????????????????????????????????????????????????????????????????????????????? */
			val nextWc = workCondition.stream().filter(c -> c.getDatePeriod().start().after(currentWc.getDatePeriod().end()) 
						&& c.getWorkingConditionItem().getLaborSystem() != currentWc.getWorkingConditionItem().getLaborSystem())
					.findFirst().orElse(null);
			
			if (nextWc == null) {
				/** ????????????????????????????????????????????????????????? */
				workConditions.add(new WorkingConditionItemWithPeriod(new DatePeriod(startDate, period.end()), 
																	currentWc.getWorkingConditionItem()));
				return workConditions;
			}
			
			/** ?????????????????????????????? */
			val endDate = nextWc.getDatePeriod().end().beforeOrEquals(period.end()) 
					? nextWc.getDatePeriod().start().addDays(-1) : period.end();

			/** ????????????????????????????????????????????????????????? */
			workConditions.add(new WorkingConditionItemWithPeriod(new DatePeriod(startDate, endDate), 
																currentWc.getWorkingConditionItem()));
			/** ????????????????????????????????? */
			startDate = endDate.addDays(1);
		}
		
		return workConditions;
	}
	
	private static AttendanceTimeOfMonthly.RequireM3 createRequire(RequireM3 require, List<AggregatePastMonthResult> aggrResults) {
		
		return new AttendanceTimeOfMonthly.RequireM3() {
			
			@Override
			public MonthlyRecordToAttendanceItemConverter createMonthlyConverter() {
				return require.createMonthlyConverter();
			}
			
			@Override
			public Optional<OutsideOTSetting> outsideOTSetting(String cid) {
				return require.outsideOTSetting(cid);
			}
			
			@Override
			public Optional<SuperHD60HConMed> superHD60HConMed(String cid) {
				return require.superHD60HConMed(cid);
			}
			
			@Override
			public ReservationOfMonthly reservation(String sid, GeneralDate date, String companyID) {
				return require.reservation(sid, date, companyID);
			}
			
			@Override
			public Optional<AggregateMethodOfMonthly> aggregateMethodOfMonthly(String cid) {
				return require.aggregateMethodOfMonthly(cid);
			}
			
			@Override
			public Optional<PredetemineTimeSetting> predetemineTimeSetByWorkTimeCode(String companyId, String workTimeCode) {
				return require.predetemineTimeSetByWorkTimeCode(companyId, workTimeCode);
			}
			
			@Override
			public Optional<AttendanceTimeOfMonthly> attendanceTimeOfMonthly(String employeeId, YearMonth yearMonth,
					ClosureId closureId, ClosureDate closureDate) {
				/** ???Require?????????????????????????????????????????????????????????????????????????????????????????????Repo?????????????????? */
				val at = aggrResults.stream().filter(c -> c.getMonthlyAttdTime().getEmployeeId().equals(employeeId)
						&& c.getMonthlyAttdTime().getYearMonth().equals(yearMonth) 
						&& c.getMonthlyAttdTime().getClosureId().equals(closureId)
						&& c.getMonthlyAttdTime().getClosureDate().equals(closureDate)).findFirst();
				if (!at.isPresent()) {
					return require.attendanceTimeOfMonthly(employeeId, yearMonth, closureId, closureDate);
				}
				return at.map(c -> c.getMonthlyAttdTime());
			}
			
			@Override
			public List<AttendanceTimeOfMonthly> attendanceTimeOfMonthly(String employeeId, YearMonth yearMonth) {
				/** ???Require?????????????????????????????????????????????????????????????????????????????????????????????Repo?????????????????? */
				val dbAts = require.attendanceTimeOfMonthly(employeeId, yearMonth);
				val cachedAts = aggrResults.stream().filter(c -> c.getMonthlyAttdTime().getEmployeeId().equals(employeeId)
						&& c.getMonthlyAttdTime().getYearMonth().equals(yearMonth))
						.map(c -> c.getMonthlyAttdTime())
						.collect(Collectors.toList());
				if (dbAts.isEmpty()) {
					return cachedAts;
				}
				if (!cachedAts.isEmpty()) {
					cachedAts.stream().forEach(c -> {
						val at = dbAts.stream().filter(d -> c.getEmployeeId().equals(employeeId)
								&& c.getYearMonth().equals(yearMonth) 
								&& c.getClosureId().equals(d.getClosureId())
								&& c.getClosureDate().equals(d.getClosureDate())).findFirst();
						if (at.isPresent()) {
							dbAts.remove(at.get());
						} 
						dbAts.add(c);
					});
				}
				return dbAts;
			}
			
			@Override
			public Optional<FlowWorkSetting> flowWorkSetting(String companyId, String workTimeCode) {
				return require.flowWorkSetting(companyId, workTimeCode);
			}
			
			@Override
			public Optional<FlexWorkSetting> flexWorkSetting(String companyId, String workTimeCode) {
				return require.flexWorkSetting(companyId, workTimeCode);
			}
			
			@Override
			public Optional<FixedWorkSetting> fixedWorkSetting(String companyId, String workTimeCode) {
				return require.fixedWorkSetting(companyId, workTimeCode);
			}
			
			@Override
			public Optional<DiffTimeWorkSetting> diffTimeWorkSetting(String companyId, String workTimeCode) {
				return require.diffTimeWorkSetting(companyId, workTimeCode);
			}
			
			@Override
			public Optional<WorkTimeSetting> workTimeSetting(String companyId, String workTimeCode) {
				return require.workTimeSetting(companyId, workTimeCode);
			}
			
			@Override
			public Optional<WorkingConditionItem> workingConditionItem(String employeeId, GeneralDate baseDate) {
				return require.workingConditionItem(employeeId, baseDate);
			}
			
			@Override
			public Optional<WeekRuleManagement> weekRuleManagement(String cid) {
				return require.weekRuleManagement(cid);
			}
			
			@Override
			public Optional<WorkType> workType(String companyId, String workTypeCd) {
				return require.workType(companyId, workTypeCd);
			}
			
			@Override
			public DailyRecordToAttendanceItemConverter createDailyConverter() {
				return require.createDailyConverter();
			}
			
			@Override
			public Optional<Closure> closure(String companyId, int closureId) {
				return require.closure(companyId, closureId);
			}
			
			@Override
			public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
				return require.employmentClosure(companyID, employmentCD);
			}
			
			@Override
			public EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId) {
				return require.employeeInfo(cacheCarrier, empId);
			}
			
			@Override
			public Optional<RegularLaborTimeSha> regularLaborTimeByEmployee(String Cid, String EmpId) {
				return require.regularLaborTimeByEmployee(Cid, EmpId);
			}
			
			@Override
			public Optional<DeforLaborTimeSha> deforLaborTimeByEmployee(String cid, String empId) {
				return require.deforLaborTimeByEmployee(cid, empId);
			}
			
			@Override
			public Optional<RegularLaborTimeEmp> regularLaborTimeByEmployment(String cid, String employmentCode) {
				return require.regularLaborTimeByEmployment(cid, employmentCode);
			}
			
			@Override
			public Optional<DeforLaborTimeEmp> deforLaborTimeByEmployment(String cid, String employmentCode) {
				return require.deforLaborTimeByEmployment(cid, employmentCode);
			}
			
			@Override
			public Optional<RegularLaborTimeWkp> regularLaborTimeByWorkplace(String cid, String wkpId) {
				return require.regularLaborTimeByWorkplace(cid, wkpId);
			}
			
			@Override
			public Optional<DeforLaborTimeWkp> deforLaborTimeByWorkplace(String cid, String wkpId) {
				return require.deforLaborTimeByWorkplace(cid, wkpId);
			}
			
			@Override
			public Optional<RegularLaborTimeCom> regularLaborTimeByCompany(String companyId) {
				return require.regularLaborTimeByCompany(companyId);
			}
			
			@Override
			public Optional<DeforLaborTimeCom> deforLaborTimeByCompany(String companyId) {
				return require.deforLaborTimeByCompany(companyId);
			}
			
			@Override
			public Optional<MonthlyWorkTimeSetCom> monthlyWorkTimeSetCom(String cid, LaborWorkTypeAttr laborAttr,
					YearMonth ym) {
				return require.monthlyWorkTimeSetCom(cid, laborAttr, ym);
			}
			
			@Override
			public Optional<MonthlyWorkTimeSetEmp> monthlyWorkTimeSetEmp(String cid, String empCode,
					LaborWorkTypeAttr laborAttr, YearMonth ym) {
				return require.monthlyWorkTimeSetEmp(cid, empCode, laborAttr, ym);
			}
			
			@Override
			public Optional<MonthlyWorkTimeSetSha> monthlyWorkTimeSetSha(String cid, String sid, LaborWorkTypeAttr laborAttr,
					YearMonth ym) {
				return require.monthlyWorkTimeSetSha(cid, sid, laborAttr, ym);
			}
			
			@Override
			public Optional<MonthlyWorkTimeSetWkp> monthlyWorkTimeSetWkp(String cid, String workplaceId,
					LaborWorkTypeAttr laborAttr, YearMonth ym) {
				return require.monthlyWorkTimeSetWkp(cid, workplaceId, laborAttr, ym);
			}
			
			@Override
			public Optional<WkpFlexMonthActCalSet> monthFlexCalcSetByWorkplace(String cid, String wkpId) {
				return require.monthFlexCalcSetByWorkplace(cid, wkpId);
			}
			
			@Override
			public Optional<EmpFlexMonthActCalSet> monthFlexCalcSetByEmployment(String cid, String empCode) {
				return require.monthFlexCalcSetByEmployment(cid, empCode);
			}
			
			@Override
			public Optional<WkpDeforLaborMonthActCalSet> monthDeforCalcSetByWorkplace(String cid, String wkpId) {
				return require.monthDeforCalcSetByWorkplace(cid, wkpId);
			}
			
			@Override
			public Optional<EmpDeforLaborMonthActCalSet> monthDeforCalcSetByEmployment(String cid, String empCode) {
				return require.monthDeforCalcSetByEmployment(cid, empCode);
			}
			
			@Override
			public List<WorkingConditionItem> workingConditionItem(String employeeId, DatePeriod datePeriod) {
				return require.workingConditionItem(employeeId, datePeriod);
			}
			
			@Override
			public Optional<WkpRegulaMonthActCalSet> monthRegularCalcSetByWorkplace(String cid, String wkpId) {
				return require.monthRegularCalcSetByWorkplace(cid, wkpId);
			}
			
			@Override
			public Optional<EmpRegulaMonthActCalSet> monthRegularCalcSetByEmployment(String cid, String empCode) {
				return require.monthRegularCalcSetByEmployment(cid, empCode);
			}
			
			@Override
			public List<String> getCanUseWorkplaceForEmp(CacheCarrier cacheCarrier, String companyId, String employeeId,
					GeneralDate baseDate) {
				return require.getCanUseWorkplaceForEmp(cacheCarrier, companyId, employeeId, baseDate);
			}
			
			@Override
			public Optional<UsageUnitSetting> usageUnitSetting(String companyId) {
				return require.usageUnitSetting(companyId);
			}
			
			@Override
			public ConditionCalcResult flexConditionCalcResult(CacheCarrier cacheCarrier, String companyId,
					CalcFlexChangeDto calc) {
				return require.flexConditionCalcResult(cacheCarrier, companyId, calc);
			}

			@Override
			public List<ClosureEmployment> employmentClosureClones(String companyID, List<String> employmentCD) {
				return require.employmentClosureClones(companyID, employmentCD);
			}

			@Override
			public List<Closure> closureClones(String companyId, List<Integer> closureId) {
				return require.closureClones(companyId, closureId);
			}

			

			@Override
			public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
				return require.findCompensatoryLeaveComSet(companyId);
			}

			@Override
			public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
				return require.getWorkSettingForFixedWork(code);
			}

			@Override
			public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
				return require.getWorkSettingForFlowWork(code);
			}

			@Override
			public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
				return require.getWorkSettingForFlexWork(code);
			}

			@Override
			public Optional<HolidayAddtionSet> holidayAddtionSet(String cid) {
				return require.holidayAddtionSet(cid);
			}

			@Override
			public Optional<PredetemineTimeSetting> predetemineTimeSetting(String cid, String workTimeCode) {
				return require.predetemineTimeSetByWorkTimeCode(cid, workTimeCode);
			}

			@Override
			public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
				return require.getWorkTime(cid,workTimeCode);
			}

			@Override
			public WorkDaysNumberOnLeaveCount workDaysNumberOnLeaveCount(String cid) {
				return require.workDaysNumberOnLeaveCount(cid);
			}
		};
	}

	public static interface RequireM4 {
		
	}
	
	public static interface RequireM5 {
		
	}
	
	public static interface RequireM3 extends AggregateMonthlyRecordServiceProc.RequireM13 {
		
	}
	
	public static interface RequireM2 extends RequireM3, RequireM4, RequireM5, 
			MonthlyCalculatingDailys.RequireM4, AgreementTimeAggregateService.Require,
			AnyItemAggregateService.Require {
		
		MonAggrEmployeeSettings monAggrEmployeeSettings(CacheCarrier cacheCarrier, String cid,
				String sid, DatePeriod period);
		
		List<WorkingConditionItemWithPeriod> getWorkingConditionItemWithPeriod(String companyID , List<String> lstEmpID, DatePeriod datePeriod);
		
		List<EditStateOfMonthlyPerformance> monthEditStates(String employeeId, YearMonth yearMonth, ClosureId closureId,
				ClosureDate closureDate);
		
		MonthlyRecordToAttendanceItemConverter createMonthlyConverter();
	}
	
	public static interface Require extends AggregatePastMonthsPeriodService.RequireM3,
			RequireM2 {
		
		Optional<RoundingSetOfMonthly> monthRoundingSet(String companyId);
		
		MonAggrCompanySettings monAggrCompanySettings(String cid);
	}
}

