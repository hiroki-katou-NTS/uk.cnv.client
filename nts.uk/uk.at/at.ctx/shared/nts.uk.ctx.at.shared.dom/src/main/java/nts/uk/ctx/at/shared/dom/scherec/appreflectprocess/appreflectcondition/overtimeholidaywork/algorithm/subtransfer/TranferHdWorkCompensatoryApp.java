package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.TranferHdWorkCompensatory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * @author thanh_nx
 *
 *         休日出勤時間の代休振替(申請用)
 */
public class TranferHdWorkCompensatoryApp {

	public static IntegrationOfDaily process(Require require, String cid, IntegrationOfDaily dailyRecord) {

		// input.日別勤怠(work）から休出枠時間（List）の内容を移送し、[申請を反映させた後の時間]へセットする
		if (!dailyRecord.getAttendanceTimeOfDailyPerformance().isPresent()
				|| !dailyRecord.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().isPresent()) {
			return dailyRecord;
		}

		List<OvertimeHdHourTransfer>timeAfterReflectApp = dailyRecord.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
				.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getWorkHolidayTime().get()
				.getHolidayWorkFrameTime().stream().map(x -> {
					return new OvertimeHdHourTransfer(x.getHolidayFrameNo().v(),
							x.getHolidayWorkTime().isPresent() ? x.getHolidayWorkTime().get().getTime()
									: new AttendanceTime(0),
							x.getTransferTime().isPresent() ? x.getTransferTime().get().getTime()
									: new AttendanceTime(0));
				}).collect(Collectors.toList());
		//休日出勤時間の代休振替
		return TranferHdWorkCompensatory.process(require, cid, dailyRecord, timeAfterReflectApp);

	}

	public static interface Require extends TranferHdWorkCompensatory.Require {
		Optional<WorkType> getWorkType(String workTypeCd);
	}
}
