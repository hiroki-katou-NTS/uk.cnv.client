package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 計算時間帯
 * @author keisuke_hoshina
 */
@Getter
public abstract class CalculationTimeSheet {
	//時間帯
	protected TimeSpanForDailyCalc timeSheet;
	//丸め
	protected TimeRoundingSetting rounding;
	@Setter
	//計上用
	protected List<TimeSheetOfDeductionItem> recordedTimeSheet= new ArrayList<>();
	@Setter
	//控除用
	protected List<TimeSheetOfDeductionItem> deductionTimeSheet = new ArrayList<>();
	
	/**
	 * コンストラクタ
	 * @param timeSheet 時間帯
	 * @param rounding 丸め設定
	 */
	public CalculationTimeSheet(TimeSpanForDailyCalc timeSheet, TimeRoundingSetting rounding) {
		this.timeSheet = timeSheet;
		this.rounding = rounding;
	}

	/**
	 * コンストラクタ
	 * @param timeSheet 時間帯
	 * @param rounding 丸め設定
	 * @param recorddeductionTimeSheets 計上用控除項目List
	 * @param deductionTimeSheets 控除用控除項目List
	 */
	public CalculationTimeSheet(
			TimeSpanForDailyCalc timeSheet,
			TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets) {
		this.timeSheet = timeSheet;
		this.rounding = rounding;
		this.recordedTimeSheet = recorddeductionTimeSheets;
		this.deductionTimeSheet = deductionTimeSheets;
	}
	
	/**
	 * 保持している控除項目Listを新しい控除時間帯として取得
	 * @return 控除時間帯
	 */
	public DeductionTimeSheet getCloneDeductionTimeSheet(){
		return new DeductionTimeSheet(
				new ArrayList<>(this.deductionTimeSheet),
				new ArrayList<>(this.recordedTimeSheet),
				new BreakTimeOfDailyAttd(),
				Optional.empty(),
				new ArrayList<>());
	}
	
	/**
	 * 指定時間を終了とする時間帯作成
	 * @return
	 */
	public Optional<TimeSpanForDailyCalc> reCreateTreatAsSiteiTimeEnd(AttendanceTime transTime,OverTimeFrameTimeSheetForCalc overTimeWork) {
		return overTimeWork.contractTimeSheet(new TimeWithDayAttr(this.calcTotalTime().valueAsMinutes() - transTime.valueAsMinutes()));
	}
	
	/**
	 * 指定時間に従って時間帯の縮小
	 * @return 縮小後の時間帯
	 */
	public Optional<TimeSpanForDailyCalc> contractTimeSheet(TimeWithDayAttr timeWithDayAttr) {
		/*ここのcalcTotalTImeは残業時間帯の時間*/
		int afterShort = calcTotalTime().valueAsMinutes() - timeWithDayAttr.valueAsMinutes();
		if(afterShort <= 0) return Optional.empty();
		TimeSpanForDailyCalc newSpan = new TimeSpanForDailyCalc(timeSheet.getTimeSpan().getStart(), timeSheet.getTimeSpan().getStart().forwardByMinutes(afterShort));
		List<TimeSheetOfDeductionItem> copyList = getNewSpanIncludeCalcrange(deductionTimeSheet,newSpan);
		for(int listn = 0 ; listn < copyList.size() ; listn++){
				/*ここのcalcTotalTimeは残業時間帯が持ってる控除時間帯の時間*/
				int differTime = copyList.get(listn).calcTotalTime().valueAsMinutes();
				/*ずらす前に範囲内に入っている時間帯の数を保持*/
				int beforeincludeSpan = copyList.size();//getNewSpanIncludeCalcrange(copyList,newSpan).size();
				//含んでいる控除時間帯分未来へずらす
				newSpan = newSpan.shiftEndAhead(differTime);
//				//
//				val moveAfterNewSpan = newSpan.shiftEndAhead(copyList.stream().map(ts -> ts.calcrange.lengthAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
				//増やしたことで、含む控除時間が増えるかどうかをチェック(含んでいる控除時間帯の数を数えなおす)
				int afterincludeSpan = getNewSpanIncludeCalcrange(deductionTimeSheet,newSpan).size();
				/*ずらした後の範囲に入っている時間帯の数とずらす前のかずを比較し増えていた場合、控除時間帯を保持してる変数に追加する*/
				if(afterincludeSpan > beforeincludeSpan) {
					copyList = Collections.emptyList();
					copyList = getNewSpanIncludeCalcrange(deductionTimeSheet,newSpan);
				}
		}
		return Optional.of(newSpan);
	}
	
	/**
	 * 時間帯と重複している控除時間帯のみを抽出する
	 * @param　控除項目の時間帯(List)
	 */
	private List<TimeSheetOfDeductionItem> getNewSpanIncludeCalcrange(List<TimeSheetOfDeductionItem> copyList , TimeSpanForDailyCalc newSpan){
		return copyList.stream().filter(tc -> newSpan.getTimeSpan().checkDuplication(tc.timeSheet.getTimeSpan()).isDuplicated()).collect(Collectors.toList());
	}
	
	/**
	 * 控除時間の合計を算出する
	 * @param dedAtr 控除区分
	 * @param conditionAtr 条件
	 * @return 控除時間
	 */
	public AttendanceTime calcDedTimeByAtr(DeductionAtr dedAtr,ConditionAtr conditionAtr) {
		val forCalcList = getDedTimeSheetByAtr(dedAtr,conditionAtr);
		return new AttendanceTime(forCalcList.stream().map(tc -> tc.calcTotalTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
	}
		
	/**
	 * 控除時間の合計を算出する
	 * @param dedAtr 控除区分
	 * @return 控除時間
	 */
	public AttendanceTime calcDedTimeByAtr(DeductionAtr dedAtr) {
		val forCalcList = (dedAtr.isDeduction())?this.deductionTimeSheet:this.recordedTimeSheet;
		return new AttendanceTime(forCalcList.stream().map(tc -> tc.calcTotalTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
	}
	
	/**
	 * 指定した控除時間帯を取得
	 * @param dedAtr 控除区分
	 * @param conditionAtr　条件
	 * @return　控除項目の時間帯List
	 */
	public List<TimeSheetOfDeductionItem> getDedTimeSheetByAtr(DeductionAtr dedAtr, ConditionAtr conditionAtr) {
		val returnList = (dedAtr.isDeduction())?this.deductionTimeSheet:this.recordedTimeSheet;
		switch(conditionAtr) {
			case BREAK:
				return returnList.stream().filter(tc -> tc.getDeductionAtr().isBreak()).collect(Collectors.toList());
			case Care:
				val list = returnList.stream().filter(tc -> tc.getDeductionAtr().isChildCare()).collect(Collectors.toList());	
				List<TimeSheetOfDeductionItem> list2 = new ArrayList<>();
				for(TimeSheetOfDeductionItem timeSheetOfDeductionItem:list) {
					if(timeSheetOfDeductionItem.getChildCareAtr().isPresent()&&timeSheetOfDeductionItem.getChildCareAtr().get().isCare()) {
						list2.add(timeSheetOfDeductionItem);
					}
				}
				return list2;
			case Child:
				val list3 = returnList.stream().filter(tc -> tc.getDeductionAtr().isChildCare()).collect(Collectors.toList());
				List<TimeSheetOfDeductionItem> list4 = new ArrayList<>();
				for(TimeSheetOfDeductionItem timeSheetOfDeductionItem:list3) {
					if(timeSheetOfDeductionItem.getChildCareAtr().isPresent()&&timeSheetOfDeductionItem.getChildCareAtr().get().isChildCare()) {
						list4.add(timeSheetOfDeductionItem);
					}
				}
				 return list4;					  
			case CompesationGoOut:
				return returnList.stream().filter(tc -> tc.getDeductionAtr().isGoOut() 
													 && tc.getGoOutReason().get().isCompensation()).collect(Collectors.toList());
			case PrivateGoOut:
				return returnList.stream().filter(tc -> tc.getDeductionAtr().isGoOut() 
													 && tc.getGoOutReason().get().isPrivate()).collect(Collectors.toList());
			case PublicGoOut:
				return returnList.stream().filter(tc -> tc.getDeductionAtr().isGoOut()
													&& tc.getGoOutReason().get().isPublic()).collect(Collectors.toList());
			case UnionGoOut:
				return returnList.stream().filter(tc -> tc.getDeductionAtr().isGoOut()
													&& tc.getGoOutReason().get().isUnion()).collect(Collectors.toList());
			default:
				throw new RuntimeException("unknown condition Atr");
		}
	}
	
	/**
	 * 時間の計算(自分自身ー自分が持つ控除時間帯.length)
	 * @return 自分自身ー自分が持つ控除時間帯.length
	 */
	public AttendanceTime calcTotalTime() {
		return deductionLengthMinutes();
	}
	
	/**
	 * 時間帯に含んでいる控除時間を差し引いた時間を計算する(メモ：トリガー)
	 * @return 時間
	 */
	private AttendanceTime deductionLengthMinutes() {
		
		if(deductionTimeSheet.isEmpty()) {
			//丸め設定の取得
			if(this.rounding != null) {
				return new AttendanceTime(rounding.round(this.timeSheet.getTimeSpan().lengthAsMinutes()));
			}
			else {
				return new AttendanceTime(this.timeSheet.getTimeSpan().lengthAsMinutes());
			}
		}
		return recursiveTotalTime() ;
	}

	/**
	 * 控除時間の計算
	 * @param conditionAtr 条件
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @return 控除時間
	 */
	public AttendanceTime calcDeductionTime(
			ConditionAtr conditionAtr,
			DeductionAtr dedAtr,
			TimeSheetRoundingAtr roundAtr,
			NotUseAtr canOffset){
		
		// 保持している控除時間帯を取得
		List<TimeSheetOfDeductionItem> itemList = this.getDedTimeSheetByAtr(dedAtr, conditionAtr);
		// 控除時間を計算
		// ※　相殺は常に行う。「時間帯毎に丸める」時のみ、控除項目ごとに先に丸めを行う。
		NotUseAtr unitRound = roundAtr == TimeSheetRoundingAtr.PerTimeSheet ? NotUseAtr.USE : NotUseAtr.NOT_USE;
		int totalMinutes = 0;
		for (TimeSheetOfDeductionItem item : itemList){
			totalMinutes += item.calcTotalTime(canOffset, unitRound).valueAsMinutes();
		}
		// 丸め区分を取得
		if (roundAtr == TimeSheetRoundingAtr.PerTimeFrame){
			// 丸め処理
			// ※　「時間帯枠毎に丸める」時、リストを合計した後に丸める。
			totalMinutes = this.rounding.round(totalMinutes);
		}
		// 控除時間を返す
		return new AttendanceTime(totalMinutes);
	}
	
	/**
	 * 控除時間の合計を求める(メモ：控除項目の時間帯から呼ぶcalcTotalTime()が再帰)
	 * @return　控除の合計時間
	 */
	protected AttendanceTime recursiveTotalTime() {
		List<TimeSheetOfDeductionItem> prodcessList = this.getDeductionTimeSheet();
		if(prodcessList.isEmpty()) return new AttendanceTime(this.timeSheet.getTimeSpan().lengthAsMinutes());
		AttendanceTime totalDedTime = new AttendanceTime(0);
		for(TimeSheetOfDeductionItem dedTimeSheet : prodcessList) {
			totalDedTime = new AttendanceTime(totalDedTime.valueAsMinutes()+dedTimeSheet.calcTotalTime().valueAsMinutes());
		}
		//丸め処理
		return new AttendanceTime(this.rounding.round(this.timeSheet.getTimeSpan().lengthAsMinutes() - totalDedTime.valueAsMinutes()));	
	}

	/**
	 * 開始から指定時間経過後の終了時刻を取得
	 * @param timeSpan　時間帯
	 * @param time　指定時間
	 * @return 指定時間経過後の終了時間
	 */
	public Optional<TimeWithDayAttr> getNewEndTime(TimeSpanForDailyCalc timeSpan, TimeWithDayAttr time) {
		Optional<TimeSpanForDailyCalc> newEnd = createTimeSpan(timeSpan,time);
		if(newEnd.isPresent()) {
			return Optional.of(newEnd.get().getTimeSpan().getEnd());
		}
		else {
			return  Optional.empty();
		}
	}
	
	/**
	 * 開始から指定時間を終了とする時間帯作成
	 * @param timeSpan 時間帯
	 * @param time　指定時間
	 * @return
	 */
	public Optional<TimeSpanForDailyCalc> createTimeSpan(TimeSpanForDailyCalc timeSpan, TimeWithDayAttr time) {
		return contractTimeSheet(time);
	}
	
	/**
	 * 控除時間帯のリストを作り直す
	 * @param baseTime 基準時間
	 * @param isDateBefore 基準時間より早い時間を切り出す
	 * @return 切り出したl控除時間帯
	 */
	public List<TimeSheetOfDeductionItem> recreateDeductionItemBeforeBase(TimeWithDayAttr baseTime,boolean isDateBefore, DeductionAtr dedAtr){
		
		List<TimeSheetOfDeductionItem> deductionList = new ArrayList<>();
		if(dedAtr.isDeduction()) {
			
			deductionList = this.deductionTimeSheet;
		}
		else {
			
			deductionList = this.recordedTimeSheet;
		}
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		
		for(TimeSheetOfDeductionItem deductionItem : deductionList) {
			
			if(deductionItem.contains(baseTime)) {
				returnList.add(deductionItem.reCreateOwn(baseTime,isDateBefore));
				//returnList.add(deductionItem);
			}
			else if(deductionItem.timeSheet.getTimeSpan().getEnd().lessThan(baseTime) && isDateBefore) {
				returnList.add(deductionItem);
			}
			else if(deductionItem.timeSheet.getTimeSpan().getStart().greaterThan(baseTime) && !isDateBefore) {
				returnList.add(deductionItem);
			}
		}
		
		return returnList;
	}
	
	/**
	 * 自分に重複している控除時間帯になるように補正して保持する
	 * @param deductionTimeSheet 追加する控除項目の時間帯リスト
	 * @param dedAtr 控除用か計上用か
	 * @param roundingSet 変更したい丸め設定(そのままでいい場合、emptyで)
	 */
	public void addDuplicatedDeductionTimeSheet(
			List<TimeSheetOfDeductionItem> deductionTimeSheet,
			DeductionAtr dedAtr,
			Optional<TimeRoundingSetting> roundingSet) {
		
		this.addDuplicatedDeductionTimeSheet(deductionTimeSheet, dedAtr, roundingSet, this.timeSheet);
	}
	
	/**
	 * 指定の時間帯に重複する控除時間帯になるように補正して保持する
	 * @param deductionTimeSheet 追加する控除項目の時間帯リスト
	 * @param dedAtr 控除用か計上用か
	 * @param roundingSet 変更したい丸め設定(そのままでいい場合、emptyで)
	 * @param targetSheet 指定の時間帯（就業時間内時間枠の短時間勤務の保持など、自分の時間帯でない幅で保持したい時に指定）
	 * @see 通常の保持には、指定の時間帯がない addDuplicatedDeductionTimeSheet を利用する事。
	 */
	public void addDuplicatedDeductionTimeSheet(
			List<TimeSheetOfDeductionItem> deductionTimeSheet,
			DeductionAtr dedAtr,
			Optional<TimeRoundingSetting> roundingSet,
			TimeSpanForDailyCalc targetSheet) {
		
		// 丸め設定がある時、丸め設定を入れ替える
		if (roundingSet.isPresent()){
			deductionTimeSheet = deductionTimeSheet.stream()
					.map(tc -> TimeSheetOfDeductionItem.createTimeSheetOfDeductionItem(
							tc.getTimeSheet(),
							roundingSet.get(), 
							tc.getRecordedTimeSheet(), 
							tc.getDeductionTimeSheet(), 
							tc.getWorkingBreakAtr(),
							tc.getGoOutReason(), 
							tc.getBreakAtr(), 
							tc.getShortTimeSheetAtr(), 
							tc.getDeductionAtr(),
							tc.getChildCareAtr(),
							tc.isRecordOutside()))
					.collect(Collectors.toList());
		}
		
		if (dedAtr.isAppropriate()){
			if (this.recordedTimeSheet != null && !this.recordedTimeSheet.isEmpty()){
				val test = this.getDupliRangeTimeSheet(deductionTimeSheet, targetSheet);
				if (test != null) this.recordedTimeSheet.addAll(test);
			}
			else{
				val test = this.getDupliRangeTimeSheet(deductionTimeSheet, targetSheet);
				if (test != null) this.recordedTimeSheet = test;
			}
		}
		else {
			if (this.deductionTimeSheet != null && !this.deductionTimeSheet.isEmpty()){
				val test = this.getDupliRangeTimeSheet(deductionTimeSheet, targetSheet);
				if (test != null) this.deductionTimeSheet.addAll(test);
			}
			else{
				val test = this.getDupliRangeTimeSheet(deductionTimeSheet, targetSheet);
				if (test != null) this.deductionTimeSheet = test;
			}
		}
	}

	/**
	 * 短時間勤務時間帯の収集
	 * @return　短時間勤務時間帯List
	 */
	public List<TimeSheetOfDeductionItem> collectShortTimeSheet() {
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		List<TimeSheetOfDeductionItem> shortTimeSheet = new ArrayList<>();
		//控除
		if(this.getDeductionTimeSheet() != null) {
			shortTimeSheet = this.getRecordedTimeSheet().stream().map(tc -> tc.collectShortTime()).flatMap(List::stream).collect(Collectors.toList());
			returnList.addAll(shortTimeSheet);
		}
		return returnList;
	}
	
	/**
	 * 控除時間帯の登録（計算時間帯）
	 * @param deductionTimeSheet 控除時間帯
	 * @param bulkRoundSet 一括丸め設定
	 */
	public void registDeductionList(
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimeRoundingSetting> bulkRoundSet){
		
		// 指定時間帯に含まれる控除時間帯リストを取得（計上）
		this.recordedTimeSheet = deductionTimeSheet.getDupliRangeTimeSheet(this.timeSheet, DeductionAtr.Appropriate);
		// 指定時間帯に含まれる控除時間帯リストを取得（控除）
		this.deductionTimeSheet = deductionTimeSheet.getDupliRangeTimeSheet(this.timeSheet, DeductionAtr.Deduction);
		
		if (bulkRoundSet.isPresent()){
			// 控除時間帯の全ての丸めを「一括丸め設定」に変える（計上、控除）
			for (TimeSheetOfDeductionItem item : this.recordedTimeSheet){
				item.rounding = bulkRoundSet.get();
			}
			for (TimeSheetOfDeductionItem item : this.deductionTimeSheet){
				item.rounding = bulkRoundSet.get();
			}
		}
	}
	
	/**
	 * 控除時間帯へ丸め設定を付与
	 * @param actualAtr 実働時間帯区分
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void grantRoundingToDeductionTimeSheet(
			ActualWorkTimeSheetAtr actualAtr,
			WorkTimezoneCommonSet commonSet){
		
		// 計上用控除時間帯に丸め設定を付与
		this.grantRoundingDeductionOrAppropriate(actualAtr, DeductionAtr.Appropriate, commonSet);
		// 控除用控除時間帯に丸め設定を付与
		this.grantRoundingDeductionOrAppropriate(actualAtr, DeductionAtr.Deduction, commonSet);
	}
	
	/**
	 * ループ処理（控除時間帯へ丸め設定を付与）
	 * @param actualAtr 実働時間帯区分
	 * @param dedAtr 控除区分
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void grantRoundingDeductionOrAppropriate(
			ActualWorkTimeSheetAtr actualAtr,
			DeductionAtr dedAtr,
			WorkTimezoneCommonSet commonSet){

		// 控除時間帯を取得
		List<TimeSheetOfDeductionItem> targetList = this.deductionTimeSheet;
		if (dedAtr.isAppropriate()) targetList = this.recordedTimeSheet;
		for (TimeSheetOfDeductionItem item : targetList){
			// 付与する丸めを判断
			Optional<TimeRoundingSetting> addRound = item.decisionAddRounding(this.rounding, actualAtr, dedAtr, commonSet);
			if (addRound.isPresent()) item.rounding = addRound.get();
		}
	}

	/**
	 * 控除項目Listを指定時間帯に縮める
	 * @param dedList 控除項目の時間帯List
	 * @param targetSheet 時間帯
	 * @return 控除項目の時間帯List
	 */
	private List<TimeSheetOfDeductionItem> getDupliRangeTimeSheet(
			List<TimeSheetOfDeductionItem> dedList,
			TimeSpanForDailyCalc targetSheet) {
		
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		for (TimeSheetOfDeductionItem timeSheet : dedList) {
			// 重複している時間帯を返す
			val dupCalcRange = timeSheet.getTimeSheet().getDuplicatedWith(targetSheet);
			if (dupCalcRange.isPresent()) {
				// 処理中の「控除項目の時間帯」を重複した時間帯で作り直す
				TimeSheetOfDeductionItem divideStartTime = timeSheet.reCreateOwn(dupCalcRange.get().getTimeSpan().getStart(), false);
				TimeSheetOfDeductionItem correctAfterTimeSheet = divideStartTime.reCreateOwn(dupCalcRange.get().getTimeSpan().getEnd(), true);
				returnList.add(correctAfterTimeSheet);
			}
		}
		return returnList;
	}
	
	/**
	 * 時間帯を指定された時間帯に変更する（計上と控除も補正する）
	 * @param timeSheet 開始時刻～終了時刻
	 */
	public void shiftTimeSheet(TimeSpanForDailyCalc timeSheet) {
		this.timeSheet = timeSheet;
		this.trimRecordedAndDeductionToSelfRange();
	}
	
	/**
	 * 開始時刻を指定された時刻に変更する（計上と控除も補正する）
	 * @param start 開始時刻
	 */
	public void shiftStart(TimeWithDayAttr start) {
		this.timeSheet = this.timeSheet.shiftOnlyStart(start);
		this.trimRecordedAndDeductionToSelfRange();
	}
	
	/**
	 * 終了時刻を指定された時刻に変更する（計上と控除も補正する）
	 * @param end 終了時刻
	 */
	public void shiftEnd(TimeWithDayAttr end) {
		this.timeSheet = this.timeSheet.shiftOnlyEnd(end);
		this.trimRecordedAndDeductionToSelfRange();
	}
	
	/**
	 * 計上と控除を自身の計算範囲へ補正する
	 */
	private void trimRecordedAndDeductionToSelfRange() {
		this.trimTimeSheet(this.timeSheet);
	}
	
	/**
	 * 計上と控除を指定の時間帯内へ補正する
	 * @param targetSheet 絞り込む時間帯
	 */
	private void trimTimeSheet(TimeSpanForDailyCalc targetSheet) {
		
		List<TimeSheetOfDeductionItem> rec = new ArrayList<>(this.recordedTimeSheet);
		this.recordedTimeSheet.clear();
		this.recordedTimeSheet.addAll(this.getDupliRangeTimeSheet(rec, targetSheet));
		
		List<TimeSheetOfDeductionItem> ded = new ArrayList<>(this.deductionTimeSheet);
		this.deductionTimeSheet.clear();
		this.deductionTimeSheet.addAll(this.getDupliRangeTimeSheet(ded, targetSheet));
	}
	
	/**
	 * 時間帯を前から指定時間分抜き出す
	 * @param time 指定時間
	 * @return 日別計算時間帯
	 */
	public Optional<TimeSpanForDailyCalc> extractForward(TimeWithDayAttr time){
		// 不要になる時間　←　全体の時間　－　指定時間
		int diff = this.calcTotalTime().valueAsMinutes() - time.valueAsMinutes();
		// 時間帯を指定時間に従って縮小
		return this.contractTimeSheet(new TimeWithDayAttr(diff));
	}
	
	/**
	 * 時間帯を後ろから指定時間分抜き出す
	 * @param time 指定時間
	 * @return 日別計算時間帯
	 */
	public Optional<TimeSpanForDailyCalc> extractBackword(TimeWithDayAttr time){
		// 時間帯を指定時間に従って縮小
		Optional<TimeSpanForDailyCalc> diff = this.contractTimeSheet(time);
		// 必要になる時間帯を判断し、返す
		if (!diff.isPresent()) return Optional.empty();
		return Optional.of(new TimeSpanForDailyCalc(
				new TimeWithDayAttr(diff.get().getEnd().valueAsMinutes()),
				new TimeWithDayAttr(this.getTimeSheet().getEnd().valueAsMinutes())));
	}
}
