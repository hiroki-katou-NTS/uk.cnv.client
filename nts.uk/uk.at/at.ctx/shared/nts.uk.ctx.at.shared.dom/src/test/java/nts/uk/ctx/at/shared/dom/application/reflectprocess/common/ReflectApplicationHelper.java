package nts.uk.ctx.at.shared.dom.application.reflectprocess.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.UsedDays;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationDateShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationTypeShare;
import nts.uk.ctx.at.shared.dom.scherec.application.common.PrePostAtrShare;
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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.FuriClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NumberOfDaySuspension;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.IntervalExemptionTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WithinOutingTotalTime;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
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
				new ArrayList<>(), Optional.empty(), new ArrayList<>(), Optional.empty());
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
				new ArrayList<>(), Optional.empty(), new ArrayList<>(), Optional.empty());
		return new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily);
	}

	public static DailyRecordOfApplication createRCWithTimeLeavFull(ScheduleRecordClassifi classification, int no) {

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
		ouenTimeSheet.add(OuenWorkTimeSheetOfDailyAttendance.create(no,
				WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("11111"), new WorkLocationCD("AAAA")),
						Optional.empty(), Optional.empty()),
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(no),
						Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
								new TimeWithDayAttr(480))),
						Optional.of(new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.AUTOMATIC_SET, null),
								new TimeWithDayAttr(1020))))));

		// 日別勤怠の勤怠時間
		List<LateTimeOfDaily> lateTimeOfDaily = new ArrayList<>();
		lateTimeOfDaily.add(new LateTimeOfDaily(TimeWithCalculation.sameTime(new AttendanceTime(0)),
				TimeWithCalculation.sameTime(new AttendanceTime(0)), new WorkNo(no),
				new TimevacationUseTimeOfDaily(new AttendanceTime(0), new AttendanceTime(0),
						new AttendanceTime(0), new AttendanceTime(0), Optional.empty(), new AttendanceTime(0), new AttendanceTime(0)),
				new IntervalExemptionTime(new AttendanceTime(0))));
		List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily = new ArrayList<>();
		leaveEarlyTimeOfDaily.add(new LeaveEarlyTimeOfDaily(TimeWithCalculation.sameTime(new AttendanceTime(0)),
				TimeWithCalculation.sameTime(new AttendanceTime(0)), new WorkNo(no),
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
		AttendanceTimeOfDailyAttendance attTime = new AttendanceTimeOfDailyAttendance(null,
				ActualWorkingTimeOfDaily.of(new TotalWorkingTime(null, null, null, null, null, lateTimeOfDaily,
						leaveEarlyTimeOfDaily, null, outingTimeOfDailyPerformance, null, null, null, null, null, null), 0, 0, 0, 0),
				null, null, null, null);

		IntegrationOfDaily domainDaily = new IntegrationOfDaily(
				new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"),
						CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use,
						DayOfWeek.FRIDAY, new ArrayList<>(), Optional.empty()),
				null, null, Optional.empty(), new ArrayList<>(), outingTime, breakTime, Optional.of(attTime),
				attendanceLeave, shortTime, Optional.empty(), Optional.empty(), Optional.empty(), new ArrayList<>(),
				tempTime, new ArrayList<>(), Optional.empty());
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
	
}
