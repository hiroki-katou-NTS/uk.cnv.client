/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getschedulesbyshift;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.AggregateScheduleDto;
import nts.uk.screen.at.app.ksu001.aggrerateschedule.ScreenQueryAggregateSchedule;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleShiftBaseResult;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.PlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryPlanAndActual;
import nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule.ScreenQueryWorkScheduleShift;

/**
 * @author laitv
 * <<ScreenQuery>> 予定・実績をシフトで取得する
 *
 */
@Stateless
public class GetScheduleActualOfShift {
	
	@Inject
	private ScreenQueryPlanAndActual screenQueryPlanAndActual;
	
	@Inject
	private ScreenQueryWorkScheduleShift screenQueryWorkScheduleShift;
	
	@Inject
	private ScreenQueryAggregateSchedule screenQueryAggrerateSchedule;
	

	/**
	 * 予定・実績をシフトで取得する（未発注）
	 *  UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD.Aa：シフト表示
	 * @param listShiftMasterNotNeedGet ・新たに取得する必要のないシフト一覧
	 * @param sids ・社員リスト　　　：List<社員ID>
	 * @param datePeriod ・期間　　　　　　：期間
	 * @param closeDate ・締め日　　　　　：日付
	 * @param getActualData ・実績も取得するか：boolean
	 * @param targetOrg ・対象組織　　　　：対象組織識別情報
	 * @param personalCounterOp ・個人計カテゴリ　：Optional<個人計カテゴリ>
	 * @param workplaceCounterOp ・職場計カテゴリ　：Optional<職場計カテゴリ>
	 */
	public SchedulesbyShiftDataResult getData(
			List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGet,
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
		WorkScheduleShiftBaseResult workScheduleShiftBaseResult =
				screenQueryWorkScheduleShift.create(
						listShiftMasterNotNeedGet,
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
						true); // シフト表示か = true
		
		Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle = new HashMap<ShiftMasterDto, Integer>();
		workScheduleShiftBaseResult.getMapShiftMasterWithWorkStyle().entrySet().forEach(i -> {
			Integer value = i.getValue().isPresent() ? i.getValue().get().value : null;
			mapShiftMasterWithWorkStyle.put(new ShiftMasterDto(i.getKey()), value);
		});
		
		return new SchedulesbyShiftDataResult(
				workScheduleShiftBaseResult.getListWorkScheduleShift(),
				mapShiftMasterWithWorkStyle,
				aggrerateSchedule.getAggreratePersonal(),
				aggrerateSchedule.getAggrerateWorkplace()
				);
	}
}
