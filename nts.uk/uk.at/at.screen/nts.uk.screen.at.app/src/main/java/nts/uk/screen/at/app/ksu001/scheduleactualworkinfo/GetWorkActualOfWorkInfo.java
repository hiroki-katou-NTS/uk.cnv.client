/**
 *
 */
package nts.uk.screen.at.app.ksu001.scheduleactualworkinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.app.cache.NestedMapCache;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordWorkFinder;
import nts.uk.ctx.at.record.dom.daily.GetDailyRecordByScheduleManagementService;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
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
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoParam;
import nts.uk.screen.at.app.ksu001.processcommon.CreateWorkScheduleWorkInforBase;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author laitv
 * ScreenQuery : 勤務実績（勤務情報）を取得する
 */
@Stateless
public class GetWorkActualOfWorkInfo {

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
	private DailyRecordWorkFinder dailyRecordWorkFinder;
	@Inject
	private CreateWorkScheduleWorkInforBase createWorkScheduleWorkInforBase;
	@Inject
	private EmpAffiliationInforAdapter empAffiliationInforAdapter;


	public List<WorkScheduleWorkInforDto> getDataActualOfWorkInfo(DisplayInWorkInfoParam param) {

		// step 1
		// call 予定管理状態に応じて日別実績を取得する
		DatePeriod period = new DatePeriod(param.startDate, param.endDate);
		RequireDailyImpl requireDailyImpl = new RequireDailyImpl(param.listSid, period);
		
		Map<EmployeeWorkingStatus , Optional<IntegrationOfDaily>> map = GetDailyRecordByScheduleManagementService.get(requireDailyImpl, param.listSid, period);

		// step 2
		// call 勤務実績で勤務予定（勤務情報）dtoを作成する
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = createWorkScheduleWorkInforBase.getDataScheduleOfWorkInfo(map);
		return listWorkScheduleWorkInfor;
	}

	
	private class RequireDailyImpl implements GetDailyRecordByScheduleManagementService.Require {

		private NestedMapCache<String, GeneralDate, DailyRecordDto> workScheduleCache;
		private KeyDateHistoryCache<String, EmpEnrollPeriodImport> affCompanyHistByEmployeeCache;
		private KeyDateHistoryCache<String, EmploymentPeriodImported> employmentPeriodCache;
		private KeyDateHistoryCache<String, EmployeeLeaveJobPeriodImport> empLeaveJobPeriodCache;
		private KeyDateHistoryCache<String, EmpLeaveWorkPeriodImport> empLeaveWorkPeriodCache;
		private KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workCondItemWithPeriodCache;

		public RequireDailyImpl(List<String> empIdList, DatePeriod period) {

			List<DailyRecordDto> sDailyRecordDtos = dailyRecordWorkFinder.find(empIdList, period);
			workScheduleCache = NestedMapCache.preloadedAll(sDailyRecordDtos.stream(),
					workSchedule -> workSchedule.getEmployeeId(),
					workSchedule -> workSchedule.getDate());

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

		private  Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>>  createEntries1(Map<String, List<EmpEnrollPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmpEnrollPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<EmpEnrollPeriodImport>> s = v.stream().map(i->new DateHistoryCache.Entry<EmpEnrollPeriodImport>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		private  Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>>  createEntries2(Map<String, List<EmploymentPeriodImported>> data) {
			Map<String, List<DateHistoryCache.Entry<EmploymentPeriodImported>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				Set<DateHistoryCache.Entry<EmploymentPeriodImported>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmploymentPeriodImported>(
								new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end()),
								new EmploymentPeriodImported(
										i.getEmpID(),
										new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end()),
										i.getEmploymentCd(),
										i.getOtpSalarySegment()))).collect(Collectors.toSet()) ;
				rs.put(k, s.stream().collect(Collectors.toList()));
			});
			return rs;
		}

		private  Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>>  createEntries3(Map<String, List<EmployeeLeaveJobPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				Set<DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmployeeLeaveJobPeriodImport>(
								new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end()),
								new EmployeeLeaveJobPeriodImport(i.getEmpID(), new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end())))).collect(Collectors.toSet()) ;
				rs.put(k, s.stream().collect(Collectors.toList()));
			});
			return rs;
		}

		private  Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>>  createEntries4(Map<String, List<EmpLeaveWorkPeriodImport>> data) {
			Map<String, List<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				Set<DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>> s = v.stream()
						.map(i->new DateHistoryCache.Entry<EmpLeaveWorkPeriodImport>(
								new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end()),
								new EmpLeaveWorkPeriodImport(
										i.getEmpID(),
										i.getTempAbsenceFrNo(),
										new DatePeriod(i.getDatePeriod().start(), i.getDatePeriod().end())))).collect(Collectors.toSet()) ;
				rs.put(k, s.stream().collect(Collectors.toList()));
			});
			return rs;
		}

		private  Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>>  createEntries5(Map<String, List<WorkingConditionItemWithPeriod>> data) {
			Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
			data.forEach( (k,v) -> {
				List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = v.stream().map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
				rs.put(k, s);
			});
			return rs;
		}

		@Override
		public Optional<IntegrationOfDaily> getDailyResults(String empId, GeneralDate date) {
			Optional<DailyRecordDto> dailyRecordDto = workScheduleCache.get(empId, date);
			if (dailyRecordDto.isPresent()) {
				IntegrationOfDaily data = dailyRecordDto.get().toDomain(empId, date);
				return Optional.of(data);
			}
			return Optional.empty();
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
