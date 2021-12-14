package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/** 入退門 */
@Getter
@NoArgsConstructor
public class AttendanceLeavingGate {

	/** 勤務NO: 勤務NO */
	private WorkNo workNo;

	/** 入門: 勤怠打刻 */
	private Optional<WorkStamp> attendance;

	/** 退門: 勤怠打刻 */
	private Optional<WorkStamp> leaving;

	public AttendanceLeavingGate (WorkNo workNo, WorkStamp attendance, WorkStamp leaving) {
		this.workNo = workNo;
		this.attendance = Optional.ofNullable(attendance);
		this.leaving = Optional.ofNullable(leaving);
	}

	public void setWorkNo(WorkNo workNo) {
		this.workNo = workNo;
	}

	public void setAttendance(Optional<WorkStamp> attendance) {
		this.attendance = attendance;
	}

	public void setLeaving(Optional<WorkStamp> leaving) {
		this.leaving = leaving;
	}	
	
	public Optional<TimeWithDayAttr> getAttendanceTime() {
		return this.getAttendance().flatMap(x -> x.getWithTimeDay());
	}

	public Optional<TimeWithDayAttr> getLeavingTime() {
		return this.getLeaving().flatMap(x -> x.getWithTimeDay());
	}
	
	public boolean leakageCheck() {
		Optional<TimeWithDayAttr> attdTimeAtr = getAttendanceTime();
		Optional<TimeWithDayAttr> leaveTimeAtr = getLeavingTime();

		return (attdTimeAtr.isPresent() && leaveTimeAtr.isPresent())
				|| (!attdTimeAtr.isPresent() && !leaveTimeAtr.isPresent());

	}
}
