package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.SWkpHistRcImported;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.StatusLock;
import nts.uk.ctx.at.record.dom.stamp.application.CheckErrorType;
import nts.uk.ctx.at.record.dom.stamp.application.MessageContent;
import nts.uk.ctx.at.record.dom.stamp.application.PromptingMessage;
import nts.uk.ctx.at.record.dom.stamp.application.StampPromptApplication;
import nts.uk.ctx.at.record.dom.stamp.application.StampRecordDis;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErAlApplication;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.workingcondition.PersonalWorkCategory;
import nts.uk.ctx.at.shared.dom.workingcondition.SingleDaySchedule;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkByIndividualWorkDay;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.enumcommon.NotUseAtr;
/**
 * 
 * @author tutk
 *
 */
public class DomainServiceHeplper {

	public static AttendanceOneDay getAttendanceOneDayDefault() {
		return new AttendanceOneDay(GeneralDate.today(), Optional.of(new TimeActualStamp()),
				Optional.of(new TimeActualStamp()), Optional.of(new TimeActualStamp()),
				Optional.of(new TimeActualStamp()));
	}

	public static DailyAttdErrorInfo getDailyAttdErrorInfoDefault() {
		return new DailyAttdErrorInfo(CheckErrorType.valueOf(0),
				new PromptingMessage(new MessageContent("MessageContent"), new ColorCode("mauden")),
				GeneralDate.today(), new ArrayList<>());
	}

	public static PromptingMessage getPromptingMessageDefault() {
		return new PromptingMessage(new MessageContent("MessageContent"), new ColorCode("mauden"));
	}

	public static DateAndTimePeriod getDateAndTimePeriodDefault() {
		return new DateAndTimePeriod(GeneralDateTime.now(), GeneralDateTime.now().addDays(1));
	}

	public static EmployeeStampInfo getEmployeeStampInfoDefault() {
		return new EmployeeStampInfo("employeeId", GeneralDate.today(),
				Arrays.asList(new StampInfoDisp(new StampNumber("stampNumber"), GeneralDateTime.now(), "123", null)));
	}

	public static StampDataOfEmployees getStampDataOfEmployeesDefault() {
		return new StampDataOfEmployees("employeeId", GeneralDate.today(), new ArrayList<>());
	}

	public static StampDataReflectResult getStampDataReflectResultDefault() {
		return new StampDataReflectResult(Optional.of(GeneralDate.today()), AtomTask.of(() -> {
		}));
	}

	public static StampInfoDisp getStampInfoDispDefault() {
		return new StampInfoDisp(new StampNumber("stampNumber"), GeneralDateTime.now(), "123", null);
	}

	public static StampToSuppress getStampToSuppressDefault() {
		return new StampToSuppress(true, true, true, true);
	}

	public static TimeCard getTimeCardDefault() {
		return new TimeCard("employeeID", new ArrayList<>());
	}

	public static StampPromptApplication getStamPromptApplication() {
		return new StampPromptApplication("000000000000-0001",
				Arrays.asList(
						new StampRecordDis(NotUseAtr.valueOf(1), CheckErrorType.valueOf(1),
								Optional.of(new PromptingMessage(new MessageContent("DUMMY"), new ColorCode("#DUMMY")))),
						new StampRecordDis(NotUseAtr.valueOf(0), CheckErrorType.valueOf(1),
								Optional.of(new PromptingMessage(new MessageContent("DUMMY"), new ColorCode("#DUMMY")))),
						new StampRecordDis(NotUseAtr.valueOf(1), CheckErrorType.valueOf(0),
								Optional.of(new PromptingMessage(new MessageContent("DUMMY"), new ColorCode("#DUMMY"))))
						));
	}

	public static EmployeeDailyPerError getEmployeeDailyPerErrorDefault() {
		return new EmployeeDailyPerError("companyId", "employeeID", GeneralDate.today(),
				new ErrorAlarmWorkRecordCode("null"), Arrays.asList(0, 1, 2));
	}

	public static List<EmployeeDailyPerError> getListEmployeeDailyPerErrorDefault() {
		return Arrays.asList(
				new EmployeeDailyPerError("companyID", "employeeID", GeneralDate.today(),
						new ErrorAlarmWorkRecordCode("null"), Arrays.asList(0, 1, 2)),
				new EmployeeDailyPerError("companyID", "employeeID", GeneralDate.today(),
						new ErrorAlarmWorkRecordCode("S005"), Arrays.asList(0, 1, 2)),
				new EmployeeDailyPerError("companyID", "employeeID", GeneralDate.today(),
						new ErrorAlarmWorkRecordCode("S005"), Arrays.asList(0, 1, 2)));
	}
	public static ErAlApplication getErAlApplicationDefault() {
		return new ErAlApplication("companyID", "S005",Arrays.asList(0,1,2));
	}
	
	
	public static WorkingConditionItem getWorkingCondWorktimeIsNull() {
		SingleDaySchedule weekdayTime = new  SingleDaySchedule(new ArrayList<>(), Optional.empty());
		PersonalWorkCategory workCategory = new PersonalWorkCategory(weekdayTime);
		WorkByIndividualWorkDay result = new WorkByIndividualWorkDay(workCategory, null);
		WorkingConditionItem data = new WorkingConditionItem(null, null, result, null,
				null, null, null, null, null, null,
				null, null, null, null);
		return data;
	}
	public static WorkingConditionItem getWorkingCondWorktimeNotNull() {
		SingleDaySchedule weekdayTime = new  SingleDaySchedule(new ArrayList<>(), Optional.of("workTime"));
		PersonalWorkCategory workCategory = new PersonalWorkCategory(weekdayTime);
		WorkByIndividualWorkDay result = new WorkByIndividualWorkDay(workCategory, null);
		WorkingConditionItem data = new WorkingConditionItem(null, null, result, null,
				null, null, null, null, null, null,
				null, null, null, null);
		return data;
	}
	
	public static PredetemineTimeSetting getPredetemineTimeSettingByTime(int value) {
		PredetemineTimeSetting data = new PredetemineTimeSetting();
		data.setStartDateClock(value);
		return data;
	}
	
	public static TimeLeavingOfDailyPerformance getTimeLeavingOfDailyPerformanceDefault() {
		List<TimeLeavingWork> timeLeavingWorks = new ArrayList<>();
		timeLeavingWorks.add(new TimeLeavingWork(
					new WorkNo(1), 
					new TimeActualStamp(), 
					new TimeActualStamp()
				));
		timeLeavingWorks.add(new TimeLeavingWork(
				new WorkNo(2), 
				new TimeActualStamp(), 
				new TimeActualStamp()
			));
		timeLeavingWorks.add(new TimeLeavingWork(
				new WorkNo(3), 
				new TimeActualStamp(), 
				new TimeActualStamp()
			));
		return new TimeLeavingOfDailyPerformance("employeeId", new WorkTimes(1), timeLeavingWorks, GeneralDate.today());
		
	}
	
	public static SWkpHistRcImported getSWkpHistRcImportedDefault() {
		SWkpHistRcImported data = new SWkpHistRcImported(new DatePeriod(GeneralDate.today(), GeneralDate.today()),
				"employeeId", "workplaceId", "workplaceCode", "workplaceName", "wkpDisplayName");
		return data;
	}
	public static DailyLock getDailyLockDefault() {
		DailyLock data = new DailyLock(
				"employeeId", GeneralDate.today(), 
				StatusLock.LOCK, StatusLock.LOCK, StatusLock.LOCK, StatusLock.LOCK, StatusLock.LOCK, StatusLock.LOCK, StatusLock.LOCK);
		return data;
	}

}
