package nts.uk.screen.at.app.kdw013.query;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author sonnlb
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.工数入力目安時間帯を取得する.工数入力目安時間帯を取得する
 */
@Stateless
public class GetEstimatedTimeZones {
	
	@Inject
	private GetLastOverTimeApplication getLastOverTimeApplication;
	
	@Inject
	private BasicScheduleService basicScheduleService;
	
	@Inject
	private PredetemineTimeSettingRepository predTimeSetRepo;
	/**
	 * 
	 * @param inteDaiy 日別勤怠(Work)
	 * @return 入力目安時間帯
	 */
	public EstimatedTimeZone get(IntegrationOfDaily inteDaiy) {
		EstimatedTimeZone result = new EstimatedTimeZone();
		//入力目安時間帯．年月日 = 日別勤怠(Work)．年月日
		result.setYmd(inteDaiy.getYmd());
		
		//入力目安時間帯．休憩時間帯 = 日別勤怠(Work)．休憩時間帯．時間帯
		result.setBreakTimeSheets(inteDaiy.getBreakTime().getBreakTimeSheets());

		// 1. 日別勤怠(Work).出退勤.isPresent 出退勤の時間帯を返す() List<時間帯>
		
		inteDaiy.getAttendanceLeave().ifPresent(al -> {
			
			/**
			 * 入力目安時間帯．開始時刻 = 求めた「開始時刻」がなければ「時間帯(使用区分付き)．開始」をセットする
			 * 入力目安時間帯．終了時刻 = 求めた「終了時刻」がなければ「時間帯(使用区分付き)．終了」をセットする
			 * 取得した「時間帯(使用区分付き)」は「使用区分 = 使用する」の1個目を利用する
			 * 
			 */
			// get workNo 1
			al.getAttendanceLeavingWork(1).ifPresent(lw -> {
				result.setStartTime( lw.getAttendanceTime().map(x-> new TimeWithDayAttr(x.v())).orElse(null));
				result.setEndTime(lw.getLeaveTime().map(x-> new TimeWithDayAttr(x.v())).orElse(null));
			});			
		});
		// 2.取得する(社員ID, 年月日) 日別勤怠(Work).社員ID,日別勤怠(Work).年月日 Optional<残業申請>
		
		this.getLastOverTimeApplication.get(inteDaiy.getEmployeeId(), inteDaiy.getYmd()).ifPresent(oApp -> {
			
			/**
			 * 取得したList<時間帯>と残業申請から開始時刻と終了時刻を求める
			 * 
			 * 【比較対象】
			 * ・取得したList<時間帯>の1個目
			 * ・残業申請．勤務時間帯．時間帯
			 * 
			 * 【求め方】
			 * ・開始時刻：取得した「時間帯．開始時刻」と「残業申請．勤務時間帯．時間帯．開始時刻」の小さい方を開始時刻とする
			 * ・終了時刻：取得した「時間帯．終了時刻」と「残業申請．勤務時間帯．時間帯．終了時刻」の大きい方を終了時刻とする
			 */
			
			oApp.getWorkHoursOp().ifPresent(wh -> {
				wh.stream().mapToInt(x -> x.getTimeZone().getStartTime().v()).min().ifPresent(min -> {
					result.setStartTime(new TimeWithDayAttr(min));
				});
				
				wh.stream().mapToInt(x -> x.getTimeZone().getEndTime().v()).max().ifPresent(max -> {
					result.setEndTime(new TimeWithDayAttr(max));
				});
				//入力目安時間帯．残業時間帯 = 取得した「残業申請．勤務時間帯．時間帯」をセットする
				result.setOverTimeZones(wh.stream()
						.map(x -> x.getTimeZone())
						.collect(Collectors.toList()));
			});
			
			
		});
		
		
		//(求めた「開始時刻」.isEmpty OR 求めた「終了時刻」.isEmpty) AND 「日別勤怠(Work)．勤務情報．勤務情報．就業時間帯コード」.isPresent
		
		if (result.getStartTime() == null || result.getEndTime() == null) {
			inteDaiy.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().ifPresent(wtCd -> {
				// ・勤務種類コード = INPUT「日別勤怠(Work)．勤務情報．勤務情報．勤務種類コード」
				String workTypeCode = inteDaiy.getWorkInformation().getRecordInfo().getWorkTypeCode().v();

				// 3. call
				WorkStyle wkStyle = this.basicScheduleService.checkWorkDay(workTypeCode);

				// 出勤休日区分 <> １日休日系
				if (wkStyle != null && !wkStyle.equals(WorkStyle.ONE_DAY_REST)) {
					// 4. 取得する(就業時間帯コード)
					this.predTimeSetRepo.findByWorkTimeCode(AppContexts.user().companyId(), wtCd.v())
							.ifPresent(predSet -> {
								// 5. 所定時間設定.isPresent
								// 午前午後区分に応じた所定時間帯(午前午後区分) List<計算用時間帯>
								/**
								 * ※取得した「出勤休日区分」に応じて「午前午後区分」を渡す
								 * 「出勤休日区分」= 午前出勤系 ⇒ 「午前午後区分」= 午前
								 * 「出勤休日区分」= 午後出勤系 ⇒ 「午前午後区分」= 午後
								 * 「出勤休日区分」= １日出勤系 ⇒ 「午前午後区分」= 1日
								 */
								AmPmAtr atr = null;

								switch (wkStyle) {
								case ONE_DAY_WORK:
									atr = AmPmAtr.ONE_DAY;
									break;
								case MORNING_WORK:
									atr = AmPmAtr.AM;
									break;
								case AFTERNOON_WORK:
									atr = AmPmAtr.PM;
									break;
								}
								
								
								if (result.getStartTime() == null) {
									predSet.getTimezoneByAmPmAtr(atr).stream().mapToInt(x -> x.getStart().v()).min()
											.ifPresent(x -> {
												result.setStartTime(new TimeWithDayAttr(x));
											});
								}

								if (result.getEndTime() == null) {
									predSet.getTimezoneByAmPmAtr(atr).stream().mapToInt(x -> x.getEnd().v()).max()
											.ifPresent(x -> {
												result.setEndTime(new TimeWithDayAttr(x));
											});

								}
							});
				}
			});

		}
		
		return result;
	}

}
