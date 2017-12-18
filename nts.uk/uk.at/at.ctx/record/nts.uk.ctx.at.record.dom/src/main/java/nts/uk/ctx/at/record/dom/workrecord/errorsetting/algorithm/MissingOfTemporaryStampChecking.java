package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm.CreateEmployeeDailyPerError;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingWork;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;

/*
 * 臨時系打刻漏れをチェックする
 */
@Stateless
public class MissingOfTemporaryStampChecking {
	
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepository;

	@Inject
	private CreateEmployeeDailyPerError createEmployeeDailyPerError;
	
	public void missingOfTemporaryStampChecking(String companyID, String employeeID, GeneralDate processingDate) {
//		OutPutProcess outPutProcess = OutPutProcess.NO_ERROR;
		TemporaryTimeOfDailyPerformance temporaryTimeOfDailyPerformance = this.temporaryTimeOfDailyPerformanceRepository.findByKey(employeeID, processingDate).get();
		List<TimeLeavingWork> timeLeavingWorks = temporaryTimeOfDailyPerformance.getTimeLeavingWorks();

		List<Integer> attendanceItemIds = new ArrayList<>();
		
		for (TimeLeavingWork timeLeavingWork : timeLeavingWorks) {
			if (timeLeavingWork.getAttendanceStamp() == null || (timeLeavingWork.getAttendanceStamp() != null
					&& timeLeavingWork.getAttendanceStamp().getStamp() == null)) {
				if (timeLeavingWork.getWorkNo().v().intValue() == 1) {
					attendanceItemIds.add(51);
				} else if (timeLeavingWork.getWorkNo().v().intValue() == 2) {
					attendanceItemIds.add(59);
				} else if (timeLeavingWork.getWorkNo().v().intValue() == 3) {
					attendanceItemIds.add(67);
				}
			}
			if (timeLeavingWork.getLeaveStamp() == null || (timeLeavingWork.getLeaveStamp() != null
					&& timeLeavingWork.getLeaveStamp().getStamp() == null)) {
				if (timeLeavingWork.getWorkNo().v().intValue() == 1) {
					attendanceItemIds.add(53);
				} else if (timeLeavingWork.getWorkNo().v().intValue() == 2) {
					attendanceItemIds.add(61);
				} else if (timeLeavingWork.getWorkNo().v().intValue() == 3) {
					attendanceItemIds.add(69);
				}
			}
			createEmployeeDailyPerError.createEmployeeDailyPerError(companyID, employeeID, processingDate, new ErrorAlarmWorkRecordCode("S001"), attendanceItemIds);
		}

//		return outPutProcess;
	}
}
