package nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkplaborcostandtime.WkpLaborCostAndTimeDto;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.wkplaborcostandtime.WkpLaborCostAndTimeFinder;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.CountLaborCostTimeService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.LaborCostAggregationUnit;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.LaborCostAndTime;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.WorkplaceCounterLaborCostAndTime;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.shr.com.context.AppContexts;

/**
 * 人件費・時間を集計する
 * @author hoangnd
 *
 */
@Stateless
public class ScreenQueryLaborCostAndTime {
	
	@Inject
	private WkpLaborCostAndTimeFinder wkpLaborCostAndTimeFinder;
	
	@Inject
	private LaborCostBudgetRepository laborCostBudgetRepository;
	
	public Map<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>> aggrerate(
			TargetOrgIdenInfor targetOrg,
			List<IntegrationOfDaily> aggrerateintegrationOfDaily,
			DatePeriod datePeriod
			) {
		
		Require require = new Require(laborCostBudgetRepository);
		Map<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>> output = new HashMap<GeneralDate, Map<LaborCostAggregationUnitDto, BigDecimal>>();
		// 1: 取得する(会社ID)
		Optional<WorkplaceCounterLaborCostAndTime> wkpLaborCostAndTime;
		List<WkpLaborCostAndTimeDto> wkpLaborCostAndTimes = wkpLaborCostAndTimeFinder.findById();
		if (CollectionUtil.isEmpty(wkpLaborCostAndTimes)) {
			wkpLaborCostAndTime = Optional.empty();
		} else {
			Map<AggregationUnitOfLaborCosts, LaborCostAndTime> laborCostAndTimeList =
					wkpLaborCostAndTimes.stream()
					.collect(Collectors.toMap(
							e -> AggregationUnitOfLaborCosts.of(e.getLaborCostAndTimeType()),
							e -> e.getLaborCostAndTimeDtos().toDomain()));
			wkpLaborCostAndTime = Optional.of(WorkplaceCounterLaborCostAndTime.create(laborCostAndTimeList));
		}
		
		// 2: 集計する(Require, 対象組織識別情報, 期間, Map<人件費・時間の集計単位, 人件費・時間>, List<日別勤怠(Work)>)
		if (wkpLaborCostAndTime.isPresent()) {
			Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>> tallies = CountLaborCostTimeService.aggregate(
					require,
					targetOrg,
					datePeriod,
					wkpLaborCostAndTime.get().getLaborCostAndTimeList(),
					aggrerateintegrationOfDaily);
			
			output = tallies.entrySet()
				   .stream()
				   .collect(Collectors.toMap(
						   e -> e.getKey(),
						   e -> e.getValue()
						   			.entrySet()
						   			.stream()
						   			.collect(Collectors.toMap(
						   					z -> LaborCostAggregationUnitDto.fromDomain(z.getKey()),
						   					z -> z.getValue()))
						   
						   ));
		}
		
		return output;
	}
	

	@AllArgsConstructor
	private static class Require implements CountLaborCostTimeService.Require {

		@Inject
		private LaborCostBudgetRepository laborCostBudgetRepository;
		
		@Override
		public List<LaborCostBudget> getLaborCostBudgetList(TargetOrgIdenInfor targetOrg, DatePeriod datePeriod) {
			return laborCostBudgetRepository.get(AppContexts.user().companyId(), targetOrg, datePeriod);
		}
		
	}
}
