package nts.uk.screen.at.app.ksu003.getworkscheduleinfor;

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
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.GetWorkScheduleByScheduleManagementService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.screen.at.app.ksu003.getlistempworkhours.EmpTaskInfoDto;
import nts.uk.screen.at.app.ksu003.getlistempworkhours.GetListEmpWorkHours;
import nts.uk.shr.com.context.AppContexts;

/**
 * ??ScreenQuery?? ?????????????????????????????????
 * UKDesign.UniversalK.??????.KSU_??????????????????.KSU003_??????????????????????????????(?????????).A?????????????????????????????????(?????????).???????????????OCD.?????????????????????????????????
 * @author HieuLt
 *
 */
@Stateless
public class GetWorkScheduleInfor {

	@Inject
	private WorkScheduleRepository workScheduleRepo;
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
	private GetListEmpWorkHours getListEmpWorkHours;
	@Inject
	private EmpAffiliationInforAdapter empAffiliationInforAdapter;
	
	public List<EmpTaskInfoDto> get(List<String> lstEmpId , DatePeriod datePeriod ){
		//????????????(Require, List<??????ID>, ??????)
		// Call DS ?????????????????????????????????????????????????????????
		RequireImpl requireImpl = new RequireImpl(lstEmpId, datePeriod);
		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> mngStatusAndWScheMap =  GetWorkScheduleByScheduleManagementService.getScheduleManagement(requireImpl, lstEmpId, datePeriod);
		//Collection<Optional<WorkSchedule>> optWorkSchedule = mngStatusAndWScheMap.values();
		List<EmpTaskInfoDto> data = getListEmpWorkHours.get(mngStatusAndWScheMap);
		return data;
	}
	
	private class RequireImpl implements GetWorkScheduleByScheduleManagementService.Require {
		private KeyDateHistoryCache<String, EmpEnrollPeriodImport> affCompanyHistByEmployeeCache;
		private KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workCondItemWithPeriodCache;
		private KeyDateHistoryCache<String, EmployeeLeaveJobPeriodImport> empLeaveJobPeriodCache;
		private KeyDateHistoryCache<String, EmpLeaveWorkPeriodImport> empLeaveWorkPeriodCache;
		private KeyDateHistoryCache<String, EmploymentPeriodImported> employmentPeriodCache;
		private NestedMapCache<String, GeneralDate, WorkSchedule> workScheduleCache;
		
		public RequireImpl(List<String> empIdList, DatePeriod period) {

			List<WorkSchedule> lstWorkSchedule = workScheduleRepo.getList(empIdList, period);
			workScheduleCache = NestedMapCache.preloadedAll(lstWorkSchedule.stream(),
					workSchedule -> workSchedule.getEmployeeID(), workSchedule -> workSchedule.getYmd());

			List<EmpEnrollPeriodImport> affCompanyHists =  empComHisAdapter.getEnrollmentPeriod(empIdList, period);
			Map<String, List<EmpEnrollPeriodImport>> data2 = affCompanyHists.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			affCompanyHistByEmployeeCache = KeyDateHistoryCache.loaded(createEntries1(data2));
			
			List<EmploymentPeriodImported> listEmploymentPeriodImported = employmentHisScheduleAdapter.getEmploymentPeriod(empIdList, period);
			Map<String, List<EmploymentPeriodImported>> data3 = listEmploymentPeriodImported.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			employmentPeriodCache = KeyDateHistoryCache.loaded(createEntries2(data3));

			List<EmployeeLeaveJobPeriodImport> empLeaveJobPeriods = empLeaveHisAdapter.getLeaveBySpecifyingPeriod(empIdList, period);
			Map<String, List<EmployeeLeaveJobPeriodImport>> data4 = empLeaveJobPeriods.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			empLeaveJobPeriodCache = KeyDateHistoryCache.loaded(createEntries3(data4));

			List<EmpLeaveWorkPeriodImport> empLeaveWorkPeriods =  empLeaveWorkHisAdapter.getHolidayPeriod(empIdList, period);
			Map<String, List<EmpLeaveWorkPeriodImport>> data5 = empLeaveWorkPeriods.stream().collect(Collectors.groupingBy(item ->item.getEmpID()));
			empLeaveWorkPeriodCache = KeyDateHistoryCache.loaded(createEntries4(data5));

			List<WorkingConditionItemWithPeriod> listData = workCondRepo.getWorkingConditionItemWithPeriod(AppContexts.user().companyId(),empIdList, period);
			Map<String, List<WorkingConditionItemWithPeriod>> data6 = listData.stream().collect(Collectors.groupingBy(item ->item.getWorkingConditionItem().getEmployeeId()));
			workCondItemWithPeriodCache = KeyDateHistoryCache.loaded(createEntries5(data6));
		}
		private Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>>  createEntries1(Map<String, List<EmpEnrollPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmpEnrollPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmpEnrollPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}
		
		private Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>>  createEntries2(Map<String, List<EmploymentPeriodImported>> data) {
			Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmploymentPeriodImported>> s = v.stream().map(i->new DateHistoryCache.Entry<EmploymentPeriodImported>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}
		
		private Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>>  createEntries3(Map<String, List<EmployeeLeaveJobPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}
		
		private Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>>  createEntries4(Map<String, List<EmpLeaveWorkPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}
		
		private Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>>  createEntries5(Map<String, List<WorkingConditionItemWithPeriod>> data) {
			Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = v.stream().map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}
		@Override
		public Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String sid, GeneralDate startDate) {
			return affCompanyHistByEmployeeCache.get(sid, startDate);
		}

		@Override
		public Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate baseDate) {
			Optional<WorkingConditionItemWithPeriod> data = workCondItemWithPeriodCache.get(employeeId, baseDate);
			return data.isPresent() ? Optional.of(data.get().getWorkingConditionItem()) : Optional.empty();
		}

		@Override
		public Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String sid, GeneralDate startDate) {
			return  empLeaveJobPeriodCache.get(sid, startDate);
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
		public Optional<WorkSchedule> get(String employeeId, GeneralDate date) {
			return workScheduleCache.get(employeeId, date);
		}
		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate baseDate, List<String> lstEmpId) {
			return empAffiliationInforAdapter.getEmpOrganization(baseDate, lstEmpId);
		}
		
	}
}
