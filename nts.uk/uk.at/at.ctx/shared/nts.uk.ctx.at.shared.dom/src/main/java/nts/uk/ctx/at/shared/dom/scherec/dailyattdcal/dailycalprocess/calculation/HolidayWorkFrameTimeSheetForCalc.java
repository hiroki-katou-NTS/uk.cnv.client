package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.MidNightTimeSheetForCalcList;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.someitems.BonusPayTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.midnighttimezone.MidNightTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktime.common.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimezoneNo;
import nts.uk.ctx.at.shared.dom.worktime.common.HDWorkTimeSheetSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.SettlementOrder;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkHolidayTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 計算用休出枠時間帯
 * @author keisuke_hoshina
 */
@Getter
public class HolidayWorkFrameTimeSheetForCalc extends ActualWorkingTimeSheet{
	
	//休出枠時間帯No
	private HolidayWorkFrameTime frameTime;
	
	//拘束時間として扱う
	private boolean TreatAsTimeSpentAtWork;
	
	//休出枠時間
	private EmTimezoneNo HolidayWorkTimeSheetNo; 
	
	//法定区分
	private Finally<StaturoryAtrOfHolidayWork> statutoryAtr;
	
	/**
	 * constructor
	 * @param timeSheet 時間帯(丸め付き)
	 * @param calculationTimeSheet 計算範囲
	 * @param deductionTimeSheets 控除項目の時間帯
	 * @param bonusPayTimeSheet 加給時間帯
	 * @param midNighttimeSheet 
	 * @param frameTime
	 * @param treatAsTimeSpentAtWork
	 * @param holidayWorkTimeSheetNo
	 */
	public HolidayWorkFrameTimeSheetForCalc(TimeSpanForDailyCalc timeSheet, TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets, List<BonusPayTimeSheetForCalc> bonusPayTimeSheet,
			List<SpecBonusPayTimeSheetForCalc> specifiedBonusPayTimeSheet, MidNightTimeSheetForCalcList midNighttimeSheet,
			HolidayWorkFrameTime frameTime, boolean treatAsTimeSpentAtWork, EmTimezoneNo holidayWorkTimeSheetNo,
			Finally<StaturoryAtrOfHolidayWork> statutoryAtr) {
		super(timeSheet, rounding, recorddeductionTimeSheets, deductionTimeSheets, bonusPayTimeSheet, specifiedBonusPayTimeSheet,
				midNighttimeSheet);
		this.frameTime = frameTime;
		TreatAsTimeSpentAtWork = treatAsTimeSpentAtWork;
		HolidayWorkTimeSheetNo = holidayWorkTimeSheetNo;
		this.statutoryAtr = statutoryAtr;
	}

	/**
	 * 固定勤務(休日出勤）
	 * @param attendanceLeave 出退勤
	 * @param holidayWorkTimeList 休出時間の時間帯設定
	 * @param todayWorkType 勤務種類
	 * @param bonuspaySetting 加給設定
	 * @param midNightTimeSheet 深夜時間帯
	 * @param deductionTimeSheet 控除時間帯
	 * @param commonSetting 就業時間帯の共通設定
	 * @param specificDateAttrSheets 特定日区分
	 * @return 計算用休出枠時間帯List
	 */
	public static List<HolidayWorkFrameTimeSheetForCalc> createHolidayTimeWorkFrame(
			TimeLeavingWork attendanceLeave,
			List<HDWorkTimeSheetSetting> holidayWorkTimeList,
			WorkType todayWorkType,
			Optional<BonusPaySetting> bonuspaySetting,
			MidNightTimeSheet midNightTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSetting,
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrSheets){
		
		List<HolidayWorkFrameTimeSheetForCalc> returnList = new ArrayList<>();
		for (HDWorkTimeSheetSetting holidayWorkTime: holidayWorkTimeList) {
			Optional<HolidayWorkFrameTimeSheetForCalc> holidayWorkFrameTimeSheet = createHolidayTimeWorkFrameTimeSheet(
					attendanceLeave,
					holidayWorkTime,
					todayWorkType,
					bonuspaySetting,
					midNightTimeSheet,
					deductionTimeSheet,
					commonSetting,
					specificDateAttrSheets);
			if (holidayWorkFrameTimeSheet.isPresent()){
				returnList.add(holidayWorkFrameTimeSheet.get());
			}
		}
		return returnList;
	}
	
	/**
	 * 計算用休出枠時間帯から休出枠時間帯へ変換する
	 * @return　休出枠時間帯
	 */
	public HolidayWorkFrameTimeSheet changeNotWorkFrameTimeSheet() {
		return new HolidayWorkFrameTimeSheet(this.getFrameTime().getHolidayFrameNo(),this.timeSheet.getTimeSpan());
	}

	/**
	 * 休出枠時間帯の作成
	 * @param attendanceLeave 出退勤
	 * @param holidayWorkSet 休出時間の時間帯設定
	 * @param today 勤務種類
	 * @param bonuspaySetting 加給設定
	 * @param midNightTimeSheet 深夜時間帯
	 * @param deductTimeSheet 控除時間帯
	 * @param commonSetting 就業時間帯の共通設定
	 * @param specificDateAttrSheets 特定日区分
	 * @return 休出枠時間帯
	 */
	public static Optional<HolidayWorkFrameTimeSheetForCalc> createHolidayTimeWorkFrameTimeSheet(
			TimeLeavingWork attendanceLeave,
			HDWorkTimeSheetSetting holidayWorkSet,
			WorkType today,
			Optional<BonusPaySetting> bonuspaySetting,
			MidNightTimeSheet midNightTimeSheet,
			DeductionTimeSheet deductTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSetting,
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrSheets) {

		// 計算範囲の取得
		Optional<TimeSpanForCalc> duplicateTimeSpan = holidayWorkSet.getTimezone().timeSpan().getDuplicatedWith(
				attendanceLeave.getTimespan());
		if (!duplicateTimeSpan.isPresent()) return Optional.empty();
		// 休日区分を取得
		HolidayAtr holidayAtr = today.getWorkTypeSetList().get(0).getHolidayAtr();
		// 休出枠Noを判断
		BreakFrameNo breakFrameNo = holidayWorkSet.decisionBreakFrameNoByHolidayAtr(holidayAtr);
		// 休出枠時間の作成
		HolidayWorkFrameTime holidayTimeFrame = new HolidayWorkFrameTime(
				new HolidayWorkFrameNo(breakFrameNo.v().intValue()),
				Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
				Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
				Finally.of(new AttendanceTime(0)));
		// 休出枠時間帯の作成
		HolidayWorkFrameTimeSheetForCalc holidayWorkFrameTimeSheet = new HolidayWorkFrameTimeSheetForCalc(
				new TimeSpanForDailyCalc(duplicateTimeSpan.get()),
				holidayWorkSet.getTimezone().getRounding(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				MidNightTimeSheetForCalcList.createEmpty(),
				holidayTimeFrame,
				false,
				new EmTimezoneNo(holidayWorkSet.getWorkTimeNo()),
				Finally.of(StaturoryAtrOfHolidayWork.deicisionAtrByHolidayAtr(holidayAtr)));
		// 控除時間帯の登録
		holidayWorkFrameTimeSheet.registDeductionList(ActualWorkTimeSheetAtr.HolidayWork, deductTimeSheet, commonSetting);
		// 加給時間帯を作成
		holidayWorkFrameTimeSheet.createBonusPayTimeSheet(bonuspaySetting, specificDateAttrSheets, deductTimeSheet);;
		// 深夜時間帯を作成
		holidayWorkFrameTimeSheet.createMidNightTimeSheet(midNightTimeSheet, commonSetting, deductTimeSheet);
		
		return Optional.of(holidayWorkFrameTimeSheet);
	}
	
	/**
	 * 残業時間帯時間枠に残業時間を埋める
	 * @param autoCalcSet 時間外の自動計算区分
	 * @return 残業時間枠時間帯クラス
	 */
	public HolidayWorkFrameTime calcOverTimeWorkTime(AutoCalRestTimeSetting autoCalcSet) {
		AttendanceTime holidayWorkTime;
		if(autoCalcSet.getLateNightTime().getCalAtr().isCalculateEmbossing()) {
			holidayWorkTime = new AttendanceTime(0);
		}
		else {
			holidayWorkTime = this.calcTotalTime();
		}
		return  new HolidayWorkFrameTime(this.frameTime.getHolidayFrameNo()
				,this.frameTime.getTransferTime()
				,Finally.of(TimeDivergenceWithCalculation.sameTime(holidayWorkTime))
				,this.frameTime.getBeforeApplicationTime());
	}
	
	/**
	 * 計算処理
	 * 休出時間の計算
	 * @param autoCalcSet 自動計算設定
	 * @return 休出時間
	 */
	public TimeDivergenceWithCalculation correctCalculationTime(AutoCalSetting autoCalcSet) {
		AttendanceTime time = autoCalcSet.getCalAtr().isCalculateEmbossing() ? this.calcTotalTime() : AttendanceTime.ZERO;
		AttendanceTime calcTime = this.calcTotalTime();
		return TimeDivergenceWithCalculation.createTimeWithCalculation(time, calcTime);
	}
	
//	/**
//	 *　指定条件の控除項目だけの控除時間
//	 * @param forcsList
//	 * @param atr
//	 * @return
//	 */
//	public AttendanceTime forcs(List<TimeSheetOfDeductionItem> forcsList,ConditionAtr atr,DeductionAtr dedAtr){
//		AttendanceTime dedTotalTime = new AttendanceTime(0);
//		val loopList = this.getDedTimeSheetByAtr(dedAtr, atr);
//		for(TimeSheetOfDeductionItem deduTimeSheet: loopList) {
//			if(deduTimeSheet.checkIncludeCalculation(atr)) {
//				dedTotalTime = dedTotalTime.addMinutes(deduTimeSheet.calcTotalTime().valueAsMinutes());
//			}
//		}
//		return dedTotalTime;
//	}
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
	/**
	 * 休出枠時間帯の作成（流動）
	 * @param todayWorkType 当日の勤務種類
	 * @param flowWorkSetting 流動勤務設定
	 * @param deductTimeSheet 控除時間帯
	 * @param itemsWithinCalc 計算範囲内控除
	 * @param holidayStartEnd 休出開始終了時刻
	 * @param bonusPaySetting 加給設定
	 * @param specDateAttr 日別実績の特定日区分
	 * @param midNightTimeSheet 深夜時間帯
	 * @param processingTimezone 流動休出時間帯
	 * @return 計算用休出枠時間帯
	 */
	public static HolidayWorkFrameTimeSheetForCalc createAsFlow(
			WorkType todayWorkType,
			FlowWorkSetting flowWorkSetting,
			DeductionTimeSheet deductTimeSheet,
			List<TimeSheetOfDeductionItem> itemsWithinCalc,
			TimeSpanForDailyCalc holidayStartEnd,
			Optional<BonusPaySetting> bonusPaySetting,
			Optional<SpecificDateAttrOfDailyAttd> specDateAttr,
			MidNightTimeSheet midNightTimeSheet,
			FlowWorkHolidayTimeZone processingTimezone) {
		
		// 就業時間帯の休出設定を取得　～　終了時刻の計算
		TimeWithDayAttr endTime = HolidayWorkFrameTimeSheetForCalc.calcEndTimeForFlow(
				processingTimezone,
				flowWorkSetting.getOffdayWorkTimezone().getLstWorkTimezone(),
				itemsWithinCalc,
				holidayStartEnd);
		// 法定区分を取得
		HolidayAtr holidayAtr = todayWorkType.getWorkTypeSetList().get(0).getHolidayAtr();
		// 休出枠Noをセット
		BreakFrameNo breakFrameNo = processingTimezone.getBreakFrameNoToHolidayAtr(holidayAtr);
		// 休出枠時間を作成
		HolidayWorkFrameTime frameTime = new HolidayWorkFrameTime(
				new HolidayWorkFrameNo(breakFrameNo.v().intValue()),
				Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
				Finally.of(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
				Finally.of(new AttendanceTime(0)));
		// 時間帯を作成
		HolidayWorkFrameTimeSheetForCalc holidayWorkFrameTimeSheet = new HolidayWorkFrameTimeSheetForCalc(
				new TimeSpanForDailyCalc(holidayStartEnd.getStart(), endTime),
				processingTimezone.getFlowTimeSetting().getRounding(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				MidNightTimeSheetForCalcList.createEmpty(),
				frameTime,
				false,
				new EmTimezoneNo(processingTimezone.getWorktimeNo()),
				Finally.of(StaturoryAtrOfHolidayWork.deicisionAtrByHolidayAtr(todayWorkType.getHolidayAtr().get())));
		// 控除時間帯の登録
		holidayWorkFrameTimeSheet.registDeductionList(ActualWorkTimeSheetAtr.HolidayWork, deductTimeSheet,
				Optional.of(flowWorkSetting.getCommonSetting()));
		// 加給時間帯を作成
		holidayWorkFrameTimeSheet.createBonusPayTimeSheet(bonusPaySetting, specDateAttr, deductTimeSheet);
		// 深夜時間帯を作成
		holidayWorkFrameTimeSheet.createMidNightTimeSheet(
				midNightTimeSheet, Optional.of(flowWorkSetting.getCommonSetting()), deductTimeSheet);
		
		return holidayWorkFrameTimeSheet;
	}
	
	/**
	 * 終了時刻の計算（流動）
	 * @param processingHolidayTimeZone 処理中の流動休出時間帯
	 * @param holidayTimezones 流動休出時間帯(List)
	 * @param timeSheetOfDeductionItems 控除項目の時間帯(List)
	 * @param holidayStartEnd 休出開始時刻
	 * @param leaveStampTime 退勤時刻
	 * @return 終了時刻
	 */
	private static TimeWithDayAttr calcEndTimeForFlow(
			FlowWorkHolidayTimeZone processingHolidayTimeZone,
			List<FlowWorkHolidayTimeZone> holidayTimezones,
			List<TimeSheetOfDeductionItem> timeSheetOfDeductionItems,
			TimeSpanForDailyCalc holidayStartEnd) {
		
		Optional<FlowWorkHolidayTimeZone> plusOneHolidayTimezone = holidayTimezones.stream()
				.filter(timezone -> timezone.getWorktimeNo().equals(processingHolidayTimeZone.getWorktimeNo()+1))
				.findFirst();
		
		TimeWithDayAttr endTime = TimeWithDayAttr.THE_PRESENT_DAY_0000;
		
		if(plusOneHolidayTimezone.isPresent()) {
			//休出枠の時間を計算する
			AttendanceTime holidayFrameTime = plusOneHolidayTimezone.get().getFlowTimeSetting().getElapsedTime().minusMinutes(
					processingHolidayTimeZone.getFlowTimeSetting().getElapsedTime().valueAsMinutes());
			
			//休出枠時間から終了時刻を計算する
			endTime = holidayStartEnd.getStart().forwardByMinutes(holidayFrameTime.valueAsMinutes());
			
			TimeSpanForDailyCalc timeSpan = new TimeSpanForDailyCalc(holidayStartEnd.getStart(), endTime);
			
			//控除時間分、終了時刻をズラす
			endTime = timeSpan.forwardByDeductionTime(timeSheetOfDeductionItems);
			
			//間にinput.退勤時刻があるか確認、input.退勤時刻に置き換え
			if(timeSpan.contains(holidayStartEnd.getEnd())) endTime = holidayStartEnd.getEnd();
		}
		else {
			endTime = holidayStartEnd.getEnd();
		}
		return endTime;
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 時間帯
	 * @param commonSet 就業時間帯の共通設定
	 * @return 休出枠時間帯
	 */
	public Optional<HolidayWorkFrameTimeSheetForCalc> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, Optional<WorkTimezoneCommonSet> commonSet) {
		Optional<TimeSpanForDailyCalc> duplicate = this.timeSheet.getDuplicatedWith(timeSpan);
		if(!duplicate.isPresent()) {
			return Optional.empty();
		}
		HolidayWorkFrameTimeSheetForCalc recreated = new HolidayWorkFrameTimeSheetForCalc(
				duplicate.get(),
				this.rounding.clone(),
				this.recordedTimeSheet.stream().map(r -> r.getAfterDeleteOffsetTime()).collect(Collectors.toList()),
				this.deductionTimeSheet.stream().map(d -> d.getAfterDeleteOffsetTime()).collect(Collectors.toList()),
				this.getDuplicatedBonusPayNotStatic(this.bonusPayTimeSheet, duplicate.get()),
				this.getDuplicatedSpecBonusPayzNotStatic(this.specBonusPayTimesheet, duplicate.get()),
				this.midNightTimeSheet.getDuplicateRangeTimeSheet(duplicate.get()),
				this.frameTime.clone(),
				this.TreatAsTimeSpentAtWork,
				new EmTimezoneNo(this.HolidayWorkTimeSheetNo.v().intValue()),
				this.statutoryAtr.isPresent() ? Finally.of(StaturoryAtrOfHolidayWork.valueOf(this.statutoryAtr.get().toString())) : Finally.empty());
		
		//控除時間帯の登録
		recreated.registDeductionList(ActualWorkTimeSheetAtr.HolidayWork, this.getCloneDeductionTimeSheet(), commonSet);
		
		return Optional.of(recreated);
	}
}
