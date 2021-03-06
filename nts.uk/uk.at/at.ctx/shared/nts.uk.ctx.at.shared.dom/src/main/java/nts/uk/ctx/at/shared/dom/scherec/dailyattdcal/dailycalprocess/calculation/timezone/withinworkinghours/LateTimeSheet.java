package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.holidaypriorityorder.CompanyHolidayPriorityOrder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.ootsuka.OotsukaStaticService;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OtherEmTimezoneLateEarlySet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * ???????????????
 * @author keisuke_hoshina
 */
@AllArgsConstructor
@Getter
public class LateTimeSheet {
	
	// ??????????????????????????????empty
	//??????????????????
	private Optional<LateLeaveEarlyTimeSheet> forRecordTimeSheet;
	
	//??????????????????
	private Optional<LateLeaveEarlyTimeSheet> forDeducationTimeSheet;
	
	//??????No
	private int workNo;
	
	//???????????????????????????????????????
	private Optional<AttendanceTime> noCoreFlexLateTime = Optional.empty();
	
	public LateTimeSheet(
			Optional<LateLeaveEarlyTimeSheet> recordTimeSheet,
			Optional<LateLeaveEarlyTimeSheet> deductionTimeSheet,
			int workNo) {
		
		this.forRecordTimeSheet = recordTimeSheet;
		this.forDeducationTimeSheet = deductionTimeSheet;
		this.workNo = workNo;
	}
	
	/**
	 * ??????????????????????????????????????????
	 * @param dedAtr
	 * @return
	 */
	public Optional<LateLeaveEarlyTimeSheet> getDecitionTimeSheet(DeductionAtr dedAtr){
		if(dedAtr.isAppropriate()) {
			return this.forRecordTimeSheet;
		}
		return this.forDeducationTimeSheet;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * @param dedAtr
	 * @return
	 */
	public void setDecitionTimeSheet(DeductionAtr dedAtr, Optional<LateLeaveEarlyTimeSheet> forTimeSheet){
		if(dedAtr.isAppropriate()) {
			this.forRecordTimeSheet = forTimeSheet;
		}
		this.forDeducationTimeSheet = forTimeSheet;
	}
	
	/**
	 * ????????????????????????
	 * @param lateDesClock ??????????????????
	 * @param timeLeavingWork ?????????
	 * @param otherEmTimezoneLateEarlySet ??????????????????????????????????????????
	 * @param withinWorkTimeFrame ????????????????????????
	 * @param deductionTimeSheet ???????????????
	 * @param predetermineTimeSet ??????????????????
	 * @param workNo ??????NO
	 * @param workType ????????????
	 * @param predetermineTimeSetForCalc ???????????????????????????
	 * @param integrationOfWorkTime ?????????????????????
	 * @param integrationOfDaily ????????????(Work)
	 * @return ???????????????
	 */
	public static Optional<LateTimeSheet> createLateTimeSheet(
			Optional<LateDecisionClock> lateDesClock,
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimezoneUse> predetermineTimeSet,
			int workNo,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		//????????????
		Optional<TimeWithDayAttr> attendance = timeLeavingWork.getAttendanceTime();
		if(!attendance.isPresent() || !lateDesClock.isPresent() || !predetermineTimeSet.isPresent()) {
			return Optional.empty();
		}
		
		//???????????????
		Optional<LateLeaveEarlyTimeSheet> lateAppTimeSheet = Optional.empty();
		//?????????????????????
		Optional<LateLeaveEarlyTimeSheet> lateDeductTimeSheet = Optional.empty();
		
		//??????????????????????????????????????????	
		if(lateDesClock.get().getLateDecisionClock().lessThan(attendance.get())) {
			//??????????????????????????????
			lateDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
			//????????????????????????
			lateAppTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Appropriate,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}else {
			//??????????????????????????????
			lateDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}
		if(!lateAppTimeSheet.isPresent() && !lateDeductTimeSheet.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LateTimeSheet(lateAppTimeSheet, lateDeductTimeSheet, workNo));
	}
	
	/**
	 * ????????????
	 * @param deductionAtr ????????????
	 * @param timeLeavingWork ?????????
	 * @param integrationOfWorkTime ?????????????????????
	 * @param integrationOfDaily ????????????(Work)
	 * @param predetermineTimeSet ???????????????????????????????????????
	 * @param withinWorkTimeFrame ????????????????????????
	 * @param deductionTimeSheet ???????????????
	 * @param workType ????????????
	 * @param predetermineTimeForSet ??????????????????
	 * @param otherEmTimezoneLateEarlySet ??????????????????????????????????????????
	 * @return ?????????????????????
	 */
	private static Optional<LateLeaveEarlyTimeSheet> createLateLeaveEarlyTimeSheet(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimezoneUse predetermineTimeSet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){

		// ??????????????????????????????
		DeductionTimeSheet deductForLateEarly = new DeductionTimeSheet(
				new ArrayList<>(deductionTimeSheet.getForDeductionTimeZoneList()),
				new ArrayList<>(deductionTimeSheet.getForRecordTimeZoneList()),
				deductionTimeSheet.getBreakTimeOfDailyList(),
				deductionTimeSheet.getDailyGoOutSheet(),
				deductionTimeSheet.getShortTimeSheets());
		// ???????????????????????????????????????
		List<TimeSheetOfDeductionItem> ootsukaBreak = OotsukaStaticService.getBreakTimeSheet(
				workType, integrationOfWorkTime, integrationOfDaily.getAttendanceLeave());
		deductForLateEarly.getForDeductionTimeZoneList().addAll(ootsukaBreak);
		deductForLateEarly.getForRecordTimeZoneList().addAll(ootsukaBreak);
		// ??????????????????????????????
		Optional<LateLeaveEarlyTimeSheet> instance = createLateTimeSheetInstance(
				deductionAtr,
				timeLeavingWork,
				integrationOfWorkTime,
				predetermineTimeSet,
				withinWorkTimeFrame,
				deductForLateEarly,
				workType,
				predetermineTimeForSet,
				otherEmTimezoneLateEarlySet);
		
		return instance;
	}
	
	/**
	 * ??????????????????????????????
	 * @param deductionAtr ????????????
	 * @param timeLeavingWork ?????????
	 * @param integrationOfWorkTime ?????????????????????
	 * @param predetermineTimeSet ???????????????????????????????????????
	 * @param withinWorkTimeFrame ????????????????????????
	 * @param deductTimeSheet ??????????????????????????????
	 * @param workType ????????????
	 * @param predetermineTimeForSet ??????????????????
	 * @param otherEmTimezoneLateEarlySet ??????????????????????????????????????????
	 * @return ?????????????????????
	 */
	private static Optional<LateLeaveEarlyTimeSheet> createLateTimeSheetInstance(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			TimezoneUse predetermineTimeSet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductTimeSheet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){

		// ???????????????????????????
		Optional<TimeSpanForDailyCalc> calcRange = LateDecisionClock.getCalcRange(
				predetermineTimeSet, timeLeavingWork, integrationOfWorkTime,
				predetermineTimeForSet, workType.getDailyWork().decisionNeedPredTime());
		if (!calcRange.isPresent()) return Optional.empty();
		TimeWithDayAttr lateStartClock = calcRange.get().getStart();
		// ?????????????????????????????????????????????
		Optional<LateLeaveEarlyTimeSheet> beforeAdjustOpt = checkTimeSheetForCalcLateTime(
				deductionAtr, timeLeavingWork, integrationOfWorkTime,
				deductTimeSheet, lateStartClock, withinWorkTimeFrame, otherEmTimezoneLateEarlySet);
		if (!beforeAdjustOpt.isPresent()) return Optional.empty();
		LateLeaveEarlyTimeSheet beforeAdjust = beforeAdjustOpt.get();
		// ????????????????????????????????????????????????
		AttendanceTime beforeRounding = beforeAdjust.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE);
		AttendanceTime afterRounding = beforeAdjust.calcTotalTime();
		// ???????????????????????????
		TimeWithDayAttr lateEndClock = beforeAdjust.getLateEndTime(beforeRounding, afterRounding, deductTimeSheet);
		// ??????????????????????????????
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(lateStartClock, lateEndClock), beforeAdjust.getRounding());
		// ????????????????????????
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.Late,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// ??????????????????????????????
		return Optional.of(result);
	}

	/**
	 * ?????????????????????????????????????????????
	 * @param deductionAtr ????????????
	 * @param timeLeavingWork ?????????
	 * @param integrationOfWorkTime ?????????????????????
	 * @param deductTimeSheet ???????????????
	 * @param lateStartClock ??????????????????
	 * @param withinWorkTimeFrame ????????????????????????
	 * @param otherEmTimezoneLateEarlySet ??????????????????????????????????????????
	 * @return ?????????????????????
	 */
	private static Optional<LateLeaveEarlyTimeSheet> checkTimeSheetForCalcLateTime(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			DeductionTimeSheet deductTimeSheet,
			TimeWithDayAttr lateStartClock,
			WithinWorkTimeFrame withinWorkTimeFrame,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){
		
		// ???????????????????????????
		TimeWithDayAttr attendance = null;
		if (timeLeavingWork.getAttendanceStamp().isPresent()){
			TimeActualStamp attdStamp = timeLeavingWork.getAttendanceStamp().get();
			if (attdStamp.getStamp().isPresent()){
				WorkStamp stamp = attdStamp.getStamp().get();
				if (stamp.getTimeDay().getTimeWithDay().isPresent()) {
					attendance = stamp.getTimeDay().getTimeWithDay().get();
				}
			}
		}
		if (attendance == null) return Optional.empty();
		// ?????????????????????input.??????????????????
		TimeWithDayAttr start = lateStartClock;
		// ?????????????????????????????????
		TimeWithDayAttr end = attendance;
		if (attendance.greaterThan(withinWorkTimeFrame.getTimeSheet().getEnd())){
			// ????????????????????????????????????????????????????????????
			end = withinWorkTimeFrame.getTimeSheet().getEnd();
		}
		// ?????????????????????
		TimeRoundingSetting roundingSet = otherEmTimezoneLateEarlySet.getRoundingSetByDedAtr(deductionAtr.isDeduction());
		// ??????????????????????????????
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(start, end),
				new TimeRoundingSetting(roundingSet.getRoundingTime(), roundingSet.getRounding()));
		// ????????????????????????
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.Late,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// ??????????????????????????????
		return Optional.of(result);
	}
	
	/**
	 * ???????????????????????????
	 * @param late ????????????
	 * @param deductOffset ????????????????????????
	 * @param roundAtr ???????????????
	 * @return ??????????????????
	 */
	public TimeWithCalculation calcForRecordTime(boolean late, boolean deductOffset, NotUseAtr roundAtr){
		//?????????????????????
		AttendanceTime calcforRecordTime = AttendanceTime.ZERO;
		//???????????????0???00
		AttendanceTime lateTimeForRecord = AttendanceTime.ZERO;
		if (!this.forRecordTimeSheet.isPresent()) {
			return TimeWithCalculation.createTimeWithCalculation(lateTimeForRecord, calcforRecordTime);
		}
		// ?????????????????????
		if (late) {
			// ?????????????????????
			lateTimeForRecord = this.forRecordTimeSheet.get()
					.calcTotalTime(deductOffset ? NotUseAtr.USE : NotUseAtr.NOT_USE, roundAtr);
		}

		// ???????????????????????????
		calcforRecordTime = this.forRecordTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, roundAtr);
		
		// ?????????????????????????????????????????????
		return TimeWithCalculation.createTimeWithCalculation(lateTimeForRecord, calcforRecordTime);
		
	}
	
	/**
	 * ???????????????????????????
	 * @param late ????????????
	 * @param notUseAtr ??????????????????
	 * @return ??????????????????
	 */
	public TimeWithCalculation calcDedctionTime(boolean late, NotUseAtr notUseAtr) {
		TimeWithCalculation lateDeductionTime = TimeWithCalculation.sameTime(new AttendanceTime(0));
		if(notUseAtr==NotUseAtr.USE) {//??????????????????
			AttendanceTime calcDeductionTime = this.forDeducationTimeSheet.isPresent() ?
					this.forDeducationTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, NotUseAtr.USE) :
						new AttendanceTime(0);
			lateDeductionTime =  late ?
					TimeWithCalculation.sameTime(calcDeductionTime) :
						TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(0),calcDeductionTime);
		}
		return lateDeductionTime;
	}
	
	/**
	 * ???????????????????????????????????????(??????)?????????
	 * @return???????????????????????????
	 */
	public List<TimeSheetOfDeductionItem> getShortTimeSheet(){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		if(this.getForRecordTimeSheet() != null && this.getForRecordTimeSheet().isPresent()) {
			returnList.addAll(this.getForRecordTimeSheet().get().collectShortTimeSheet());
		}
		return returnList;
	}
	
	/**
	 * ?????????????????????????????????
	 * @param deductionAtr ?????? or ??????
	 * @param companyholidayPriorityOrder ??????????????????????????????
	 * @param timeVacationUseTime ???????????????????????????????????????
	 */
	public void offsetVacationTime(
			DeductionAtr deductionAtr,
			CompanyHolidayPriorityOrder companyholidayPriorityOrder,
			TimevacationUseTimeOfDaily timeVacationUseTime){
		
		if (!this.getDecitionTimeSheet(deductionAtr).isPresent()) return;
		// ????????????
		timeVacationUseTime = this.getDecitionTimeSheet(deductionAtr).get().offsetProcess(
				companyholidayPriorityOrder, timeVacationUseTime, NotUseAtr.NOT_USE);
	}
	
	/**
	 * ????????????????????????????????????
	 * @param timeSpan ?????????
	 * @param deductionTimeSheet ???????????????
	 * @param commonSet ??????????????????????????????
	 * @return ???????????????
	 */
	public Optional<LateTimeSheet> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, DeductionTimeSheet deductionTimeSheet, WorkTimezoneCommonSet commonSet) {
		//?????????????????????????????????
		Optional<LateLeaveEarlyTimeSheet> record = this.forRecordTimeSheet.flatMap(
				r -> r.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.Late, deductionTimeSheet, commonSet));
		//?????????????????????????????????
		Optional<LateLeaveEarlyTimeSheet> deducation = this.forDeducationTimeSheet.flatMap(
				d -> d.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.Late, deductionTimeSheet, commonSet));
		if(!record.isPresent() && !deducation.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LateTimeSheet(
				record,
				deducation,
				this.workNo,
				this.noCoreFlexLateTime.map(n -> new AttendanceTime(n.valueAsMinutes()))));
	}
}
