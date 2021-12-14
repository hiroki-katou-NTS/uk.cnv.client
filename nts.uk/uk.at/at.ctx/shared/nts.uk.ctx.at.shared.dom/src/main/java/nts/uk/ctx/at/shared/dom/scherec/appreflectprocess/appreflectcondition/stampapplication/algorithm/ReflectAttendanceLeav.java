package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.ReflectAttendance;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.TimeReflectFromApp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author thanh_nx
 * 
 *         出退勤の反映（打刻）
 */
public class ReflectAttendanceLeav {

	// 反映する
	public static List<Integer> reflect(Require require, String cid, DailyRecordOfApplication dailyApp,
			List<TimeStampAppShare> listTimeStampApp) {
		List<Integer> lstItemId = new ArrayList<>();
		// 申請から反映する時刻を作成する
		List<TimeReflectFromApp> reflectTimeLst = listTimeStampApp.stream()
				.map(x -> new TimeReflectFromApp(new WorkNo(x.getDestinationTimeApp().getEngraveFrameNo()),
						x.getDestinationTimeApp().getStartEndClassification(), x.getTimeOfDay(), x.getWorkLocationCd()))
				.collect(Collectors.toList());

		// 時刻を反映する
		lstItemId.addAll(ReflectAttendance.reflectTime(require, cid, dailyApp, ScheduleRecordClassifi.RECORD, reflectTimeLst,
				Optional.of(TimeChangeMeans.APPLICATION)));
		return lstItemId;
	}

	public static interface Require extends ReflectAttendance.Require {

	}
}
