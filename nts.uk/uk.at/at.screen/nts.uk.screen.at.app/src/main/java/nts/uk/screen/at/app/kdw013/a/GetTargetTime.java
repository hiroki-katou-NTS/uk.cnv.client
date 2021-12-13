package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.screen.at.app.kdw013.query.GetApplicationData;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.残業申請・休出時間申請の対象時間を取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetTargetTime {

	@Inject
	private GetApplicationData applicationData;

	/**
	 * 
	 * @param sid                    対象者
	 * @param mode                   画面モード（入力モード(1)/確認モード(2)）
	 * @param integrationOfDailyList List<日別勤怠(Work)>
	 * @return List<残業休出時間>
	 */
	public List<OvertimeLeaveTime> get(String sid, int mode, List<IntegrationOfDaily> integrationOfDailyList) {
		List<OvertimeLeaveTime> overtimeLeaveTimes = new ArrayList<>();

		// 【条件】
		// 日別勤怠(Work)．勤怠時間．勤務時間．総労働時間．所定外時間．残業時間.isPresent AND 画面モード = 入力モード
		for (IntegrationOfDaily i : integrationOfDailyList) {

			// 残業時間
			// 1: <call>()
			if (i.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
					.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent() && mode == 1) {
				// 1.1: 残業合計時間の計算する(): 勤怠時間
				OverTimeOfDaily overTimeWork = i.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getOverTimeWork().get();

				// 勤怠時間
				int calOvertime = overTimeWork.calcTotalFrameTime().v();

				// 1.2: [残業合計時間 > 0]:取得する(対象者,申請種類.残業申請,日別勤怠(Work).年月日,事前事後区分.事後):List<申請>
				if (calOvertime > 0) {
					List<Application> lstApplication = applicationData.get(sid,
							ApplicationType.OVER_TIME_APPLICATION.value, i.getYmd(), PrePostAtr.POSTERIOR.value);

					// 1.3: [List<申請>.isEmpty]:create(日別勤怠(Work).年月日,残業休出区分.残業申請,残業合計時間)
					if (lstApplication.isEmpty()) {
						overtimeLeaveTimes.add(new OvertimeLeaveTime(i.getYmd(),
								OverTimeLeaveType.OVER_TIME_APPLICATION.value, calOvertime));
					}
				}

			}

			// 休出時間
			// 2: <call>()
			if (i.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
					.getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent() && mode == 1) {
				// 2.1: 休出合計時間():勤怠時間
				HolidayWorkTimeOfDaily holidayTime = i.getAttendanceTimeOfDailyPerformance().get()
						.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily()
						.getWorkHolidayTime().get();

				// 勤怠時間
				int calHolidayTime = holidayTime.calcTotalFrameTime().v();

				// 2.2: [休出合計時間 > 0]:取得する(対象者,申請種類.休出時間申請,日別勤怠(Work).年月日,事前事後区分.事後): List<申請>
				if (calHolidayTime > 0) {
					List<Application> lstApplication = applicationData.get(sid,
							ApplicationType.HOLIDAY_WORK_APPLICATION.value, i.getYmd(), PrePostAtr.POSTERIOR.value);

					// 2.3: [List<申請>.isEmpty]:create(日別勤怠(Work).年月日,残業休出区分.休日出勤申請,休出合計時間)
					if (lstApplication.isEmpty()) {
						overtimeLeaveTimes.add(new OvertimeLeaveTime(i.getYmd(),
								OverTimeLeaveType.HOLIDAY_WORK_APPLICATION.value, calHolidayTime));
					}
				}

			}
		}

		return overtimeLeaveTimes;
	}

}
