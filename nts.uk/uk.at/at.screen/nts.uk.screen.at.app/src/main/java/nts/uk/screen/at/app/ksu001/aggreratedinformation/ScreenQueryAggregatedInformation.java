package nts.uk.screen.at.app.ksu001.aggreratedinformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.schedule.app.find.budget.external.ExternalBudgetDto;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.AggregateScheduleDto;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.ScreenQueryAggregateSchedule;
import nts.uk.screen.at.app.ksu001.aggrerateworkplacetotal.ScreenQueryExternalBudgetPerformance;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.PlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryPlanAndActual;
/**
 * 集計情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */


@Stateless
public class ScreenQueryAggregatedInformation {

	@Inject
	private ScreenQueryExternalBudgetPerformance screenQueryExternalBudgetPerformance;
	
	@Inject
	private ScreenQueryPlanAndActual screenQueryPlanAndActual;
	
	@Inject
	private ScreenQueryAggregateSchedule screenQueryAggrerateSchedule;
	
	/**
	 * 
	 * @param sids 社員リスト
	 * @param datePeriod 期間
	 * @param closeDate 締め日
	 * @param isAchievement 実績も取得するか
	 * @param personalCounterOp 取得する個人計
	 * @param workplaceCounterOp 取得する職場計
	 * @param isShiftDisplay シフト表示か
	 * @return
	 */
	public AggregatedInformationDto get(
			List<String> sids,
			DatePeriod datePeriod,
			DateInMonth closeDate,
			Boolean isAchievement,
			TargetOrgIdenInfor targetOrgIdenInfor, // EA is not description
			Optional<PersonalCounterCategory> personalCounterOp,
			Optional<WorkplaceCounterCategory> workplaceCounterOp,
			boolean isShiftDisplay
			) {
		AggregatedInformationDto output = new AggregatedInformationDto();
		// 1: 取得する職場計.isPresent && 取得する職場計 == 外部予算実績
		if (workplaceCounterOp.map(x -> x == WorkplaceCounterCategory.EXTERNAL_BUDGET).orElse(false)) {
			
			// 取得する(対象組織識別情報, 期間)
			Map<GeneralDate, Map<ExternalBudgetDto, String>> externalBudget =
						screenQueryExternalBudgetPerformance.aggrerate(
							targetOrgIdenInfor,
							datePeriod);
			output.setExternalBudget(externalBudget);
		}
		//2: 取得する(List<社員ID>, 期間, boolean)
		PlanAndActual planAndActual =
					screenQueryPlanAndActual.getPlanAndActual(sids, datePeriod, isAchievement);
		
		
		// 3: 集計する(List<社員ID>, 期間, 日付, , , 対象組織識別情報, Optional<個人計カテゴリ>, Optional<職場計カテゴリ>, boolean)
		AggregateScheduleDto aggrerateSchedule = 
						screenQueryAggrerateSchedule.aggrerateSchedule(
								sids,
								datePeriod,
								closeDate,
								planAndActual.getSchedule(),
								planAndActual.getDailySchedule(),
								targetOrgIdenInfor,
								personalCounterOp,
								workplaceCounterOp,
								isShiftDisplay);
		
		output.setAggrerateSchedule(aggrerateSchedule);
		
		return output;
	}
}
