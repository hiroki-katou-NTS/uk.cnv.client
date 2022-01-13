package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenAttendanceTimeEachTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

@Getter
/** 日別実績の応援作業別勤怠時間 */
public class OuenWorkTimeOfDaily extends AggregateRoot {

	/** 社員ID: 社員ID */
	private String empId;

	/** 年月日: 年月日 */
	private GeneralDate ymd;

	/** 応援時間: 日別勤怠の応援作業時間 */
	private List<OuenWorkTimeOfDailyAttendance> ouenTimes;

	private OuenWorkTimeOfDaily(String empId, GeneralDate ymd, List<OuenWorkTimeOfDailyAttendance> ouenTimes) {
		super();
		this.empId = empId;
		this.ymd = ymd;
		this.ouenTimes = ouenTimes;
	}

	public static OuenWorkTimeOfDaily create(String empId, GeneralDate ymd,
			List<OuenWorkTimeOfDailyAttendance> ouenTimes) {

		return new OuenWorkTimeOfDaily(empId, ymd, ouenTimes);
	}

//■Public
	/** [1] 変更する */
	public void setOuenTime(List<OuenWorkTimeOfDailyAttendance> ouenTimes) {
		this.ouenTimes = ouenTimes;
	}

	public void updateOuenTime(OuenWorkTimeOfDailyAttendance ouenTimeNew) {
		Optional<OuenWorkTimeOfDailyAttendance> ouenTimeOld = this.ouenTimes.stream()
				.filter(c -> c.getWorkNo().v() == ouenTimeNew.getWorkNo().v()).findFirst();
		if (ouenTimeOld.isPresent()) {
			this.ouenTimes.removeIf(c -> c.getWorkNo().v() == ouenTimeNew.getWorkNo().v());

			OuenWorkTimeOfDailyAttendance updatedOuenTime = ouenTimeOld.get();
			this.ouenTimes.add(OuenWorkTimeOfDailyAttendance.create(ouenTimeNew.getWorkNo(),
					OuenAttendanceTimeEachTimeSheet.create(ouenTimeNew.getWorkTime().getTotalTime(),
							updatedOuenTime.getWorkTime().getBreakTime(), updatedOuenTime.getWorkTime().getWithinTime(),
							updatedOuenTime.getWorkTime().getWithinAmount(),
							updatedOuenTime.getWorkTime().getMedicalTime(),
							updatedOuenTime.getWorkTime().getPremiumTime()),
					updatedOuenTime.getMoveTime(), updatedOuenTime.getAmount()));
		}
	}
}
