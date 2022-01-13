package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import nts.arc.layer.app.cache.DateHistoryCache;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceMergingService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkClassificationAsAggregationTarget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkdayHolidayCounterService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkingTimeCounterService;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.CompanyEvent;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.CompanyEventRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.WorkplaceEvent;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.event.WorkplaceEventRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHolidayRepository;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.company.CompanySpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.company.CompanySpecificDateRepository;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.item.SpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.item.SpecificDateItemRepository;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.primitives.SpecificDateItemNo;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.workplace.WorkplaceSpecificDateItem;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.workplace.WorkplaceSpecificDateRepository;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AttendanceTimesForAggregation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.screen.at.app.ksu001.processcommon.ScreenQueryCreateWorkSchedule;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.PlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryPlanAndActual;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.MonthlyDataDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PeriodListPeriodDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PersonalSchedulesDataDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PlansResultsDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WeeklyDataDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkplaceHistoryWorkplaceHistoryItem;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author ThanhPV 
 */

@Stateless
public class KSU002Finder {
	
	@Inject
	private WeekRuleManagementRepo weekRuleManagementRepo;
	
	@Inject
	private ScreenQueryPlanAndActual screenQueryPlanAndActual;
	
	@Inject
	private ScreenQueryCreateWorkSchedule screenQueryCreateWorkSchedule;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private AffWorkplaceHistoryRepository affWorkplaceHistoryRepo;
	
	@Inject
	private AffWorkplaceHistoryItemRepository affWorkplaceHistoryItemRepo;
	
	/**
	 * @name 予定・実績を取得する
	 */
	public PlansResultsDto getPlansResults(DisplayInWorkInfoInput param) {
		
		//1:取得(期間, 曜日)
		PeriodListPeriodDto periodListPeriod = this.getPeriodList(param.getPeriod(), param.getStartWeekDate());
		
		//2:取得する(List<社員ID>, 期間, boolean)
		PlanAndActual planAndActual = screenQueryPlanAndActual.getPlanAndActual(param.listSid, periodListPeriod.datePeriod, param.actualData);
		
		//3:取得する()
		List<WorkScheduleWorkInforDto> workScheduleWorkInfor = screenQueryCreateWorkSchedule.get(planAndActual.getSchedule(), planAndActual.getDailySchedule(), param.actualData);
		
		//4:集計する(社員ID, 期間, List<期間>, int, int)
		PersonalSchedulesDataDto personalSchedulesData = this.getPersonalSchedules(new EmployeeId(param.listSid.get(0)), param.getPeriod(), periodListPeriod.datePeriodList, planAndActual.getSchedule(), planAndActual.getDailySchedule());
		
		PlansResultsDto result = new PlansResultsDto(personalSchedulesData, workScheduleWorkInfor);
		
		List<DateInformation> dateInformation = this.getDateInformation(param.listSid.stream().map(e -> new EmployeeId(e)).collect(Collectors.toList()), periodListPeriod.datePeriod);
		result.setWorkScheduleWorkInfor2(dateInformation, param.actualData);
		
		if(param.actualData) {
			result.setWorkScheduleWorkDaily(workScheduleWorkInfor);
		}
		
		
		return result;
	}
	
	
	/**
	 * @name 取得期間と週合計期間リストを取得する
	 */
	public PeriodListPeriodDto getPeriodList(DatePeriod period, DayOfWeek startWeekDate) {
		
		String cid = AppContexts.user().companyId();
		//週の管理
		Optional<WeekRuleManagement> weekRuleManagement = weekRuleManagementRepo.find(cid);
		
		if(!weekRuleManagement.isPresent()) {
			return new PeriodListPeriodDto(new ArrayList<DatePeriod>(), period);
		}else if(weekRuleManagement.get().getDayOfWeek().value != startWeekDate.value){
			return new PeriodListPeriodDto(new ArrayList<DatePeriod>(), period);
		}else {
			GeneralDate start = period.start();
			while (start.dayOfWeekEnum().value != startWeekDate.value) {
				start = start.addDays(-1);
			}
			GeneralDate end = period.end();
			end = end.addDays(1);
			while (end.dayOfWeekEnum().value != startWeekDate.value) {
				end = end.addDays(1);
			}
			end = end.addDays(-1);
			
			PeriodListPeriodDto result = new PeriodListPeriodDto();
			result.setDatePeriod(new DatePeriod(start, end));
			List<DatePeriod> datePeriodList = new ArrayList<DatePeriod>();
			GeneralDate startDateNew = start;
			while (startDateNew.before(end)) {
				datePeriodList.add(new DatePeriod(startDateNew, startDateNew.addDays(6)));
				startDateNew = startDateNew.addDays(7);
			}
			result.setDatePeriodList(datePeriodList);
			return result;
		}
			
	}
	
	/**
	 * @name 個人スケジュールを集計する
	 */
	public PersonalSchedulesDataDto getPersonalSchedules(EmployeeId employeeId, DatePeriod period, List<DatePeriod> datePeriodList, Map<EmployeeWorkingStatus, Optional<WorkSchedule>> schedule, Map<EmployeeWorkingStatus , Optional<IntegrationOfDaily>> dailySchedule) {
		//1:<call>
		/* $予定リスト = 管理状態と勤務予定Map.values: filter: $.isPresent map: $.日別勤怠Workに変換する() */
		List<IntegrationOfDaily> integrationOfDailyList = schedule.values().stream().filter(o -> o.isPresent()).map(c -> c.get().convertToIntegrationOfDaily()).collect(Collectors.toList());
		/* $実績リスト = 管理状態と勤務実績Map.values: filter: $.isPresent*/
		List<IntegrationOfDaily> integrationOfDailyListDaily = dailySchedule.values().stream().filter(c->c.isPresent()).map(c->c.get()).collect(Collectors.toList());
		
		//2: 集計する(社員ID, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
		MonthlyDataDto monthlyData = this.getMonthlyData(employeeId, period, integrationOfDailyList, integrationOfDailyListDaily);
		
		//3: 集計する(社員ID, List<期間>, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
		List<WeeklyDataDto> weeklyData = this.getweeklyData(employeeId, datePeriodList, integrationOfDailyList, integrationOfDailyListDaily);
		
		return new PersonalSchedulesDataDto(monthlyData, weeklyData);
	}
	
	/**
	 * @name 月の集計データを集計する
	 * @param integrationOfDailyList 予定リスト
	 * @param integrationOfDailyListDaily 実績リスト
	 */
	public MonthlyDataDto getMonthlyData(EmployeeId employeeId, DatePeriod period, List<IntegrationOfDaily> integrationOfDailyList, List<IntegrationOfDaily> integrationOfDailyListDaily) {
		
		/*1: マージする(List<社員ID>, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>) */
		List<IntegrationOfDaily> mergeToFlatList = DailyAttendanceMergingService.mergeToFlatList(Arrays.asList(employeeId), period, integrationOfDailyList, integrationOfDailyListDaily);
		
		/*2: 集計する(List<日別勤怠(Work)>)*/
		Map<EmployeeId, Map<AttendanceTimesForAggregation, BigDecimal>> workingTimeCounter = WorkingTimeCounterService.get(mergeToFlatList);
		//2.1: <call>
		//$当月の労働時間 = result.get(社員ID).get( 就業時間 )
		Double workingHoursMonth = workingTimeCounter.isEmpty() ? 0 : workingTimeCounter.get(employeeId).get(AttendanceTimesForAggregation.WORKING_WITHIN).doubleValue();
		
		/*3: 集計する(Require, List<日別勤怠(Work)>) */
		Map<EmployeeId, Map<WorkClassificationAsAggregationTarget, BigDecimal>> workdayHolidayCounter = WorkdayHolidayCounterService.count(new Require(AppContexts.user().companyId()), mergeToFlatList);
		//3.1: <call>()
		//$当月の出勤日数 = result.get(社員ID).get( 出勤 )
		Double numberWorkingDaysCurrentMonth = workdayHolidayCounter.isEmpty() ? 0 : workdayHolidayCounter.get(employeeId).get(WorkClassificationAsAggregationTarget.WORKING).doubleValue();
		//$当月の休日日数 = result.get(社員ID).get( 休日 )
		Double numberHolidaysCurrentMonth = workdayHolidayCounter.isEmpty() ? 0 : workdayHolidayCounter.get(employeeId).get(WorkClassificationAsAggregationTarget.HOLIDAY).doubleValue();
		
		return new MonthlyDataDto(workingHoursMonth, numberWorkingDaysCurrentMonth, numberHolidaysCurrentMonth);
	}
	
	/**
	 * @name 週の集計データを集計する
	 * @param employeeId
	 * @param datePeriodList
	 * @param integrationOfDailyList 実績予定リスト
	 * @param integrationOfDailyListDaily 実績リスト
	 */
	public List<WeeklyDataDto> getweeklyData(EmployeeId employeeId, List<DatePeriod> datePeriodList, List<IntegrationOfDaily> integrationOfDailyList, List<IntegrationOfDaily> integrationOfDailyListDaily) {
		
		List<WeeklyDataDto> result = new ArrayList<WeeklyDataDto>();
		int no = 0;
		//Loop 期間 in input.週合計期間
		for (DatePeriod period : datePeriodList) {
			
			//1.1: マージする(List<社員ID>, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
			List<IntegrationOfDaily> dailyWorks = DailyAttendanceMergingService.mergeToFlatList(Arrays.asList(employeeId), period, integrationOfDailyList, integrationOfDailyListDaily);
			
			//1.2: 集計する(List<日別勤怠(Work)>)
			Map<EmployeeId, Map<AttendanceTimesForAggregation, BigDecimal>> workingTimeCounter = WorkingTimeCounterService.get(dailyWorks);
			//1.2.1: <call>
			//$労働時間 = result.get(社員ID).get( 就業時間 )
			Double workingHoursMonth = workingTimeCounter.isEmpty() ? 0 : workingTimeCounter.get(employeeId).get(AttendanceTimesForAggregation.WORKING_WITHIN).doubleValue();
			
			//1.3: 集計する(Require, List<日別勤怠(Work)>)
			Map<EmployeeId, Map<WorkClassificationAsAggregationTarget, BigDecimal>> workdayHolidayCounter = WorkdayHolidayCounterService.count(new Require(AppContexts.user().companyId()), dailyWorks);
			
			//1.3.1: <call>()
			//$休日日数 = result.get(社員ID).get (休日）
			Double numberHolidaysCurrentMonth = workdayHolidayCounter.isEmpty() ? 0 : workdayHolidayCounter.get(employeeId).get(WorkClassificationAsAggregationTarget.HOLIDAY).doubleValue();
			
			result.add(new WeeklyDataDto( ++no, workingHoursMonth, numberHolidaysCurrentMonth));
		}
		
		return result;
	}
	
	//期間中の年月日情報を取得する
	public List<DateInformation> getDateInformation(List<EmployeeId> employeeId, DatePeriod period) {
		
		//1: <call>(List<社員ID>, 期間)
		WorkplaceHistoryWorkplaceHistoryItem workplaceHistoryWorkplaceHistoryItem = this.getWorkplaceHistoryWorkplaceHistoryItem(employeeId, period);
				
		//2: create(所属職場履歴項目(List))
		List<DateHistoryCache.Entry<AffWorkplaceHistoryItem>> list = new ArrayList<DateHistoryCache.Entry<AffWorkplaceHistoryItem>>();
		workplaceHistoryWorkplaceHistoryItem.getWorkplaceHistory().forEach(h ->{
			h.getHistoryItems().forEach(e ->{
				Optional<AffWorkplaceHistoryItem> affWorkplaceHistoryItem = workplaceHistoryWorkplaceHistoryItem.getWorkplaceHistoryItem().stream().filter(c -> c.getHistoryId().equals(e.identifier())).findFirst();
				if(affWorkplaceHistoryItem.isPresent()) {
					list.add(DateHistoryCache.Entry.of(e.span(), affWorkplaceHistoryItem.get()));
				}
			});
		});
		DateHistoryCache<AffWorkplaceHistoryItem> dateHistoryCache = new DateHistoryCache<AffWorkplaceHistoryItem>(list);
		
		RequireImpl require = new RequireImpl();
		
		List<DateInformation> result = new ArrayList<DateInformation>();
		period.datesBetween().forEach(date ->{
			Optional<AffWorkplaceHistoryItem> affWorkplaceHistoryItem = dateHistoryCache.get(date);
			if(affWorkplaceHistoryItem.isPresent()) {
				TargetOrgIdenInfor idenInfor = TargetOrgIdenInfor.creatIdentifiWorkplace(affWorkplaceHistoryItem.get().getWorkplaceId());

				result.add(DateInformation.create(require, date, idenInfor));
			}
		});
		
		return result;
	}
	
	//社員（List）と期間から職場履歴を取得する
	public WorkplaceHistoryWorkplaceHistoryItem getWorkplaceHistoryWorkplaceHistoryItem(List<EmployeeId> employeeId, DatePeriod period) {
		
		//ドメインモデル「所属職場履歴」を全て取得する 
		//パラメータ．期間を含む履歴項目の履歴IDを取得する
		List<AffWorkplaceHistory> workplaceHistory = affWorkplaceHistoryRepo.findByEmployeesWithPeriod(employeeId.stream().map(c->c.v()).collect(Collectors.toList()), period);
		
		List<String> historyIds = new ArrayList<>();
		workplaceHistory.forEach(wh -> historyIds.addAll(wh.getHistoryIds()));
		
		//ドメインモデル「所属職場履歴項目」を全て取得する 
		List<AffWorkplaceHistoryItem> workplaceHistoryItem = affWorkplaceHistoryItemRepo.findByHistIds(historyIds);
		
		return new WorkplaceHistoryWorkplaceHistoryItem(workplaceHistory, workplaceHistoryItem);
	}
	
	
	@RequiredArgsConstructor
	class Require implements WorkdayHolidayCounterService.Require{
		
		private final String companyId;
		
		@Override
		public Optional<WorkType> getWorkType(WorkTypeCode workTypeCd) {
			return workTypeRepository.findByPK(companyId, workTypeCd.v());
		}
		
	}
	
	@Inject
	private WorkplaceSpecificDateRepository workplaceSpecificDateRepo;
	@Inject
	private CompanySpecificDateRepository companySpecificDateRepo;
	@Inject
	private WorkplaceEventRepository workplaceEventRepo;
	@Inject
	private CompanyEventRepository companyEventRepo;
	@Inject
	private PublicHolidayRepository publicHolidayRepo;
	@Inject
	private SpecificDateItemRepository specificDateItemRepo;
	
	@RequiredArgsConstructor
	class RequireImpl implements DateInformation.Require {

		@Override
		public List<WorkplaceSpecificDateItem> getWorkplaceSpecByDate(String workplaceId, GeneralDate specificDate) {
			List<WorkplaceSpecificDateItem> data = workplaceSpecificDateRepo.getWorkplaceSpecByDate(workplaceId,
					specificDate);
			return data;
		}

		@Override
		public List<CompanySpecificDateItem> getComSpecByDate(GeneralDate specificDate) {
			List<CompanySpecificDateItem> data = companySpecificDateRepo
					.getComSpecByDate(AppContexts.user().companyId(), specificDate);
			return data;
		}

		@Override
		public Optional<WorkplaceEvent> findByPK(String workplaceId, GeneralDate date) {
			Optional<WorkplaceEvent> data = workplaceEventRepo.findByPK(workplaceId, date);
			return data;
		}

		@Override
		public Optional<CompanyEvent> findCompanyEventByPK(GeneralDate date) {
			Optional<CompanyEvent> data = companyEventRepo.findByPK(AppContexts.user().companyId(), date);
			return data;
		}

		@Override
		public Optional<PublicHoliday> getHolidaysByDate(GeneralDate date) {
			Optional<PublicHoliday> data = publicHolidayRepo.getHolidaysByDate(AppContexts.user().companyId(), date);
			return data;
		}

		@Override
		public List<SpecificDateItem> getSpecifiDateByListCode(List<SpecificDateItemNo> lstSpecificDateItemNo) {
			if (lstSpecificDateItemNo.isEmpty()) {
				return new ArrayList<>();
			}

			List<Integer> _lstSpecificDateItemNo = lstSpecificDateItemNo.stream().map(mapper -> mapper.v())
					.collect(Collectors.toList());
			List<SpecificDateItem> data = specificDateItemRepo.getSpecifiDateByListCode(AppContexts.user().companyId(),
					_lstSpecificDateItemNo);
			return data;
		}
	}
}
