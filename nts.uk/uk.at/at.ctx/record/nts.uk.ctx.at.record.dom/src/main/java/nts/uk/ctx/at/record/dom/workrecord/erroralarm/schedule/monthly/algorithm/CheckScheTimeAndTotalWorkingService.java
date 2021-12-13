package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workrule.closure.PresentClosingPeriodImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.AttendanceTimeOfDailyAttendanceImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.workrule.workdeadline.algorithm.MonthIsBeforeThisMonthChecking;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト.スケジュール日次・月次・年間.スケジュール月次のアラームリストのチェック条件.アルゴリズム.使用しない.特定属性の項目の予定を作成する.時間チェック条件をチェック.予定時間＋総労働時間を計算
 * 
 */
@Stateless
public class CheckScheTimeAndTotalWorkingService {
	@Inject
	private MonthIsBeforeThisMonthChecking monthIsBeforeThisMonthChecking;
	
	/**
	 * 予定時間＋総労働時間をチェック
	 * 
	 * @param date 年月
	 * @param attendanceTimeOfMonthly 月別実績の勤怠時間
	 * @param presentClosingPeriod 現在の締め期間
	 * @param lstDaily List＜日別実績＞
	 * @param lstWorkSchedule List＜勤務予定＞
	 */
	public int getScheTimeAndTotalWorkingTime(
			YearMonth ym, 
			AttendanceTimeOfMonthly attendanceTimeOfMonthly, 
			PresentClosingPeriodImport presentClosingPeriod,
			List<IntegrationOfDaily> lstDaily, 
			List<WorkScheduleWorkInforImport> lstWorkSchedule) {
		int totalTime = 0;
		
		// 当月より前の月かチェック
		if (presentClosingPeriod != null) {
			boolean isBeforeThisMonth = monthIsBeforeThisMonthChecking.checkMonthIsBeforeThisMonth(ym,
					presentClosingPeriod.getProcessingYm());
			if (isBeforeThisMonth) {
				// Input．月別実績　 ＝＝ Empty　の場合
				if (attendanceTimeOfMonthly == null) {
					// 総労働　＝　０
					return 0;
				}
				
				// Input．月別実績　 != Empty の場合　＃119013
				// 総労働　を計算
				// 総労働　＝　Input．月別実績．勤怠時間．月の計算．総労働時間
				return attendanceTimeOfMonthly.getMonthlyCalculation().getTotalWorkingTime().v();
			}
		}
		
		// システム日付
		GeneralDate today = GeneralDate.today();
		
		// Input．年月の開始日から終了日までループする
		DatePeriod dPeriod = new DatePeriod(ym.firstGeneralDate(), ym.lastGeneralDate());
		List<GeneralDate> listDate = dPeriod.datesBetween();
		for(int day = 0; day < listDate.size(); day++) {
			GeneralDate exDate = listDate.get(day);
			
			// ・勤務予定　＝　Input．List＜勤務予定＞から年月日　＝　ループ中の年月日を探す
			Optional<WorkScheduleWorkInforImport> workScheduleOpt = lstWorkSchedule.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
			
			// ・日別実績　＝　Input．List＜日別実績＞から年月日　＝　ループ中の年月日を探す
			Optional<IntegrationOfDaily> dailyOpt = lstDaily.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
			
			// システム日付＜＝ループ中年月日
			if (today.beforeOrEquals(exDate)) {
				if (workScheduleOpt.isPresent() && workScheduleOpt.get().getOptAttendanceTime().isPresent()) {
					// 総労働　＋＝　探した勤務予定．勤怠時間．勤務時間．総労働時間．総労働時間
					AttendanceTimeOfDailyAttendanceImport attTimeOfDay = workScheduleOpt.get().getOptAttendanceTime().get();
					totalTime += attTimeOfDay.getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
				}
			} else {
				// 探した日別実績　！＝　Empty　AND　探した日別実績．勤怠時間　！＝　Empty
				//　－＞　総労働　＋＝　探した日別実績．勤怠時間．勤務時間．総労働時間．総労働時間
				if (dailyOpt.isPresent() && dailyOpt.get().getAttendanceTimeOfDailyPerformance().isPresent()) {
					AttendanceTimeOfDailyAttendance attTimeOfDaily = dailyOpt.get().getAttendanceTimeOfDailyPerformance().get();
					totalTime += attTimeOfDaily.getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime().v();
				}
				
				// 探した日別実績　＝＝　Empty　AND　探した勤務予定　！＝　Empty　AND　探した勤務予定．勤怠時間　！＝　Empty
				// 　－＞　総労働　＋＝　探した勤務予定．勤怠時間．勤務時間．総労働時間．総労働時間
				if (!dailyOpt.isPresent() && workScheduleOpt.isPresent() && workScheduleOpt.get().getOptAttendanceTime().isPresent()) {
					AttendanceTimeOfDailyAttendanceImport attTimeOfDay = workScheduleOpt.get().getOptAttendanceTime().get();
					totalTime += attTimeOfDay.getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
				}
			}
		}
		
		return totalTime;
	}
}
