package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.screen.at.app.ksu001.aggreratepersonaltotal.TotalTimesDto;

/**
 * 職場計を集計する
 * @author hoangnd
 *
 */
@Stateless
public class ScreenQueryAggregateWorkplaceTotal {
	
	@Inject
	private ScreenQueryLaborCostAndTime screenQueryLaborCostAndTime;
	
	@Inject
	private ScreenQueryAggregateNumberTimeWp screenQueryAggrerateNumberTime;
	
	@Inject
	private ScreenQueryAggregateNumberPeople screenQueryAggrerateNumberPeople;
	
	@Inject
	private ScreenQueryExternalBudgetPerformance screenQueryExternalBudgetPerformance;
	
	
	/**
	 * 
	 * @param targetOrg 対象組織識別情報
	 * @param workplaceCounterOp 職場計カテゴリ
	 * @param aggrerateintegrationOfDaily List<日別勤怠(Work)>
	 * @param datePeriod 期間
	 */
	public AggregateWorkplaceDto aggrerate(
			TargetOrgIdenInfor targetOrg,
			WorkplaceCounterCategory workplaceCounterOp,
			List<IntegrationOfDaily> aggrerateintegrationOfDaily,
			DatePeriod datePeriod
			) {
		
		AggregateWorkplaceDto output = new AggregateWorkplaceDto();
		// 職場計カテゴリ == 人件費・時間
		if (workplaceCounterOp == WorkplaceCounterCategory.LABOR_COSTS_AND_TIME) {
			// 1: 集計する(対象組織識別情報, 期間, List<日別勤怠(Work)>)
			Map<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>> laborCostAndTime = 
					screenQueryLaborCostAndTime.aggrerate(
													targetOrg,
													aggrerateintegrationOfDaily,
													datePeriod);
			
			output.setLaborCostAndTime(laborCostAndTime);
			
		}
		// 職場計カテゴリ == 回数集計
		else if (workplaceCounterOp == WorkplaceCounterCategory.TIMES_COUNTING) {
			//2: 集計する(List<日別勤怠(Work)>)
			Map<GeneralDate, Map<TotalTimesDto, BigDecimal>> timeCount = 
					screenQueryAggrerateNumberTime.aggrerate(aggrerateintegrationOfDaily);
			output.setTimeCount(timeCount);
		
		}
		// 職場計カテゴリ == 雇用人数 or 分類人数 or 職位人数
		else if (workplaceCounterOp == WorkplaceCounterCategory.EMPLOYMENT_PEOPLE
				|| workplaceCounterOp == WorkplaceCounterCategory.CLASSIFICATION_PEOPLE
				|| workplaceCounterOp == WorkplaceCounterCategory.POSITION_PEOPLE) {
			// 3: 集計する(年月日, List<日別勤怠(Work)>, 職場計カテゴリ)
			AggregateNumberPeopleDto aggrerateNumberPeople = 
					screenQueryAggrerateNumberPeople.aggrerate(
							datePeriod.end(),
							aggrerateintegrationOfDaily,
							workplaceCounterOp);
			output.setAggrerateNumberPeople(aggrerateNumberPeople);
			
		}
		// 職場計カテゴリ == 外部予算実績
		else if (workplaceCounterOp == WorkplaceCounterCategory.EXTERNAL_BUDGET) {
			//4: 取得する(対象組織識別情報, 期間)
			Map<GeneralDate, Map<ExternalBudgetDto, String>> externalBudget = 
			screenQueryExternalBudgetPerformance.aggrerate(
					targetOrg,
					datePeriod
					);
			
			output.setExternalBudget(externalBudget);
		}
		
		return output;
		
	}

}
