package nts.uk.screen.at.app.ksu001.aggrerateschedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceMergingService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.AggregatePersonalDto;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.ScreenQueryAggregatePersonal;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.AggregateWorkplaceDto;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.ScreenQueryAggregatePeopleMethod;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.ScreenQueryAggregateWorkplaceTotal;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.WorkInfo;
/**
 * スケジュール集計をする
 * @author hoangnd
 *
 */

@Stateless
public class ScreenQueryAggregateSchedule {

	@Inject 
	private ScreenQueryAggregatePersonal screenQueryAggreratePersonal;
	
	@Inject
	private ScreenQueryAggregateWorkplaceTotal screenQueryAggrerateWorkplaceTotal;
	
	@Inject
	private ScreenQueryAggregatePeopleMethod screenQueryAggreratePeopleMethod;
	
	/**
	 * 
	 * @param sids '・社員リスト　：List<社員ID>
	 * @param datePeriod 期間：期間
	 * @param closeDate  ・締め日：日付
	 * @param workSchedules 管理状態と勤務予定Map：Map<社員の予定管理状態, Optional<勤務予定>>
	 * @param integrationOfDailys 管理状態と勤務実績Map：Map<社員の予定管理状態, Optional<日別勤怠(Work)>>
	 * @param targetOrg 対象組織：対象組織識別情報
	 * @param personalCounterOp 集計したい個人計　　　：Optional<個人計カテゴリ>
	 * @param workplaceCounterOp 集計したい職場計　　　：Optional<職場計カテゴリ>
	 * @param isShiftDisplay シフト表示か　　　　　：boolean
	 * @return
	 */
	public AggregateScheduleDto aggrerateSchedule(
			List<String> sids,
			DatePeriod datePeriod,
			DateInMonth closeDate,
			Map<EmployeeWorkingStatus, Optional<WorkSchedule>> workSchedules,
			Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> integrationOfDailys,
			TargetOrgIdenInfor targetOrg,
			Optional<PersonalCounterCategory> personalCounterOp,
			Optional<WorkplaceCounterCategory> workplaceCounterOp,
			boolean isShiftDisplay
			) {
		AggregateScheduleDto output = new AggregateScheduleDto();
		// 1: 日別勤怠Workに変換する()
		List<IntegrationOfDaily> integrationOfDailySchedules = workSchedules.entrySet()
					 .stream()
					 .map(x -> x.getValue())
					 .filter(x -> x.isPresent())
					 .map(x -> x.get().convertToIntegrationOfDaily())
					 .collect(Collectors.toList());
		
		
		// 2: マージする(List<社員ID>, 期間, List<日別勤怠(Work)>, List<日別勤怠(Work)>)
		List<IntegrationOfDaily> aggrerateintegrationOfDaily = DailyAttendanceMergingService.mergeToFlatList(
				sids.stream()
					.map(EmployeeId::new)
					.collect(Collectors.toList()),
				datePeriod,
				integrationOfDailySchedules,
				integrationOfDailys.entrySet()
								   .stream()
								   .map(x -> x.getValue())
								   .filter(x -> x.isPresent())
								   .map(x -> x.get())
								   .collect(Collectors.toList()));
		
		if (personalCounterOp.isPresent()) {
			// 3: 集計する(個人計カテゴリ, List<日別勤怠(Work)>, 期間, 日付)
			AggregatePersonalDto aggreratePersonal = screenQueryAggreratePersonal.aggreratePersonal(
					personalCounterOp.get(),
					aggrerateintegrationOfDaily,
					datePeriod,
					closeDate				
					);
			
			output.aggreratePersonal = aggreratePersonal;
			
		}
		// 4:  集計したい職場計.isPresent
		if (workplaceCounterOp.isPresent()) {
			// 4.1: 集計する(対象組織識別情報, 職場計カテゴリ, List<日別勤怠(Work)>, 期間)
			AggregateWorkplaceDto aggrerateWorkplace = screenQueryAggrerateWorkplaceTotal.aggrerate(
					targetOrg,
					workplaceCounterOp.get(),
					aggrerateintegrationOfDaily,
					datePeriod
					);
			
			// 職場計カテゴリ == 就業時間帯別の利用人数
			if (workplaceCounterOp.get() == WorkplaceCounterCategory.WORKTIME_PEOPLE) {
				//4.2:  集計する(対象組織識別情報, 期間, List<日別勤怠(work)>, List<日別勤怠(work)>, boolean)
				Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkInfo>>> peopleMethod =
						screenQueryAggreratePeopleMethod.get(
									targetOrg,
									datePeriod,
									integrationOfDailySchedules,
									integrationOfDailys.entrySet()
													.stream()
													.map(x -> x.getValue())
													.filter(x -> x.isPresent())
													.map(x -> x.get())
													.collect(Collectors.toList()),
									isShiftDisplay);
				aggrerateWorkplace.setPeopleMethod(peopleMethod);
				
			}
			output.aggrerateWorkplace = aggrerateWorkplace;
		}
		
		
		
		return output;
					 
		
	}
}
