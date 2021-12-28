package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTimeSheet;

@AllArgsConstructor
@Getter
public class OverTimeOfDailyCommand {
	// 残業枠時間帯
	private List<OverTimeFrameTimeSheet> overTimeWorkFrameTimeSheet;
	// 残業枠時間
	@Setter
	private List<OverTimeFrameTime> overTimeWorkFrameTime;
	// 法定外深夜時間 (所定外深夜時間)
	@Setter
	private Finally<ExcessOverTimeWorkMidNightTime> excessOverTimeWorkMidNightTime;
	// 残業拘束時間
	private AttendanceTime overTimeWorkSpentAtWork = new AttendanceTime(0);
	// 変形法定内残業
	private AttendanceTime irregularWithinPrescribedOverTimeWork = new AttendanceTime(0);
	// フレックス時間
	@Setter
	private FlexTime flexTime = new FlexTime(
			TimeDivergenceWithCalculationMinusExist.sameTime(new AttendanceTimeOfExistMinus(0)), new AttendanceTime(0));
}
