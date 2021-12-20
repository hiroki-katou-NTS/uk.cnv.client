package nts.uk.ctx.at.record.pubimp.dailyresult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.app.cache.NestedMapCache;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.OptionalUtil;
import nts.uk.ctx.at.record.dom.daily.GetDailyRecordByScheduleManagementService;
import nts.uk.ctx.at.record.pub.dailyresult.DailyRecordPub;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DailyRecordPubImpl implements DailyRecordPub{

	@Inject
	DailyRecordShareFinder dailyRecordShareFinder;

	@Inject
	private EmpComHisAdapter empComHisAdapter;

	@Inject
	private WorkingConditionRepository workCondRepo;

	@Inject
	private EmpLeaveHistoryAdapter empLeaveHisAdapter;

	@Inject
	private EmpLeaveWorkHistoryAdapter empLeaveWorkHisAdapter;

	@Inject
	private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
	
	@Inject
	private EmpAffiliationInforAdapter empAffiliationInforAdapter;

	@Override
	public List<IntegrationOfDaily> getDailyRecordByScheduleManagement(List<String> employeeIds, DatePeriod period) {

		RequireImpl requireImpl = new RequireImpl(employeeIds, period);

		return GetDailyRecordByScheduleManagementService.get(requireImpl, employeeIds, period)
						.values().stream()
						.flatMap(OptionalUtil::stream)
						.collect(Collectors.toList());
	}

	private class RequireImpl implements GetDailyRecordByScheduleManagementService.Require {

		private NestedMapCache<String, GeneralDate, IntegrationOfDaily> dailyRecordCache;
		private KeyDateHistoryCache<String, EmpEnrollPeriodImport> affCompanyHistByEmployeeCache;
		private KeyDateHistoryCache<String, EmploymentPeriodImported> employmentPeriodCache;
		private KeyDateHistoryCache<String, EmployeeLeaveJobPeriodImport> empLeaveJobPeriodCache;
		private KeyDateHistoryCache<String, EmpLeaveWorkPeriodImport> empLeaveWorkPeriodCache;
		private KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workCondItemWithPeriodCache;

		public RequireImpl(List<String> employeeIds, DatePeriod period) {

			List<IntegrationOfDaily> dailyRecordList =  dailyRecordShareFinder.findByListEmployeeId(employeeIds, period);
			dailyRecordCache = NestedMapCache.preloadedAll(
					dailyRecordList.stream(),
					dailyRecord -> dailyRecord.getEmployeeId(),
					dailyRecord -> dailyRecord.getYmd());

			List<EmpEnrollPeriodImport> affCompanyHists =  empComHisAdapter.getEnrollmentPeriod(employeeIds, period);
			Map<String, List<EmpEnrollPeriodImport>> data2 = affCompanyHists.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			affCompanyHistByEmployeeCache = KeyDateHistoryCache.loaded(createEntries1(data2));

			List<EmploymentPeriodImported> listEmploymentPeriodImported = employmentHisScheduleAdapter.getEmploymentPeriod(employeeIds, period);
			Map<String, List<EmploymentPeriodImported>> data3 = listEmploymentPeriodImported.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			employmentPeriodCache = KeyDateHistoryCache.loaded(createEntries2(data3));

			List<EmployeeLeaveJobPeriodImport> empLeaveJobPeriods = empLeaveHisAdapter.getLeaveBySpecifyingPeriod(employeeIds, period);
			Map<String, List<EmployeeLeaveJobPeriodImport>> data4 = empLeaveJobPeriods.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			empLeaveJobPeriodCache = KeyDateHistoryCache.loaded(createEntries3(data4));

			List<EmpLeaveWorkPeriodImport> empLeaveWorkPeriods =  empLeaveWorkHisAdapter.getHolidayPeriod(employeeIds, period);
			Map<String, List<EmpLeaveWorkPeriodImport>> data5 = empLeaveWorkPeriods.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			empLeaveWorkPeriodCache = KeyDateHistoryCache.loaded(createEntries4(data5));

			List<WorkingConditionItemWithPeriod> listData = workCondRepo.getWorkingConditionItemWithPeriod(AppContexts.user().companyId(),employeeIds, period);
			Map<String, List<WorkingConditionItemWithPeriod>> data6 = listData.stream().collect(Collectors.groupingBy(item ->item.getWorkingConditionItem().getEmployeeId()));
			workCondItemWithPeriodCache = KeyDateHistoryCache.loaded(createEntries5(data6));
		}

		private Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>> createEntries1(Map<String, List<EmpEnrollPeriodImport>> data) {

			Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmpEnrollPeriodImport>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmpEnrollPeriodImport>(i.getDatePeriod(),i))
						.collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		private Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>> createEntries2(Map<String, List<EmploymentPeriodImported>> data) {

			Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmploymentPeriodImported>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmploymentPeriodImported>(i.getDatePeriod(),i))
						.collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		private Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> createEntries3(Map<String, List<EmployeeLeaveJobPeriodImport>> data) {

			Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>(i.getDatePeriod(),i))
						.collect(Collectors.toList());
				rs.put(k, s);
			});
			return rs;
		}

		private Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>> createEntries4(Map<String, List<EmpLeaveWorkPeriodImport>> data) {

			Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>(i.getDatePeriod(),i))
						.collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		private Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> createEntries5(Map<String, List<WorkingConditionItemWithPeriod>> data) {

			Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i))
						.collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		@Override
		public Optional<IntegrationOfDaily> getDailyResults(String empId, GeneralDate date) {
			return dailyRecordCache.get(empId, date);
		}

		@Override
		public Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String sid, GeneralDate startDate) {
			return affCompanyHistByEmployeeCache.get(sid, startDate);
		}

		@Override
		public Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate baseDate) {
			return workCondItemWithPeriodCache.get(employeeId, baseDate)
					.map(workCondition -> workCondition.getWorkingConditionItem());
		}

		@Override
		public Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String sid, GeneralDate startDate) {
			return empLeaveJobPeriodCache.get(sid, startDate);
		}

		@Override
		public Optional<EmpLeaveWorkPeriodImport> specAndGetHolidayPeriod(String sid, GeneralDate startDate) {
			return empLeaveWorkPeriodCache.get(sid, startDate);
		}

		@Override
		public Optional<EmploymentPeriodImported> getEmploymentHistory(String sid, GeneralDate startDate) {
			return employmentPeriodCache.get(sid, startDate);
		}

		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate baseDate, List<String> lstEmpId) {
			
			return empAffiliationInforAdapter.getEmpOrganization(baseDate, lstEmpId);
		}

	}

}
