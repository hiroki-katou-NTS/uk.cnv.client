package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.layer.app.cache.KeyDateHistoryCache;
import nts.arc.layer.app.cache.NestedMapCache;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.GetWorkScheduleByScheduleManagementService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkHistoryAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpComHisAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpAffiliationInforAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.ksu001.processcommon.CreateWorkScheduleWorkInfor;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.DateInfoDuringThePeriodDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.EditStateOfDailyAttdDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.GetDateInfoDuringThePeriodInput;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * @author chungnt ScreenQuery : 勤務予定を取得する
 */

@Stateless
public class GetScheduleOfWorkInfo002 {
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
	private GetDateInfoDuringThePeriod getDateInfoDuringThePeriod;
	@Inject
	private CreateWorkScheduleWorkInfor scheduleWorkInfor;
	
	@Inject KSU002Finder kSU002Finder;
	
	public List<WorkScheduleWorkInforDto> getDataScheduleOfWorkInfo(DisplayInWorkInfoInput param) {

		// step 1 start
		// call 予定管理状態に応じて勤務予定を取得する
		DatePeriod period = new DatePeriod(param.getStartDate(), param.getEndDate());
		RequireImpl RequireImpl = new RequireImpl(param.listSid, period, workScheduleRepo, empComHisAdapter,
				workCondRepo, empLeaveHisAdapter, empLeaveWorkHisAdapter, employmentHisScheduleAdapter);

		// 管理状態と勤務予定Map
		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> mngStatusAndWScheMap = GetWorkScheduleByScheduleManagementService
				.getScheduleManagement(RequireImpl, param.listSid, period);


		List<nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto> listDtoCommon = scheduleWorkInfor.getDataScheduleOfWorkInfo(mngStatusAndWScheMap);
		
		List<DateInformation> dateInformation = kSU002Finder.getDateInformation(param.listSid.stream().map(e -> new EmployeeId(e)).collect(Collectors.toList()), period);

		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = listDtoCommon
				.stream()
				.map(m -> {
					List<String> sids = new ArrayList<>();
					sids.add(AppContexts.user().employeeId());
					GetDateInfoDuringThePeriodInput param1 = new GetDateInfoDuringThePeriodInput();
					param1.setGeneralDate(m.getDate());
					param1.setSids(sids);

					EditStateOfDailyAttdDto workTypeEditStatus = null;
					if (m.workTypeEditStatus != null) {
						workTypeEditStatus = new EditStateOfDailyAttdDto(m.workTypeEditStatus.getAttendanceItemId(), m.workTypeEditStatus.getEditStateSetting());
					}

					EditStateOfDailyAttdDto workTimeEditStatus = null;
					if (m.workTimeEditStatus != null) {
						workTimeEditStatus = new EditStateOfDailyAttdDto(m.workTimeEditStatus.getAttendanceItemId(), m.workTimeEditStatus.getEditStateSetting());
					}

					EditStateOfDailyAttdDto startTimeEditState = null;
					if (m.startTimeEditState != null) {
						startTimeEditState = new EditStateOfDailyAttdDto(m.startTimeEditState.getAttendanceItemId(), m.startTimeEditState.getEditStateSetting());
					}

					EditStateOfDailyAttdDto endTimeEditState = null;
					if (m.endTimeEditState != null) {
						endTimeEditState = new EditStateOfDailyAttdDto(m.endTimeEditState.getAttendanceItemId(), m.endTimeEditState.getEditStateSetting());
					}

					// Xửa lý theo hướng của anh Lai
					Integer startTime = m.startTime;
					Integer endTime = m.endTime;

					if (startTime != null && endTime != null) {
						if (startTime == 0 && endTime == 0){
							startTime = null;
							endTime = null;
						}
					}


					WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
							.employeeId(m.getEmployeeId())
							.date(m.getDate())
							.haveData(m.haveData)
							.achievements(null)
							.confirmed(m.confirmed)
							.needToWork(m.needToWork)
							.supportCategory(m.supportCategory)
							.workTypeCode(m.workTypeCode)
							.workTypeName(m.getWorkTypeNameKsu002())
							.workTypeEditStatus(workTypeEditStatus)
							.workTimeCode(m.getWorkTimeCode())
							.workTimeName(m.getWorkTimeNameKsu002())
							.workTimeEditStatus(workTimeEditStatus)
							.startTime(startTime)
							.startTimeEditState(startTimeEditState)
							.endTime(endTime)
							.endTimeEditState(endTimeEditState)
							.workHolidayCls(m.workHolidayCls)
							.dateInfoDuringThePeriod(dateInformation.stream().filter(c -> c.getYmd().equals(m.getDate())).findFirst().map(e -> new DateInfoDuringThePeriodDto(e)).orElse(new DateInfoDuringThePeriodDto()))
							.workTimeForm(m.workTimeForm)
							.build();
					return dto;
				}).collect(Collectors.toList());

		return listWorkScheduleWorkInfor;
	}

	@AllArgsConstructor
	private static class RequireWorkInforImpl implements WorkInformation.Require {

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private WorkTypeRepository workTypeRepo;

		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;

		@Inject
		private BasicScheduleService basicScheduleService;

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}
	}

	@AllArgsConstructor
	private static class RequireImpl implements GetWorkScheduleByScheduleManagementService.Require {

		private NestedMapCache<String, GeneralDate, WorkSchedule> workScheduleCache;
		private KeyDateHistoryCache<String, EmpEnrollPeriodImport> affCompanyHistByEmployeeCache;
		private KeyDateHistoryCache<String, EmploymentPeriodImported> employmentPeriodCache;
		private KeyDateHistoryCache<String, EmployeeLeaveJobPeriodImport> empLeaveJobPeriodCache;
		private KeyDateHistoryCache<String, EmpLeaveWorkPeriodImport> empLeaveWorkPeriodCache;
		private KeyDateHistoryCache<String, WorkingConditionItemWithPeriod> workCondItemWithPeriodCache;
		@Inject
		private EmpAffiliationInforAdapter empAffiliationInforAdapter;

		public RequireImpl(List<String> empIdList, DatePeriod period, WorkScheduleRepository workScheduleRepo,
				EmpComHisAdapter empComHisAdapter, WorkingConditionRepository workCondRepo,
				EmpLeaveHistoryAdapter empLeaveHisAdapter, EmpLeaveWorkHistoryAdapter empLeaveWorkHisAdapter,
				EmploymentHisScheduleAdapter employmentHisScheduleAdapter) {

			List<WorkSchedule> lstWorkSchedule = workScheduleRepo.getList(empIdList, period);
			workScheduleCache = NestedMapCache.preloadedAll(lstWorkSchedule.stream(),
					workSchedule -> workSchedule.getEmployeeID(), workSchedule -> workSchedule.getYmd());

			List<EmpEnrollPeriodImport> affCompanyHists = empComHisAdapter.getEnrollmentPeriod(empIdList, period);
			affCompanyHistByEmployeeCache = KeyDateHistoryCache.loaded(affCompanyHists.stream().collect(Collectors
					.toMap(h -> h.getEmpID(), h -> Arrays.asList(DateHistoryCache.Entry.of(h.getDatePeriod(), h)))));

			List<EmploymentPeriodImported> listEmploymentPeriodImported = employmentHisScheduleAdapter
					.getEmploymentPeriod(empIdList, period);
			List<EmploymentPeriodImported> listEmploymentPeriodImported1 = listEmploymentPeriodImported.stream()
					.filter( distinctByKey(p -> p.getEmpID()) )
					.collect( Collectors.toList() );
			employmentPeriodCache = KeyDateHistoryCache.loaded(listEmploymentPeriodImported1.stream().collect(Collectors
					.toMap(h -> h.getEmpID(), h -> Arrays.asList(DateHistoryCache.Entry.of(h.getDatePeriod(), h)))));

			List<EmployeeLeaveJobPeriodImport> empLeaveJobPeriods = empLeaveHisAdapter
					.getLeaveBySpecifyingPeriod(empIdList, period);
			empLeaveJobPeriodCache = KeyDateHistoryCache.loaded(empLeaveJobPeriods.stream().collect(Collectors
					.toMap(h -> h.getEmpID(), h -> Arrays.asList(DateHistoryCache.Entry.of(h.getDatePeriod(), h)))));

			List<EmpLeaveWorkPeriodImport> empLeaveWorkPeriods = empLeaveWorkHisAdapter.getHolidayPeriod(empIdList,
					period);
			empLeaveWorkPeriodCache = KeyDateHistoryCache.loaded(empLeaveWorkPeriods.stream().collect(Collectors
					.toMap(h -> h.getEmpID(), h -> Arrays.asList(DateHistoryCache.Entry.of(h.getDatePeriod(), h)))));

			List<WorkingConditionItemWithPeriod> listData = workCondRepo.getWorkingConditionItemWithPeriod(AppContexts.user().companyId(),empIdList, period);
			Map<String, List<WorkingConditionItemWithPeriod>> data = listData.stream().collect(Collectors.groupingBy(item ->item.getWorkingConditionItem().getEmployeeId()));
			workCondItemWithPeriodCache = KeyDateHistoryCache.loaded(createEntries(data));
		}

		public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
		{
			Map<Object, Boolean> map = new ConcurrentHashMap<>();
			return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}

		@Override
		public Optional<WorkSchedule> get(String employeeId, GeneralDate date) {
			Optional<WorkSchedule> result = workScheduleCache.get(employeeId, date);
			return result;
		}

		@Override
		public Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String sid, GeneralDate startDate) {
			Optional<EmpEnrollPeriodImport> data = affCompanyHistByEmployeeCache.get(sid, startDate);
			return data;
		}

		@Override
		public Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate baseDate) {
			Optional<WorkingConditionItemWithPeriod> data = workCondItemWithPeriodCache.get(employeeId, baseDate);
			return data.isPresent() ? Optional.of(data.get().getWorkingConditionItem()) : Optional.empty();
		}

		@Override
		public Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String sid, GeneralDate startDate) {
			Optional<EmployeeLeaveJobPeriodImport> data = empLeaveJobPeriodCache.get(sid, startDate);
			return data;
		}

		@Override
		public Optional<EmpLeaveWorkPeriodImport> specAndGetHolidayPeriod(String sid, GeneralDate startDate) {
			Optional<EmpLeaveWorkPeriodImport> data = empLeaveWorkPeriodCache.get(sid, startDate);
			return data;
		}

		@Override
		public Optional<EmploymentPeriodImported> getEmploymentHistory(String sid, GeneralDate startDate) {
			Optional<EmploymentPeriodImported> data = employmentPeriodCache.get(sid, startDate);
			return data;
		}

		@Override
		public List<EmpOrganizationImport> getEmpOrganization(GeneralDate baseDate, List<String> lstEmpId) {
			return empAffiliationInforAdapter.getEmpOrganization(baseDate, lstEmpId);
		}
	}
	
	private static Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>>  createEntries(Map<String, List<WorkingConditionItemWithPeriod>> data) {
		Map<String, List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>>> rs = new HashMap<>();
		data.forEach( (k,v) -> {
			List<DateHistoryCache.Entry<WorkingConditionItemWithPeriod>> s = v.stream().map(i->new DateHistoryCache.Entry<WorkingConditionItemWithPeriod>(i.getDatePeriod(),i)).collect(Collectors.toList()) ;
			rs.put(k, s);
		});
		return rs;
	}
}
