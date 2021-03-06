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
 * ????????????????????????????????????
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
	 * ????????????????????????????????????
	 * @param recordClass ??????
	 * @param premiumAtr ????????????
	 * @return ????????????????????????????????????
	 */
	public static ShortWorkTimeOfDaily calcShortWorkTime(
			ManageReGetClass recordClass,
			PremiumAtr premiumAtr) {
		
		WorkTimes workTimes = new WorkTimes(0);
		DeductionTotalTime totalTime = DeductionTotalTime.defaultValue();
		DeductionTotalTime totalDeductionTime = DeductionTotalTime.defaultValue();
		ChildCareAtr careAtr = getChildCareAttributeToDaily(recordClass.getIntegrationOfDaily());
		ShortWorkTimeOfDaily zeroValue = new ShortWorkTimeOfDaily(workTimes, totalTime, totalDeductionTime, careAtr);
		
		// ?????????????????????
		if (!recordClass.getWorkType().isPresent()) return zeroValue;
		WorkType workType = recordClass.getWorkType().get();
		// ??????????????????????????????
		if (workType.isWorkingDay() == false) return zeroValue;
		
		if(recordClass.getCalculatable() && recordClass.getIntegrationOfDaily().getShortTime().isPresent()){
			//?????????????????????
			workTimes = new WorkTimes(recordClass.getIntegrationOfDaily().getShortTime().get().getShortWorkingTimeSheets().stream()
					.filter(tc -> tc.getChildCareAttr().equals(careAtr))
					.collect(Collectors.toList())
					.size());
			
			//?????????????????????
			totalTime = calcTotalShortWorkTime(recordClass, DeductionAtr.Appropriate, careAtr, premiumAtr);
			
			//?????????????????????
			totalDeductionTime = calcTotalShortWorkTime(recordClass, DeductionAtr.Deduction, careAtr, premiumAtr);
		}
		return new ShortWorkTimeOfDaily(workTimes, totalTime, totalDeductionTime, careAtr);
	}
	
	public static WorkTimes calcWorkTimes(ManageReGetClass recordClass,ConditionAtr condition) {
		
		List<TimeSheetOfDeductionItem> list = new ArrayList<>();
		DeductionAtr dedAtr = DeductionAtr.Appropriate;
		//????????????????????????
		WithinWorkTimeSheet withinWorkTimeSheet = recordClass.getCalculationRangeOfOneDay().getWithinWorkingTimeSheet().get();
		//??????????????????????????????
		list.addAll(withinWorkTimeSheet.getShortTimeSheet().stream().filter(tc -> tc.calcTotalTime().greaterThan(0)).collect(Collectors.toList()));
		
		for(WithinWorkTimeFrame withinWorkTimeFrame:withinWorkTimeSheet.getWithinWorkTimeFrame()) {
			list.addAll(withinWorkTimeFrame.getDedTimeSheetByAtr(dedAtr, condition));
			//??????
			if(withinWorkTimeFrame.getLateTimeSheet().isPresent()&&withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLateTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(dedAtr, condition));
			}
			//??????
			if(withinWorkTimeFrame.getLeaveEarlyTimeSheet().isPresent()&&withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
				list.addAll(withinWorkTimeFrame.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().get().getDedTimeSheetByAtr(dedAtr, condition));
			}
		}
		//????????????????????????
		Optional<OutsideWorkTimeSheet> outsideWorkTimeSheet = Optional.empty();
		if(recordClass.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().isPresent()) {
			recordClass.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get();
		}
		//??????
		if(outsideWorkTimeSheet.isPresent()) {
			if(outsideWorkTimeSheet.get().getOverTimeWorkSheet().isPresent()) {
				for(OverTimeFrameTimeSheetForCalc overTimeFrameTimeSheetForCalc:outsideWorkTimeSheet.get().getOverTimeWorkSheet().get().getFrameTimeSheets()) {
					list.addAll(overTimeFrameTimeSheetForCalc.getDedTimeSheetByAtr(dedAtr, condition));
				}
			}
			//??????
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
	 * ??????????????????????????????
	 * @param recordClass ??????
	 * @param dedAtr ????????????
	 * @param careAtr ??????????????????
	 * @param premiumAtr ????????????
	 * @return ??????????????????
	 */
	public static DeductionTotalTime calcTotalShortWorkTime(
			ManageReGetClass recordClass,
			DeductionAtr dedAtr,
			ChildCareAtr careAtr,
			PremiumAtr premiumAtr){
		
		DeductionTotalTime result = DeductionTotalTime.defaultValue();
		
		// ????????????????????????????????????????????????????????????
		if (decisionDeductChild(dedAtr, premiumAtr, recordClass.getHolidayCalcMethodSet())){
			CalculationRangeOfOneDay oneDay = recordClass.getCalculationRangeOfOneDay();
			ConditionAtr conditionAtr = (careAtr.isChildCare() ? ConditionAtr.Child : ConditionAtr.Care);
			// ??????????????????????????????
			TimeWithCalculation withinTime = oneDay.getDeductionTime(
					conditionAtr, dedAtr, StatutoryAtr.Statutory, TimeSheetRoundingAtr.ALL, Optional.empty(), NotUseAtr.NOT_USE);
			// ??????????????????????????????
			TimeWithCalculation excessTime = oneDay.getDeductionTime(
					conditionAtr, dedAtr, StatutoryAtr.Excess, TimeSheetRoundingAtr.ALL, Optional.empty(), NotUseAtr.NOT_USE);
			// ?????????????????????
			result = DeductionTotalTime.of(
					withinTime.addMinutes(excessTime.getTime(), excessTime.getCalcTime()),
					withinTime,
					excessTime);
		}
		if (dedAtr.isAppropriate()){
			if (recordClass.getCalculationRangeOfOneDay().getShortTimeWSWithoutWork().isPresent()){
				ShortTimeWorkSheetWithoutWork withoutWork =
						recordClass.getCalculationRangeOfOneDay().getShortTimeWSWithoutWork().get();
				// ????????????????????????????????????????????????????????????
				ConditionAtr conditionAtr = ConditionAtr.Child;
				if (careAtr.isCare()) conditionAtr = ConditionAtr.Care;
				AttendanceTime withinTime = withoutWork.sumShortWorkTimeWithoutWork(
						conditionAtr, TimeSheetRoundingAtr.ALL, Optional.empty(), true);
				// ????????????????????????????????????????????????????????????
				AttendanceTime withoutTime = withoutWork.sumShortWorkTimeWithoutWork(
						conditionAtr, TimeSheetRoundingAtr.ALL, Optional.empty(), false);
				
				AttendanceTime totalTime = withinTime.addMinutes(withoutTime.valueAsMinutes());
				result = DeductionTotalTime.of(
						result.getTotalTime().addMinutes(totalTime, totalTime),
						result.getWithinStatutoryTotalTime().addMinutes(withinTime, withinTime),
						result.getExcessOfStatutoryTotalTime().addMinutes(withoutTime, withoutTime));
			}
		}
		// ???????????????????????????
		return result;
	}
	
	/**
	 * ????????????????????????????????????????????????????????????
	 * @param dedAtr ????????????
	 * @param premiumAtr ????????????
	 * @param holidayCalcMethodSet ??????????????????????????????
	 * @return true:????????????,false:???????????????
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
	 * ????????????(Work)???????????????????????????????????????
	 * @param integrationOfDaily ????????????(Work)
	 * @return ??????????????????
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
