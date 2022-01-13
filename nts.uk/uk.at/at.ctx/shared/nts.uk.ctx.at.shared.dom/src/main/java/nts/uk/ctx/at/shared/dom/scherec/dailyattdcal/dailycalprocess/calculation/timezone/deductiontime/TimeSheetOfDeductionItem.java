package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.FluidFixedAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationOffSetItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.holidaypriorityorder.CompanyHolidayPriorityOrder;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductGoOutRoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.GoOutTimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.GoOutTypeRoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.LateEarlyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.RestClockManageAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.RestTimeOfficeWorkCalcMethod;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneGoOutSet;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeMethodSet;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 控除項目の時間帯
 * @author keisuke_hoshina
 */
@Getter
public class TimeSheetOfDeductionItem extends TimeVacationOffSetItem implements Cloneable {
	//勤務間区分
	//勤務間区分 2
	private WorkingBreakTimeAtr workingBreakAtr;
	//外出理由 5
	private Finally<GoingOutReason> goOutReason;
	//休憩種別 (休憩区分) 4
	private Finally<BreakClassification> breakAtr;
	//短時間勤務時間帯区分 (短時間勤務区分)  7
	private Optional<ShortTimeSheetAtr> shortTimeSheetAtr;
	//控除区分 1
	private DeductionClassification deductionAtr;
	//育児介護区分 6
	private Optional<ChildCareAtr> childCareAtr;
	/** 勤務外を記録する */
	private boolean recordOutside;
	
	/**
	 * 控除項目の時間帯作成(育児介護区分対応版)
	 * @param timeSpan
	 * @param goOutReason
	 * @param breakAtr
	 * @param deductionAtr
	 * @param withinStatutoryAtr
	 */
	private TimeSheetOfDeductionItem(TimeSpanForDailyCalc timeSheet, TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets,
			WorkingBreakTimeAtr workingBreakAtr,
			Finally<GoingOutReason> goOutReason,
			Finally<BreakClassification> breakAtr, Optional<ShortTimeSheetAtr> shortTimeSheetAtr,
			DeductionClassification deductionAtr,Optional<ChildCareAtr> childCareAtr,
			boolean recordOutside) {
		super(timeSheet, rounding, recorddeductionTimeSheets, deductionTimeSheets);
		this.workingBreakAtr = workingBreakAtr;
		this.goOutReason = goOutReason;
		this.breakAtr = breakAtr;
		this.shortTimeSheetAtr = shortTimeSheetAtr;
		this.deductionAtr = deductionAtr;
		this.childCareAtr = childCareAtr;
		this.recordOutside = recordOutside;
	}
	
	/**
	 * 控除項目の時間帯作成　
	 * @param timeSpan
	 * @param goOutReason
	 * @param breakAtr
	 * @param deductionAtr
	 * @param withinStatutoryAtr
	 * @return
	 */
	public static TimeSheetOfDeductionItem createTimeSheetOfDeductionItem(TimeSpanForDailyCalc timeSheet
			,TimeRoundingSetting rounding
			,List<TimeSheetOfDeductionItem> recorddeductionTimeSheets
			,List<TimeSheetOfDeductionItem> deductionTimeSheets
			,WorkingBreakTimeAtr workingBreakAtr
			,Finally<GoingOutReason> goOutReason
			,Finally<BreakClassification> breakAtr
			,Optional<ShortTimeSheetAtr> shortTimeSheetAtr
			,DeductionClassification deductionAtr
			,Optional<ChildCareAtr> childCareAtr
			,boolean recordOutside) {
		
		return new TimeSheetOfDeductionItem(
				timeSheet
				,rounding
				,recorddeductionTimeSheets
				,deductionTimeSheets
				,workingBreakAtr
				,goOutReason
				,breakAtr
				,shortTimeSheetAtr
				,deductionAtr
				,childCareAtr
				,recordOutside
				);
	}
	
	/**
	 * 受けとった計算範囲で時間を入れ替える
	 * @param timeSpan 時間帯
	 * @return　控除項目の時間帯
	 */
	public TimeSheetOfDeductionItem cloneWithNewTimeSpan(Optional<TimeSpanForDailyCalc> timeSpan) {
		return cloneWithValue(timeSpan, this.deductionAtr);
	}
	
	/**
	 * 受けとった計算範囲で計上なし時間として入れ替える
	 * @param timeSpan 時間帯
	 * @return　控除項目の時間帯
	 */
	public TimeSheetOfDeductionItem createNoRecord(Optional<TimeSpanForDailyCalc> timeSpan) {
		return cloneWithValue(timeSpan, DeductionClassification.NON_RECORD);
	}
	
	/**
	 * 勤務外を計上する時間として作成する
	 * @param timeSpan 時間帯
	 * @return 控除項目の時間帯
	 */
	public TimeSheetOfDeductionItem createRecordOutTime(TimeSpanForDailyCalc timeSpan) {
		TimeSheetOfDeductionItem result = cloneWithNewTimeSpan(Optional.of(timeSpan));
		result.recordOutside = true;
		return result;
	}
	
	/**
	 * 指定の値でクローンを作成する
	 * @param timeSpan 時間帯
	 * @param dedAtr 控除区分
	 * @return 控除項目の時間帯
	 */
	private TimeSheetOfDeductionItem cloneWithValue(
			Optional<TimeSpanForDailyCalc> timeSpan,
			DeductionClassification dedAtr) {
		
		TimeSpanForDailyCalc targetSpan = this.timeSheet;
		if (timeSpan.isPresent()) targetSpan = timeSpan.get();
		
		TimeSheetOfDeductionItem result = this.clone();
		result.timeSheet = targetSpan;
		result.deductionAtr = dedAtr;
		result.recordedTimeSheet = new ArrayList<>();
		result.deductionTimeSheet = new ArrayList<>();
		result.addDuplicatedDeductionTimeSheet(this.recordedTimeSheet, DeductionAtr.Appropriate, Optional.empty());
		result.addDuplicatedDeductionTimeSheet(this.deductionTimeSheet, DeductionAtr.Deduction, Optional.empty());
		return result;
	}
	
	/**
	 * 範囲を比較したい対象
	 * @return
	 */
	public Optional<TimeSheetOfDeductionItem> createDuplicateRange(TimeSpanForDailyCalc timeSpan) {
		//重複範囲取得
		val duplicateSpan = timeSpan.getDuplicatedWith(this.timeSheet);
		//重複有
		if(duplicateSpan.isPresent())
			return Optional.of(this.cloneWithNewTimeSpan(duplicateSpan));
		//重複無
		return Optional.empty();
	}
	
	/**
	 * 控除項目の時間帯の法定内区分を法定外へ置き換える
	 * @return 法定内区分を法定外に変更した控除項目の時間帯
	 */
	public TimeSheetOfDeductionItem StatutoryAtrFromWithinToExcess() {
		return new TimeSheetOfDeductionItem(this.timeSheet, 
											this.rounding, 
											this.recordedTimeSheet,
											this.deductionTimeSheet, 
											this.workingBreakAtr,
											this.goOutReason, 
											this.breakAtr,
											this.shortTimeSheetAtr,
											this.deductionAtr,
											this.childCareAtr,
											this.recordOutside);
	}
	
	/** 終了時刻に従って、外出時間帯を分割 */
	public void changeToBreak() {
		this.breakAtr = Finally.of(BreakClassification.BREAK_STAMP);
		this.deductionAtr = DeductionClassification.BREAK;
	}
	
	/**
	 * 控除時間帯と控除時間帯の重複チェック
	 * @param baseTimeSheet 現ループ中のリスト　
	 * @param compareTimeSheet　次のループで取り出すリスト　
	 */
	public List<TimeSheetOfDeductionItem> deplicateBreakGoOut(TimeSheetOfDeductionItem compareTimeSheet,WorkTimeMethodSet setMethod,RestClockManageAtr clockManage
															,FluidFixedAtr fluidFixedAtr,WorkTimeDailyAtr workTimeDailyAtr) {
		List<TimeSheetOfDeductionItem> map = new ArrayList<TimeSheetOfDeductionItem>();
		List<TimeSpanForDailyCalc> baseThisNotDupSpan = this.timeSheet.getNotDuplicationWith(compareTimeSheet.timeSheet);
		List<TimeSpanForDailyCalc> baseCompareNotDupSpan = compareTimeSheet.timeSheet.getNotDuplicationWith(this.timeSheet);
		
		/*両方とも育児　*/
		/*if文の中身を別メソッドに実装する*/
		if(this.getDeductionAtr().isChildCare() && compareTimeSheet.getDeductionAtr().isChildCare()) {
			map.add(this);
			map.addAll(baseCompareNotDupSpan.stream().map(tc -> compareTimeSheet.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
			return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
		}
		/*前半育児　　後半外出*/
		else if(this.getDeductionAtr().isChildCare() && compareTimeSheet.getDeductionAtr().isGoOut()) {
			map.add(this);
			map.addAll(baseCompareNotDupSpan.stream().map(tc -> compareTimeSheet.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
			return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
		}
		/*前半外出、、後半育児*/
		else if(this.getDeductionAtr().isGoOut() && compareTimeSheet.getDeductionAtr().isChildCare()) {
			map.addAll(baseThisNotDupSpan.stream().map(tc -> this.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
			map.add(compareTimeSheet);
			return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
		}
		
		/*前半休憩、後半外出*/
		else if((this.getDeductionAtr().isBreak() && compareTimeSheet.getDeductionAtr().isGoOut())){
			if(!fluidFixedAtr.isFluidWork()) {
				TimeSpanForDailyCalc duplicationSpan = this.getTimeSheet().getDuplicatedWith(compareTimeSheet.getTimeSheet()).get();
				//休憩を削る
				map.add(this);
				
				//外出と被っている休憩を控除時間帯側の外出へ入れる
				if(compareTimeSheet.deductionTimeSheet == null
					|| compareTimeSheet.deductionTimeSheet.isEmpty()){
					compareTimeSheet.deductionTimeSheet = 
							new ArrayList<>(Arrays.asList(new TimeSheetOfDeductionItem(duplicationSpan
																	   , new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	   , Collections.emptyList()
																	   , Collections.emptyList()
																	   , this.workingBreakAtr
																	   , this.getGoOutReason()
																	   , this.breakAtr
																	   , Optional.empty()
																	   , this.getDeductionAtr()
																	   , Optional.empty()
																	   , false)));
				}
				else {
					compareTimeSheet.deductionTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan
							 , new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
							 , Collections.emptyList()
							 , Collections.emptyList()
							 , this.workingBreakAtr
							 , this.getGoOutReason()
							 , this.breakAtr
							 , Optional.empty()
							 , this.getDeductionAtr()
							 , Optional.empty()
							 , false));
				}

				//外出と被っている休憩を計上時間帯側の外出へ入れる
				if(compareTimeSheet.recordedTimeSheet == null
					|| compareTimeSheet.recordedTimeSheet.isEmpty()){
					compareTimeSheet.recordedTimeSheet = 
							new ArrayList<>(Arrays.asList(new TimeSheetOfDeductionItem(duplicationSpan
																	   , new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	   , Collections.emptyList()
																	   , Collections.emptyList()
																	   , this.workingBreakAtr
																	   , this.getGoOutReason()
																	   , this.breakAtr
																	   , Optional.empty()
																	   , this.getDeductionAtr()
																	   , Optional.empty()
																	   , false)));
				}
				else {
					compareTimeSheet.recordedTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan
							 , new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
							 , Collections.emptyList()
							 , Collections.emptyList()
							 , this.getWorkingBreakAtr()
							 , this.getGoOutReason()
							 , this.breakAtr
							 , Optional.empty()
							 , this.getDeductionAtr()
							 , Optional.empty()
							 , false));
				}
				//後半の外出を入れる
				map.add(compareTimeSheet);
				return map;
			}
			else {
				if(setMethod.isFluidWork()) {
					if(clockManage.isNotClockManage()) {
						return collectionBreakTime(this,compareTimeSheet);
					}
				}
			}
			//前半休憩
			map.add(this);
			//後半外出
			map.addAll(baseCompareNotDupSpan.stream().map(tc -> compareTimeSheet.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
			return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
		}
		/*前半外出、後半休憩*/
		else if(this.getDeductionAtr().isGoOut() && compareTimeSheet.getDeductionAtr().isBreak()){
			if(!fluidFixedAtr.isFluidWork()) {
				
				TimeSpanForDailyCalc duplicationSpan = compareTimeSheet.getTimeSheet().getDuplicatedWith(this.getTimeSheet()).get();
				//外出を入れる
				
				//外出の控除時間帯へ休憩を入れる
				this.deductionTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan
																		, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																		, new ArrayList<>()
																		, new ArrayList<>()
																		, compareTimeSheet.getWorkingBreakAtr()
																		, compareTimeSheet.getGoOutReason()
																		, compareTimeSheet.getBreakAtr()
																		, Optional.empty()
																		, compareTimeSheet.getDeductionAtr()
																		, Optional.empty()
																		, false));
				//外出の計上時間帯へ休憩を入れる
				this.recordedTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan
																		, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																		, new ArrayList<>()
																		, new ArrayList<>()
																		, compareTimeSheet.getWorkingBreakAtr()
																		, compareTimeSheet.getGoOutReason()
																		, compareTimeSheet.getBreakAtr()
																		, Optional.empty()
																		, compareTimeSheet.getDeductionAtr()
																		, Optional.empty()
																		, false));

			}
			else {
				if(setMethod.isFluidWork()||workTimeDailyAtr.isFlex()) {
					if(clockManage.isNotClockManage()) {
						return collectionBreakTime(compareTimeSheet,this);
					}
					//外出入れる
					map.addAll(this.timeSheet.getNotDuplicationWith(compareTimeSheet.timeSheet).stream()
							.map(t -> this.cloneWithNewTimeSpan(Optional.of(t)))
							.collect(Collectors.toList()));
					//休憩を入れる
					map.add(compareTimeSheet);
					return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
				}
			}
			//外出入れる
			map.add(this);
			//休憩を入れる
			map.add(compareTimeSheet);
			return map;
		}
		/*休憩系と休憩系*/
		else if(this.getDeductionAtr().isBreak() && compareTimeSheet.getDeductionAtr().isBreak()) {
			/*前半休憩、後半休憩打刻*/
			if(this.getBreakAtr().get().isBreak() && compareTimeSheet.getBreakAtr().get().isBreakStamp()) {
				map.addAll(baseThisNotDupSpan.stream().map(tc -> this.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
				map.add(compareTimeSheet);
				return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
			}
			/*前半休憩打刻、後半休憩*/
			else if((this.getBreakAtr().get().isBreakStamp() && compareTimeSheet.getBreakAtr().get().isBreak())){
				map.add(this);
				map.addAll(baseCompareNotDupSpan.stream().map(tc -> compareTimeSheet.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
				return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
			}
			/*休憩と休憩　→　育児と育児の重複と同じにする(後ろにある時間の開始を前の終了に合わせる)*/
			else if(this.getBreakAtr().get().isBreak() && compareTimeSheet.getBreakAtr().get().isBreak()) {
				map.add(this);
				if(baseCompareNotDupSpan!= null) {
					map.addAll(baseCompareNotDupSpan.stream().map(tc -> compareTimeSheet.cloneWithNewTimeSpan(Optional.of(tc))).collect(Collectors.toList()));
				}
				else {
					map.add(compareTimeSheet.cloneWithNewTimeSpan(Optional.of(new TimeSpanForDailyCalc(this.timeSheet.getTimeSpan().getStart(),this.timeSheet.getTimeSpan().getStart()))));
				}
				
				return map.stream().sorted((a, b) -> a.timeSheet.getStart().compareTo(b.timeSheet.getStart())).collect(Collectors.toList());
			}
		}
		
		//前半育児　後半休憩
		else if(this.getDeductionAtr().isChildCare() && compareTimeSheet.getDeductionAtr().isBreak()) {
			//育児に被っている休憩の範囲を取得
			Optional<TimeSpanForDailyCalc> duplicationSpan = this.getTimeSheet().getDuplicatedWith(compareTimeSheet.getTimeSheet());
			
			if(duplicationSpan.isPresent()) {
				//育児の控除時間帯へ休憩を入れる
				this.deductionTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan.get()
																	, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	, Collections.emptyList()
																	, Collections.emptyList()
																	, compareTimeSheet.getWorkingBreakAtr()
																	, compareTimeSheet.getGoOutReason()
																	, compareTimeSheet.getBreakAtr()
																	, Optional.empty()
																	, compareTimeSheet.getDeductionAtr()
																	, Optional.empty()
																	, false));
				//育児の計上時間帯へ休憩を入れる
				this.recordedTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan.get()
																	, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	, Collections.emptyList()
																	, Collections.emptyList()
																	, compareTimeSheet.getWorkingBreakAtr()
																	, compareTimeSheet.getGoOutReason()
																	, compareTimeSheet.getBreakAtr()
																	, Optional.empty()
																	, compareTimeSheet.getDeductionAtr()
																	, Optional.empty()
																	, false));
			}
			//育児入れる
			map.add(this);
			//休憩を入れる
			map.add(compareTimeSheet);
			return map;
		}
		//前半休憩　後半育児
		else if(this.getDeductionAtr().isBreak() && compareTimeSheet.getDeductionAtr().isChildCare()) {
			//育児に被っている休憩の範囲を取得
			Optional<TimeSpanForDailyCalc> duplicationSpan = compareTimeSheet.getTimeSheet().getDuplicatedWith(this.getTimeSheet());
			
			if(duplicationSpan.isPresent()) {
				//育児の控除時間帯へ休憩を入れる
				compareTimeSheet.deductionTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan.get()
																	, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	, Collections.emptyList()
																	, Collections.emptyList()
																	, this.getWorkingBreakAtr()
																	, this.getGoOutReason()
																	, this.getBreakAtr()
																	, Optional.empty()
																	, this.getDeductionAtr()
																	, Optional.empty()
																	, false));
				//外出の計上時間帯へ休憩を入れる
				compareTimeSheet.recordedTimeSheet.add(new TimeSheetOfDeductionItem(duplicationSpan.get()
																	, new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)
																	, Collections.emptyList()
																	, Collections.emptyList()
																	, this.getWorkingBreakAtr()
																	, this.getGoOutReason()
																	, this.getBreakAtr()
																	, Optional.empty()
																	, this.getDeductionAtr()
																	, Optional.empty()
																	, false));
			}
		}
		map.add(this);
		map.add(compareTimeSheet);
		return map; 
	}
	
	/**
	 * 休憩と外出時間帯の重複部分を補正する
	 * @param frontBreakTimeSheet 休憩時間帯
	 * @param goOutTimeSheet　外出時間帯
	 * @return 補正後の休憩、外出時間帯
	 */
	public List<TimeSheetOfDeductionItem> collectionBreakTime(TimeSheetOfDeductionItem frontBreakTimeSheet, TimeSheetOfDeductionItem backGoOutTimeSheet){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		switch(frontBreakTimeSheet.getTimeSheet().checkDuplication(backGoOutTimeSheet.getTimeSheet())) {
		case CONNOTATE_ENDTIME://終了時間含む
		case SAME_SPAN://同じ期間
			returnList.add(frontBreakTimeSheet.cloneWithNewTimeSpan(Optional.of(frontBreakTimeSheet.getTimeSheet().shiftAhead(frontBreakTimeSheet.getTimeSheet().getDuplicatedWith(backGoOutTimeSheet.getTimeSheet()).get().lengthAsMinutes()))));
			returnList.add(backGoOutTimeSheet);
			return returnList;
		case CONTAINED://含まれている(べース側が短い)
			/*休憩を外出の後ろにずらす*/
			returnList.add(frontBreakTimeSheet.cloneWithNewTimeSpan(Optional.of(frontBreakTimeSheet.getTimeSheet().shiftAhead(backGoOutTimeSheet.getTimeSheet().getEnd().valueAsMinutes() - frontBreakTimeSheet.getTimeSheet().getStart().valueAsMinutes()))));
			returnList.add(backGoOutTimeSheet);
		case CONTAINS://比較相手を含んでいる
		case CONNOTATE_BEGINTIME://開始時間を含む
			returnList.add(frontBreakTimeSheet.cloneWithNewTimeSpan(Optional.of(new TimeSpanForDailyCalc(frontBreakTimeSheet.start(),backGoOutTimeSheet.start()))));
			returnList.add(backGoOutTimeSheet);
			returnList.add(frontBreakTimeSheet.cloneWithNewTimeSpan(Optional.of(new TimeSpanForDailyCalc(backGoOutTimeSheet.end(),frontBreakTimeSheet.getTimeSheet().getEnd().forwardByMinutes(backGoOutTimeSheet.getTimeSheet().lengthAsMinutes())))));
			return returnList;
		case NOT_DUPLICATE://重複していない
			returnList.add(frontBreakTimeSheet);
			returnList.add(backGoOutTimeSheet);
			return returnList;
		default://例外
			throw new RuntimeException("unknown duplicate Atr" + frontBreakTimeSheet.getTimeSheet().checkDuplication(backGoOutTimeSheet.getTimeSheet()));
		}
	}
	
	public TimeWithDayAttr start() {
		return ((CalculationTimeSheet)this).getTimeSheet().getStart();
	}
	
	public TimeWithDayAttr end() {
		return ((CalculationTimeSheet)this).getTimeSheet().getEnd();
	}
	
	public boolean contains(TimeWithDayAttr baseTime) {
		return ((CalculationTimeSheet)this).getTimeSheet().contains(baseTime);
	}
	
	/**
	 * 終了時間と基準時間の早い方の時間を取得する
	 * @param basePoint　基準時間
	 * @return 時刻が早い方
	 */
	public TimeSpanForDailyCalc decisionNewSpan(TimeSpanForDailyCalc timeSpan,TimeWithDayAttr baseTime,boolean isDateBefore) {
		if(isDateBefore) {
			val endOclock = timeSpan.getEnd().lessThan(baseTime) ? timeSpan.getEnd() : baseTime; 
			return new TimeSpanForDailyCalc(timeSpan.getStart(),endOclock);
		}
		else {
			val startOclock = baseTime.lessThan(timeSpan.getStart()) ? timeSpan.getStart() : baseTime;
			return new TimeSpanForDailyCalc(startOclock,timeSpan.getEnd());
		}
	}
	
	/**
	 * 再帰中に自分自身を作り直す処理
	 * @param baseTime 分割をしたい中心の時間
	 * @param isDateBefore 開始時間～中心の時間までを切り出したい場合　true
	 * @return
	 */
	public TimeSheetOfDeductionItem reCreateOwn(TimeWithDayAttr baseTime,boolean isDateBefore) {
			List<TimeSheetOfDeductionItem>  recorddeductionTimeSheets = this.recreateDeductionItemBeforeBase(baseTime,isDateBefore,DeductionAtr.Appropriate);
			List<TimeSheetOfDeductionItem>  deductionTimeSheets = this.recreateDeductionItemBeforeBase(baseTime,isDateBefore,DeductionAtr.Deduction);
			TimeSpanForDailyCalc renewSpan = decisionNewSpan(this.getTimeSheet(),baseTime,isDateBefore);
			return new TimeSheetOfDeductionItem(renewSpan,
												this.rounding,
												recorddeductionTimeSheets,
												deductionTimeSheets,
												this.workingBreakAtr,this.goOutReason,this.breakAtr,this.shortTimeSheetAtr,this.deductionAtr,this.childCareAtr,
												this.recordOutside);
	}
	
	/**
	 * 法定内区分を法定外にして自分自身を作り直す
	 */
	public TimeSheetOfDeductionItem createWithExcessAtr(){
		return new TimeSheetOfDeductionItem(this.getTimeSheet(),this.rounding,this.recordedTimeSheet,this.deductionTimeSheet,this.workingBreakAtr,
				this.goOutReason,this.breakAtr,this.shortTimeSheetAtr,this.deductionAtr,this.childCareAtr,this.recordOutside);
	}
	
	/**
	 * 休憩時間帯の計算範囲の取得 
	 * @param timeList 出勤退勤の時間リスト
	 * @param calcMethod　休憩時間中に退勤した場合の計算方法
	 * @param deplicateoneTimeRange 1日の範囲と控除時間帯の重複部分
	 * @return
	 */
	public List<TimeSheetOfDeductionItem> getBreakCalcRange(List<TimeLeavingWork> timeList,RestTimeOfficeWorkCalcMethod calcMethod,
			Optional<TimeSpanForDailyCalc> deplicateOneTimeRange, DeductionAtr dedAtr) {
		if(!deplicateOneTimeRange.isPresent()) {
			return Collections.emptyList();
		}
		List<TimeSheetOfDeductionItem> timesheets = new ArrayList<>();
		for(TimeLeavingWork time : timeList) {
			timesheets.addAll(getIncludeAttendanceOrLeaveDuplicateTimeSheet(time, calcMethod, dedAtr, deplicateOneTimeRange.get()));
		}
		return timesheets;
	}
	
	/**
	 * 休憩時間帯に出勤、退勤が含まれているかの判定ののち重複時間帯の取得
	 * @param time 出退勤クラス
	 * @param calcMethod　休憩時間中に退勤した場合の計算方法
	 * @param oneDayRange 1日の範囲と控除時間帯の重複
	 * @return
	 */
	public List<TimeSheetOfDeductionItem> getIncludeAttendanceOrLeaveDuplicateTimeSheet(
			TimeLeavingWork time, RestTimeOfficeWorkCalcMethod calcMethod, DeductionAtr dedAtr,
			TimeSpanForDailyCalc oneDayRange) {
		
		List<TimeSheetOfDeductionItem> result = new ArrayList<>();
		TimeWithDayAttr newStart = oneDayRange.getStart();
		TimeWithDayAttr newEnd = oneDayRange.getEnd();
		
		//控除の場合、出退勤との重複は見ない
		if (dedAtr == DeductionAtr.Deduction) {
			result.add(cloneWithNewTimeSpan(Optional.of(new TimeSpanForDailyCalc(newStart, newEnd))));
			return result;
		}
		//出勤時刻が含まれているか判断する
		if(oneDayRange.getStart().lessThan(time.getTimespan().getStart())
				&& time.getTimespan().getStart().lessThanOrEqualTo(oneDayRange.getEnd())
				&& dedAtr.isAppropriate()) {
			return result;
		}
		//退勤時間を含んでいるかチェック
		if(oneDayRange.contains(time.getTimespan().getEnd())) {
			//出勤時間を含んでいるチェック
			if(oneDayRange.contains(time.getTimespan().getStart())){
				newStart = time.getTimespan().getStart();
			}
			switch(calcMethod) {
				//計上しない
				case NOT_APPROP_ALL:
					result.add(createNoRecord(Optional.empty()));
					return result;
				//全て計上
				case APPROP_ALL:
					result.add(createRecordOutTime(new TimeSpanForDailyCalc(newStart, newEnd)));
					return result;
				//退勤時間まで計上
				case OFFICE_WORK_APPROP_ALL:
					result.add(cloneWithNewTimeSpan(Optional.of(new TimeSpanForDailyCalc(newStart, time.getTimespan().getEnd()))));
					result.add(createNoRecord(Optional.of(new TimeSpanForDailyCalc(time.getTimespan().getEnd(), newEnd))));
					return result;
				//例外
				default:
					throw new RuntimeException("unknown CalcMethodIfLeaveWorkDuringBreakTime:" + calcMethod);
			}
		} else {
			//1日の計算範囲と出退勤の重複範囲取得
			result.add(cloneWithNewTimeSpan(oneDayRange.getDuplicatedWith(new TimeSpanForDailyCalc(time.getTimespan()))));
			return result;
		}
	}
	
	/**
	 * 付与する丸めを判断
	 * @param rounding 元の丸め設定
	 * @param actualAtr 実働時間帯区分
	 * @param dedAtr 控除区分
	 * @param commonSet 就業時間帯の共通設定
	 * @return 付与する丸め設定
	 */
	public Optional<TimeRoundingSetting> decisionAddRounding(
			TimeRoundingSetting rounding,
			ActualWorkTimeSheetAtr actualAtr,
			DeductionAtr dedAtr,
			WorkTimezoneCommonSet commonSet) {
		
		switch(this.getDeductionAtr()) {
		//休憩
		case BREAK:
			if(this.getWorkingBreakAtr().isWorking()) {
				return Optional.empty();
			}
			return Optional.of(rounding);
		//介護
		case CHILD_CARE:
			return getShortTimeRounding(dedAtr, commonSet,rounding);
		//外出
		case GO_OUT:
			if(actualAtr.isWithinWorkTime()) {
				return goOutingRoundingActual(commonSet.getGoOutSet(), actualAtr, dedAtr,rounding);
			}
			return Optional.of(new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
		//計上無し
		case NON_RECORD:
			return Optional.of(rounding);
		default:
			throw new RuntimeException("Unknown DeductionAtr:"+this.getDeductionAtr());
		}
	}
	
	/**
	 * 外出の控除相殺時間をセットする
	 * @param priorityOrder 時間休暇相殺優先順位
	 * @param useTimes 時間休暇使用時間
	 */
	public void setDeductionOffsetTimeOfOuting(
			CompanyHolidayPriorityOrder priorityOrder,
			Map<GoingOutReason, TimevacationUseTimeOfDaily> useTimes){
		
		if (!this.deductionAtr.isGoOut()) return;
		if (!this.goOutReason.isPresent()) return;
		if (!this.goOutReason.get().isPrivateOrUnion()) return;
		if (!useTimes.containsKey(this.goOutReason.get())) return;
		TimevacationUseTimeOfDaily target = useTimes.get(this.getGoOutReason().get());
		target = this.offsetProcess(priorityOrder, target, NotUseAtr.NOT_USE);
	}
	
	private Optional<TimeRoundingSetting> getShortTimeRounding(DeductionAtr dedAtr, WorkTimezoneCommonSet commonSet,TimeRoundingSetting rounding) {
		switch(dedAtr) {
			//計上
			case Appropriate:
			//控除
			case Deduction:
				if(this.getShortTimeSheetAtr().isPresent()) {
					switch(this.getShortTimeSheetAtr().get()) {
					//退勤後
					case AFTER_LEAVING:
						val afterLeaving = commonSet.getLateEarlySet().getOtherClassSets().stream().filter(tc -> tc.getLateEarlyAtr().equals(LateEarlyAtr.EARLY)).findFirst();
						return afterLeaving.isPresent()?Optional.of(afterLeaving.get().getDelTimeRoundingSet()):Optional.empty();
						//出勤前
					case BEFORE_ATTENDANCE:
						val beforeAttendance = commonSet.getLateEarlySet().getOtherClassSets().stream().filter(tc -> tc.getLateEarlyAtr().equals(LateEarlyAtr.LATE)).findFirst();
						return beforeAttendance.isPresent()?Optional.of(beforeAttendance.get().getDelTimeRoundingSet()):Optional.empty();
						//勤務中
					case WORKING_TIME:
						return goOutingRoundingActual(commonSet.getGoOutSet(),ActualWorkTimeSheetAtr.WithinWorkTime,dedAtr,rounding);
					default:
						throw new RuntimeException("Unknown shortTimeSheetAtr:"+ this.getShortTimeSheetAtr().get());
					}
				}
				else {
					return Optional.empty();
				}
			default:
				throw new RuntimeException("Unknown DeductionAtr:"+dedAtr);
		}
	}
	
	
	/**
	 * 就内・残業・休出どの設定を使用するか決定する
	 * @param goOutSet　外出設定
	 * @param actualAtr　実働時間帯区分
	 * @param dedAtr　控除区分
	 * @return　外出丸め設定
	 */
	private Optional<TimeRoundingSetting> goOutingRoundingActual(WorkTimezoneGoOutSet goOutSet,ActualWorkTimeSheetAtr actualAtr,DeductionAtr dedAtr,TimeRoundingSetting rounding){
		
		if(goOutSet.getTotalRoundingSet().getSetSameFrameRounding().isRoundingAndTotal()) {
			switch(actualAtr) {
			//就業時間内
			case WithinWorkTime:
				return goOutingRounding(dedAtr,goOutSet.getDiffTimezoneSetting().getWorkTimezone(),rounding);
			//残業
			case EarlyWork://早出
			case OverTimeWork://普通
			case StatutoryOverTimeWork://法内
				return goOutingRounding(dedAtr,goOutSet.getDiffTimezoneSetting().getOttimezone(),rounding);
			//休出
			case HolidayWork:
				return goOutingRounding(dedAtr,goOutSet.getDiffTimezoneSetting().getPubHolWorkTimezone(),rounding);
			default:
				throw new RuntimeException("Unknown ActualAtr:"+actualAtr);
			}
		}
		
		return Optional.of(new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
	}
	
	/**
	 * 外出理由からどの設定を参照するか決定する
	 * @param dedAtr 控除区分
	 * @param set　丸め設定
	 * @return 外出時丸め設定取得(逆丸め、丸め判定)
	 */
	private Optional<TimeRoundingSetting> goOutingRounding(DeductionAtr dedAtr,GoOutTypeRoundingSet set,TimeRoundingSetting rounding) {
		if(this.getGoOutReason() != null
		 &&this.getGoOutReason().isPresent()) {
			switch(this.getGoOutReason().get()) {
			case PRIVATE://私用
			case UNION://組合
				return Optional.of(goOutingRond(dedAtr,set.getPrivateUnionGoOut(), rounding));
			case COMPENSATION://公用
			case PUBLIC://有償
				return Optional.of(goOutingRond(dedAtr,set.getOfficalUseCompenGoOut(), rounding));
			default://例外
				throw new RuntimeException("Unknown GoOutReason:"+this.getGoOutReason().get());
			}
		}
		return Optional.empty();
	}
	
	/**
	 * 控除or計上どちらを使用する蚊決定する
	 * @param dedAtr 控除区分
	 * @param set 丸め設定
	 * @return　外出時丸め設定取得(逆丸め、丸め判定)
	 */
	private TimeRoundingSetting goOutingRond(DeductionAtr dedAtr,DeductGoOutRoundingSet set,TimeRoundingSetting rounding) {
		switch(dedAtr) {
		//計上
		case Appropriate:
			return getouting(set.getApproTimeRoundingSetting(),rounding);
		//控除
		case Deduction:
			return getouting(set.getDeductTimeRoundingSetting(),rounding);
		default:
			throw new RuntimeException("Unknown DedctionAtr:"+dedAtr);
		}
	}
	
	/**
	 * 外出時丸め設定取得(逆丸め、丸め判定)
	 * @param set 丸め設定
	 * @return　外出時丸め設定取得(逆丸め、丸め判定)
	 */
	private TimeRoundingSetting getouting(GoOutTimeRoundingSetting set,TimeRoundingSetting rounding) {
		switch(set.getRoundingMethod()) {
		//逆丸め
		case INDIVIDUAL_ROUNDING:
			//return set.getRoundingSetting().getReverseRounding();
			return rounding.getReverseRounding();
		//個別丸め
		case REVERSE_ROUNDING_EACH_TIMEZONE:
			return set.getRoundingSetting();
		default:
			throw new RuntimeException("Unknown GetRoundinMethod:"+set.getRoundingMethod());
		}
	}

	/**
	 * 短時間勤務時間帯の収集
	 * @return　自身と自身が持つ短時間勤務リスト
	 */
	public List<TimeSheetOfDeductionItem> collectShortTime() {
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		if(this.getDeductionAtr().isChildCare()) {
			returnList.add(this);
		}
		returnList.addAll(this.collectShortTimeSheet());
		return returnList;
	}
	
	public static TimeSheetOfDeductionItem createFromDeductionTimeSheet(nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime dTimeSheet) {
		return TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(new TimeSpanForDailyCalc(dTimeSheet.getStart(), dTimeSheet.getEnd()),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN),
				  Collections.emptyList(),
				  Collections.emptyList(),
				  WorkingBreakTimeAtr.NOTWORKING,
				  Finally.empty(),
				  Finally.of(BreakClassification.BREAK),
				  Optional.empty(),
				  DeductionClassification.BREAK,
				  Optional.empty(),
				  false
				  );
				
	}
	
	/**
	 * 外出の相殺時間を削除する
	 * （私用外出、組合外出の相殺時間のみを削除して返す）
	 * @return
	 */
	public TimeSheetOfDeductionItem getAfterDeleteOffsetTime() {
		if(this.deductionAtr.isGoOut() 
				&& this.goOutReason.isPresent()?this.goOutReason.get().isPrivateOrUnion():false) {
			//控除相殺時間を渡さずに作成する
			return new TimeSheetOfDeductionItem(
					this.timeSheet.clone(),
					this.rounding.clone(),
					this.recordedTimeSheet.stream().map(r -> r.clone()).collect(Collectors.toList()),
					this.deductionTimeSheet.stream().map(d -> d.clone()).collect(Collectors.toList()),
					WorkingBreakTimeAtr.valueOf(this.workingBreakAtr.toString()),
					this.goOutReason.isPresent()
						? Finally.of(GoingOutReason.valueOf(this.goOutReason.get().value))
						: Finally.empty(),
					this.breakAtr.isPresent()
						? Finally.of(BreakClassification.valueOf(this.breakAtr.get().toString()))
						: Finally.empty(),
					this.shortTimeSheetAtr.map(s -> ShortTimeSheetAtr.valueOf(s.toString())),
					DeductionClassification.valueOf(this.deductionAtr.toString()),
					this.childCareAtr.map(c -> ChildCareAtr.valueOf(c.value)),
					this.recordOutside ? true : false);
		}
		return this.clone();
	}

	public TimeSheetOfDeductionItem clone() {
		TimeSheetOfDeductionItem clone = new TimeSheetOfDeductionItem(
				this.timeSheet,
				this.rounding,
				this.recordedTimeSheet,
				this.deductionTimeSheet,
				this.workingBreakAtr,
				this.goOutReason,
				this.breakAtr,
				this.shortTimeSheetAtr,
				this.deductionAtr,
				this.childCareAtr,
				this.recordOutside);
		try {
			clone.timeSheet = this.timeSheet.clone();
			clone.rounding = this.rounding.clone();
			clone.recordedTimeSheet = this.recordedTimeSheet.stream().map(r -> r.clone()).collect(Collectors.toList());
			clone.deductionTimeSheet = this.deductionTimeSheet.stream().map(d -> d.clone()).collect(Collectors.toList());
			
			clone.deductionOffSetTime = this.deductionOffSetTime.map(d -> d.clone());
			
			clone.workingBreakAtr = WorkingBreakTimeAtr.valueOf(this.workingBreakAtr.toString());
			clone.goOutReason = this.goOutReason.isPresent()
					? Finally.of(GoingOutReason.valueOf(this.goOutReason.get().value))
					: Finally.empty();
			clone.breakAtr = this.breakAtr.isPresent()
					? Finally.of(BreakClassification.valueOf(this.breakAtr.get().toString()))
					: Finally.empty();
			clone.shortTimeSheetAtr = this.shortTimeSheetAtr.map(s -> ShortTimeSheetAtr.valueOf(s.toString()));
			clone.deductionAtr = DeductionClassification.valueOf(this.deductionAtr.toString());
			clone.childCareAtr = this.childCareAtr.map(c -> ChildCareAtr.valueOf(c.value));
			clone.recordOutside = this.recordOutside ? true : false;
		}
		catch (Exception e) {
			throw new RuntimeException("TimeSheetOfDeductionItem clone error.");
		}
		return clone;
	}
	
	public TimeSheetOfDeductionItem reCreateOwn(TimeSpanForDailyCalc range) {
		TimeSheetOfDeductionItem divideStartTime = this.reCreateOwn(range.getStart(), false);
		TimeSheetOfDeductionItem correctAfterTimeSheet = divideStartTime.reCreateOwn(range.getEnd(), true);
		return correctAfterTimeSheet;
	}
}
