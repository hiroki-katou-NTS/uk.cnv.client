package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.setting.SpecBonusPayTimesheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.MidNightTimeSheetForCalcList;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.someitems.BonusPayTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.midnighttimezone.MidNightTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 実働時間帯
 * @author keisuke_hoshina
 */
@Getter
@Setter
public abstract class ActualWorkingTimeSheet extends CalculationTimeSheet{

	//加給
	protected List<BonusPayTimeSheetForCalc> bonusPayTimeSheet = new ArrayList<>();
	//特定日加給
	protected List<SpecBonusPayTimeSheetForCalc> specBonusPayTimesheet = new ArrayList<>();
	//深夜
	protected MidNightTimeSheetForCalcList midNightTimeSheet;
	
	public ActualWorkingTimeSheet(TimeSpanForDailyCalc timeSheet, TimeRoundingSetting rounding) {
		super(timeSheet, rounding);
	}
	
	public ActualWorkingTimeSheet(
			TimeSpanForDailyCalc timeSheet,
			TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets,
			List<BonusPayTimeSheetForCalc> bonusPayTimeSheet,
			List<SpecBonusPayTimeSheetForCalc> specifiedBonusPayTimeSheet,
			MidNightTimeSheetForCalcList midNighttimeSheet) {
		
		super(timeSheet, rounding, recorddeductionTimeSheets,deductionTimeSheets);
		this.bonusPayTimeSheet = bonusPayTimeSheet;
		this.specBonusPayTimesheet = specifiedBonusPayTimeSheet;
		this.midNightTimeSheet = midNighttimeSheet;		
	}
	
	/**
	 * 加給時間帯のリストを作り直す
	 * @param baseTime 基準時間
	 * @param isDateBefore 基準時間より早い時間を切り出す
	 * @return 切り出した加給時間帯
	 */
	public List<BonusPayTimeSheetForCalc> recreateBonusPayListBeforeBase(TimeWithDayAttr baseTime,boolean isDateBefore){
		List<BonusPayTimeSheetForCalc> bonusPayList = new ArrayList<>();
	//	for(BonusPayTimeSheetForCalc bonusPay : bonusPayList) {
//			if(bonusPay..contains(baseTime)) {
//				bonusPayList.add(bonusPay.reCreateOwn(baseTime,isDateBefore));
//			}
//			else if(bonusPay.calcrange.getEnd().lessThan(baseTime) && isDateBefore) {
//				bonusPayList.add(bonusPay);
//			}
//			else if(bonusPay.calcrange.getStart().greaterThan(baseTime) && !isDateBefore) {
//				bonusPayList.add(bonusPay);
//			}
		//}
		return bonusPayList; 
	}
	
	/**
	 * 特定日加給時間帯のリストを作り直す
	 * @param baseTime 基準時間
	 * @param isDateBefore 基準時間より早い時間を切り出す
	 * @return 切り出した加給時間帯
	 */
	public List<SpecBonusPayTimeSheetForCalc> recreateSpecifiedBonusPayListBeforeBase(TimeWithDayAttr baseTime,boolean isDateBefore){
		List<SpecBonusPayTimeSheetForCalc> specifiedBonusPayList = new ArrayList<>();
//		for(SpecBonusPayTimeSheetForCalc specifiedBonusPay : specifiedBonusPayList) {
//			if(specifiedBonusPay.contains(baseTime)) {
//				specifiedBonusPayList.add(specifiedBonusPay.reCreateOwn(baseTime,isDateBefore));
//			}
//			else if(specifiedBonusPay.calcrange.getEnd().lessThan(baseTime) && isDateBefore) {
//				specifiedBonusPayList.add(specifiedBonusPay);
//			}
//			else if(specifiedBonusPay.calcrange.getStart().greaterThan(baseTime) && !isDateBefore) {
//				specifiedBonusPayList.add(specifiedBonusPay);
//			}
//		}
		return specifiedBonusPayList; 
	}
	
	/**
	 * 深夜時間帯一覧を作り直す
	 * @param baseTime 指定時刻
	 * @param isDateBefore 指定時刻より早い時間を切り出す
	 * @return 切り出した深夜時間帯一覧
	 */
	public MidNightTimeSheetForCalcList recreateMidNightTimeSheetBeforeBase(TimeWithDayAttr baseTime,boolean isDateBefore){
		return this.midNightTimeSheet.recreateMidNightTimeSheetBeforeBase(baseTime, isDateBefore);
	}
	
	/**
	 * 加給時間の計算
	 * アルゴリズム：時間の計算
	 * @param actualWorkAtr 実働区分
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 加給時間(List)
	 */
	public List<BonusPayTime> calcBonusPay(ActualWorkTimeSheetAtr actualWorkAtr, List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		List<BonusPayTime> bonusPayTimeList = new ArrayList<>();
		for(BonusPayTimeSheetForCalc bonusPaySheet : this.bonusPayTimeSheet){
			//加給時間帯計算設定の取得
			Optional<BPTimeItemSetting> set = bpTimeItemSets.stream()
					.filter(b -> b.getTimeItemNo() == bonusPaySheet.getRaiseSalaryTimeItemNo().v().intValue() && b.getTimeItemTypeAtr().isNomalType())
					.findFirst();
			//加給時間の計算
			AttendanceTime calcTime = bonusPaySheet.calcTotalTime();
			AttendanceTime time = set.map(s -> s.isCalc(actualWorkAtr, calcAtrOfDaily)).orElse(false)
					? bonusPaySheet.calcTotalTime()
					: AttendanceTime.ZERO;
			bonusPayTimeList.add(new BonusPayTime(
					bonusPaySheet.getRaiseSalaryTimeItemNo().v().intValue(),
					time,
					TimeWithCalculation.createTimeWithCalculation(actualWorkAtr.isWithinWorkTime()?time:new AttendanceTime(0),actualWorkAtr.isWithinWorkTime()?calcTime:new AttendanceTime(0)),
					TimeWithCalculation.createTimeWithCalculation(actualWorkAtr.isWithinWorkTime()?new AttendanceTime(0):time,actualWorkAtr.isWithinWorkTime()?new AttendanceTime(0):calcTime)));
		}
		return bonusPayTimeList;
	}
	
	/**
	 * 特定加給時間の計算
	 * アルゴリズム：時間の計算
	 * @param actualWorkAtr 実働区分
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 特定加給時間クラス(List)
	 */
	public List<BonusPayTime> calcSpacifiedBonusPay(ActualWorkTimeSheetAtr actualWorkAtr, List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> bonusPayTimeList = new ArrayList<>();
		for(SpecBonusPayTimeSheetForCalc bonusPaySheet : this.specBonusPayTimesheet){
			//加給時間帯計算設定の取得
			Optional<BPTimeItemSetting> set = bpTimeItemSets.stream()
					.filter(b -> b.getTimeItemNo() == bonusPaySheet.getSpecBonusPayNumber().v().intValue() && b.getTimeItemTypeAtr().isSpecialType())
					.findFirst();
			AttendanceTime calcTime = bonusPaySheet.calcTotalTime();
			AttendanceTime time = set.map(s -> s.isCalc(actualWorkAtr, calcAtrOfDaily)).orElse(false)
					? bonusPaySheet.calcTotalTime()
					: AttendanceTime.ZERO;
			bonusPayTimeList.add(
					new BonusPayTime(bonusPaySheet.getSpecBonusPayNumber().v().intValue(),
					time,
					TimeWithCalculation.createTimeWithCalculation(actualWorkAtr.isWithinWorkTime()?time:new AttendanceTime(0),actualWorkAtr.isWithinWorkTime()?calcTime:new AttendanceTime(0)),
					TimeWithCalculation.createTimeWithCalculation(actualWorkAtr.isWithinWorkTime()?new AttendanceTime(0):time,actualWorkAtr.isWithinWorkTime()?new AttendanceTime(0):calcTime)));
		}
		return bonusPayTimeList;
	}
	
	/**
	 * 指定された時間帯と重複している加給時間帯を取得
	 * @param bonusPayTimeSheet
	 * @param duplicateTimeSheet
	 * @return
	 */
	public static List<BonusPayTimeSheetForCalc> getDuplicatedBonusPay(List<BonusPayTimeSheetForCalc> bonusPayTimeSheet,TimeSpanForDailyCalc timeSpan){
		return bonusPayTimeSheet.stream()
								.filter(tc -> tc.getTimeSheet().getDuplicatedWith(timeSpan).isPresent())
								.map(tc -> tc.convertForCalcCorrectRange(tc.getTimeSheet().getDuplicatedWith(timeSpan).get()))
								.collect(Collectors.toList());
	}
	
	/**
	 * 指定された時間帯と重複している加給時間帯を取得
	 * @param bonusPayTimeSheet
	 * @param duplicateTimeSheet
	 * @return
	 */
	public List<BonusPayTimeSheetForCalc> getDuplicatedBonusPayNotStatic(List<BonusPayTimeSheetForCalc> bonusPayTimeSheet,TimeSpanForDailyCalc timeSpan){
		return bonusPayTimeSheet.stream()
								.filter(tc -> tc.getTimeSheet().getTimeSpan().checkDuplication(timeSpan.getTimeSpan()).isDuplicated())
								.map(tc -> tc.convertForCalcCorrectRange(tc.getTimeSheet().getDuplicatedWith(timeSpan).get()))
								.collect(Collectors.toList());
	}
	
	/**
	 * 指定された時間帯と重複している特定加給時間帯を取得
	 * @param bonusPayTimeSheet
	 * @param duplicateTimeSheet
	 * @return
	 */
	public static List<SpecBonusPayTimeSheetForCalc> getDuplicatedSpecBonusPay(List<SpecBonusPayTimeSheetForCalc> bonusPayTimeSheet,TimeSpanForDailyCalc timeSpan){
		return bonusPayTimeSheet.stream()
								.filter(tc -> tc.getTimeSheet().getTimeSpan().checkDuplication(timeSpan.getTimeSpan()).isDuplicated())
								.map(tc -> tc.convertForCalcCorrectRange(tc.getTimeSheet().getDuplicatedWith(timeSpan).get()))
								.collect(Collectors.toList());
	}
	
	/**
	 * 指定された時間帯と重複している特定加給時間帯を取得
	 * @param bonusPayTimeSheet
	 * @param duplicateTimeSheet
	 * @return
	 */
	public List<SpecBonusPayTimeSheetForCalc> getDuplicatedSpecBonusPayzNotStatic(List<SpecBonusPayTimeSheetForCalc> bonusPayTimeSheet,TimeSpanForDailyCalc timeSpan){
		return bonusPayTimeSheet.stream()
								.filter(tc -> tc.getTimeSheet().getTimeSpan().checkDuplication(timeSpan.getTimeSpan()).isDuplicated())
								.map(tc -> tc.convertForCalcCorrectRange(tc.getTimeSheet().getDuplicatedWith(timeSpan).get()))
								.collect(Collectors.toList());
	}
	
	/**
	 * 加給時間帯の作成
	 * @param bonuspaySetting 加給設定
	 * @param duplicateTimeSheet 計算範囲
	 * @param deductTimeSheet 控除時間帯
	 * @return 計算用加給時間帯List
	 */
	public static List<BonusPayTimeSheetForCalc> getBonusPayTimeSheetIncludeDedTimeSheet(
			Optional<BonusPaySetting> bonuspaySetting,
			TimeSpanForDailyCalc duplicateTimeSheet,
			DeductionTimeSheet deductTimeSheet){
		
		List<BonusPayTimeSheetForCalc> duplicatedBonusPay = new ArrayList<>();
		
		// 加給時間帯を取得
		if (bonuspaySetting.isPresent()){
			// 使用区分の判断　～　丸め設定を取得
			List<BonusPayTimeSheetForCalc> bpTimeSheet = bonuspaySetting.get().getLstBonusPayTimesheet().stream()
					.filter(tc -> tc.getUseAtr().isUse())
  					.map(tc -> BonusPayTimeSheetForCalc.convertForCalc(tc))
  					.collect(Collectors.toList());
			// 計算範囲と重複している加給時間帯を取得
			duplicatedBonusPay = getDuplicatedBonusPay(bpTimeSheet, duplicateTimeSheet);
			// 控除時間帯の登録
			for (BonusPayTimeSheetForCalc bonusPay : duplicatedBonusPay){
				bonusPay.registDeductionList(deductTimeSheet,
						Optional.of(new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)));
			}
		}
		return duplicatedBonusPay;
	}

	/**
	 * 特定日加給時間帯の作成
	 * @param bonuspaySetting 加給設定
	 * @param duplicateTimeSheet 計算範囲
	 * @param deductTimeSheet 控除時間帯
	 * @param specificDateAttrSheets 特定日区分
	 * @return 計算用特定加給時間帯List
	 */
	public static List<SpecBonusPayTimeSheetForCalc> getSpecBonusPayTimeSheetIncludeDedTimeSheet(
			Optional<BonusPaySetting> bonuspaySetting,
			TimeSpanForDailyCalc duplicateTimeSheet,
			DeductionTimeSheet deductTimeSheet,
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrSheets){
		
		List<SpecBonusPayTimeSheetForCalc> duplicatedSpecBonusPay = new ArrayList<>();
		
		// 特定日加給時間帯を取得
		if (bonuspaySetting.isPresent()){
			// 使用区分の判断
			List<SpecBonusPayTimesheet> specBpTimeSheet = bonuspaySetting.get().getLstSpecBonusPayTimesheet().stream()
					.filter(tc -> tc.getUseAtr().isUse())
					.collect(Collectors.toList());
			// 特定日設定を取得
			if(specificDateAttrSheets.isPresent()) {
				// 該当する特定日かどうか判断
				List<SpecBonusPayTimesheet> useSpecTimeSheet = getUseSpecTimeSheet(
						specBpTimeSheet, specificDateAttrSheets.get().getUseNo());
				// 計算範囲と重複している特定日加給時間帯を取得　～　丸め設定を取得
				duplicatedSpecBonusPay = getDuplicatedSpecBonusPay(
						useSpecTimeSheet.stream()
						.map(tc ->SpecBonusPayTimeSheetForCalc.convertForCalc(tc)).collect(Collectors.toList()),
						duplicateTimeSheet);
				// 控除時間帯の登録
				for (SpecBonusPayTimeSheetForCalc specBonusPay : duplicatedSpecBonusPay){
					specBonusPay.registDeductionList(deductTimeSheet,
							Optional.of(new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN)));
				}
			}
		}
		return duplicatedSpecBonusPay;
	}
	
	/**
	 * 日別実績の特定日区分を基に特定日として利用するか判定する
	 * @param specBpTimeSheets　特定日時間帯
	 * @param specNoList　特定日NOリスト
	 * @return 使用する特定日時間帯
	 */
	private static List<SpecBonusPayTimesheet> getUseSpecTimeSheet(List<SpecBonusPayTimesheet> specBpTimeSheets,List<SpecificDateItemNo> specNoList){
		List<SpecBonusPayTimesheet> returnList = new ArrayList<>();
		for(SpecBonusPayTimesheet specTimeSheet:specBpTimeSheets) {
			if(specNoList.contains(new SpecificDateItemNo(specTimeSheet.getDateCode())))
				returnList.add(specTimeSheet);
		}
		return returnList;
	}
	
	/**
	 * 深夜時間帯の作成
	 * @param midNightTimeSheet 深夜時間帯
	 * @param commonSetting 就業時間帯の共通設定
	 * @param deductTimeSheet 控除時間帯
	 */
	public void createMidNightTimeSheet(
			MidNightTimeSheet midNightTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSetting,
			DeductionTimeSheet deductTimeSheet) {
		
		this.midNightTimeSheet = ActualWorkingTimeSheet.getMidNightTimeSheetIncludeDedTimeSheet(
				midNightTimeSheet, this.timeSheet, deductTimeSheet, commonSetting);
	}
	
	/**
	 * 深夜時間帯の作成
	 * @param midNightTimeSheet 深夜時間帯
	 * @param duplicateTimeSheet 計算範囲
	 * @param deductTimeSheet 控除時間帯
	 * @param commonSetting 共通設定
	 * @return 計算用深夜時間帯List
	 */
	public static MidNightTimeSheetForCalcList getMidNightTimeSheetIncludeDedTimeSheet(
			MidNightTimeSheet midNightTimeSheet,
			TimeSpanForDailyCalc duplicateTimeSheet,
			DeductionTimeSheet deductTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSetting){
		
		// 共通設定から深夜丸め設定を取得
		TimeRoundingSetting timeRoundingSetting = commonSetting.isPresent()
				? commonSetting.get().getLateNightTimeSet().getRoundingSetting()
				: new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN);
		// 深夜時間帯一覧を作成する
		return MidNightTimeSheetForCalcList.create(duplicateTimeSheet, midNightTimeSheet, deductTimeSheet, timeRoundingSetting);
	}
	
	/**
	 * 時間帯の作成（加給、特定加給時間帯の作成）
	 * @param bonuspaySetting 加給設定
	 * @param specificDateAttrSheets 日別実績の特定日区分
	 * @param deductTimeSheet 控除時間帯
	 */
	public void createBonusPayTimeSheet(
			Optional<BonusPaySetting> bonuspaySetting,
			Optional<SpecificDateAttrOfDailyAttd> specificDateAttrSheets,
			DeductionTimeSheet deductTimeSheet){
	
		// 加給時間帯の作成
		this.bonusPayTimeSheet = ActualWorkingTimeSheet.getBonusPayTimeSheetIncludeDedTimeSheet(
				bonuspaySetting, this.timeSheet, deductTimeSheet);
		// 特定日加給時間帯の作成
		this.specBonusPayTimesheet = ActualWorkingTimeSheet.getSpecBonusPayTimeSheetIncludeDedTimeSheet(
				bonuspaySetting, this.timeSheet, deductTimeSheet, specificDateAttrSheets);
	}
	
	/**
	 * 控除時間帯の登録（実働時間帯）
	 * @param actualAtr 実働時間帯区分
	 * @param deductionTimeSheet 控除時間帯
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void registDeductionList(
			ActualWorkTimeSheetAtr actualAtr,
			DeductionTimeSheet deductionTimeSheet,
			Optional<WorkTimezoneCommonSet> commonSet){
		
		// 控除時間帯の登録
		this.registDeductionList(deductionTimeSheet, Optional.empty());
		// 控除時間帯へ丸め設定を付与
		if (commonSet.isPresent()) this.grantRoundingToDeductionTimeSheet(actualAtr, commonSet.get());
	}

	/**
	 * 控除回数の計算
	 * @param conditionAtr 条件
	 * @param dedAtr 控除区分
	 * @return 控除時間
	 */
	public int calcDeductionCount(
			ConditionAtr conditionAtr,
			DeductionAtr dedAtr){
		
		// 指定した控除時間帯を取得
		List<TimeSheetOfDeductionItem> itemList = this.getDedTimeSheetByAtr(dedAtr, conditionAtr);
		// Listの件数を返す
		return itemList.size();
	}
	
	/**
	 * 深夜時間の計算
	 * @param autoCalcSet 自動計算設定
	 * @return 深夜時間
	 */
	public TimeDivergenceWithCalculation calcMidNightTime(AutoCalSetting autoCalcSet) {
		AttendanceTime time = autoCalcSet.getCalAtr().isCalculateEmbossing() ? this.getMidNightTimeSheet().calcTotalTime() : AttendanceTime.ZERO;
		AttendanceTime calcTime = this.getMidNightTimeSheet().calcTotalTime();
		return TimeDivergenceWithCalculation.createTimeWithCalculation(time, calcTime);
	}
}
