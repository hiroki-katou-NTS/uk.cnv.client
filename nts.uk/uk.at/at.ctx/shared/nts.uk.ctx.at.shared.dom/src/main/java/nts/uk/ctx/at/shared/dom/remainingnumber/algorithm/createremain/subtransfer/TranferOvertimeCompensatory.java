package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.common.CompensatoryOccurrenceDivision;

/**
 * @author thanh_nx
 *
 *         残業時間の代休振替
 */
public class TranferOvertimeCompensatory {

	public static IntegrationOfDaily process(Require require, String cid, IntegrationOfDaily dailyRecord,
			List<OvertimeHdHourTransfer> timeAfterReflectApp) {

		if (!dailyRecord.getAttendanceTimeOfDailyPerformance().isPresent()
				|| !dailyRecord.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
			return dailyRecord;
		}
		
		// 最大の時間帯でworkを作成
		IntegrationOfDaily dailyRecordNew = CreateWorkMaxTimeZone.process(require, cid, dailyRecord);

		MaximumTimeZone maxTimeZone = new MaximumTimeZone();
		List<OvertimeHdHourTransfer> maxTime = new ArrayList<>();
		if (!dailyRecordNew.getAttendanceTimeOfDailyPerformance().isPresent()
				|| !dailyRecordNew.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily()
						.getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()) {
			return dailyRecord;
		}

		OverTimeOfDaily overTimeWork = dailyRecordNew.getAttendanceTimeOfDailyPerformance().get()
				.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork()
				.get();

		// 計算日別勤怠(wrok）から残業枠時間帯（List）の内容を移送し、[最大時間帯]へセットする
		maxTimeZone.getTimeSpan().addAll(overTimeWork.getOverTimeWorkFrameTimeSheet().stream()
				.map(x -> Pair.of(x.getFrameNo(), x.getTimeSpan())).collect(Collectors.toList()));

		// 計算日別勤怠(work）から残業枠時間（List）の内容を移送して、[最大の時間]を作成する
		maxTime.addAll(IntStream.range(0, overTimeWork.getOverTimeWorkFrameTimeSheet().size()).boxed().map(indx -> {
			return new OvertimeHdHourTransfer(indx,
					overTimeWork.getOverTimeWorkFrameTimeSheet().get(indx).getOverTimeCalc(),
					overTimeWork.getOverTimeWorkFrameTimeSheet().get(indx).getTranferTimeCalc());
		}).collect(Collectors.toList()));

		// 代休振替処理
		List<OvertimeHdHourTransfer> lstMaxTime = SubstituteTransferProcess.process(require, cid,
				dailyRecordNew.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().map(x -> x.v()),
				CompensatoryOccurrenceDivision.FromOverTime, maxTimeZone, maxTime, timeAfterReflectApp);

		// [振替をした後の時間(List）]のすべての内容を[日別勤怠(work）]にセットする
		dailyRecord.getAttendanceTimeOfDailyPerformance().get()
		.getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork()
		.ifPresent(overtimeOld -> {
			overtimeOld.getOverTimeWorkFrameTime().stream().map(x -> {
				val maxTimeTemp = lstMaxTime.stream().filter(y -> y.getNo() == x.getOverWorkFrameNo().v()).findFirst();
				if (!maxTimeTemp.isPresent()) {
					return x;
				}
				x.getOverTimeWork().setTime(maxTimeTemp.get().getTime());
				x.getTransferTime().setTime(maxTimeTemp.get().getTransferTime());
				return x;
			}).collect(Collectors.toList());
		});

		return dailyRecord;

	}

	public static interface Require extends CreateWorkMaxTimeZone.Require, SubstituteTransferProcess.Require {

	}
}
