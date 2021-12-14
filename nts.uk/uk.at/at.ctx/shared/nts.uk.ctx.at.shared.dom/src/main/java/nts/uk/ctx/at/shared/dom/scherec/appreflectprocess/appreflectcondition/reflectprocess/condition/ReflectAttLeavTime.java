package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelAppStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;

/**
 * @author thanh_nx
 *
 *         出勤退勤時刻の反映
 */
public class ReflectAttLeavTime {

	public static List<Integer> reflect(Require require, String cid, DailyRecordOfApplication dailyApp,
			TimeReflectFromApp reflectTime, Optional<TimeChangeMeans> timeChangeMeanOpt) {

		List<Integer> lstItemId = new ArrayList<Integer>();
		// [日別勤怠(work）の出退勤]をチェック
		// 日別勤怠の出退勤
		Optional<TimeLeavingWork> attendanceLeave = dailyApp.getAttendanceLeave()
				.flatMap(at -> at.getAttendanceLeavingWork(reflectTime.getWorkNo()));

		if (attendanceLeave.isPresent()) {
			if (reflectTime.getStartEndClassification() == StartEndClassificationShare.START) {
				// 時刻を変更してもいいか判断する
				if (timeChangeMeanOpt.isPresent() && attendanceLeave.flatMap(c -> c.getStampOfAttendance())
						.map(x -> x.isCanChangeTime(require, cid, timeChangeMeanOpt.get())).orElse(true)) {
					if (!attendanceLeave.get().getAttendanceStamp().isPresent()) {
						attendanceLeave.get().setAttendanceStamp(
								Optional.of(TimeActualStamp.createDefaultWithReason(timeChangeMeanOpt.get())));
					}
					if (!attendanceLeave.get().getAttendanceStamp().get().getStamp().isPresent()) {
						attendanceLeave.get().getAttendanceStamp().get()
								.setStamp(Optional.of(WorkStamp.createDefault()));
					}
					attendanceLeave.flatMap(c -> c.getStampOfAttendance()).ifPresent(at -> {
						at.getTimeDay().setTimeWithDay(Optional.ofNullable(reflectTime.getTimeOfDay()));
						at.getTimeDay().getReasonTimeChange().setTimeChangeMeans(timeChangeMeanOpt.get());

						if (reflectTime.getWorkLocationCd().isPresent()) {
							lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(30,
									reflectTime.getWorkNo().v(), 10)));
							at.setLocationCode(reflectTime.getWorkLocationCd());
						}
						lstItemId.addAll(
								Arrays.asList(CancelAppStamp.createItemId(31, reflectTime.getWorkNo().v(), 10)));
						
					});
				}
			}

			if (reflectTime.getStartEndClassification() == StartEndClassificationShare.END) {
				// 時刻を変更してもいいか判断する
				if (timeChangeMeanOpt.isPresent() && attendanceLeave.flatMap(c -> c.getStampOfLeave())
						.map(x -> x.isCanChangeTime(require, cid, timeChangeMeanOpt.get())).orElse(true)) {
					if (!attendanceLeave.get().getLeaveStamp().isPresent()) {
						attendanceLeave.get().setLeaveStamp(
								Optional.of(TimeActualStamp.createDefaultWithReason(timeChangeMeanOpt.get())));
					}
					
					if (!attendanceLeave.get().getLeaveStamp().get().getStamp().isPresent()) {
						attendanceLeave.get().getLeaveStamp().get().setStamp(Optional.of(WorkStamp.createDefault()));
					}
					
					attendanceLeave.flatMap(c -> c.getStampOfLeave()).ifPresent(at -> {
						at.getTimeDay().setTimeWithDay(Optional.ofNullable(reflectTime.getTimeOfDay()));
						at.getTimeDay().getReasonTimeChange().setTimeChangeMeans(timeChangeMeanOpt.get());

						if (reflectTime.getWorkLocationCd().isPresent()) {
							lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(33,
									reflectTime.getWorkNo().v(), 10)));
							at.setLocationCode(reflectTime.getWorkLocationCd());
						}
						
						lstItemId.addAll(
								Arrays.asList(CancelAppStamp.createItemId(34, reflectTime.getWorkNo().v(), 10)));
					});
				}
			}
		} else {
			TimeLeavingWork work = new TimeLeavingWork(reflectTime.getWorkNo(), null, null);
			if (reflectTime.getStartEndClassification() == StartEndClassificationShare.START) {
				work.setAttendanceStamp(
						Optional.of(new TimeActualStamp(null,
								new WorkStamp(new WorkTimeInformation(
										new ReasonTimeChange(timeChangeMeanOpt.get(), Optional.empty()),
										reflectTime.getTimeOfDay()), reflectTime.getWorkLocationCd()),
								0)));
				
				if (reflectTime.getWorkLocationCd().isPresent()) {
					lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(30,
							reflectTime.getWorkNo().v(), 10)));
				}
				
				lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(31, reflectTime.getWorkNo().v(), 10)));
			}
			if (reflectTime.getStartEndClassification() == StartEndClassificationShare.END) {
				work.setLeaveStamp(
						Optional.of(new TimeActualStamp(null,
								new WorkStamp(new WorkTimeInformation(
										new ReasonTimeChange(timeChangeMeanOpt.get(), Optional.empty()),
										reflectTime.getTimeOfDay()), reflectTime.getWorkLocationCd()),
								0)));
				
				if (reflectTime.getWorkLocationCd().isPresent()) {
					lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(33,
							reflectTime.getWorkNo().v(), 10)));
				}
				
				lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(34, reflectTime.getWorkNo().v(), 10)));
			}

			if (dailyApp.getAttendanceLeave().isPresent()) {
				dailyApp.getAttendanceLeave().get().getTimeLeavingWorks().add(work);
			} else {
				List<TimeLeavingWork> lst = new ArrayList<TimeLeavingWork>();
				lst.add(work);
				dailyApp.setAttendanceLeave(Optional.of(new TimeLeavingOfDailyAttd(lst, new WorkTimes(0))));
			}

		}
		
		// 出退勤回数の計算
		dailyApp.getAttendanceLeave().ifPresent(x -> x.setCountWorkTime());
		
		// 申請反映状態にする
		UpdateEditSttCreateBeforeAppReflect.update(dailyApp, lstItemId);
		return lstItemId;
	}

	public static interface Require extends WorkStamp.Require {

	}
}
