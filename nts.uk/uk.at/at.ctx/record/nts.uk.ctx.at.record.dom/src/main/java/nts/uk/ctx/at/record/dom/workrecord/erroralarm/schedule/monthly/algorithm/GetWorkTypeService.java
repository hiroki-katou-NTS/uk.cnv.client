package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.勤務実績.勤務実績のエラーアラーム設定.アラームリスト.スケジュール日次・月次・年間.スケジュール月次のアラームリストのチェック条件.アルゴリズム.使用しない.特定属性の項目の予定を作成する.日数チェック条件をチェック.勤務種類コードを取得
 */
@Stateless
public class GetWorkTypeService {
	
	/**
	 * 勤務種類コードを取得
	 * 
	 * @param date 年月日
	 * @param lstWorkSchedule List＜勤務予定＞
	 * @param lstDaily List＜日別実績＞
	 * @return 勤務種類コード
	 */
	public ScheMonWorkTypeWorkTime getWorkTypeCode(GeneralDate date, List<WorkScheduleWorkInforImport> lstWorkSchedule, List<IntegrationOfDaily> lstDaily) {		
		// データを探す
		
		// ・勤務予定　＝　Input．List＜勤務予定＞から年月日　＝　ループ中の年月日を探す
		Optional<WorkScheduleWorkInforImport> workScheduleOpt = lstWorkSchedule.stream().filter(x -> x.getYmd().equals(date)).findFirst();
		
		// ・日別実績　＝　Input．List＜日別実績＞から年月日　＝　ループ中の年月日を探す
		Optional<IntegrationOfDaily> dailyOpt = lstDaily.stream().filter(x -> x.getYmd().equals(date)).findFirst();
		
		// 探したデータをチェック
		// 探した　勤務予定且つ日別実績がEmptyの場合
		if (!workScheduleOpt.isPresent() && !dailyOpt.isPresent()) {
			return null;
		}
		
		// 以外の場合
		// システム日付と比べる
		GeneralDate sysDate = GeneralDate.today();
		
		// システム日付＜＝ループ中年月日
		if (sysDate.beforeOrEquals(date)) {
			// 勤務種類コードをセット
			// 探した勤務予定　！＝　Empty　AND　探した勤務予定．勤怠時間　！＝　Empty
			//　勤務種類コード ＝　探した勤務予定．勤務情報．勤務情報．勤務種類コード
			// 就業時間帯コード　＝　探した勤務予定．勤務情報．勤務情報．就業時間帯コード
			return getWorkTypeCodeFromWorkSched(workScheduleOpt);			
		}
		
		// 探した日別実績　！＝　Empty　AND　探した日別実績．勤怠時間　！＝　Empty
		// 　・勤務種類コード　＝　探した日別実績．勤務情報．勤務情報．勤務種類コード
		// 探した日別実績　＝＝　Empty　AND　探した勤務予定　！＝　Empty　AND　探した勤務予定．勤怠時間　！＝　Empty
		// 　・勤務種類コード　＝　探した勤務予定．勤務情報．勤務情報．勤務種類コード
		if (!dailyOpt.isPresent()) {
			return getWorkTypeCodeFromWorkSched(workScheduleOpt);
		}
		
		IntegrationOfDaily daily = dailyOpt.get();
		if (!daily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			return null;
		}
		
		// 　・就業時間帯コード　＝　探した日別実績．勤務情報．勤務情報．就業時間帯コード
		String workTimeCode = daily.getWorkInformation().getRecordInfo().getWorkTimeCode() != null ? daily.getWorkInformation().getRecordInfo().getWorkTimeCode().v() : null;
		return ScheMonWorkTypeWorkTime.builder()
				.workTimeCode(Optional.ofNullable(workTimeCode))
				.workTypeCode(daily.getWorkInformation().getRecordInfo().getWorkTypeCode().v())
				.build();
	}
	
	/**
	 * 
	 * @param workScheduleOpt 勤務予定
	 * @return 勤務種類コード
	 */
	private ScheMonWorkTypeWorkTime getWorkTypeCodeFromWorkSched(Optional<WorkScheduleWorkInforImport> workScheduleOpt) {
		// 勤務種類コードをセット
		// 探した勤務予定　！＝　Empty　AND　探した勤務予定．勤怠時間　！＝　Empty
		if (!workScheduleOpt.isPresent()) {
			return null;
		}
		
		WorkScheduleWorkInforImport workSchedule = workScheduleOpt.get();
		if (!workSchedule.getOptAttendanceTime().isPresent()) {
			return null;
		}
		
		//　勤務種類コード＝　探した勤務予定．勤務情報．勤務情報．勤務種類コード
		return ScheMonWorkTypeWorkTime.builder()
				.workTimeCode(Optional.ofNullable(workSchedule.getWorkTime()))
				.workTypeCode(workSchedule.getWorkType())
				.build();
	}
}
