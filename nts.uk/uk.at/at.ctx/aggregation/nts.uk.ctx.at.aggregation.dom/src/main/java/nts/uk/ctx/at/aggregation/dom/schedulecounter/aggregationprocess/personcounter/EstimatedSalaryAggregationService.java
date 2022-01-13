package nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.OneMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceGettingService;
import nts.uk.ctx.at.aggregation.dom.common.ScheRecGettingAtr;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployeeGettingService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountList;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountValue;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.StepOfCriterionAmount;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.DailyAttendanceGroupingUtil;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.LaborCostsTotalizationService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 個人計の想定給与額カテゴリを集計する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.集計処理.個人計.個人計の想定給与額カテゴリを集計する
 * @author kumiko_otake
 */
@Stateless
public class EstimatedSalaryAggregationService {

	/**
	 * 月間想定給与額を集計する
	 * @param require require
	 * @param baseYM 基準年月
	 * @param closingDay 締め日
	 * @param dlyAtdList 日別勤怠リスト
	 * @return
	 */
	public static Map<EmployeeId, EstimatedSalary> aggregateByMonthly(Require require
			, YearMonth baseYM, DateInMonth closingDay, List<IntegrationOfDaily> dlyAtdList
	) {

		// 期間を決定する( 基準年月＋締め日による1か月間 )
		val period = new OneMonth( closingDay ).periodOf( baseYM );

		// 期間内の日別勤怠を集計する
		val filtered = dlyAtdList.stream()
				.filter( e -> period.contains( e.getYmd() ) )
				.collect(Collectors.toList());
		return EstimatedSalaryAggregationService.getAggregateResult( require, period.end(), true, filtered );

	}

	/**
	 * 累計想定給与額を集計する
	 * @param require require
	 * @param baseYM 基準年月
	 * @param closingDay 締め日
	 * @param empIds 社員IDリスト
	 * @return
	 */
	public static Map<EmployeeId, EstimatedSalary> aggregateByCumulatively(Require require
			, YearMonth baseYM, DateInMonth closingDay, List<EmployeeId> empIds
	) {

		// 期間を決定する( 1/1～基準年月/締め日 )
		val period = new DatePeriod( GeneralDate.dayInYear( baseYM.year(), 1 ), closingDay.dateOf( baseYM ) );

		// 集計を実行する
		val dlyAtdList = DailyAttendanceGettingService
				.get( require, empIds, period, ScheRecGettingAtr.SCHEDULE_WITH_RECORD )
				.get( ScheRecGettingAtr.SCHEDULE_WITH_RECORD );
		return EstimatedSalaryAggregationService.getAggregateResult( require, period.end(), false, dlyAtdList );

	}

	/**
	 * 年間想定給与額を集計する
	 * @param require require
	 * @param baseDate 基準日
	 * @param empIds 社員IDリスト
	 * @return
	 */
	public static Map<EmployeeId, EstimatedSalary> aggregateByYearly(Require require, GeneralDate baseDate, List<EmployeeId> empIds) {

		// 期間を決定する( 1/1～12/31 )
		val period = DatePeriod.years( 1, GeneralDate.dayInYear( baseDate.year(), 1 ) );

		// 集計を実行する
		val dlyAtdList = DailyAttendanceGettingService
				.get( require, empIds, period, ScheRecGettingAtr.SCHEDULE_WITH_RECORD )
				.get( ScheRecGettingAtr.SCHEDULE_WITH_RECORD );
		return EstimatedSalaryAggregationService.getAggregateResult( require, baseDate, false, dlyAtdList );

	}


	/**
	 * 集計結果を取得する
	 * @param require require
	 * @param baseDate 基準日
	 * @param isNeedMonthly 月間を取得するか
	 * @param dlyAtdList 日別勤怠リスト
	 * @return 想定給与額
	 */
	private static Map<EmployeeId, EstimatedSalary> getAggregateResult(Require require
			, GeneralDate baseDate, boolean isNeedMonthly, List<IntegrationOfDaily> dlyAtdList
	) {

		// 想定給与額を取得
		val results = EstimatedSalaryAggregationService.aggregate(dlyAtdList);

		// 想定給与額に対応する目安金額を取得
		return results.entrySet().stream()
				.collect(Collectors.toMap( Map.Entry::getKey, e -> {
					val stage = EstimatedSalaryAggregationService
							.getStepOfCriterionAmount( require, e.getKey(), baseDate, isNeedMonthly, e.getValue() );
					return new EstimatedSalary( e.getValue(), stage.getCriterionAmount(), stage.getBackground() );
				} ));

	}

	/**
	 * 想定給与額を集計する
	 * @param dlyAtdList 日別勤怠リスト
	 * @return 社員ごとの想定給与額
	 */
	private static Map<EmployeeId, BigDecimal> aggregate(List<IntegrationOfDaily> dlyAtdList) {

		// List<日別勤怠(Work)> → Map<社員ID, List<日別勤怠の勤怠時間>>
		val dlyAtdListByEmpId = DailyAttendanceGroupingUtil
				.byEmployeeIdWithoutEmpty( dlyAtdList, IntegrationOfDaily::getAttendanceTimeOfDailyPerformance );

		// Map<社員ID, List<日別勤怠の勤怠時間>> → Map<社員ID, BigDecimal>
		return dlyAtdListByEmpId.entrySet().stream()
				.collect(Collectors.toMap( Map.Entry::getKey
						, e -> LaborCostsTotalizationService
									.totalizeAmounts( Arrays.asList(AggregationUnitOfLaborCosts.TOTAL), e.getValue() )
									.get(AggregationUnitOfLaborCosts.TOTAL) ));

	}

	/**
	 * 目安金額の段階を取得する
	 * @param require Require
	 * @param empId 社員ID
	 * @param baseDate 基準日
	 * @param isNeedMonthly 月間を取得するか
	 * @param estimatedSalary 想定給与額
	 * @return 目安金額の段階
	 */
	private static StepOfCriterionAmount getStepOfCriterionAmount(Require require
			, EmployeeId empId, GeneralDate baseDate, boolean isNeedMonthly, BigDecimal estimatedSalary
	) {

		// 目安金額を取得
		val refferenceAmount = CriterionAmountForEmployeeGettingService.get( require, empId, baseDate );

		// 想定給与額から目安金額の段階を取得
		val list = (isNeedMonthly)
						? refferenceAmount.getMonthly()
						: refferenceAmount.getYearly();
		return list.getStepOfEstimateAmount( require, new CriterionAmountValue( estimatedSalary.intValue() ) );

	}



	public static interface Require
			extends DailyAttendanceGettingService.Require
				,	CriterionAmountForEmployeeGettingService.Require
				,	CriterionAmountList.Require {
	}


}
