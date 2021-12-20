package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureIdPresentClosingPeriod;
import nts.uk.ctx.at.shared.dom.workrule.workdeadline.algorithm.MonthIsBeforeThisMonthChecking;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト.スケジュール日次・月次・年間.スケジュール月次のアラームリストのチェック条件.アルゴリズム.使用しない.特定属性の項目の予定を作成する.対比のチェック条件をチェック.基準時間比（通常）をチェック.合計就業時間を計算
 *
 */
@Stateless
public class CalMonWorkingTimeService {
	@Inject
	private MonthIsBeforeThisMonthChecking monthIsBeforeThisMonthChecking;
	
	/**
	 * 合計就業時間を計算
	 * @return 合計就業時間
	 *
	 */
	public Double calTotalWorkingTime(
			String cid, YearMonth ym, 
			List<WorkScheduleWorkInforImport> workSchedules, 
			List<IntegrationOfDaily> integrationDailys,
			ClosureIdPresentClosingPeriod closurePeriod,
			AttendanceTimeOfMonthly attendanceTimeOfMonthly) {
		// 当月より前の月かチェック
		boolean isBeforeThisMonth = monthIsBeforeThisMonthChecking.checkMonthIsBeforeThisMonth(ym, closurePeriod.getCurrentClosingPeriod().getProcessingYm());
		if (isBeforeThisMonth) {
			// Input．月別実績　＝＝　Emptyの場合　＃117183
			if (attendanceTimeOfMonthly == null) {
				return 0.0;
			}
			
			// 合計就業時間　を計算
			MonthlyCalculation monthlyCalculation = attendanceTimeOfMonthly.getMonthlyCalculation();
			// Input．月別実績．勤怠時間．月の計算．集計時間．就業時間．就業時間
			double workTime = monthlyCalculation.getAggregateTime().getWorkTime().getWorkTime().v().doubleValue();
			// Input．月別実績．勤怠時間．月の計算．実働時間．週割増合計時間
			double weeklyTotalPremiumTime = monthlyCalculation.getActualWorkingTime().getWeeklyTotalPremiumTime().v().doubleValue();
			// Input．月別実績．勤怠時間．月の計算．実働時間．月割増合計時間
			double monthlyTotalPremiumTime = monthlyCalculation.getActualWorkingTime().getMonthlyTotalPremiumTime().v().doubleValue();
			return workTime + weeklyTotalPremiumTime + monthlyTotalPremiumTime;
		}
		
		// システム日付
		GeneralDate sysDate = GeneralDate.today();
		// 合計
		Double totalTime = 0.0;
		
		// Input．年月の開始日から終了日までループ
		DatePeriod dPeriod = new DatePeriod(ym.firstGeneralDate(), ym.lastGeneralDate());
		List<GeneralDate> listDate = dPeriod.datesBetween();
		for(int day = 0; day < listDate.size(); day++) {
			GeneralDate exDate = listDate.get(day);
			
			// 勤務予定　＝　Input．List＜勤務予定＞から年月日　＝　ループ中の年月日を探す
			Optional<WorkScheduleWorkInforImport> workScheduleOpt = workSchedules.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
			// 日別実績　＝　Input．List＜日別実績＞から年月日　＝　ループ中の年月日を探す
			Optional<IntegrationOfDaily> dailyOpt = integrationDailys.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
			if (!workScheduleOpt.isPresent() && !dailyOpt.isPresent()) {
				continue;
			}
			
			// 総労働時間
			double actualTimeWorkSche = 0.0;
			if (workScheduleOpt.isPresent()) {
				if (workScheduleOpt.get().getOptAttendanceTime().isPresent()) {
					// 探した勤務予定．勤怠時間．勤務時間．総労働時間．総労働時間
					actualTimeWorkSche = workScheduleOpt.get().getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
				}
			}
			
			// システム日付＜＝ループ中年月日
			if (sysDate.beforeOrEquals(exDate)) {
				// 合計就業時間　を計算
				if (workScheduleOpt.isPresent() && workScheduleOpt.get().getOptAttendanceTime().isPresent()) {
					// 合計就業時間　＋＝　探した勤務予定．勤怠時間．勤務時間．総労働時間．総労働時間
					totalTime += actualTimeWorkSche;
				}
			} else {
				// 合計就業時間を計算
				if (dailyOpt.isPresent()) {
					if (dailyOpt.get().getAttendanceTimeOfDailyPerformance().isPresent()) {
						// 合計就業時間　＋＝　探した日別実績．勤怠時間．勤務時間．総労働時間．総労働時間
						totalTime += dailyOpt.get().getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getTotalTime().v().doubleValue();
					}
				} else {
					if (workScheduleOpt.isPresent() && workScheduleOpt.get().getOptAttendanceTime().isPresent()) {
						// 合計就業時間　＋＝　探した勤務予定．勤怠時間．勤務時間．総労働時間．総労働時間
						totalTime += actualTimeWorkSche;
					}
				}
			}
		}
		
		return totalTime;
	}
}
