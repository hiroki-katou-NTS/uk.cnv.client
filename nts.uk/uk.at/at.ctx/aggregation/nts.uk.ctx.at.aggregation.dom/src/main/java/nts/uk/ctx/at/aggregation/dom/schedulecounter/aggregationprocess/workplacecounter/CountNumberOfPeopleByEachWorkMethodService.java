package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfWorkMethod;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.DailyAttendanceGroupingUtil;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.NumberOfEmployeesByWorkMethodCountingService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * 職場計の勤務方法別人数カテゴリを集計する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.集計処理.職場計.職場計の勤務方法別人数カテゴリを集計する
 * @author dan_pv
 */
public class CountNumberOfPeopleByEachWorkMethodService {
	
	/**
	 * 就業時間帯別人数を取得する
	 * @param require
	 * @param targetOrg 対象組織
	 * @param period 期間
	 * @param scheduleList 予定日別勤怠リスト
	 * @param actualList 実績日別勤怠リスト
	 * @return
	 */
	public static Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<WorkTimeCode>> > getByWorkTime(
			Require require,
			TargetOrgIdenInfor targetOrg,
			DatePeriod period,
			List<IntegrationOfDaily> scheduleList,
			List<IntegrationOfDaily> actualList ) {
		
		val workingDayScheduleList = getOnlyWorkingDay( require, scheduleList );
		val workingDayActualList = getOnlyWorkingDay( require, actualList );
		
		return countByWorkMethod(
				require, 
				targetOrg, 
				period, 
				workingDayScheduleList, 
				workingDayActualList, 
				AggregationUnitOfWorkMethod.WORK_TIME, 
				workMethod -> new WorkTimeCode(workMethod) );
	} 
	
	/**
	 * シフト別人数を取得する	
	 * @param require
	 * @param targetOrg 対象組織
	 * @param period 期間
	 * @param scheduleList 予定日別勤怠リスト
	 * @param actualList 実績日別勤怠リスト
	 * @return
	 */
	public static Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<ShiftMasterCode>>> getByShift(
			Require require,
			TargetOrgIdenInfor targetOrg,
			DatePeriod period,
			List<IntegrationOfDaily> scheduleList,
			List<IntegrationOfDaily> actualList) {
		
		return countByWorkMethod(
				require, 
				targetOrg, 
				period, 
				scheduleList, 
				actualList, 
				AggregationUnitOfWorkMethod.SHIFT, 
				workMethod -> new ShiftMasterCode(workMethod) );
	} 
	
	/**
	 * 勤務方法別に集計する
	 * @param require
	 * @param targetOrg 対象組織
	 * @param period 期間
	 * @param scheduleList 予定日別勤怠リスト
	 * @param actualList 実績日別勤怠リスト
	 * @param unit 集計単位
	 * @param changeWorkMethodProcess 勤務方法変換処理
	 * @return
	 */
	private static <T> Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<T>> > countByWorkMethod(
			Require require,
			TargetOrgIdenInfor targetOrg,
			DatePeriod period,
			List<IntegrationOfDaily> scheduleList,
			List<IntegrationOfDaily> actualList,
			AggregationUnitOfWorkMethod unit,
			Function<String, T> changeWorkMethodProcess
			) {
		
		val scheduleNumberForEachDate = count(require, period, scheduleList, unit, changeWorkMethodProcess);
		val actualNumberForEachDate = count(require, period, actualList, unit, changeWorkMethodProcess);
		
		Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<T>>> result = new HashMap<>();
		period.datesBetween().stream()
			.forEach( date -> {
				
				Map<T, BigDecimal> scheduleNumberList = scheduleNumberForEachDate.getOrDefault(date, Collections.emptyMap() ); 
				Map<T, BigDecimal> actualNumberList = actualNumberForEachDate.getOrDefault(date, Collections.emptyMap() ); 
						
				List<NumberOfPeopleByEachWorkMethod<T>> value = mapping( Collections.emptyMap() , scheduleNumberList, actualNumberList);
				
				result.put(date, value);
			});
		return result;
	}
	
	/**
	 * カウントする
	 * @param require
	 * @param period 期間
	 * @param workList 日別勤怠リスト
	 * @param unit 集計単位
	 * @param changeWorkMethodProcess 勤務方法変換処理
	 * @return
	 */
	private static <T>  Map<GeneralDate, Map<T, BigDecimal>> count(
			Require require,
			DatePeriod period,
			List<IntegrationOfDaily> workList,
			AggregationUnitOfWorkMethod unit,
			Function<String, T> changeWorkMethodProcess ) {
		
		val filteredWorkList = workList.stream()
				.filter( work -> period.contains( work.getYmd() ) ) 
				.collect( Collectors.toList() );
		
		val workListForEachDate = DailyAttendanceGroupingUtil.byDateWithAnyItem( filteredWorkList, work -> work.getWorkInformation() );
		
		Map<GeneralDate, Map<T, BigDecimal>> result = new HashMap<>();
		workListForEachDate.forEach( (date, workInfoList) -> {
			// 勤務方法別の人数を集計する#集計する
			Map<String, BigDecimal> countResult = NumberOfEmployeesByWorkMethodCountingService.count(require, unit, workInfoList );
			
			Map<T, BigDecimal> numberOfEmpForEachWorkMethod = countResult.entrySet().stream()
					.collect(Collectors.toMap(
						entry -> changeWorkMethodProcess.apply( entry.getKey() ), 
						entry -> entry.getValue() ));
			
			result.put(date, numberOfEmpForEachWorkMethod);
		});
		
		return result;
	}
	
	/**
	 * マッピングする	
	 * @param planNumberList 計画人数リスト
	 * @param scheduleNumberList 予定人数リスト
	 * @param actualNumberList 実績人数リスト
	 * @return
	 */
	private static <T> List<NumberOfPeopleByEachWorkMethod<T>> mapping(
			Map<T, BigDecimal> planNumberList,
			Map<T, BigDecimal> scheduleNumberList,
			Map<T, BigDecimal> actualNumberList
			) {
		Set<T> workMethods = new HashSet<>();
		workMethods.addAll(planNumberList.keySet());
		workMethods.addAll(scheduleNumberList.keySet());
		workMethods.addAll(actualNumberList.keySet());
		
		return workMethods.stream()
			.map( workMethod -> {
				BigDecimal planNumber = planNumberList.getOrDefault(workMethod, BigDecimal.ZERO);
				BigDecimal scheduleNumber = scheduleNumberList.getOrDefault(workMethod, BigDecimal.ZERO);
				BigDecimal actualNumber = actualNumberList.getOrDefault(workMethod, BigDecimal.ZERO);
				
				return new NumberOfPeopleByEachWorkMethod<>( workMethod, planNumber, scheduleNumber, actualNumber );
			} ).collect(Collectors.toList());
		
	}
	
	/**
	 * 出勤系の日別勤怠のみ取得する
	 * @param require
	 * @param dailyWorks 日別勤怠リスト
	 * @return
	 */
	private static List<IntegrationOfDaily> getOnlyWorkingDay( Require require, List<IntegrationOfDaily> dailyWorks ){
		
		val workTypeCodes = dailyWorks.stream()
				.filter( dw -> dw.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().isPresent() )
				.map( dw -> dw.getWorkInformation().getRecordInfo().getWorkTypeCode() )
				.distinct()
				.collect(Collectors.toList());
		
		val workingDayWorkTypeCodes = require.getWorkTypes(workTypeCodes).stream()
				.filter( wt -> wt.isWorkingDay() )
				.map( wt -> wt.getWorkTypeCode() )
				.collect( Collectors.toList() );
		
		return dailyWorks.stream()
				.filter( dw -> workingDayWorkTypeCodes.contains( dw.getWorkInformation().getRecordInfo().getWorkTypeCode() ) )
				.collect( Collectors.toList() );
	}
	
	public static interface Require extends NumberOfEmployeesByWorkMethodCountingService.Require {
		
		/**
		 * 勤務種類を取得する
		 * @param workTypeCodes 勤務種類コードリスト
		 * @return
		 */
		List< WorkType> getWorkTypes( List<WorkTypeCode> workTypeCodes );
	}
	

}
