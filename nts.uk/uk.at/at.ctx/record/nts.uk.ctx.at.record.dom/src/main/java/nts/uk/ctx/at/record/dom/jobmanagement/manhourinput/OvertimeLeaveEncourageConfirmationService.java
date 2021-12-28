package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ws.rs.core.Application;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力.残業休出申請を促すか確認する
 * DS: 残業休出申請を促すか確認する
 * 
 * @author tutt
 */
@Stateless
public class OvertimeLeaveEncourageConfirmationService {

	/**
	 * 
	 * @param require
	 * @param cId        会社ID
	 * @param sId        社員ID
	 * @param inputDates 年月日
	 * @return List<促す対象申請>
	 */
	public static List<EncouragedTargetApplication> check(Require1 require1, Require2 require2, String cId, String sId,
			List<GeneralDate> inputDates) {

		// $締め期間 = require.社員に対応する締め期間を取得する(社員ID,年月日#今日())
		DatePeriod closingPeriod = require1.getPeriod(sId, GeneralDate.today());
		
		// $処理日リスト = 対象日リスト：filter $ >= $締め期間.開始日
		List<GeneralDate> dates = inputDates.stream().filter(f -> f.afterOrEquals((closingPeriod.start()))).collect(Collectors.toList());
		
		// if $処理日リスト.isEmpty()		
		// return List.Empty	
		if (dates.isEmpty()) {
			return new ArrayList<>();
		}
		
		// $勤務情報リスト = require.勤務情報を取得する(社員ID,$処理日リスト)	
		List<WorkInfoOfDailyPerformance> workInfos = require1.findByListDate(sId, dates);
		
		// $応援作業リスト = require.応援作業別勤怠時間帯を取得する(社員ID,$処理日リスト)
		List<OuenWorkTimeSheetOfDaily> ouenTasks = require1.get(sId, dates);
		
		// $促す対象申請リスト = List.Empty
		List<EncouragedTargetApplication> result = new ArrayList<>();
		
		// $処理日リスト：
		dates.stream().forEach(d -> {
			
			// $対象勤務情報 = $勤務情報リスト：filter $.年月日 == $
			WorkInfoOfDailyPerformance workInfo = workInfos.stream().filter(f -> f.getYmd().equals(d)).findAny().get();
			
			// $対象応援作業 = $応援作業リスト：filter $.年月日 == $
			OuenWorkTimeSheetOfDaily ouenWorkTimeSheet = ouenTasks.stream().filter(f -> f.getYmd().equals(d)).findAny().get();
			
			// if [prv-1] 申請を促す必要か($対象勤務情報,$対象応援作業)
			if (isNeedToEncourageApp(workInfo, ouenWorkTimeSheet)) {
				
				// $勤務種類 = $対象勤務情報.勤務情報.勤務情報.勤務種類コード
				WorkTypeCode workTypeCode = workInfo.getWorkInformation().getRecordInfo().getWorkTypeCode();
				
				// $申請種類 = require.勤務種類から促す申請を判断する(require,会社ID,社員ID,$,$勤務種類)
				Optional<ApplicationTypeShare> appTypeOpt = require1.toDecide(require2, cId, sId, d, workTypeCode);
				
				//if $申請種類.isPresent
					if (appTypeOpt.isPresent()) {
						
						// $促す対象申請リスト.add(促す対象申請#促す対象申請($,$申請種類)
						result.add(new EncouragedTargetApplication(d, appTypeOpt.get()));
					}
			}
		});
		
		return result;
	}

	/**
	 * [prv-1] 申請を促す必要か 始業終業時刻と応援作業時刻を比較して申請を促す必要か確認する
	 * 
	 * @param workInfo  日別実績の勤務情報
	 * @param timesheet 日別実績の応援作業別勤怠時間帯
	 * @return boolean
	 */
	private static boolean isNeedToEncourageApp(WorkInfoOfDailyPerformance workInfo, OuenWorkTimeSheetOfDaily timesheet) {

		// $基準開始 = 日別実績の勤務情報.始業終業時間帯：filter $.勤務NO == 1
		// map $.出勤
		TimeWithDayAttr attendance = workInfo.getWorkInformation().getScheduleTimeSheets().stream()
				.filter(f -> f.getWorkNo().v() == 1).map(m -> m.getAttendance()).findAny().orElse(null);

		// $基準終了 = 日別実績の勤務情報.始業終業時間帯：filter $.勤務NO == 1
		// map $.退勤
		TimeWithDayAttr leaveWork = workInfo.getWorkInformation().getScheduleTimeSheets().stream()
				.filter(f -> f.getWorkNo().v() == 1).map(m -> m.getLeaveWork()).findAny().orElse(null);

		// $比較開始 = 日別実績の応援作業別勤怠時間帯.応援時間帯： sort $.時間帯.開始.時刻 ASC
		// map $.時間帯.開始.時刻
		// first
		Optional<TimeWithDayAttr> start = timesheet.getOuenTimeSheet().stream()
				.sorted((a, b) -> a.getTimeSheet().getStart().get().getTimeWithDay().get().v()
						.compareTo(b.getTimeSheet().getStart().get().getTimeWithDay().get().v()))
				.map(m -> m.getTimeSheet().getStart().get().getTimeWithDay()).collect(Collectors.toList()).get(0);

		// $比較終了 = 日別実績の応援作業別勤怠時間帯.応援時間帯： sort $.時間帯.終了.時刻 ASC
		// map $.時間帯.終了.時刻
		// last
		Optional<TimeWithDayAttr> end = timesheet.getOuenTimeSheet().stream()
				.sorted((a, b) -> a.getTimeSheet().getEnd().get().getTimeWithDay().get().v()
						.compareTo(b.getTimeSheet().getEnd().get().getTimeWithDay().get().v()))
				.map(m -> m.getTimeSheet().getEnd().get().getTimeWithDay()).findFirst().orElse(Optional.empty());

		// if ($基準開始.isEmpty AND $比較開始.isPresent)
		// OR ($基準終了.isEmpty AND $比較終了.isPresent)
		// OR $基準開始 > $比較開始 OR $基準終了 < $比較終了
		// return true
		if ((attendance == null && start.isPresent()) || (leaveWork == null && end.isPresent())
				|| (attendance.v() > start.get().v() || leaveWork.v() < end.get().v())) {
			return true;
		}

		return false;
	}

	// ■Require
	public static interface Require1 {
		// [R-1] 社員に対応する締め期間を取得する
		// アルゴリズム.社員に対応する締め期間を取得する(社員ID,基準日)
		DatePeriod getPeriod(String employeeId, GeneralDate date);

		// [R-2] 勤務情報を取得する
		// 日別実績の勤務情報Repository.取得する(社員ID,年月日リスト)l
		List<WorkInfoOfDailyPerformance> findByListDate(String employeeId, List<GeneralDate> dates);

		// [R-3] 応援作業別勤怠時間帯を取得する
		// 日別実績の応援作業別勤怠時間帯Repository.取得する(社員ID,年月日リスト)
		List<OuenWorkTimeSheetOfDaily> get(String sId, List<GeneralDate> dates);

		// [R-4] 勤務種類から促す申請を判断する
		// 勤務種類から促す申請を判断するAdapter.判断する(require,会社ID,社員ID,申請日,勤務種類コード)
		Optional<ApplicationTypeShare> toDecide(Require2 require2, String cId, String sId, GeneralDate date, WorkTypeCode workTypeCode);
	}
	

	// ■Require
	public static interface Require2 {
		// [R-1] 勤務種類を取得する
		Optional<WorkType> findByPK(String companyId, String workTypeCd);

		// [R-2] 申請を取得する
		List<Application> getAllApplicationByAppTypeAndPrePostAtr(String employeeID, int appType, GeneralDate appDate, int prePostAtr);
	}
}
