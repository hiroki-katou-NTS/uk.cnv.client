package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.PremiumCalcMethodDetailOfHoliday;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.WorkTimeCalcMethodDetailOfHoliday;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.DeductionTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.ShortTimeWorkSheetWithoutWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeFrame;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.StatutoryAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 日別実績の短時間勤務時間
 * @author ken_takasu
 */

@Getter
public class ShortWorkTimeOfDaily {
	
	private WorkTimes workTimes;
	private DeductionTotalTime totalTime;
	private DeductionTotalTime totalDeductionTime;
	private ChildCareAtr childCareAttribute;
	
	/**
	 * Constructor 
	 */
	public ShortWorkTimeOfDaily(WorkTimes workTimes, DeductionTotalTime totalTime,
			DeductionTotalTime totalDeductionTime, ChildCareAtr childCareAttribute) {
		super();
		this.workTimes = workTimes;
		this.totalTime = totalTime;
		this.totalDeductionTime = totalDeductionTime;
		this.childCareAttribute = childCareAttribute;
	}
	
	/**
	 * 日別実績の短時間勤務時間
	 * @param recordClass 実績
	 * @param premiumAtr 割増区分
	 * @return 日別実績の短時間勤務時間
	 */
	public static ShortWorkTimeOfDaily calcShortWorkTime(
			ManageReGetClass recordClass,
			PremiumAtr premiumAtr) {
		
		WorkTimes workTimes = new WorkTimes(0);
		DeductionTotalTime totalTime = DeductionTotalTime.defaultValue();
		DeductionTotalTime totalDeductionTime = DeductionTotalTime.defaultValue();
		ChildCareAtr careAtr = getChildCareAttributeToDaily(recordClass.getIntegrationOfDaily());
		ShortWorkTimeOfDaily zeroValue = new ShortWorkTimeOfDaily(workTimes, totalTime, totalDeductionTime, careAtr);
		
		// 勤務種類を確認
		if (!recordClass.getWorkType().isPresent()) return zeroValue;
		WorkType workType = recordClass.getWorkType().get();
		// 出勤系かどうかの判断
		if (workType.isWorkingDay() == false) return zeroValue;
		
		if(recordClass.getCalculatable() && recordClass.getIntegrationOfDaily().getShortTime().isPresent()){
			//短時間勤務回数
			workTimes = new WorkTimes(recordClass.getIntegrationOfDaily().getShortTime().get().getShortWorkingTimeSheets().stream()
					.filter(tc -> tc.getChildCareAttr().equals(careAtr))
					.collect(Collectors.toList())
					.size());
			
			//計上時間の計算
			totalTime = calcTotalShortWorkTime(recordClass, DeductionAtr.Appropriate, careAtr, premiumAtr);
			
			//控除時間の計算
			totalDeductionTime = calcTotalShortWorkTime(recordClass, DeductionAtr.Deduction, careAtr, premiumAtr);
		}
		return new ShortWorkTimeOfDaily(workTimes, totalTime, totalDeductionTime, careAtr);
	}
	
	public static WorkTimes calcWorkTimes(ManageReGetClass recordClass,ConditionAtr condition) {
		
		List<TimeSheetOfDeductionItem> list = new ArrayList<>();
		DeductionAtr dedAtr = DeductionAtr.Appropriate;
		//就業時間内時間帯
		WithinWorkTimeSheet withinWorkTimeSheet = recordClass.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet().get();
		//就業時間帯育児時間帯
		list.addAll(withinWorkTimeSheet.getShortTimeSheet().stream().filter(tc -> tc.calcTotalTime().greaterThan(0)).collect(Collectors.toList()));
		
		for(WithinWorkTimeFrame withinWorkTimeFrame:withinWorkTimeSheet.getWithinWorkTimeFrame()) {
			list.addAll(withinWorkTimeFrame.getDedTimeSheetByAtr(dedAtr, condition));
			//遅刻
			if(withinWorkTimeFrame.getLateTimeSheet().isPresent()&&withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(dedAtr, condition));
			}
			//早退
			if(withinWorkTimeFrame.getLeaveEarlyTimeSheet().isPresent()&&withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(dedAtr, condition));
			}
		}
		//就業時間外時間帯
		Optional<OutsideWorkTimeSheet> outsideWorkTimeSheet = Optional.empty();
		if(recordClass.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().isPresent()) {
			recordClass.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get();
		}
		//残業
		if(outsideWorkTimeSheet.isPresent()) {
			if(outsideWorkTimeSheet.get().getOverTimeWorkSheet().isPresent()) {
				for(OverTimeFrameTimeSheetForCalc overTimeFrameTimeSheetForCalc:outsideWorkTimeSheet.get().getOverTimeWorkSheet().get().getFrameTimeSheets()) {
					list.addAll(overTimeFrameTimeSheetForCalc.getDedTimeSheetByAtr(dedAtr, condition));
				}
			}
			//休出
			if(outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().isPresent()) {
				for(HolidayWorkFrameTimeSheetForCalc holidayWorkFrameTimeSheetForCalc:outsideWorkTimeSheet.get().getHolidayWorkTimeSheet().get().getWorkHolidayTime()) {
					list.addAll(holidayWorkFrameTimeSheetForCalc.getDedTimeSheetByAtr(dedAtr, condition));
				}
			}
		}
		
		List<TimeSheetOfDeductionItem> result = new ArrayList<>();
		for(TimeSheetOfDeductionItem timeSheetOfDeductionItem:list){
			if(timeSheetOfDeductionItem.calcTotalTime().greaterThan(0)) {
				result.add(timeSheetOfDeductionItem);
			}
		}
		return new WorkTimes(result.size());
	}
	
	/**
	 * 短時間勤務時間の計算
	 * @param recordClass 実績
	 * @param dedAtr 控除区分
	 * @param careAtr 育児介護区分
	 * @param premiumAtr 割増区分
	 * @return 控除合計時間
	 */
	public static DeductionTotalTime calcTotalShortWorkTime(
			ManageReGetClass recordClass,
			DeductionAtr dedAtr,
			ChildCareAtr careAtr,
			PremiumAtr premiumAtr){
		
		DeductionTotalTime result = DeductionTotalTime.defaultValue();
		
		// 短時間勤務を控除するかどうかの設定を確認
		if (decisionDeductChild(dedAtr, premiumAtr, recordClass.getHolidayCalcMethodSet())){
			CalculationRangeOfOneDay oneDay = recordClass.getCalculationRangeOfOneDay();
			ConditionAtr conditionAtr = (careAtr.isChildCare() ? ConditionAtr.Child : ConditionAtr.Care);
			// 所定内育児時間の計算
			TimeWithCalculation withinTime = oneDay.getDeductionTime(
					conditionAtr, dedAtr, StatutoryAtr.Statutory, TimeSheetRoundingAtr.ALL, Optional.empty(), NotUseAtr.NOT_USE);
			// 所定外育児時間の計算
			TimeWithCalculation excessTime = oneDay.getDeductionTime(
					conditionAtr, dedAtr, StatutoryAtr.Excess, TimeSheetRoundingAtr.ALL, Optional.empty(), NotUseAtr.NOT_USE);
			// 合計時間の計算
			result = DeductionTotalTime.of(
					withinTime.addMinutes(excessTime.getTime(), excessTime.getCalcTime()),
					withinTime,
					excessTime);
		}
		if (dedAtr.isAppropriate()){
			if (recordClass.getCalculationRangeOfOneDay().getShortTimeWSWithoutWork().isPresent()){
				ShortTimeWorkSheetWithoutWork withoutWork =
						recordClass.getCalculationRangeOfOneDay().getShortTimeWSWithoutWork().get();
				// 勤務外短時間勤務時間を累計する（所定内）
				ConditionAtr conditionAtr = ConditionAtr.Child;
				if (careAtr.isCare()) conditionAtr = ConditionAtr.Care;
				AttendanceTime withinTime = withoutWork.sumShortWorkTimeWithoutWork(
						conditionAtr, TimeSheetRoundingAtr.ALL, Optional.empty(), true);
				// 勤務外短時間勤務時間を累計する（所定外）
				AttendanceTime withoutTime = withoutWork.sumShortWorkTimeWithoutWork(
						conditionAtr, TimeSheetRoundingAtr.ALL, Optional.empty(), false);
				
				AttendanceTime totalTime = withinTime.addMinutes(withoutTime.valueAsMinutes());
				result = DeductionTotalTime.of(
						result.getTotalTime().addMinutes(totalTime, totalTime),
						result.getWithinStatutoryTotalTime().addMinutes(withinTime, withinTime),
						result.getExcessOfStatutoryTotalTime().addMinutes(withoutTime, withoutTime));
			}
		}
		// 控除合計時間を返す
		return result;
	}
	
	/**
	 * 短時間勤務を控除するかどうかの設定を確認
	 * @param dedAtr 控除区分
	 * @param premiumAtr 割増区分
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @return true:控除する,false:控除しない
	 */
	private static boolean decisionDeductChild(
			DeductionAtr dedAtr,
			PremiumAtr premiumAtr,
			HolidayCalcMethodSet holidayCalcMethodSet) {
		
		if (dedAtr.isAppropriate()) return true;
		
		if (premiumAtr.isRegularWork()) {			
			Optional<WorkTimeCalcMethodDetailOfHoliday> advancedSet = holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet();
			if (advancedSet.isPresent()){
				if (advancedSet.get().getCalculateIncludCareTime() == NotUseAtr.USE) return false;
			}
		}
		else{
			Optional<PremiumCalcMethodDetailOfHoliday> advanceSet = holidayCalcMethodSet.getPremiumCalcMethodOfHoliday().getAdvanceSet();
			if (advanceSet.isPresent()){
				if (advanceSet.get().getCalculateIncludCareTime() == NotUseAtr.USE) return true;
			}
		}
		return true;
	}
	
	/**
	 * 日別実績(Work)から育児介護区分を取得する
	 * @param integrationOfDaily 日別実績(Work)
	 * @return 育児介護区分
	 */
	public static ChildCareAtr getChildCareAttributeToDaily(IntegrationOfDaily integrationOfDaily) {
		if(integrationOfDaily.getShortTime().isPresent()) {
			val firstTimeSheet = integrationOfDaily.getShortTime().get().getShortWorkingTimeSheets().stream().findFirst();
			if(firstTimeSheet.isPresent()) {
				return firstTimeSheet.get().getChildCareAttr();
			}
		}
		if(integrationOfDaily.getAttendanceTimeOfDailyPerformance().isPresent()) {
			return integrationOfDaily.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getShotrTimeOfDaily().getChildCareAttribute();
		}
		return ChildCareAtr.CHILD_CARE;
	}
	
}
