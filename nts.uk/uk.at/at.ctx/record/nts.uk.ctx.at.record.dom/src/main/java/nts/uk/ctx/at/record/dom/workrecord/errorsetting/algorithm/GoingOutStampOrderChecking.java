package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.CheckState;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicateStateAtr;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.DuplicationStatusOfTimeZone;
import nts.uk.ctx.at.shared.dom.worktime.algorithm.rangeofdaytimezone.RangeOfDayTimeZoneService;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 外出系打刻順序不正をチェックする
 * 
 * @author nampt
 *
 */
@Stateless
public class GoingOutStampOrderChecking {

	@Inject
	private RangeOfDayTimeZoneService rangeOfDayTimeZoneService;

	public List<EmployeeDailyPerError> goingOutStampOrderChecking(String companyId, String employeeId,
			GeneralDate processingDate, OutingTimeOfDailyPerformance outingTimeOfDailyPerformance,
			TimeLeavingOfDailyPerformance timeLeavingOfDailyPerformance,
			TemporaryTimeOfDailyPerformance temporaryTimeOfDailyPerformance) {

		List<EmployeeDailyPerError> employeeDailyPerErrorList = new ArrayList<>();

		if (outingTimeOfDailyPerformance != null && !outingTimeOfDailyPerformance.getOutingTime().getOutingTimeSheets().isEmpty()) {
			//ドメインモデル「日別実績の外出時間帯」を取得する
			List<OutingTimeSheet> outingTimeSheets = outingTimeOfDailyPerformance.getOutingTime().getOutingTimeSheets();

			// List<OutingTimeSheet> newOutingTimeSheets2 =
			// outingTimeSheets.stream()
			// .filter(item -> item.getGoOut() != null &&
			// item.getGoOut().isPresent()
			// && item.getGoOut().get().getStamp() != null &&
			// item.getGoOut().get().getStamp().isPresent()
			// && item.getGoOut().get().getStamp().get().getTimeWithDay() !=
			// null)
			// .collect(Collectors.toList());
			//
			List<OutingTimeSheet> newOutingTimeSheets = outingTimeSheets.stream()
					.filter(item -> (item.getComeBack().isPresent() 
							&& item.getComeBack().get().getTimeDay().getTimeWithDay().isPresent())
							&& (item.getGoOut().isPresent() 
									&& item.getGoOut().get().getTimeDay().getTimeWithDay().isPresent()))
					.collect(Collectors.toList());
			//時間帯をソートする (Sắp xếp list)
			newOutingTimeSheets.sort((e1, e2) -> (e1.getGoOut().get().getTimeDay().getTimeWithDay().get().v()
					.compareTo(e2.getGoOut().get().getTimeDay().getTimeWithDay().get().v())));
			//外出枠NOに番号付けする (Đánh số vào 外出枠NO)
			int outingFrameNo = 1;
			for (OutingTimeSheet item : newOutingTimeSheets) {
				Optional<WorkStamp> goOut = item.getGoOut();
				GoingOutReason reasonForGoOut = item.getReasonForGoOut();
				Optional<WorkStamp> comeBack = item.getComeBack();
				item = new OutingTimeSheet(new OutingFrameNo(outingFrameNo), goOut,
						reasonForGoOut, comeBack);
				outingFrameNo++;
			}

			List<OutingTimeSheet> outingTimeSheetsTemp = new ArrayList<>();

			for (OutingTimeSheet outingTimeSheet : newOutingTimeSheets) {
				if (outingTimeSheet.getGoOut().get().getTimeDay().getTimeWithDay().get()
						.lessThanOrEqualTo(outingTimeSheet.getComeBack().get().getTimeDay().getTimeWithDay().get())) {

					List<Integer> attendanceItemIDList = new ArrayList<>();

					if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(1))) {
						attendanceItemIDList.add(88);
						attendanceItemIDList.add(91);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(2))) {
						attendanceItemIDList.add(95);
						attendanceItemIDList.add(98);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(3))) {
						attendanceItemIDList.add(102);
						attendanceItemIDList.add(105);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(4))) {
						attendanceItemIDList.add(109);
						attendanceItemIDList.add(112);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(5))) {
						attendanceItemIDList.add(116);
						attendanceItemIDList.add(119);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(6))) {
						attendanceItemIDList.add(123);
						attendanceItemIDList.add(126);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(7))) {
						attendanceItemIDList.add(130);
						attendanceItemIDList.add(133);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(8))) {
						attendanceItemIDList.add(137);
						attendanceItemIDList.add(140);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(9))) {
						attendanceItemIDList.add(144);
						attendanceItemIDList.add(147);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(10))) {
						attendanceItemIDList.add(151);
						attendanceItemIDList.add(154);
					}

					// list contain different
					outingTimeSheetsTemp = newOutingTimeSheets.stream()
							.filter(item -> !item.getOutingFrameNo().equals(outingTimeSheet.getOutingFrameNo()))
							.collect(Collectors.toList());

					TimeWithDayAttr stampStartTimeFirstTime = outingTimeSheet.getGoOut().get()
							.getTimeDay().getTimeWithDay().get();
					TimeWithDayAttr endStartTimeFirstTime = outingTimeSheet.getComeBack().get()
							.getTimeDay().getTimeWithDay().get();
					TimeSpanForCalc timeSpanFirstTime = new TimeSpanForCalc(stampStartTimeFirstTime,
							endStartTimeFirstTime);

					List<DuplicationStatusOfTimeZone> newList = new ArrayList<>();

					// 他の時間帯との時間帯重複を確認する
					// check with another outingFrameNo
					for (OutingTimeSheet timeSheet : outingTimeSheetsTemp) {
						TimeWithDayAttr stampStartTimeSecondTime = timeSheet.getGoOut().get()
								.getTimeDay().getTimeWithDay().get();
						TimeWithDayAttr endStartTimesecondTime = timeSheet.getComeBack().get()
								.getTimeDay().getTimeWithDay().get();
						TimeSpanForCalc timeSpanSecondTime = new TimeSpanForCalc(stampStartTimeSecondTime,
								endStartTimesecondTime);

						DuplicateStateAtr duplicateStateAtr = this.rangeOfDayTimeZoneService
								.checkPeriodDuplication(timeSpanFirstTime, timeSpanSecondTime);
						DuplicationStatusOfTimeZone duplicationStatusOfTimeZone = this.rangeOfDayTimeZoneService
								.checkStateAtr(duplicateStateAtr);
						newList.add(duplicationStatusOfTimeZone);
					}

					if (!newList.stream().allMatch(item -> item == DuplicationStatusOfTimeZone.NON_OVERLAPPING)) {
						// if (!attendanceItemIDList.isEmpty()) {
						// createEmployeeDailyPerError.createEmployeeDailyPerError(companyId,
						// employeeId,
						// processingDate, new
						// ErrorAlarmWorkRecordCode("S004"),
						// attendanceItemIDList);
						// }
						EmployeeDailyPerError employeeDailyPerError = new EmployeeDailyPerError(companyId, employeeId,
								processingDate, new ErrorAlarmWorkRecordCode("S004"), attendanceItemIDList);
						employeeDailyPerErrorList.add(employeeDailyPerError);
					} else {
						// 出退勤時間帯に包含されているか確認する
						CheckState checkState = checkConjugation(companyId, employeeId, processingDate, outingTimeSheet,
								timeLeavingOfDailyPerformance, temporaryTimeOfDailyPerformance);
						if (checkState == CheckState.NON_INCLUSION) {
							EmployeeDailyPerError employeeDailyPerError = new EmployeeDailyPerError(companyId,
									employeeId, processingDate, new ErrorAlarmWorkRecordCode("S004"),
									attendanceItemIDList);
							employeeDailyPerErrorList.add(employeeDailyPerError);
						}
					}
				}
			}
		}
		return employeeDailyPerErrorList;
	}

	private CheckState checkConjugation(String comanyID, String employeeID, GeneralDate processingDate,
			OutingTimeSheet outingTimeSheet, TimeLeavingOfDailyPerformance timeLeavingOfDailyPerformance,
			TemporaryTimeOfDailyPerformance temporaryTimeOfDailyPerformance) {
		CheckState state = CheckState.INCLUSION;

		// Optional<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformance
		// = this.timeLeavingOfDailyPerformanceRepository
		// .findByKey(employeeID, processingDate);

		TimeSpanForCalc timeSpanFirstTime = null;
		if (outingTimeSheet.getGoOut() != null && outingTimeSheet.getGoOut().isPresent()
				&& outingTimeSheet.getGoOut().get().getTimeDay().getTimeWithDay().isPresent()
				&& outingTimeSheet.getComeBack() != null && outingTimeSheet.getComeBack().isPresent()
				&& outingTimeSheet.getComeBack().get().getTimeDay().getTimeWithDay().isPresent()) {
			TimeWithDayAttr stampStartTimeFirstTime = outingTimeSheet.getGoOut().get()
					.getTimeDay().getTimeWithDay().get();
			TimeWithDayAttr endStartTimeFirstTime = outingTimeSheet.getComeBack().get()
					.getTimeDay().getTimeWithDay().get();
			timeSpanFirstTime = new TimeSpanForCalc(stampStartTimeFirstTime, endStartTimeFirstTime);
		}

		if (timeLeavingOfDailyPerformance != null && timeLeavingOfDailyPerformance.getAttendance() !=null &&
				!timeLeavingOfDailyPerformance.getAttendance().getTimeLeavingWorks().isEmpty()
				&& timeSpanFirstTime != null) {
			List<TimeLeavingWork> timeLeavingWorks = timeLeavingOfDailyPerformance.getAttendance().getTimeLeavingWorks();
			for (TimeLeavingWork timeLeavingWork : timeLeavingWorks) {
				// if(timeLeavingWork.getAttendanceStamp().getStamp() != null){
				if (timeLeavingWork.getAttendanceStamp() != null && timeLeavingWork.getAttendanceStamp().isPresent()
						&& timeLeavingWork.getAttendanceStamp().get().getStamp() != null
						&& timeLeavingWork.getAttendanceStamp().get().getStamp().isPresent()
						&&timeLeavingWork.getAttendanceStamp().get().getStamp()
						.get().getTimeDay().getTimeWithDay().isPresent()
						&& timeLeavingWork.getLeaveStamp() != null && timeLeavingWork.getLeaveStamp().isPresent()
						&& timeLeavingWork.getLeaveStamp().get().getStamp() != null
						&& timeLeavingWork.getLeaveStamp().get().getStamp().isPresent()
						&& timeLeavingWork.getLeaveStamp().get().getStamp().get()
						.getTimeDay().getTimeWithDay().isPresent()) {
					TimeWithDayAttr stampStartTimeSecondTime = timeLeavingWork.getAttendanceStamp().get().getStamp()
							.get().getTimeDay().getTimeWithDay().get();
					TimeWithDayAttr endStartTimesecondTime = timeLeavingWork.getLeaveStamp().get().getStamp().get()
							.getTimeDay().getTimeWithDay().get();
					TimeSpanForCalc timeSpanSecondTime = new TimeSpanForCalc(stampStartTimeSecondTime,
							endStartTimesecondTime);
					DuplicateStateAtr duplicateStateAtr = this.rangeOfDayTimeZoneService
							.checkPeriodDuplication(timeSpanFirstTime, timeSpanSecondTime);
					DuplicationStatusOfTimeZone duplicationStatusOfTimeZone = this.rangeOfDayTimeZoneService
							.checkStateAtr(duplicateStateAtr);
					if (duplicationStatusOfTimeZone == DuplicationStatusOfTimeZone.SAME_WORK_TIME
							|| duplicationStatusOfTimeZone == DuplicationStatusOfTimeZone.INCLUSION_OUTSIDE) {
						state = CheckState.INCLUSION;
					} else {
						// Optional<TemporaryTimeOfDailyPerformance>
						// temporaryTimeOfDailyPerformance =
						// this.temporaryTimeOfDailyPerformanceRepository
						// .findByKey(employeeID, processingDate);
						if (temporaryTimeOfDailyPerformance != null
								&& !temporaryTimeOfDailyPerformance.getAttendance().getTimeLeavingWorks().isEmpty()) {
							List<TimeLeavingWork> leavingWorks = temporaryTimeOfDailyPerformance.getAttendance().getTimeLeavingWorks();
							for (TimeLeavingWork leavingWork : leavingWorks) {
								if (leavingWork.getAttendanceStamp() != null
										&& leavingWork.getAttendanceStamp().isPresent()
										&& leavingWork.getAttendanceStamp().get().getStamp() != null
										&& leavingWork.getAttendanceStamp().get().getStamp().isPresent()
										&& leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
										&& leavingWork.getLeaveStamp() != null
										&& leavingWork.getLeaveStamp().isPresent()
										&& leavingWork.getLeaveStamp().get().getStamp() != null
										&& leavingWork.getLeaveStamp().get().getStamp().isPresent()
										&& leavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
									TimeWithDayAttr newStampStartTimeSecondTime = leavingWork.getAttendanceStamp().get()
											.getStamp().get().getTimeDay().getTimeWithDay().get();
									TimeWithDayAttr newEndStartTimesecondTime = leavingWork.getLeaveStamp().get()
											.getStamp().get().getTimeDay().getTimeWithDay().get();
									TimeSpanForCalc newTimeSpanSecondTime = new TimeSpanForCalc(
											newStampStartTimeSecondTime, newEndStartTimesecondTime);
									DuplicateStateAtr newDuplicateStateAtr = this.rangeOfDayTimeZoneService
											.checkPeriodDuplication(timeSpanFirstTime, newTimeSpanSecondTime);
									DuplicationStatusOfTimeZone newDuplicationStatusOfTimeZone = this.rangeOfDayTimeZoneService
											.checkStateAtr(newDuplicateStateAtr);
									if (newDuplicationStatusOfTimeZone == DuplicationStatusOfTimeZone.SAME_WORK_TIME
											|| newDuplicationStatusOfTimeZone == DuplicationStatusOfTimeZone.INCLUSION_OUTSIDE) {
										state = CheckState.INCLUSION;
									} else {
										state = CheckState.NON_INCLUSION;
									}
								}
							}
						}
					}
				}
				// }
			}
		}

		return state;
	}
}
