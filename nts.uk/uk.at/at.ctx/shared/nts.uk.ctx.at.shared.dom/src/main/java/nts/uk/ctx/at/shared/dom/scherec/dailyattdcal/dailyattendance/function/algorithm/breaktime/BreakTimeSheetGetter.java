package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.breaktime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionClassification;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours.WithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.JustCorrectionAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * ?????????????????????????????????????????????
 */
public class BreakTimeSheetGetter {

	/** ????????????????????? */
	public static List<BreakTimeSheet> get(RequireM1 require,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			IntegrationOfDaily domainDaily, boolean correctWithEndTime) {
		

		
		val cid = AppContexts.user().companyId();
		
		val workType = require.workType(cid, domainDaily.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).orElse(null);
		if (workType == null) {
			return new ArrayList<>();
		}
		/** ???????????????????????? */
		if (workType.getAttendanceHolidayAttr() == AttendanceHolidayAttr.HOLIDAY) {
			
			/** ???????????????????????? */
			return new ArrayList<>();
		}
		
		/** require.???????????????????????? */
		val workTimeSet = getWorkTime(require, cid, domainDaily);
		
		/** ???????????????=NULL???????????? */
		if(workTimeSet == null) {
			return new ArrayList<>();
		}
		if (!workTimeSet.isFixBreak(workType) && !domainDaily.getAttendanceLeave().isPresent()) {
			return new ArrayList<>();
		}
		/** ????????????????????????????????????????????? */
		val oneDayCalcRange = require.createOneDayRange(require.predetemineTimeSetting(cid, workTimeSet.getCode().v()), 
				domainDaily, Optional.of(workTimeSet.getCommonSetting()), 
				workType, JustCorrectionAtr.USE, Optional.of(workTimeSet.getCode()));
		
		List<TimeSheetOfDeductionItem> deductionTimeSheet = new ArrayList<>();
		
		switch (workTimeSet.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm()) {
		case FIXED: /** ???????????? */
			deductionTimeSheet = oneDayCalcRange.getDeductionTimeSheetOnFixed(
					workType, workTimeSet, domainDaily, companyCommonSetting, personDailySetting, correctWithEndTime);
			break;
		case FLEX: /** ????????????????????? */
			
			if(workTimeSet.isFixBreak(workType)) {
				/** ???????????? */
				deductionTimeSheet = oneDayCalcRange.getDeductionTimeSheetOnFixed(
						workType, workTimeSet, domainDaily, companyCommonSetting, personDailySetting, correctWithEndTime);
				
			} else  {
				
				/** ???????????? */
				deductionTimeSheet = getDeductionTimeSheetOnFlexFlow(require, workType, workTimeSet, 
						domainDaily, oneDayCalcRange, companyCommonSetting, personDailySetting, correctWithEndTime);
			}
			break;
		case FLOW: /** ?????? */
			if(workTimeSet.isFixBreak(workType)) {
				deductionTimeSheet =  oneDayCalcRange.getDeductionTimeSheetOnFixed(workType, workTimeSet, domainDaily,
						companyCommonSetting, personDailySetting, correctWithEndTime);
			}
			else {
				WithinWorkTimeSheet withinWorkTimeSheet = new WithinWorkTimeSheet(new ArrayList<>(), new ArrayList<>(), 
						Optional.empty(), Optional.empty());	
				
				/** ????????????????????? */
				deductionTimeSheet = oneDayCalcRange.prePocessForFlowCorrect(
						companyCommonSetting, personDailySetting, workType, workTimeSet, domainDaily, 
						domainDaily.getAttendanceLeave().get(), 
						withinWorkTimeSheet, correctWithEndTime);	
			}
			
			
			break;
		default:
			
			deductionTimeSheet = new ArrayList<>();
			break;
		}
		
		/** ???????????????????????? */ 
		deductionTimeSheet = deductionTimeSheet.stream().filter(c -> c.getDeductionAtr() == DeductionClassification.BREAK)
				.collect(Collectors.toList());
		
		List<BreakTimeSheet> breakTimeSheet = new ArrayList<>();
		for(int idx = 0; idx < deductionTimeSheet.size(); idx++) {
			val dudectionSheet = deductionTimeSheet.get(idx);
			
			breakTimeSheet.add(new BreakTimeSheet(new BreakFrameNo(idx + 1),
							   dudectionSheet.getTimeSheet().getStart(), 
							   dudectionSheet.getTimeSheet().getEnd()));
		}
		
		/** ???????????????????????? */
		return breakTimeSheet;
	}
	
	private static List<TimeSheetOfDeductionItem> getDeductionTimeSheetOnFlexFlow(RequireM3 require, WorkType workType,
			IntegrationOfWorkTime workTime, IntegrationOfDaily integrationOfDaily,
			CalculationRangeOfOneDay oneDayCalcRange,
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			boolean correctWithEndTime) {
		
		if (!integrationOfDaily.getAttendanceLeave().isPresent()) {
			return new ArrayList<>();
		}
		
		val attendanceLeaveWorks = integrationOfDaily.getAttendanceLeave().map(c -> c.getTimeLeavingWorks()).orElse(new ArrayList<>());
		
		val withinWorkTimeSheet = new WithinWorkTimeSheet(new ArrayList<>(), new ArrayList<>(), Optional.empty(), Optional.empty());
		
		List<TimeLeavingWork> calcLateTimeLeavingWorksWorks = new ArrayList<>();
		for(TimeLeavingWork timeLeavingWork : attendanceLeaveWorks) {
			calcLateTimeLeavingWorksWorks.add(
					oneDayCalcRange.calcLateTimeSheet(workType, workTime, integrationOfDaily,
							new DeductionTimeSheet(
									new ArrayList<>(),
									new ArrayList<>(),
									new BreakTimeOfDailyAttd(),
									Optional.empty(),
									new ArrayList<>()),
							personDailySetting.getAddSetting().getVacationCalcMethodSet(),
							timeLeavingWork, 
							withinWorkTimeSheet));
		}
		
		val attendanceLeave = new TimeLeavingOfDailyAttd(calcLateTimeLeavingWorksWorks, new WorkTimes(calcLateTimeLeavingWorksWorks.size()));
		
		val lateTimeSheet = withinWorkTimeSheet.getWithinWorkTimeFrame().stream()
												.filter(c -> c.getLateTimeSheet().isPresent())
												.map(c -> c.getLateTimeSheet().get())
												.collect(Collectors.toList());
		
		/** ????????????????????????????????? */
		val timeSheet = oneDayCalcRange.provisionalDeterminationOfDeductionTimeSheet(
				workType, workTime, integrationOfDaily, 
				oneDayCalcRange.getOneDayOfRange(), Optional.of(attendanceLeave), 
				oneDayCalcRange.getPredetermineTimeSetForCalc(), lateTimeSheet, correctWithEndTime, Optional.empty(),
				companyCommonSetting, personDailySetting);
		
		return timeSheet.getForDeductionTimeZoneList();
	}
	
	private static IntegrationOfWorkTime getWorkTime(RequireM2 require, String cid, IntegrationOfDaily domainDaily) {
		/** require.???????????????????????? */
		val workTimeSet = domainDaily.getWorkInformation().getRecordInfo()
				.getWorkTimeCodeNotNull()
				.flatMap(c -> require.workTimeSetting(cid, c.v()))
				.orElse(null);
		
		/** ???????????????=NULL???????????? */
		if(workTimeSet == null) {
			return null;
		}
		
		switch(workTimeSet.getWorkTimeDivision().getWorkTimeForm()) {
			case FIXED:				
				return new IntegrationOfWorkTime(workTimeSet.getWorktimeCode(), workTimeSet, 
						                         require.fixedWorkSetting(cid, workTimeSet.getWorktimeCode().v()).get());
			case FLEX:				
				return new IntegrationOfWorkTime(workTimeSet.getWorktimeCode(), workTimeSet, 
												 require.flexWorkSetting(cid, workTimeSet.getWorktimeCode().v()).get());
			case FLOW:				
				return new IntegrationOfWorkTime(workTimeSet.getWorktimeCode(), workTimeSet, 
						                         require.flowWorkSetting(cid, workTimeSet.getWorktimeCode().v()).get());
			case TIMEDIFFERENCE:	
				throw new RuntimeException("Unimplemented");/*???????????????????????????????????????2020/5/19 ??????*/
			default:				
				throw new RuntimeException("Non-conformity No Work");
		}
	}

	public static interface RequireM3 {

		Optional<PredetemineTimeSetting> predetemineTimeSetting(String cid, String workTimeCode);
	}

	public static interface RequireM2 {
		
		Optional<WorkTimeSetting> workTimeSetting(String companyId, String workTimeCode);
		
		Optional<FixedWorkSetting> fixedWorkSetting(String companyId, String workTimeCode);
		
		Optional<FlowWorkSetting> flowWorkSetting(String companyId, String workTimeCode);
		
		Optional<FlexWorkSetting> flexWorkSetting(String companyId,String workTimeCode);
	}

	public static interface RequireM1 extends RequireM2, RequireM3 {
		
		Optional<WorkType> workType(String companyId, String workTypeCd);
		
		CalculationRangeOfOneDay createOneDayRange(Optional<PredetemineTimeSetting> predetemineTimeSet,
				IntegrationOfDaily integrationOfDaily, Optional<WorkTimezoneCommonSet> commonSet,
				WorkType workType, JustCorrectionAtr justCorrectionAtr, Optional<WorkTimeCode> workTimeCode);
	}
}
