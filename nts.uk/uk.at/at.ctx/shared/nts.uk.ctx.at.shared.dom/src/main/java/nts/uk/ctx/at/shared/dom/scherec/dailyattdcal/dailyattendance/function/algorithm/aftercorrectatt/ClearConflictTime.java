package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.aftercorrectatt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.ClearHolidayWorkTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.ClearOvertimeHour;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.algorithm.GetWorkTypeServiceShare;

/**
 * @author ThanhNX
 * 
 *         矛盾した時間をクリアする
 */
@Stateless
public class ClearConflictTime {

//	@Inject
//	private WorkTypeRepository workTypeRepo;

	@Inject
	private GetWorkTypeServiceShare getWorkTypeService;

	// 矛盾した時間をクリアする
	public IntegrationOfDaily process(IntegrationOfDaily domainDaily) {

		// 属性：編集状態を取得する
		List<EditStateOfDailyAttd> lstEditState = domainDaily.getEditState().stream()
				.filter(x -> x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_MYSELF
						&& x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_OTHER)
				.collect(Collectors.toList());

		String workTypeCode = domainDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v();
		// 勤務種類を取得
		// 1日半日出勤・1日休日系の判定
		Optional<WorkType>worktype = getWorkTypeService.getWorkType(workTypeCode);
		if(!worktype.isPresent())
			return domainDaily;
		AttendanceHolidayAttr attHolidayAttr = worktype.get().chechAttendanceDay().toAttendanceHolidayAttr();

		if (attHolidayAttr == AttendanceHolidayAttr.HOLIDAY) {
			// 残業時間のクリア
			if (domainDaily.getAttendanceTimeOfDailyPerformance().isPresent()
					&& domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
				ClearOvertimeHour.clearOvertime(
						domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
								.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get(),
						lstEditState);
			}

			if (domainDaily.getAttendanceTimeOfDailyPerformance().isPresent()
					&& domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
				// 休出時間のクリア
				ClearHolidayWorkTime.clearHolidaytime(
						domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
								.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get(),
						lstEditState);
			}
		} else if (attHolidayAttr == AttendanceHolidayAttr.FULL_TIME) {
			if (domainDaily.getAttendanceTimeOfDailyPerformance().isPresent()
					&& domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
				// 残業時間のクリア
				ClearOvertimeHour.clearOvertime(
						domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
								.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get(),
						lstEditState);
			}

		} else {

			// 休出時間のクリア
			if (domainDaily.getAttendanceTimeOfDailyPerformance().isPresent()
					&& domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
							.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
				// 休出時間のクリア
				ClearHolidayWorkTime.clearHolidaytime(
						domainDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
								.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get(),
						lstEditState);
			}
		}

		// 日別実績(Work)を返す
		return domainDaily;

	}

}
