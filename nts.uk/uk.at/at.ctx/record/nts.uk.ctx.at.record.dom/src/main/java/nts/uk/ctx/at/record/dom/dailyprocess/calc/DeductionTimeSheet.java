package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.enums.BreakType;
import nts.uk.ctx.at.record.dom.daily.DeductionTotalTime;
import nts.uk.ctx.at.record.dom.daily.LateTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.TimeWithCalculation;
import nts.uk.ctx.at.record.dom.daily.breaktimegoout.BreakTimeOfDaily;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.CommonRestSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.FixedRestCalculateMethod;
import nts.uk.ctx.at.shared.dom.worktime.common.RestClockManageAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.RestTimeOfficeWorkCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowFixedRestSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowRestCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowRestSet;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkRestTimezone;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDivision;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeMethodSet;

/**
 * 控除時間帯
 * @author keisuke_hoshina
 *
 */
@RequiredArgsConstructor
@Getter
public class DeductionTimeSheet {
	//控除用
	private final List<TimeSheetOfDeductionItem> forDeductionTimeZoneList;
	//計上用
	private final List<TimeSheetOfDeductionItem> forRecordTimeZoneList;
	
	
	public static DeductionTimeSheet createTimeSheetForFixBreakTime(WorkTimeMethodSet setMethod,RestClockManageAtr clockManage,
			OutingTimeOfDailyPerformance dailyGoOutSheet,TimeSpanForCalc oneDayRange,CommonRestSetting CommonSet, TimeLeavingOfDailyPerformance attendanceLeaveWork
			,FixedRestCalculateMethod fixedCalc,WorkTimeDivision workTimeDivision,FlowFixedRestSet noStampSet, FlowRestCalcMethod fluidSet
			, WorkTimeMethodSet workTimeMethodSet,Optional<FlowWorkRestTimezone> fluRestTime,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList) {
		//計上用
		val record = createDedctionTimeSheet(AcquisitionConditionsAtr.All,setMethod,clockManage,dailyGoOutSheet,oneDayRange,CommonSet, attendanceLeaveWork,fixedCalc,workTimeDivision,noStampSet, fluidSet, workTimeMethodSet,fluRestTime,breakTimeOfDailyList);
		//控除用
		val ded = createDedctionTimeSheet(AcquisitionConditionsAtr.ForDeduction,setMethod,clockManage,dailyGoOutSheet,oneDayRange,CommonSet, attendanceLeaveWork,fixedCalc,workTimeDivision,noStampSet, fluidSet, workTimeMethodSet,fluRestTime,breakTimeOfDailyList);
		return new DeductionTimeSheet(record,ded);
	}
	/**
	 * 控除時間帯の作成
	 * @param acqAtr 取得条件区分
	 * @param setMethod 就業時間帯の設定方法
	 * @param clockManage 休憩打刻の時刻管理設定区分
	 * @param dailyGoOutSheet 日別実績の外出時間帯
	 * @param oneDayRange 1日の計算範囲
	 * @param CommonSet 共通の休憩設定
	 * @param attendanceLeaveWork 日別実績の出退勤
	 * @param fixedCalc 固定休憩の計算方法
	 * @param workTimeDivision 就業時間帯勤務区分
	 * @param noStampSet 休憩未打刻時の休憩設定
	 * @param fluidSet 固定休憩の設定
	 * @return 控除時間帯
	 */
	private static List<TimeSheetOfDeductionItem> createDedctionTimeSheet(AcquisitionConditionsAtr acqAtr,WorkTimeMethodSet setMethod,RestClockManageAtr clockManage,
			OutingTimeOfDailyPerformance dailyGoOutSheet,TimeSpanForCalc oneDayRange,CommonRestSetting CommonSet, TimeLeavingOfDailyPerformance attendanceLeaveWork
								,FixedRestCalculateMethod fixedCalc,WorkTimeDivision workTimeDivision,FlowFixedRestSet noStampSet, FlowRestCalcMethod fluidSet
								, WorkTimeMethodSet workTimeMethodSet,Optional<FlowWorkRestTimezone> fluRestTime,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList){
		
		/*控除時間帯取得　控除時間帯リストへコピー*/
		List<TimeSheetOfDeductionItem> useDedTimeSheet = collectDeductionTimes(dailyGoOutSheet,oneDayRange,CommonSet
				,attendanceLeaveWork,fixedCalc,workTimeDivision,noStampSet,fluidSet,acqAtr
				,workTimeMethodSet,fluRestTime,breakTimeOfDailyList);
		
		/*重複部分補正処理*/
		useDedTimeSheet = new DeductionTimeSheetAdjustDuplicationTime(useDedTimeSheet).reCreate(setMethod, clockManage,workTimeDivision.getWorkTimeDailyAtr());
		
		/*控除でない外出削除*/
		List<TimeSheetOfDeductionItem> goOutDeletedList = new ArrayList<TimeSheetOfDeductionItem>();
		for(TimeSheetOfDeductionItem timesheet: useDedTimeSheet) {
			if(!(timesheet.getDeductionAtr().isGoOut() && timesheet.getGoOutReason().get().isPublicOrCmpensation())) {
				goOutDeletedList.add(timesheet);
			}
		}
		
		/*ここに丸め設定系の処理を置く*/
		
		return goOutDeletedList;
	}

	
	/**
	 * 控除時間に該当する項目を集め控除時間帯を作成する
	 * @param dailyGoOutSheet 
	 * @param oneDayRange 
	 * @param CommonSet 
	 * @param attendanceLeaveWork 
	 * @param fixedCalc 
	 * @param workTimeDivision 
	 * @param noStampSet 
	 * @param fluidSet 
	 * @param acqAtr 
	 * @return 
	 */
	public static List<TimeSheetOfDeductionItem> collectDeductionTimes(OutingTimeOfDailyPerformance dailyGoOutSheet,TimeSpanForCalc oneDayRange,CommonRestSetting CommonSet
										, TimeLeavingOfDailyPerformance attendanceLeaveWork,FixedRestCalculateMethod fixedCalc,WorkTimeDivision workTimeDivision,FlowFixedRestSet noStampSet
										, FlowRestCalcMethod fluidSet, AcquisitionConditionsAtr acqAtr 
										, WorkTimeMethodSet workTimeMethodSet,Optional<FlowWorkRestTimezone> fluRestTime,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList
										) {
		List<TimeSheetOfDeductionItem> sheetList = new ArrayList<TimeSheetOfDeductionItem>(); 
		/*休憩時間帯取得*/
		sheetList.addAll(getBreakTimeSheet(workTimeDivision, fixedCalc, noStampSet, fluidSet,breakTimeOfDailyList, dailyGoOutSheet));
		/*外出時間帯取得*/
		sheetList.addAll(dailyGoOutSheet.removeUnuseItemBaseOnAtr(acqAtr,workTimeMethodSet,fluRestTime,noStampSet,fluidSet));
		/*育児時間帯を取得*/
		
		
		/*ソート処理*/
		sheetList.stream().sorted((first,second) -> first.calcrange.getStart().compareTo(second.calcrange.getStart()));
		/*計算範囲による絞り込み*/
		List<TimeSheetOfDeductionItem> reNewSheetList = refineCalcRange(sheetList,oneDayRange,CommonSet.getCalculateMethod()
				, attendanceLeaveWork);
		return reNewSheetList;
		
	}
	

	/**
	 * 計算範囲による絞り込みを行うためのループ
	 * @param dedTimeSheets 控除項目の時間帯
	 * @param oneDayRange　1日の範囲
	 * @return 控除項目の時間帯リスト
	 */
	public static List<TimeSheetOfDeductionItem> refineCalcRange(List<TimeSheetOfDeductionItem> dedTimeSheets,TimeSpanForCalc oneDayRange,RestTimeOfficeWorkCalcMethod calcMethod
												,TimeLeavingOfDailyPerformance attendanceLeaveWork) {
		List<TimeSheetOfDeductionItem> sheetList = new ArrayList<TimeSheetOfDeductionItem>(); 
		for(TimeSheetOfDeductionItem timeSheet: dedTimeSheets){
			switch(timeSheet.getDeductionAtr()) {
			case CHILD_CARE:
			case GO_OUT:
				Optional<TimeSpanForCalc> duplicateGoOutSheet = oneDayRange.getDuplicatedWith(timeSheet.calcrange);
				if(duplicateGoOutSheet.isPresent()) {
						/*ここで入れる控除、加給、特定日、深夜は duplicateGoOutSheetと同じ範囲に絞り込む*/
						sheetList.add(TimeSheetOfDeductionItem.createTimeSheetOfDeductionItemAsFixed(
																						  timeSheet.timeSheet
																						, duplicateGoOutSheet.get()
																						, timeSheet.recordedTimeSheet
																						, timeSheet.deductionTimeSheet
																						, timeSheet.bonusPayTimeSheet
																						, timeSheet.specBonusPayTimesheet
																						, timeSheet.midNightTimeSheet
																						, timeSheet.getGoOutReason()
																						, timeSheet.getBreakAtr()
																						, timeSheet.getDeductionAtr()));
						
				}
				break;
			case BREAK:
			
				List<TimeSpanForCalc> duplicateBreakSheet = timeSheet.getBreakCalcRange(attendanceLeaveWork.getTimeLeavingWorks(),calcMethod,oneDayRange.getDuplicatedWith(timeSheet.calcrange));
				if(!duplicateBreakSheet.isEmpty())
				{
					duplicateBreakSheet.forEach(tc -> {
						/*ここで入れる控除、加給、特定日、深夜は duplicateGoOutSheetと同じ範囲に絞り込む*/
						sheetList.add(TimeSheetOfDeductionItem.createTimeSheetOfDeductionItemAsFixed(
																				  timeSheet.getTimeSheet()
																				, timeSheet.calcrange
																				, timeSheet.recordedTimeSheet
																				, timeSheet.deductionTimeSheet
																				, timeSheet.bonusPayTimeSheet
																				, timeSheet.specBonusPayTimesheet
																				, timeSheet.midNightTimeSheet
																				, timeSheet.getGoOutReason()
																				, timeSheet.getBreakAtr()
																				, timeSheet.getDeductionAtr()));
					});
				}
				break;
			default:
				throw new RuntimeException("unknown deductionAtr:" + timeSheet.getDeductionAtr());
			}
		}
		return sheetList;
	}

	
	/**
	 * 全控除項目の時間帯の合計を算出する
	 * @return 控除時間
	 */
	public AttendanceTime calcDeductionAllTimeSheet(DeductionAtr dedAtr,TimeSpanForCalc workTimeSpan) {
		List<TimeSheetOfDeductionItem> duplicatitedworkTime = getCalcRange(workTimeSpan);
		AttendanceTime sumTime = new AttendanceTime(0);
		
		/*stream.collect.summingInt()*/
		for(TimeSheetOfDeductionItem dedItem :duplicatitedworkTime) {
			sumTime = sumTime.addMinutes(dedItem.calcTotalTime().valueAsMinutes());
		}
		return sumTime;
	}
	
	/**
	 * 休憩時間帯の合計時間を算出する
	 * @param deductionTimeSheetList
	 * @return
	 */
	public AttendanceTime calcDeductionTotalTime(List<TimeSheetOfDeductionItem> deductionItemTimeSheetList) {
		AttendanceTime totalTime = new AttendanceTime(0);
		for(TimeSheetOfDeductionItem deductionItemTimeSheet: deductionItemTimeSheetList) {
			totalTime.addMinutes(deductionItemTimeSheet.calcTotalTime().valueAsMinutes());
		}
		return totalTime;
	}
	
//	/**
//	 * 引数の時間帯に重複する休憩時間帯の合計時間（分）を返す
//	 * @param baseTimeSheet
//	 * @return
//	 */
//	public int sumBreakTimeIn(TimeSpanForCalc baseTimeSheet) {
//		return this.breakTimeSheet.sumBreakTimeIn(baseTimeSheet);
//	}
//	
	/**
	 * 算出された休憩時間の合計を取得する
	 * @param dedAtr
	 * @return
	 */
	public DeductionTotalTime getTotalBreakTime(DeductionAtr dedAtr) {
		DeductionTotalTime dedTotalTime;
		switch(dedAtr) {
		case Appropriate:
			dedTotalTime = getDeductionTotalTime(forDeductionTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isBreak()).collect(Collectors.toList()));
			break;
		case Deduction:
			dedTotalTime = getDeductionTotalTime(forRecordTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isBreak()).collect(Collectors.toList()));
			break;
		default:
			throw new RuntimeException("unknown DeductionAtr" + dedAtr);
		}
		return dedTotalTime;
	}
	
	/**
	 * 算出された外出時間の合計を取得する
	 * @param dedAtr
	 * @return
	 */
	public List<DeductionTotalTime> getTotalGoOutTime(DeductionAtr dedAtr) {
		List<DeductionTotalTime> dedTotalTimeList = new ArrayList<>();
		switch(dedAtr) {
		case Appropriate:
			dedTotalTimeList.add(getDeductionTotalTime(forDeductionTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isPrivate())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forDeductionTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isCompensation())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forDeductionTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isPublic())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forDeductionTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isUnion())
																						.collect(Collectors.toList())));
			break;
		case Deduction:
			dedTotalTimeList.add(getDeductionTotalTime(forRecordTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isPrivate())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forRecordTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isCompensation())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forRecordTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isPublic())
																						.collect(Collectors.toList())));
			dedTotalTimeList.add(getDeductionTotalTime(forRecordTimeZoneList.stream().filter(tc -> tc.getDeductionAtr().isGoOut())
																						.filter(tc -> tc.getGoOutReason().get().isUnion())
																						.collect(Collectors.toList())));
			break;
		default:
			throw new RuntimeException("unknown DeductionAtr" + dedAtr);
		}
		return dedTotalTimeList;
	}
	

	/**
	 * 法定内・外、相殺時間から合計時間算出
	 * @param deductionTimeSheetList　
	 * @return
	 */
	public DeductionTotalTime getDeductionTotalTime(List<TimeSheetOfDeductionItem> deductionTimeSheetList) {
		AttendanceTime statutoryTotalTime         = calcDeductionTotalTime(deductionTimeSheetList.stream()
																					  //.filter(tc -> tc)　一時的に
																					  .collect(Collectors.toList()));
		AttendanceTime excessOfStatutoryTotalTime = calcDeductionTotalTime(deductionTimeSheetList.stream()
																					  //.filter(tc -> tc.getWithinStatutoryAtr().isExcessOfStatutory()) 一時的に
																					  .collect(Collectors.toList()));
		return DeductionTotalTime.of(TimeWithCalculation.sameTime(statutoryTotalTime.addMinutes(excessOfStatutoryTotalTime.valueAsMinutes()))
									,TimeWithCalculation.sameTime(statutoryTotalTime)
									,TimeWithCalculation.sameTime(excessOfStatutoryTotalTime));
	}
	
	
	/**
	 * 計算を行う範囲に存在する控除時間帯の抽出
	 * @param workTimeSpan 計算範囲
	 * @return　計算範囲内に存在する控除時間帯
	 */
	public List<TimeSheetOfDeductionItem> getCalcRange(TimeSpanForCalc workTimeSpan){
		return forDeductionTimeZoneList.stream().filter(tc -> workTimeSpan.contains(tc.calcrange.getSpan())).collect(Collectors.toList());
	}	
	
	/**
	 * 法定内区分を法定外へ変更する
	 * @return　法定内区分変更後の控除時間帯
	 */
	public List<TimeSheetOfDeductionItem> replaceStatutoryAtrToExcess() {
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		for(TimeSheetOfDeductionItem deductionTimeSheet : forDeductionTimeZoneList) {
			returnList.add(deductionTimeSheet.createWithExcessAtr());
		}
		return returnList;
	}
	
	/**
	 * 流動休憩開始までの間にある外出分、休憩をずらす
	 */
	public void includeUntilFluidBreakTimeStart() {
		
	}

	/**
	 * 休憩時間帯を作成する
	 * @return 休憩時間帯
	 */
	
	public static List<TimeSheetOfDeductionItem> getBreakTimeSheet(WorkTimeDivision workTimeDivision,FixedRestCalculateMethod calcRest,FlowFixedRestSet noStampSet
															,FlowRestCalcMethod calcMethod,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList,OutingTimeOfDailyPerformance goOutTimeSheetList) {
		List<TimeSheetOfDeductionItem> timeSheets = new ArrayList<>();
		/*流動orフレックスかどうか判定*/
		if(!workTimeDivision.isfluidorFlex()) {
			/*固定休憩時間帯作成*/
			timeSheets.addAll(getFixedBreakTimeSheet(calcRest,breakTimeOfDailyList)); 
		}
		else {
			/*流動休憩時間帯作成*/
			timeSheets.addAll(getFluidBreakTimeSheet(calcMethod,true,noStampSet,breakTimeOfDailyList,goOutTimeSheetList));
		}
		
		
//		List<TimeSheetOfDeductionItem> dedTimeSheet = new ArrayList<TimeSheetOfDeductionItem>();
		//
//		for(Optional<BreakTimeOfDailyPerformance> OptionalTimeSheet : timeSheets) {
//			if(OptionalTimeSheet.isPresent()) {
//				for(BreakTimeSheet timeSheet : OptionalTimeSheet.get().getBreakTimeSheets())
//					dedTimeSheet.add(TimeSheetOfDeductionItem.createTimeSheetOfDeductionItemAsFixed(new TimeZoneRounding(timeSheet.getStartTime().getTimeWithDay(),timeSheet.getEndTime().getTimeWithDay(),null)
//																			, new TimeSpanForCalc(timeSheet.getStartTime().getTimeWithDay(),timeSheet.getEndTime().getTimeWithDay())
//																			, Collections.emptyList()
//																			, Collections.emptyList()
//																			, Collections.emptyList()
//																			, Optional.empty()
//																			, Finally.empty()
//																			, Finally.of(BreakClassification.BREAK)
//																			, DeductionClassification.BREAK));
//			}
//		}
		return timeSheets;
	}
	
	/**
	 * 固定勤務 時に休 時間帯を取得する
	 * @param restCalc 固定給系の計算方法
	 * @return 休  時間帯

 */
	public static List<TimeSheetOfDeductionItem> getFixedBreakTimeSheet(FixedRestCalculateMethod calcRest,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList) {
		//就業時間帯を参照
		if(calcRest.isReferToMaster()) {
			return breakTimeOfDailyList.stream()
										.filter(tc -> tc.getBreakType().isReferWorkTime())
										.findFirst()
										.get()
										.changeAllTimeSheetToDeductionItem();
		}
		//スケを参照
		else {
			return breakTimeOfDailyList.stream()
										.filter(tc -> tc.getBreakType().isReferSchedule())
										.findFirst()
										.get()
										.changeAllTimeSheetToDeductionItem();
		}
	}
	

	/**
	 * 流動勤務 休 設定取得
	 * @param calcMethod 流動休 の計算方
	 * @param isFixedBreakTime 流動固定休 を使用する区分
	 * @param noStampSet 休 未打刻時 休設定
	 * @return 休 時間帯
	 */
	public static List<TimeSheetOfDeductionItem> getFluidBreakTimeSheet(FlowRestCalcMethod calcMethod,boolean isFixedBreakTime,FlowFixedRestSet noStampSet,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList,
																		OutingTimeOfDailyPerformance goOutTimeSheetList) {
		if(isFixedBreakTime) {
			switch(noStampSet.getCalculateMethod()) {
				//予定を参照する
				case REFER_SCHEDULE:
					//if(予定から参照するかどうか)
					//参照する
					return getReferenceTimeSheet(BreakType.REFER_SCHEDULE,breakTimeOfDailyList);
					//しない
					//return getReferenceTimeSheetFromWorkTime();
				//マスタを参照
				case REFER_MASTER:
					return getReferenceTimeSheet(BreakType.REFER_WORK_TIME,breakTimeOfDailyList);
				//参照せずに打刻をする
				case STAMP_WHITOUT_REFER:
					return goOutTimeSheetList.changeAllTimeSheetToDeductionItem();
				default:
					throw new RuntimeException("unKnown calcMethod" + calcMethod);
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * 流動固定休憩の計算方法が指定された休憩種類の日別計算用休憩時間帯クラスを取得する　
	 * @return 日別実績の休　時間帯クラス
	 */
	public static List<TimeSheetOfDeductionItem> getReferenceTimeSheet(BreakType breakType,List<BreakTimeOfDailyPerformance> breakTimeOfDailyList){
		return breakTimeOfDailyList.stream()
									.filter(tc -> tc.getBreakType().equals(breakType))
									.findFirst()
									.get()
									.changeAllTimeSheetToDeductionItem();
	}
	
	
	
	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
//	/**
//	 * 控除時間帯の仮確定(流動用) 
//	 */
//	public void provisionalDecisionOfDeductionTimeSheet(FluidWorkSetting fluidWorkSetting) {
//		//固定休憩か流動休憩か確認する
//		if(fluidWorkSetting.getWeekdayWorkTime().getRestTime().getUseFixedRestTime()) {//固定休憩の場合
//			switch(fluidWorkSetting.getRestSetting().getFluidWorkBreakSettingDetail().getFluidPrefixBreakTimeSet().getCalcMethod()) {
//				//マスタを参照する
//				case ReferToMaster:
//				this.createDedctionTimeSheet(acqAtr, setMethod, clockManage, dailyGoOutSheet, oneDayRange, CommonSet, attendanceLeaveWork, fixedCalc, workTimeDivision, noStampSet, fluidSet);
//				//予定を参照する
//				case ReferToSchedule:
//				this.createDedctionTimeSheet(acqAtr, setMethod, clockManage, dailyGoOutSheet, oneDayRange, CommonSet, attendanceLeaveWork, fixedCalc, workTimeDivision, noStampSet, fluidSet);
//				//参照せずに打刻する
//				case StampWithoutReference:
//				this.createDedctionTimeSheet(acqAtr, setMethod, clockManage, dailyGoOutSheet, oneDayRange, CommonSet, attendanceLeaveWork, fixedCalc, workTimeDivision, noStampSet, fluidSet);
//			}
//		}else{//流動休憩の場合
//			switch(fluidWorkSetting.getRestSetting().getFluidWorkBreakSettingDetail().getFluidBreakTimeSet().getCalcMethod()) {
//				//マスタを参照する
//				case ReferToMaster:
//				
//				//マスタと打刻を併用する	
//				case ConbineMasterWithStamp:
//				
//				//参照せずに打刻する	
//				//case StampWithoutReference:
//			
//			}
//		}
//		
//	}
//	
	
//	/**
//	 * 控除時間帯の作成   流動勤務で固定休憩の場合にシフトから計算する場合の処理の事
//	 */
//	public void createLateTimeSheetForFluid(
//			WithinWorkTimeFrame withinWorkTimeFrame,
//			FluidWorkSetting fluidWorkSetting,
//			CalculationRangeOfOneDay oneDayRange) {
//		
//		//計算範囲の取得
//		AttendanceTime calcRange= oneDayRange.getPredetermineTimeSetForCalc().getOneDayRange();
//		//控除時間帯の取得　・・・保科君が作成済みの処理を呼ぶ
//		List<TimeSheetOfDeductionItem> deductionTimeSheet = this.collectDeductionTimes(
//				dailyGoOutSheet, 
//				oneDayRange, 
//				CommonSet, 
//				attendanceLeaveWork, 
//				fixedCalc, 
//				workTimeDivision, 
//				noStampSet, 
//				fluidSet, 
//				acqAtr);
//		//控除時間帯同士の重複部分を補正
//		deductionTimeSheet = new DeductionTimeSheetAdjustDuplicationTime(deductionTimeSheet)
//									.reCreate(WorkTimeMethodSet setMethod,RestClockManageAtr clockManage,WorkTimeDailyAtr workTimeDailyAtr);
//		//控除合計時間クラスを作成　　不要な可能性あり
//		//→合計時間を保持しておくためにこのインスタンスは必要(2017.11.27 by hoshina)
//		DeductionTotalTimeForFluidCalc deductionTotalTime = new DeductionTotalTimeForFluidCalc(new AttendanceTime(0),new AttendanceTime(0));
//		//流動休憩時間帯を取得する
//		List<FluRestTimeSetting> fluRestTimeSheetList = 
//				fluidWorkSetting.getWeekdayWorkTime().getRestTime().getFluidRestTime().getFluidRestTimes();
//		//外出取得開始時刻を作成する
//		AttendanceTime getGoOutStartClock = new AttendanceTime(withinWorkTimeFrame.getCalcrange().getStart().valueAsMinutes());
//		//一時的に作成(最後に控除時間帯へ追加する休憩時間帯リスト)
//		List<TimeSheetOfDeductionItem> restTimeSheetListForAddToDeductionList = new ArrayList<>();
//		
//		//流動休憩時間帯分ループ
//		for(int nowLoop = 0 ; nowLoop <fluRestTimeSheetList.size(); nowLoop++) {		
//
//			//流動休憩時間帯の作成（引数にgetGoOutStartClockを渡す
//			TimeSheetOfDeductionItem restTimeSheet = deductionTotalTime.createDeductionFluidRestTime(
//					fluRestTimeSheetList.get(nowLoop), 
//					getGoOutStartClock, 
//					nowLoop, 
//					fluidWorkSetting, 
//					deductionTimeSheet
//					/*外出と流動休憩の関係設定*/);
//			//作成した時間帯を休憩時間帯リストに格納
//			restTimeSheetListForAddToDeductionList.add(restTimeSheet);
//			getGoOutStartClock = new AttendanceTime(restTimeSheet.calcrange.getStart().valueAsMinutes());
//		}
//		//退勤時刻までの外出の処理
//		deductionTotalTime.collectDeductionTotalTime(list, getGoOutStartClock, fluidWorkSetting, roopNo);
//		//控除時間帯をソート
//		
//	}
	
	/**
	 * 控除時間中の時間休暇相殺時間の計算
	 * @return
	 */
	public DeductionOffSetTime calcTotalDeductionOffSetTime(
			LateTimeOfDaily lateTimeOfDaily,
			LateTimeSheet lateTimeSheet,
			LeaveEarlyTimeOfDaily leaveEarlyTimeOfDaily,
			LeaveEarlyTimeSheet leaveEarlyTimeSheet
			) {
		
		//遅刻相殺時間の計算
		//DeductionOffSetTime lateDeductionSffSetTime = lateTimeSheet.calcDeductionOffSetTime(lateTimeOfDaily.getTimePaidUseTime(),DeductionAtr.Appropriate);
		DeductionOffSetTime lateDeductionSffSetTime = new DeductionOffSetTime(new AttendanceTime(0),
																			  new AttendanceTime(0),
																			  new AttendanceTime(0),
																			  new AttendanceTime(0));
		//早退相殺時間の計算
		
		//外出相殺時間の計算
		
		//3つの相殺時間を合算する
		DeductionOffSetTime timeVacationOffSetTime = lateDeductionSffSetTime/* + 早退相殺時間 + 外出相殺時間*/;//合算するメソッドは別途考慮
		
		return timeVacationOffSetTime;
	}
	
}
