/**
 * 
 */
package nts.uk.screen.at.app.ksu001.scheduleactualworkinfo;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.AggregateScheduleDto;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.ScreenQueryAggregateSchedule;
import nts.uk.screen.at.app.ksu001.processcommon.ScreenQueryCreateWorkSchedule;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.PlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryPlanAndActual;

/**
 * @author laitv
 * ScreenQuery : 予定・実績を勤務情報で取得する
 */
@Stateless
public class GetScheduleActualOfWorkInfo {
	
	
	@Inject
	private ScreenQueryPlanAndActual screenQueryPlanAndActual;
	
	@Inject
	private ScreenQueryAggregateSchedule screenQueryAggrerateSchedule;
	
	@Inject
	private ScreenQueryCreateWorkSchedule screenQueryCreateWorkSchedule;
	
	/**
	 * 予定・実績を勤務情報で取得する（未発注）
	 * 
	 * @param sids // 社員リスト
	 * @param datePeriod // 期間
	 * @param closeDate // 締め日
	 * @param isAchievement // 実績も取得するか
	 * @param targetOrg // 対象組織
	 * @param personalCounterOp // 個人計カテゴリ
	 * @param workplaceCounterOp // 職場計カテゴリ
	 * @return
	 */
	public ScheduleActualOfWorkOutput getDataScheduleAndAactualOfWorkInfo(
			List<String> sids,
			DatePeriod datePeriod,
			DateInMonth closeDate,
			boolean getActualData,
			TargetOrgIdenInfor targetOrg,
			Optional<PersonalCounterCategory> personalCounterOp,
			Optional<WorkplaceCounterCategory> workplaceCounterOp
			) {
		// 1: 取得する(List<社員ID>, 期間, boolean)
		PlanAndActual planAndActual = screenQueryPlanAndActual.getPlanAndActual(
				sids,
				datePeriod,
				getActualData);
		//2 取得する()
		List<WorkScheduleWorkInforDto> workScheduleWorkInforDtos = 
				screenQueryCreateWorkSchedule.get(
					planAndActual.getSchedule(),
					planAndActual.getDailySchedule(),
					getActualData);
		
		// 3 集計する(List<社員ID>, 期間, 日付, , , 対象組織識別情報, Optional<個人計カテゴリ>, Optional<職場計カテゴリ>, boolean)
		AggregateScheduleDto aggrerateSchedule =
				screenQueryAggrerateSchedule.aggrerateSchedule(
						sids,
						datePeriod,
						closeDate,
						planAndActual.getSchedule(),
						planAndActual.getDailySchedule(),
						targetOrg,
						personalCounterOp,
						workplaceCounterOp,
						false); // シフト表示か = false
		
		return new ScheduleActualOfWorkOutput(
				workScheduleWorkInforDtos,
				aggrerateSchedule.getAggreratePersonal(),
				aggrerateSchedule.getAggrerateWorkplace()
				);
	}
}
