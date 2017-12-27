package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.algorithm.CreateEmployeeDailyPerError;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.record.dom.worktime.TemporaryTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingWork;
import nts.uk.ctx.at.record.dom.worktime.primitivevalue.WorkNo;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;

/*
 * 臨時系二重打刻をチェックする
 */
@Stateless
public class TemporaryDoubleStampChecking {
	
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepository;
	
	@Inject
	private CreateEmployeeDailyPerError createEmployeeDailyPerError;

	public void temporaryDoubleStampChecking(String companyID, String employeeID, GeneralDate processingDate){
		
		List<Integer> attendanceItemIDList = new ArrayList<>();
		
		Optional<TemporaryTimeOfDailyPerformance> temporaryTimeOfDailyPerformance = this.temporaryTimeOfDailyPerformanceRepository
				.findByKey(employeeID, processingDate);
		
		if (temporaryTimeOfDailyPerformance.isPresent()) {
			List<TimeLeavingWork> timeLeavingWorks = temporaryTimeOfDailyPerformance.get().getTimeLeavingWorks();
			for (TimeLeavingWork timeLeavingWork : timeLeavingWorks){
				
				if(timeLeavingWork.getAttendanceStamp().getNumberOfReflectionStamp() >= 2){
					if (timeLeavingWork.getWorkNo().equals(new WorkNo((1)))) {
						attendanceItemIDList.add(51);
					} else if (timeLeavingWork.getWorkNo().equals(new WorkNo((2)))) {
						attendanceItemIDList.add(59);
					} else if (timeLeavingWork.getWorkNo().equals(new WorkNo((3)))) {
						attendanceItemIDList.add(67);
					}
				}				
				if (timeLeavingWork.getLeaveStamp().getNumberOfReflectionStamp() >= 2) {
					if (timeLeavingWork.getWorkNo().equals(new WorkNo((1)))) {
						attendanceItemIDList.add(53);
					} else if (timeLeavingWork.getWorkNo().equals(new WorkNo((2)))) {
						attendanceItemIDList.add(61);
					} else if (timeLeavingWork.getWorkNo().equals(new WorkNo((3)))) {
						attendanceItemIDList.add(69);
					}
				}
			}
			this.createEmployeeDailyPerError.createEmployeeDailyPerError(companyID, employeeID, processingDate, new ErrorAlarmWorkRecordCode("S006"), attendanceItemIDList);
		}
		
	}
	
}
