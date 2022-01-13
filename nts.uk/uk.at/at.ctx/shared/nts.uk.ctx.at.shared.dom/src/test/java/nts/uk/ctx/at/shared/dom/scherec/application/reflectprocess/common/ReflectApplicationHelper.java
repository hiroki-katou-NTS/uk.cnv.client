package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationDateShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.scherec.application.holidayworktime.AppHolidayWorkShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AppOverTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.ApplicationTimeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.AttendanceTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.OverTimeShiftNightShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.OvertimeApplicationSettingShare;
import nts.uk.ctx.at.shared.dom.scherec.application.overtime.ReasonDivergenceShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppStampShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.DestinationTimeAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.DestinationTimeZoneAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.EngraveShareAtr;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppEnumShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppOtherShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeZoneStampClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AttendanceBeforeApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeGoOutTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DiverdenceReasonCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceReasonContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AbsenceOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.AnnualOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.OverSalaryOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SpecialHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.SubstituteHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TimeDigestOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.TransferHolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.YearlyReservedOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ConstraintTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.IntervalExemptionTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WithinOutingTotalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheet;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.DesignatedTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OneDayTime;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSet;
import nts.uk.ctx.at.shared.dom.worktime.common.SubHolTransferSetAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.AttendanceClock;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.com.time.TimeZone;

public class ReflectApplicationHelper {

	public static DailyRecordOfApplication createDailyRecord(ScheduleRecordClassifi classification) {

		return createDailyRecord(classification, 1);
	}

	public static DailyRecordOfApplication createDailyRecord(List<AttendanceBeforeApplicationReflect> lstAtt) {
		DailyRecordOfApplication result = createDailyRecord(ScheduleRecordClassifi.SCHEDULE);
		result.setAttendanceBeforeReflect(lstAtt);
		return result;
	}

	public static DailyRecordOfApplication createDailyRecord(ScheduleRecordClassifi classification, int numberSheet) {

		List<ScheduleTimeSheet> scheduleTimeSheets = IntStream.range(1, numberSheet + 1).boxed().map(x -> {
			return new ScheduleTimeSheet(x, 480, 1020);
		}).collect(Collectors.toList());
		IntegrationOfDaily domainDaily = new IntegrationOfDaily(
				new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"),
						CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use,
						DayOfWeek.FRIDAY, scheduleTimeSheets, Optional.empty()),
				null, null, Optional.empty(), new ArrayList<>(), Optional.empty(), new BreakTimeOfDailyAttd(), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				new ArrayList<>(), Optional.empty(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Optional.empty());
		return new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily);
	}

	public static DailyRecordOfApplication createRCWithTimeLeav(ScheduleRecordClassifi classification, int no) {
		return createRCWithTimeLeav(classification, no, TimeChangeMeans.AUTOMATIC_SET);
	}
	
	public static DailyRecordOfApplication createRCWithTimeLeav(ScheduleRecordClassifi classification, int no, TimeChangeMeans change) {

		TimeLeavingWork work = new TimeLeavingWork(new WorkNo(no), null, null);
		work.setAttendanceStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(
						new WorkTimeInformation(new ReasonTimeChange(change, null),
								new TimeWithDayAttr(480)),
						Optional.empty()),
				0)));

		work.setLeaveStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(
						new WorkTimeInformation(new ReasonTimeChange(change, null),
								new TimeWithDayAttr(1200)),
						Optional.empty()),
				0)));

		List<TimeLeavingWork> timeLeavingWorks = new ArrayList<TimeLeavingWork>();
		timeLeavingWorks.add(work);
		Optional<TimeLeavingOfDailyAttd> attendanceLeave = Optional
				.of(new TimeLeavingOfDailyAttd(timeLeavingWorks, new WorkTimes(1)));

		IntegrationOfDaily domainDaily = new IntegrationOfDaily(
				new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"),
						CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use,
						DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty()),
				null, null, Optional.empty(), new ArrayList<>(), Optional.empty(), new BreakTimeOfDailyAttd(), Optional.empty(),
				attendanceLeave, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				new ArrayList<>(), Optional.empty(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Optional.empty());
		return new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily);
	}

	public static DailyRecordOfApplication createRCWithTimeLeavFull(ScheduleRecordClassifi classification, Integer no) {
		
		////日別実績の所定外時間
		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		if(no != null) {
			overTimeWorkFrameTime.add(OverTimeFrameTime.createDefaultWithNo(no));
		}
		
		List<OverTimeFrameTimeSheet> frameTimeSheetList = new ArrayList<OverTimeFrameTimeSheet>();
		
		return createRCWithTimeLeavOverTime(classification, no, overTimeWorkFrameTime, frameTimeSheetList, new ArrayList<>(), new ArrayList<>());
	}
	
	public static DailyRecordOfApplication createRCWithTimeLeavOverTime() {

		List<OverTimeFrameTime> overTimeWorkFrameTime = new ArrayList<OverTimeFrameTime>();
		overTimeWorkFrameTime.add(OverTimeFrameTime.createDefaultWithNo(1));
		new OverTimeFrameTime(new OverTimeFrameNo(1), 
				TimeDivergenceWithCalculation.sameTime(new AttendanceTime(555)), 
				TimeDivergenceWithCalculation.sameTime(new AttendanceTime(666)), 
				new AttendanceTime(556), 
				new AttendanceTime(557));

		List<OverTimeFrameTimeSheet> frameTimeSheetList = new ArrayList<OverTimeFrameTimeSheet>();
		frameTimeSheetList.add(new OverTimeFrameTimeSheet(
				new TimeSpanForDailyCalc(new TimeWithDayAttr(444), new TimeWithDayAttr(1111)), new OverTimeFrameNo(1)));
		return createRCWithTimeLeavOverTime(ScheduleRecordClassifi.RECORD, 1, overTimeWorkFrameTime,
				frameTimeSheetList, new ArrayList<>(), new ArrayList<>());
	}

	public static DailyRecordOfApplication createSimpleAttLeav(ScheduleRecordClassifi classification, int no, 
			Function<Integer, List<Pair<Integer, Integer>>> pairAttLeav, String location) {
		List<Pair<Integer, Integer>> allAttLeav = pairAttLeav.apply(no);
		List<TimeLeavingWork> timeLeavingWorks = new ArrayList<TimeLeavingWork>();
		int index = 1;
		for (Pair<Integer, Integer> attLeav : allAttLeav) {
			TimeLeavingWork work = new TimeLeavingWork(new WorkNo(index), null, null);
			work.setAttendanceStamp(Optional.of(new TimeActualStamp(null,
					new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
							new TimeWithDayAttr(attLeav.getLeft())), Optional.of(new WorkLocationCD(location))),
					0)));

			work.setLeaveStamp(Optional.of(new TimeActualStamp(null,
					new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
							new TimeWithDayAttr(attLeav.getRight())), Optional.of(new WorkLocationCD(location))),
					0)));

			timeLeavingWorks.add(work);
			index++;
		}
		Optional<TimeLeavingOfDailyAttd> attendanceLeave = Optional
				.of(new TimeLeavingOfDailyAttd(timeLeavingWorks, new WorkTimes(1)));

		IntegrationOfDaily domainDaily = new IntegrationOfDaily(
				new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"), CalculationState.No_Calculated,
						NotUseAttribute.Not_use, NotUseAttribute.Not_use, DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty()),
				null, null, Optional.empty(), new ArrayList<>(), Optional.empty(), new BreakTimeOfDailyAttd(),
				Optional.empty(), attendanceLeave, Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), new ArrayList<>(), Optional.empty(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Optional.empty());
		return new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily);
	}

	public static DailyRecordOfApplication addTimeLeavNo(int no) {
		return addTimeLeavNo(no, createRCWithTimeLeavOverTime());
	}
	
	public static IntegrationOfDaily afterCalc(IntegrationOfDaily dom, int time, int transferTime) {
		HolidayWorkTimeOfDaily holiday = dom.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get();
		holiday.getHolidayWorkFrameTime().get(0).setHolidayWorkTime(Finally.of(TimeDivergenceWithCalculation
				.createTimeWithCalculation(new AttendanceTime(time), new AttendanceTime(300))));
		holiday.getHolidayWorkFrameTime().get(0).setTransferTime(Finally.of(TimeDivergenceWithCalculation
				.createTimeWithCalculation(new AttendanceTime(transferTime), new AttendanceTime(transferTime))));
		return dom;
	}
	public static DailyRecordOfApplication addTimeLeavNo(int no,  int start, int end, DailyRecordOfApplication daily) {
		// 日別勤怠の出退勤
		TimeLeavingWork work = new TimeLeavingWork(new WorkNo(no), null, null);
		work.setAttendanceStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
						new TimeWithDayAttr(start)), Optional.empty()),
				0)));

		work.setLeaveStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
						new TimeWithDayAttr(end)), Optional.empty()),
				0)));
		daily.getAttendanceLeave().get().getTimeLeavingWorks().removeIf(x -> x.getWorkNo().v() == no);
		daily.getAttendanceLeave().ifPresent(x -> {
			x.getTimeLeavingWorks().add(work);
		});
		return daily;
	}
	
	public static DailyRecordOfApplication addTimeLeavNo(int no,  DailyRecordOfApplication daily) {
		// 日別勤怠の出退勤
		TimeLeavingWork work = new TimeLeavingWork(new WorkNo(no), null, null);
		work.setAttendanceStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
						new TimeWithDayAttr(1201)), Optional.empty()),
				0)));

		work.setLeaveStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
						new TimeWithDayAttr(1300)), Optional.empty()),
				0)));

		daily.getAttendanceLeave().ifPresent(x -> {
			x.getTimeLeavingWorks().add(work);
		});
		return daily;
	}
	
	public static DailyRecordOfApplication createRCWithTimeLeavOverTime(ScheduleRecordClassifi classification, Integer no,
			List<OverTimeFrameTime> overTimeWorkFrameTime, List<OverTimeFrameTimeSheet> frameTimeSheetList) {
		return createRCWithTimeLeavOverTime(classification, no, overTimeWorkFrameTime, frameTimeSheetList, new ArrayList<>(), new ArrayList<>());
	}
	
	public static DailyRecordOfApplication createRCWithTimeLeavHoliday(ScheduleRecordClassifi classification, Integer no,
			List<HolidayWorkFrameTime> holidayWorkFrameTime, List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet) {
		return createRCWithTimeLeavOverTime(classification, no, new ArrayList<>(), new ArrayList<>(), holidayWorkFrameTime, holidayWorkFrameTimeSheet);
	}
	
	public static DailyRecordOfApplication createRCWithTimeLeavOverTime(ScheduleRecordClassifi classification, Integer no,
		List<OverTimeFrameTime> overTimeWorkFrameTime, List<OverTimeFrameTimeSheet> frameTimeSheetList,
		List<HolidayWorkFrameTime> holidayWorkFrameTime, List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet) {

		// 日別勤怠の出退勤
		TimeLeavingWork work = new TimeLeavingWork(new WorkNo(no), null, null);
		work.setAttendanceStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(
						new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
								new TimeWithDayAttr(480)),
						Optional.empty()),
				0)));

		work.setLeaveStamp(Optional.of(new TimeActualStamp(null,
				new WorkStamp(
						new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
								new TimeWithDayAttr(1200)),
						Optional.empty()),
				0)));

		List<TimeLeavingWork> timeLeavingWorks = new ArrayList<TimeLeavingWork>();
		timeLeavingWorks.add(work);
		Optional<TimeLeavingOfDailyAttd> attendanceLeave = Optional
				.of(new TimeLeavingOfDailyAttd(timeLeavingWorks, new WorkTimes(1)));

		// 日別勤怠の臨時出退勤
		Optional<TemporaryTimeOfDailyAttd> tempTime = Optional
				.of(new TemporaryTimeOfDailyAttd(new WorkTimes(1), timeLeavingWorks));

		// 日別勤怠の外出時間帯
		OutingTimeSheet outSheet = new OutingTimeSheet(new OutingFrameNo(1),
				Optional.of(
						new WorkStamp(
								new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
										new TimeWithDayAttr(480)),
								Optional.empty())),
				GoingOutReason.PUBLIC,
				Optional.of(
						new WorkStamp(
								new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
										new TimeWithDayAttr(480)),
								Optional.empty()))
				);

		List<OutingTimeSheet> lstSheet = new ArrayList<>();
		lstSheet.add(outSheet);
		Optional<OutingTimeOfDailyAttd> outingTime = Optional.of(new OutingTimeOfDailyAttd(lstSheet));

		// 日別勤怠の短時間勤務時間帯
		List<ShortWorkingTimeSheet> shortWorkingTimeSheets = new ArrayList<>();
		shortWorkingTimeSheets.add(new ShortWorkingTimeSheet(new ShortWorkTimFrameNo(no), ChildCareAtr.CARE,
				new TimeWithDayAttr(480), new TimeWithDayAttr(1020)));
		Optional<ShortTimeOfDailyAttd> shortTime = Optional.of(new ShortTimeOfDailyAttd(shortWorkingTimeSheets));

		// 日別勤怠の休憩時間帯
		List<BreakTimeSheet> breakTimeSheets = new ArrayList<>();
		breakTimeSheets.add(new BreakTimeSheet(new BreakFrameNo(no), new TimeWithDayAttr(480), new TimeWithDayAttr(1020)));
		BreakTimeOfDailyAttd breakTime = new BreakTimeOfDailyAttd(breakTimeSheets);

		// 日別勤怠の応援作業時間帯
		List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet = new ArrayList<>();
		ouenTimeSheet.add(OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(no),
				WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("11111"), new WorkLocationCD("AAAA")),
						Optional.empty(), Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(no),
						Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, Optional.empty()),
								new TimeWithDayAttr(480))),
						Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, Optional.empty()),
								new TimeWithDayAttr(1020)))), Optional.empty()));

		// 日別勤怠の勤怠時間
		List<LateTimeOfDaily> lateTimeOfDaily = new ArrayList<>();
		lateTimeOfDaily.add(new LateTimeOfDaily(TimeWithCalculation.sameTime(new AttendanceTime(60)),
				TimeWithCalculation.sameTime(new AttendanceTime(120)), new WorkNo(no),
				new TimevacationUseTimeOfDaily(new AttendanceTime(0), new AttendanceTime(0),
						new AttendanceTime(0), new AttendanceTime(0), Optional.empty(), new AttendanceTime(0), new AttendanceTime(0)),
				new IntervalExemptionTime(new AttendanceTime(0))));
		List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily = new ArrayList<>();
		leaveEarlyTimeOfDaily.add(new LeaveEarlyTimeOfDaily(TimeWithCalculation.sameTime(new AttendanceTime(60)),
				TimeWithCalculation.sameTime(new AttendanceTime(120)), new WorkNo(no),
				new TimevacationUseTimeOfDaily(new AttendanceTime(0), new AttendanceTime(0),
						new AttendanceTime(0), new AttendanceTime(0), Optional.empty(), new AttendanceTime(0), new AttendanceTime(0)),
				new IntervalExemptionTime(new AttendanceTime(0))));
		List<OutingTimeOfDaily> outingTimeOfDailyPerformance = new ArrayList<OutingTimeOfDaily>();
		outingTimeOfDailyPerformance.add(new OutingTimeOfDaily(new BreakTimeGoOutTimes(no), GoingOutReason.PRIVATE,
				TimevacationUseTimeOfDaily.defaultValue(),
				OutingTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
						WithinOutingTotalTime.sameTime(TimeWithCalculation.sameTime(new AttendanceTime(0))),
						TimeWithCalculation.sameTime(new AttendanceTime(0))),
				OutingTotalTime.of(TimeWithCalculation.sameTime(new AttendanceTime(0)),
						WithinOutingTotalTime.sameTime(TimeWithCalculation.sameTime(new AttendanceTime(0))),
						TimeWithCalculation.sameTime(new AttendanceTime(0))), lstSheet));
		
		//日別実績の所定外時間
		
		if(no != null && holidayWorkFrameTime.isEmpty()) {
			holidayWorkFrameTime.add(HolidayWorkFrameTime.createDefaultWithNo(no));
		}
		ExcessOfStatutoryTimeOfDaily excessOfStatutory = new ExcessOfStatutoryTimeOfDaily(
				new ExcessOfStatutoryMidNightTime(TimeDivergenceWithCalculation.defaultValue(), new AttendanceTime(0)),
				Optional.of(new OverTimeOfDaily(frameTimeSheetList, overTimeWorkFrameTime,
						Finally.of(new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation.defaultValue())))),
				Optional.of(new HolidayWorkTimeOfDaily(holidayWorkFrameTimeSheet, holidayWorkFrameTime, Finally.empty(),
						new AttendanceTime(0))));
		// 加給時間
		List<BonusPayTime> raisingSalaryTimes = new ArrayList<BonusPayTime>();
		// 特定日加給時間
		List<BonusPayTime> autoCalRaisingSalarySettings = new ArrayList<BonusPayTime>();
		if(no != null) {
			raisingSalaryTimes.add(BonusPayTime.createDefaultWithNo(no));
			autoCalRaisingSalarySettings.add(BonusPayTime.createDefaultWithNo(no));
		}
		
		List<DivergenceTime> divergenceTime = new ArrayList<DivergenceTime>();
		if(no != null) {
			divergenceTime.add(DivergenceTime.createDefaultWithNo(no));
		}
		
		HolidayOfDaily holiday = new HolidayOfDaily(new AbsenceOfDaily(new AttendanceTime(0)),
				new TimeDigestOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new YearlyReservedOfDaily(new AttendanceTime(0)),
				new SubstituteHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new OverSalaryOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new SpecialHolidayOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new AnnualOfDaily(new AttendanceTime(0), new AttendanceTime(0)),
				new TransferHolidayOfDaily(new AttendanceTime(0)));
		val actualWork = new ActualWorkingTimeOfDaily(AttendanceTime.ZERO, ConstraintTime.defaultValue(),
				AttendanceTime.ZERO,
				new TotalWorkingTime(null, null, null, null, excessOfStatutory, lateTimeOfDaily, leaveEarlyTimeOfDaily,
						null, outingTimeOfDailyPerformance,
						new RaiseSalaryTimeOfDailyPerfor(raisingSalaryTimes, autoCalRaisingSalarySettings), null, null,
						null, holiday, null),
				new DivergenceTimeOfDaily(divergenceTime), new PremiumTimeOfDailyPerformance(new ArrayList<>()));

		AttendanceTimeOfDailyAttendance attTime = new AttendanceTimeOfDailyAttendance(null, actualWork, null, null,
				null, null);

		IntegrationOfDaily domainDaily = new IntegrationOfDaily(
				new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"),
						CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use,
						DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty()),
				null, new AffiliationInforOfDailyAttd(), Optional.empty(), new ArrayList<>(), outingTime, breakTime, Optional.of(attTime),
				attendanceLeave, shortTime, Optional.empty(), Optional.empty(), Optional.empty(), new ArrayList<>(),
				tempTime, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Optional.empty());
		domainDaily.setOuenTimeSheet(ouenTimeSheet);
		return new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily);
	}

	public static WorkInfoAndTimeZone createPredeteTimeSet(int start, int end, int no) {
		List<nts.uk.ctx.at.shared.dom.worktime.common.TimeZone> timeZones = Arrays
				.asList(new nts.uk.ctx.at.shared.dom.worktime.common.TimeZone(new TimeWithDayAttr(start),
						new TimeWithDayAttr(end)));

		return new WorkInfoAndTimeZone(null, Optional.empty(), timeZones);
	}

	public static ApplicationShare createAppShare(ApplicationTypeShare appType, PrePostAtrShare pre) {

		return new ApplicationShare(1, "1", pre, "1", appType, new ApplicationDateShare(GeneralDate.today()), "1",
				GeneralDateTime.now());
	}

	public static ApplicationShare createAppShare(ApplicationTypeShare appType, PrePostAtrShare pre, GeneralDate date) {

		return new ApplicationShare(1, "1", pre, "1", appType, new ApplicationDateShare(date), "1",
				GeneralDateTime.now());
	}

	public static ApplicationShare createAppShare(PrePostAtrShare pre) {
		return createAppShare(ApplicationTypeShare.COMPLEMENT_LEAVE_APPLICATION, pre);
	}

	public static List<TimeStampAppShare> createlstTimeStamp(TimeStampAppEnumShare timeStampApp,
			StartEndClassificationShare startOrEnd, int time, int no, String location) {

		List<TimeStampAppShare> lstResult = new ArrayList<>();
		TimeStampAppShare timeStamp = new TimeStampAppShare(
				new DestinationTimeAppShare(timeStampApp, no, startOrEnd, Optional.of(no)), new TimeWithDayAttr(time),
				Optional.of(new WorkLocationCD(location)), Optional.empty(), Optional.empty());

		lstResult.add(timeStamp);

		return lstResult;
	}

	public static List<TimeStampAppShare> createlstTimeStamp(TimeStampAppEnumShare timeStampApp,
			StartEndClassificationShare startOrEnd, int time, int no) {
		return createlstTimeStamp(timeStampApp, startOrEnd, time, no, "0001");
	}

	public static List<TimeStampAppShare> createlstTimeStampWpl(StartEndClassificationShare startOrEnd, int time,
			int no, String location, String wpl) {

		List<TimeStampAppShare> lstResult = new ArrayList<>();
		TimeStampAppShare timeStamp = new TimeStampAppShare(
				new DestinationTimeAppShare(TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT, no, startOrEnd,
						Optional.of(no)),
				new TimeWithDayAttr(time), Optional.of(new WorkLocationCD(location)),
				Optional.empty(), Optional.of(new  nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.WorkplaceId(wpl)));

		lstResult.add(timeStamp);

		return lstResult;
	}
	
	public static List<TimeStampAppShare> createlstTimeStamp(StartEndClassificationShare startOrEnd, int time, int no,
			String location) {
		return createlstTimeStamp(TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT, startOrEnd, time, no, location);
	}

	public static List<TimeStampAppShare> createlstTimeStamp(StartEndClassificationShare startOrEnd, int time) {
		return createlstTimeStamp(TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT, startOrEnd, time, 1);
	}

	public static List<TimeStampAppShare> createlstTimeStamp(TimeStampAppEnumShare timeStampApp,
			StartEndClassificationShare startOrEnd, int time) {
		return createlstTimeStamp(timeStampApp, startOrEnd, time, 1);
	}

	public static List<TimeStampAppShare> createlstTimeStamp(StartEndClassificationShare startOrEnd, int time, int no) {
		return createlstTimeStamp(TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT, startOrEnd, time, no);
	}

	public static List<DestinationTimeAppShare> createlstDisTimeStamp(TimeStampAppEnumShare timeStampApp,
			StartEndClassificationShare startOrEnd, int no) {
		List<DestinationTimeAppShare> lstResult = new ArrayList<>();
		DestinationTimeAppShare result = new DestinationTimeAppShare(timeStampApp, no, startOrEnd, Optional.of(no));
		lstResult.add(result);
		return lstResult;
	}

	public static List<TimeStampAppOtherShare> createlstTimeStampOther(int no, int startTime, int endTime,
			TimeZoneStampClassificationShare care) {
		List<TimeStampAppOtherShare> lstResult = new ArrayList<>();

		TimeZone timeZone = new TimeZone(startTime, endTime);
		DestinationTimeZoneAppShare resultDes = new DestinationTimeZoneAppShare(care, no);
		TimeStampAppOtherShare result = new TimeStampAppOtherShare(resultDes, timeZone);
		lstResult.add(result);
		return lstResult;
	}

	public static List<TimeStampAppOtherShare> createlstTimeStampOther(int no, int startTime, int endTime) {
		return createlstTimeStampOther(no, startTime, endTime, TimeZoneStampClassificationShare.BREAK);
	}

	public static List<DestinationTimeZoneAppShare> createlstDisTimezone(int no,
			TimeZoneStampClassificationShare care) {
		List<DestinationTimeZoneAppShare> lstResult = new ArrayList<>();

		DestinationTimeZoneAppShare resultDes = new DestinationTimeZoneAppShare(care, no);
		lstResult.add(resultDes);
		return lstResult;
	}

	public static AppStampShare createAppStamp(TimeStampAppEnumShare type, TimeZoneStampClassificationShare classsifi,
			PrePostAtrShare pre) {
		List<TimeStampAppShare> lstStampAppShare = ReflectApplicationHelper.createlstTimeStamp(
				StartEndClassificationShare.START, // 開始終了区分
				666, // 時刻
				1, // 応援勤務枠No
				"lo");// 勤務場所

		List<DestinationTimeAppShare> listDestinationTimeApp = ReflectApplicationHelper.createlstDisTimeStamp(type,
				StartEndClassificationShare.START, 1);

		List<TimeStampAppOtherShare> listTimeStampAppOther = ReflectApplicationHelper.createlstTimeStampOther(1, 666,
				1111, classsifi);

		List<DestinationTimeZoneAppShare> listDestinationTimeZoneApp = ReflectApplicationHelper.createlstDisTimezone(1,
				classsifi);

		return new AppStampShare(lstStampAppShare, listDestinationTimeApp, listTimeStampAppOther,
				listDestinationTimeZoneApp,
				ReflectApplicationHelper.createAppShare(ApplicationTypeShare.STAMP_APPLICATION, pre));
	}

	public static AppStampShare createAppStamp(TimeStampAppEnumShare type, TimeZoneStampClassificationShare classsifi) {
		return createAppStamp(type, classsifi, PrePostAtrShare.PREDICT);
	}

	public static AppStampShare createAppStamp(PrePostAtrShare pre) {
		return createAppStamp(TimeStampAppEnumShare.ATTEENDENCE_OR_RETIREMENT, TimeZoneStampClassificationShare.BREAK,
				pre);
	}

	public static WorkInfoOfDailyAttendance createWorkInfo(String workTypeCode, double useDay, FuriClassifi furiClass) {
		Optional<NumberOfDaySuspension> opt = Optional.of(new NumberOfDaySuspension(new UsedDays(useDay), furiClass));
		WorkInfoOfDailyAttendance workInfo = new WorkInfoOfDailyAttendance(new WorkInformation(workTypeCode, "001"),
				CalculationState.No_Calculated, NotUseAttribute.Not_use,
				NotUseAttribute.Not_use, DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty());
		workInfo.setNumberDaySuspension(opt);
		return workInfo;
	}

	public static WorkInfoOfDailyAttendance createWorkInfoDefault(String workTypeCode) {
		return new WorkInfoOfDailyAttendance(new WorkInformation(workTypeCode, "001"),
				CalculationState.No_Calculated, NotUseAttribute.Not_use,
				NotUseAttribute.Not_use, DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty());
	}

	public static  AppRecordImageShare createAppRecord() {
		return createAppRecord(EngraveShareAtr.ATTENDANCE);
	}
	
	public static  AppRecordImageShare createAppRecord(EngraveShareAtr atr) {

		ApplicationShare app = ReflectApplicationHelper.createAppShare(ApplicationTypeShare.STAMP_APPLICATION,
				PrePostAtrShare.POSTERIOR, GeneralDate.ymd(2020, 1, 1));

		return new AppRecordImageShare(atr, new AttendanceClock(600),
				Optional.of(GoingOutReason.PUBLIC), app);
	}
	
	public static AppOverTimeShare createOverTimeAppNone() {
		return createOverTimeAppBeforeAfter(PrePostAtrShare.POSTERIOR);
	}
	
	public static AppOverTimeShare createOverTimeAppBeforeAfter(PrePostAtrShare type) {
		ApplicationShare app = ReflectApplicationHelper.createAppShare(ApplicationTypeShare.OVER_TIME_APPLICATION,
				type, GeneralDate.ymd(2020, 1, 1));
		AppOverTimeShare createOverTimeApp = createOverTimeApp(0);
		createOverTimeApp.setApplication(app);
		return createOverTimeApp;
	}
	
	public static AppOverTimeShare createOverTimeApp(int midNightOutSide) {
		return createOverTimeAppWorkHours(midNightOutSide, 0, 0);
	}

	public static AppOverTimeShare createOverTimeAppWorkHours(int startTime, int endTime) {
		return createOverTimeAppWorkHours(0, startTime, endTime);
	}
	
	public static AppOverTimeShare createOverTimeAppWorkHours(int midNightOutSide, int startTime, int endTime) {
		return createOverTimeAppWorkHours("003", "003", midNightOutSide, startTime, endTime);
	}
	
	public static AppOverTimeShare createOverTimeAppWorkHours(String workType, String workTime, int startTime, int endTime) {
		return createOverTimeAppWorkHours("003", "003", 0, startTime, endTime);
	}
	
	public static AppOverTimeShare createOverTimeAppWorkHours(String workType, String workTime, int midNightOutSide, int startTime, int endTime) {
		List<TimeZoneWithWorkNo> breakTimeOp = new ArrayList<>();
		breakTimeOp.add(new TimeZoneWithWorkNo(1, 482, 1082));
		
		List<TimeZoneWithWorkNo> workHours = new ArrayList<>();
		workHours.add(new TimeZoneWithWorkNo(1, startTime, endTime));
		
		Optional<WorkInformation> workInfoOp = Optional.of(new WorkInformation(workType, workTime));

		AppOverTimeShare overTime = new AppOverTimeShare(
				new ApplicationTimeShare(new ArrayList<>(), Optional.empty(), //
						Optional.of(new OverTimeShiftNightShare(new ArrayList<>(), new AttendanceTime(midNightOutSide),
								new AttendanceTime(midNightOutSide))), //
						new ArrayList<>(), new ArrayList<>()),
				breakTimeOp, // 休憩時間帯
				workHours, //
				workInfoOp // 勤務情報
				);
		overTime.setPrePostAtr(PrePostAtrShare.PREDICT);
		return overTime;
	}
	
	public static AppOverTimeShare createOverTime(AttendanceTypeShare attendanceType,
			Integer applicationTime) {
		List<TimeZoneWithWorkNo> breakTimeOp = new ArrayList<>();
		breakTimeOp.add(new TimeZoneWithWorkNo(1, 482, 1082));
		
		List<TimeZoneWithWorkNo> workHours = new ArrayList<>();
		Optional<WorkInformation> workInfoOp = Optional.of(new WorkInformation("003", "003"));
		 List<OvertimeApplicationSettingShare> applicationTimes = new ArrayList<>();
		 applicationTimes.add(new OvertimeApplicationSettingShare(1, attendanceType, applicationTime));
		

		AppOverTimeShare overTime = new AppOverTimeShare(
				new ApplicationTimeShare(applicationTimes, Optional.empty(), //
						Optional.of(new OverTimeShiftNightShare(new ArrayList<>(), new AttendanceTime(0),
								new AttendanceTime(0))), //
						new ArrayList<>(), new ArrayList<>()),
				breakTimeOp, // 休憩時間帯
				workHours, //
				workInfoOp // 勤務情報
				);
		overTime.setPrePostAtr(PrePostAtrShare.PREDICT);
		return overTime;
	}
	
	public static AppOverTimeShare createOverTimeReason(String reason, String reasonCode, Integer no) {
		List<TimeZoneWithWorkNo> breakTimeOp = new ArrayList<>();
		breakTimeOp.add(new TimeZoneWithWorkNo(1, 482, 1082));

		List<TimeZoneWithWorkNo> workHours = new ArrayList<>();
		Optional<WorkInformation> workInfoOp = Optional.of(new WorkInformation("003", "003"));
		List<ReasonDivergenceShare> reasonDissociation = new ArrayList<>();
		reasonDissociation.add(
				new ReasonDivergenceShare(new DivergenceReasonContent(reason), new DiverdenceReasonCode(reasonCode), no));

		AppOverTimeShare overTime = new AppOverTimeShare(
				new ApplicationTimeShare(new ArrayList<>(), Optional.empty(), //
						Optional.of(new OverTimeShiftNightShare(new ArrayList<>(), new AttendanceTime(0),
								new AttendanceTime(0))), //
						new ArrayList<>(), reasonDissociation),
				breakTimeOp, // 休憩時間帯
				workHours, //
				workInfoOp // 勤務情報
				);
		overTime.setPrePostAtr(PrePostAtrShare.PREDICT);
		return overTime;
	}
	
	public static OvertimeApplicationSettingShare createAppSettingShare(AttendanceTypeShare attendanceType,
			Integer applicationTime) {
		return  createAppSettingShare(0, attendanceType, applicationTime);
	}
	public static OvertimeApplicationSettingShare createAppSettingShare(Integer no, AttendanceTypeShare attendanceType,
			Integer applicationTime) {

		return new OvertimeApplicationSettingShare(no, attendanceType, applicationTime);

	}
	
	public static AppHolidayWorkShare createAppHoliday( int no, int att, int leav) {
		return createAppHoliday("005", "006", no, att, leav, 0, 0, 0, false, false);
	}
	
	public static AppHolidayWorkShare createAppHoliday(boolean backHomeAtr, boolean goOut) {
		return createAppHoliday("005", "006", 1, 0, 0, 0, 0, 0, backHomeAtr, goOut);
	}
	public static AppHolidayWorkShare createAppHoliday(String workTypeCode, String workTimeCode) {
		return createAppHoliday(workTypeCode, workTimeCode, 1, 0, 0, 0, 0, 0);
	}
	public static AppHolidayWorkShare createAppHoliday(String workTypeCode, String workTimeCode, int no, int att, int leav,
			int overtime) {
		return createAppHoliday(workTypeCode, workTimeCode, no, att, leav, 0, 0, overtime);
	}
	
	public static AppHolidayWorkShare createAppHoliday(String workTypeCode, String workTimeCode, int no, int att, int leav,
			int breakTimeStart, int breakTimeEnd, int overtime) {
	return createAppHoliday(workTypeCode, workTimeCode, no, att, leav, breakTimeStart, breakTimeEnd, overtime, false, false);
	}
	
	public static AppHolidayWorkShare createAppHolidayBreak(int no, int breakTimeStart, int breakTimeEnd,
			int overtime, String reason, String reasonCode) {
		return createAppHoliday("005", "006", no, 111, 666, breakTimeStart, breakTimeEnd, overtime,  false, false, reason, reasonCode, AttendanceTypeShare.NORMALOVERTIME);
	}
	
	public static AppHolidayWorkShare createAppHoliday(String workTypeCode, String workTimeCode, int no, int att, int leav,
			int breakTimeStart, int breakTimeEnd, int overtime, boolean backHomeAtr, boolean goOut) {
		return createAppHoliday(workTypeCode, workTimeCode, no, att, leav, breakTimeStart, breakTimeEnd, overtime, backHomeAtr, goOut, "", "", AttendanceTypeShare.NORMALOVERTIME);
	}
	
	public static AppHolidayWorkShare createAppHd(String workTypeCode, String workTimeCode, int no, int att, int leav,
			int overtime) {
		return createAppHoliday(workTypeCode, workTimeCode, no, att, leav, 0, 0, overtime, false, false, "", "", AttendanceTypeShare.BREAKTIME);
	}
	
	public static AppHolidayWorkShare createAppHoliday(String workTypeCode, String workTimeCode, int no, int att, int leav,
			int breakTimeStart, int breakTimeEnd, int overtime, boolean backHomeAtr, boolean goOut, String reason, String reasonCode, AttendanceTypeShare type) {
		List<TimeZoneWithWorkNo> breakTimeList = new ArrayList<>();
		breakTimeList.add(new TimeZoneWithWorkNo(no, breakTimeStart, breakTimeEnd));

		List<TimeZoneWithWorkNo> workingTimeList = new ArrayList<>();
		workingTimeList.add(new TimeZoneWithWorkNo(no, att, leav));

		OvertimeApplicationSettingShare overTime = new OvertimeApplicationSettingShare(1,
				type, overtime);// 残業時間

		List<ReasonDivergenceShare> reasonDissociation = new ArrayList<>();
		reasonDissociation.add(
				new ReasonDivergenceShare(new DivergenceReasonContent(reason), new DiverdenceReasonCode(reasonCode), no));
		List<OvertimeApplicationSettingShare> applicationTimeDet = new ArrayList<>();
		applicationTimeDet.add(overTime);
		val appTimeShare = new ApplicationTimeShare(applicationTimeDet, Optional.empty(), // 申請時間
				Optional.of(new OverTimeShiftNightShare(new ArrayList<>(), new AttendanceTime(0), // 合計外深夜時間
						new AttendanceTime(0))), //
				new ArrayList<>(), reasonDissociation);

		return new AppHolidayWorkShare(
				ReflectApplicationHelper.createAppShare(ApplicationTypeShare.HOLIDAY_WORK_APPLICATION,
						PrePostAtrShare.PREDICT),
				new WorkInformation(workTypeCode, workTimeCode), // 勤務情報
				appTimeShare, //
				backHomeAtr, //
				goOut, //
				breakTimeList, // 休憩時間帯
				workingTimeList // 勤務時間帯
				);//

	}
	
	
	public static Optional<SubHolTransferSet> createSubTrans(int certainTime, int oneDayTime, int hafDayTime,
			SubHolTransferSetAtr atr) {
		return Optional.of(new SubHolTransferSet(new OneDayTime(certainTime), true,
				new DesignatedTime(new OneDayTime(oneDayTime), new OneDayTime(hafDayTime)), atr));
	}
}