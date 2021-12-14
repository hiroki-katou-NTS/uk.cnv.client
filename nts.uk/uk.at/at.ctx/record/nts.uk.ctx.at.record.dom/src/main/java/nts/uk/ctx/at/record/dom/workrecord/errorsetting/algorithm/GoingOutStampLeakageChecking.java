package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.breakorgoout.OutingTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.CorrectionResult;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.calculationsetting.GoBackOutCorrectionClass;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.OutingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;

/*
 * 外出系打刻漏れをチェックする
 */
@Stateless
public class GoingOutStampLeakageChecking {

	@Inject
	private StampReflectionManagementRepository stampReflectionManagementRepository;

	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingOfDailyPerformanceRepository;

	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepository;

	public List<EmployeeDailyPerError> goingOutStampLeakageChecking(String companyID, String employeeID,
			GeneralDate processingDate, OutingTimeOfDailyPerformance outingTimeOfDailyPerformance) {

		List<EmployeeDailyPerError> employeeDailyPerErrorList = new ArrayList<>();

		// Optional<OutingTimeOfDailyPerformance> outingTimeOfDailyPerformance =
		// this.outingTimeOfDailyPerformanceRepository
		// .findByEmployeeIdAndDate(employeeID, processingDate);

		if (outingTimeOfDailyPerformance != null && !outingTimeOfDailyPerformance.getOutingTime().getOutingTimeSheets().isEmpty()) {

			List<OutingTimeSheet> outingTimeSheets = outingTimeOfDailyPerformance.getOutingTime().getOutingTimeSheets();

			for (OutingTimeSheet outingTimeSheet : outingTimeSheets) {

				if (!outingTimeSheet.leakageCheck() && !outingTimeSheet.getGoOutWithTimeDay().isPresent()) {
					List<Integer> attendanceItemIDList = new ArrayList<>();

					if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(1))) {
						attendanceItemIDList.add(88);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(2))) {
						attendanceItemIDList.add(95);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(3))) {
						attendanceItemIDList.add(102);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(4))) {
						attendanceItemIDList.add(109);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(5))) {
						attendanceItemIDList.add(116);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(6))) {
						attendanceItemIDList.add(123);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(7))) {
						attendanceItemIDList.add(130);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(8))) {
						attendanceItemIDList.add(137);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(9))) {
						attendanceItemIDList.add(144);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(10))) {
						attendanceItemIDList.add(151);
					}

					// 外出に打刻漏れ補正する
					CorrectionResult correctionResult = stampLeakageCorrect(companyID, employeeID, processingDate,
							outingTimeSheet);
					if (correctionResult == CorrectionResult.FAILURE) {
						if (!attendanceItemIDList.isEmpty()) {
							EmployeeDailyPerError employeeDailyPerError = new EmployeeDailyPerError(companyID,
									employeeID, processingDate, new ErrorAlarmWorkRecordCode("S001"),
									attendanceItemIDList);
							employeeDailyPerErrorList.add(employeeDailyPerError);
						}
						// if (!attendanceItemIDList.isEmpty()) {
						// createEmployeeDailyPerError.createEmployeeDailyPerError(companID,
						// employeeID, processingDate,
						// new ErrorAlarmWorkRecordCode("S001"),
						// attendanceItemIDList);
						// }
					}
				}

				if (!outingTimeSheet.leakageCheck() && !outingTimeSheet.getComeBackWithTimeDay().isPresent()) {

					List<Integer> newAttendanceItemIDList = new ArrayList<>();

					if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(1))) {
						newAttendanceItemIDList.add(91);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(2))) {
						newAttendanceItemIDList.add(98);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(3))) {
						newAttendanceItemIDList.add(105);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(4))) {
						newAttendanceItemIDList.add(112);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(5))) {
						newAttendanceItemIDList.add(119);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(6))) {
						newAttendanceItemIDList.add(126);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(7))) {
						newAttendanceItemIDList.add(133);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(8))) {
						newAttendanceItemIDList.add(140);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(9))) {
						newAttendanceItemIDList.add(147);
					} else if (outingTimeSheet.getOutingFrameNo().equals(new OutingFrameNo(10))) {
						newAttendanceItemIDList.add(154);
					}

					// 戻りに打刻漏れ補正する
					CorrectionResult returnCorrectionResult = returnStampLeakageCorrect(companyID, employeeID,
							processingDate, outingTimeSheet);
					if (returnCorrectionResult == CorrectionResult.FAILURE) {
						if (!newAttendanceItemIDList.isEmpty()) {
							EmployeeDailyPerError employeeDailyPerError = new EmployeeDailyPerError(companyID,
									employeeID, processingDate, new ErrorAlarmWorkRecordCode("S001"),
									newAttendanceItemIDList);
							employeeDailyPerErrorList.add(employeeDailyPerError);
						}
						// if (!newAttendanceItemIDList.isEmpty()) {
						// createEmployeeDailyPerError.createEmployeeDailyPerError(companID,
						// employeeID, processingDate,
						// new ErrorAlarmWorkRecordCode("S001"),
						// newAttendanceItemIDList);
						// }
					}
				}
			}
		}
		return employeeDailyPerErrorList;
	}

	private CorrectionResult stampLeakageCorrect(String companyId, String employeeID, GeneralDate processingDate,
			OutingTimeSheet outingTimeSheet) {
		CorrectionResult correctionResult = CorrectionResult.SUCCESS;

		Optional<StampReflectionManagement> stampReflectionManagement = stampReflectionManagementRepository
				.findByCid(companyId);

		if (stampReflectionManagement.isPresent()) {
			GoBackOutCorrectionClass goBackOutCorrectionClass = stampReflectionManagement.get()
					.getGoBackOutCorrectionClass();
			if (goBackOutCorrectionClass == GoBackOutCorrectionClass.DO_NOT_CORRECT) {
				correctionResult = CorrectionResult.FAILURE;
			} else {
				// 漏れ補正に使う退勤を確認する
				WorkStamp stamp = leakageCorrection(companyId, employeeID, processingDate,
						outingTimeSheet.getComeBack());
				if (stamp != null) {
					correctionResult = CorrectionResult.SUCCESS;
				} else {
					correctionResult = CorrectionResult.FAILURE;
				}
			}
		}

		return correctionResult;
	}

	private WorkStamp leakageCorrection(String companyId, String employeeID, GeneralDate processingDate,
			Optional<WorkStamp> comeBack) {
		// 漏れ補正打刻をクリア
		WorkStamp stamp = null;

		Optional<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformance = this.timeLeavingOfDailyPerformanceRepository
				.findByKey(employeeID, processingDate);

		if (timeLeavingOfDailyPerformance.isPresent()) {
			List<TimeLeavingWork> timeLeavingWorks = timeLeavingOfDailyPerformance.get().getAttendance().getTimeLeavingWorks();

			for (TimeLeavingWork timeLeavingWork : timeLeavingWorks) {
				if (timeLeavingWork.getAttendanceStamp() != null && timeLeavingWork.getAttendanceStamp().isPresent()
						&& timeLeavingWork.getLeaveStamp() != null && timeLeavingWork.getLeaveStamp().isPresent()) {
					if ((timeLeavingWork.getAttendanceStamp().get().getStamp() != null
							&& timeLeavingWork.getAttendanceStamp().get().getStamp().isPresent())
							&& (timeLeavingWork.getLeaveStamp().get().getStamp() != null
									&& timeLeavingWork.getLeaveStamp().get().getStamp().isPresent())
							&& timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
						if ((comeBack != null && comeBack.isPresent() 
								&& timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.lessThanOrEqualTo(comeBack.get().getTimeDay().getTimeWithDay().get())
								&& timeLeavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.greaterThanOrEqualTo(comeBack.get().getTimeDay().getTimeWithDay().get()))
								|| comeBack == null || !comeBack.isPresent()
								|| (comeBack.isPresent() && comeBack == null)
								|| (comeBack.isPresent() && !comeBack.isPresent())) {
							if (timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE
									|| timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION) {
								stamp = new WorkStamp(
										timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get(),
										timeLeavingWork.getAttendanceStamp().get().getStamp().get().getLocationCode()
												.isPresent()
														? timeLeavingWork.getAttendanceStamp().get().getStamp().get()
																.getLocationCode().get()
														: null,
										TimeChangeMeans.AUTOMATIC_SET,null);
							}
						}

					}
				}

			}
		}

		Optional<TemporaryTimeOfDailyPerformance> temporaryTimeOfDailyPerformance = this.temporaryTimeOfDailyPerformanceRepository
				.findByKey(employeeID, processingDate);

		if (temporaryTimeOfDailyPerformance.isPresent()) {
			List<TimeLeavingWork> leavingWorks = temporaryTimeOfDailyPerformance.get().getAttendance().getTimeLeavingWorks();

			for (TimeLeavingWork leavingWork : leavingWorks) {
				if (leavingWork.getAttendanceStamp() != null && leavingWork.getAttendanceStamp().isPresent()
						&& leavingWork.getLeaveStamp() != null && leavingWork.getLeaveStamp().isPresent()) {
					if (leavingWork.getAttendanceStamp().get().getStamp() != null
							&& leavingWork.getAttendanceStamp().get().getStamp().isPresent()
							&& leavingWork.getLeaveStamp().get().getStamp() != null
							&& leavingWork.getLeaveStamp().get().getStamp().isPresent()
							&& leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							&& comeBack.get().getTimeDay().getTimeWithDay().isPresent()) {
						if ((comeBack != null && comeBack.isPresent() 
								&& leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.lessThanOrEqualTo(comeBack.get().getTimeDay().getTimeWithDay().get())
								&& leavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.greaterThanOrEqualTo(comeBack.get().getTimeDay().getTimeWithDay().get()))
								|| comeBack == null || !comeBack.isPresent()
								|| (comeBack.isPresent() && comeBack.get() == null)
								|| (comeBack.isPresent() && !comeBack.isPresent())) {
							if (leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE
									|| leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION) {
								stamp = new WorkStamp(
										leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get(),
										leavingWork.getAttendanceStamp().get().getStamp().get().getLocationCode()
												.isPresent()
														? leavingWork.getAttendanceStamp().get().getStamp().get()
																.getLocationCode().get()
														: null,
										TimeChangeMeans.AUTOMATIC_SET,null);
							}
						}
					}
				}
			}
		}
		return stamp;
	}

	private CorrectionResult returnStampLeakageCorrect(String companyId, String employeeID, GeneralDate processingDate,
			OutingTimeSheet outingTimeSheet) {
		CorrectionResult correctionResult = CorrectionResult.SUCCESS;

		Optional<StampReflectionManagement> stampReflectionManagement = stampReflectionManagementRepository
				.findByCid(companyId);

		if (stampReflectionManagement.isPresent()) {
			GoBackOutCorrectionClass goBackOutCorrectionClass = stampReflectionManagement.get()
					.getGoBackOutCorrectionClass();
			if (goBackOutCorrectionClass == GoBackOutCorrectionClass.DO_NOT_CORRECT) {
				correctionResult = CorrectionResult.FAILURE;
			} else {
				// 漏れ補正に使う退勤を確認する
				WorkStamp stamp = leaveWorkLeakageCorrection(companyId, employeeID, processingDate,
						outingTimeSheet.getGoOut());
				if (stamp != null) {
					correctionResult = CorrectionResult.SUCCESS;
				} else {
					correctionResult = CorrectionResult.FAILURE;
				}
			}
		}

		return correctionResult;
	}

	private WorkStamp leaveWorkLeakageCorrection(String companyId, String employeeID, GeneralDate processingDate,
			Optional<WorkStamp> goOut) {
		// 漏れ補正打刻をクリア
		WorkStamp stamp = null;

		Optional<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformance = this.timeLeavingOfDailyPerformanceRepository
				.findByKey(employeeID, processingDate);
		if (timeLeavingOfDailyPerformance.isPresent()) {
			List<TimeLeavingWork> timeLeavingWorks = timeLeavingOfDailyPerformance.get().getAttendance().getTimeLeavingWorks();

			for (TimeLeavingWork timeLeavingWork : timeLeavingWorks) {
				if ((timeLeavingWork.getAttendanceStamp().get().getStamp() != null
						&& timeLeavingWork.getAttendanceStamp().get().getStamp().isPresent()
						&& timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())
						&& (timeLeavingWork.getLeaveStamp().get().getStamp() != null
								&& timeLeavingWork.getLeaveStamp().get().getStamp().isPresent()
								&& timeLeavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())) {
					if ((goOut != null && goOut.isPresent() 
							&& timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
									.lessThanOrEqualTo(goOut.get().getTimeDay().getTimeWithDay().get())
							&& timeLeavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
									.greaterThanOrEqualTo(goOut.get().getTimeDay().getTimeWithDay().get()))
							|| goOut == null || !goOut.isPresent()
							|| (goOut.isPresent() && goOut == null)
							|| (goOut.isPresent() && !goOut.isPresent())) {
						if (timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
						.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE
								|| timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
								.getReasonTimeChange().getTimeChangeMeans()  == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION) {
							stamp = new WorkStamp(
									timeLeavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get(),
									timeLeavingWork.getAttendanceStamp().get().getStamp().get().getLocationCode()
											.isPresent()
													? timeLeavingWork.getAttendanceStamp().get().getStamp().get()
															.getLocationCode().get()
													: null,
									TimeChangeMeans.AUTOMATIC_SET,null);
						}
					}
				}
			}
		}

		Optional<TemporaryTimeOfDailyPerformance> temporaryTimeOfDailyPerformance = this.temporaryTimeOfDailyPerformanceRepository
				.findByKey(employeeID, processingDate);

		if (temporaryTimeOfDailyPerformance.isPresent()) {
			List<TimeLeavingWork> leavingWorks = temporaryTimeOfDailyPerformance.get().getAttendance().getTimeLeavingWorks();

			for (TimeLeavingWork leavingWork : leavingWorks) {
				if (leavingWork.getAttendanceStamp() != null && leavingWork.getAttendanceStamp().isPresent()
						&& leavingWork.getLeaveStamp() != null && leavingWork.getLeaveStamp().isPresent()) {
					if (leavingWork.getAttendanceStamp().get().getStamp() != null
							&& leavingWork.getAttendanceStamp().get().getStamp().isPresent()
							&& leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()
							&& leavingWork.getLeaveStamp().get().getStamp() != null
							&& leavingWork.getLeaveStamp().get().getStamp().isPresent()
							&& leavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
						if ((goOut != null && goOut.isPresent() 
								&& leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.lessThanOrEqualTo(goOut.get().getTimeDay().getTimeWithDay().get())
								&& leavingWork.getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get()
										.greaterThanOrEqualTo(goOut.get().getTimeDay().getTimeWithDay().get()))
								|| goOut == null || !goOut.isPresent()
								|| (goOut.isPresent() && goOut == null)
								|| (goOut.isPresent() && !goOut.isPresent())) {
							if (leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE
									|| leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay()
									.getReasonTimeChange().getTimeChangeMeans() == TimeChangeMeans.DIRECT_BOUNCE_APPLICATION) {
								stamp = new WorkStamp(
										leavingWork.getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get(),
										leavingWork.getAttendanceStamp().get().getStamp().get().getLocationCode()
												.isPresent()
														? leavingWork.getAttendanceStamp().get().getStamp().get()
																.getLocationCode().get()
														: null,
										TimeChangeMeans.AUTOMATIC_SET,null);
							}
						}
					}

				}
			}
		}

		return stamp;
	}
}
