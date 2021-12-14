package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.algorithm.subtransfer;

import java.util.List;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.OvertimeHdHourTransfer;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.TranferOvertimeCompensatory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * @author thanh_nx
 *
 *         残業時間の代休振替(申請用)
 */
public class TranferOvertimeCompensatoryApp {

	public static IntegrationOfDaily process(Require require, String cid, IntegrationOfDaily dailyRecord) {

		//input.日別勤怠(work）から残業枠時間（List）の内容を移送し、[申請を反映させた後の時間]へセットする
		if (!dailyRecord.getAttendanceTimeOfDailyPerformance().isPresent()
				|| !dailyRecord.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
			return dailyRecord;
		}
		
		List<OvertimeHdHourTransfer> timeAfterReflectApp = dailyRecord.getAttendanceTimeOfDailyPerformance().get()
				.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork()
				.get().getOverTimeWorkFrameTime().stream().map(x -> {
			return new OvertimeHdHourTransfer(x.getOverWorkFrameNo().v(), x.getOverTimeWork().getTime(),
					x.getTransferTime().getTime());
		}).collect(Collectors.toList());
		
		//残業時間の代休振替
		return TranferOvertimeCompensatory.process(require, cid, dailyRecord, timeAfterReflectApp);

	}

	public static interface Require extends TranferOvertimeCompensatory.Require {

	}
}
