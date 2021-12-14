package nts.uk.ctx.at.record.dom.dailyresultcreationprocess.creationprocess.creationclass.dailywork;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;

/**
 * @author thanh_nx
 *
 *         日別実績を作成して、打刻を反映するOutput
 */
@AllArgsConstructor
@Getter
public class ReflectStampDailyAttdOutput {

//	/日別勤怠(Work)
	private IntegrationOfDaily integrationOfDaily;

	// 日別勤怠の何が変更されたか
	private ChangeDailyAttendance changeDailyAttendance;
}
