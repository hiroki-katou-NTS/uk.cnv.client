package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.UpdateEditSttCreateBeforeAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

/**
 * @author thanh_nx
 *
 *         臨時出退勤の反映
 */
public class ReflectTemporaryAttLeav {

	public static List<Integer> reflect(DailyRecordOfApplication dailyApp, List<TimeStampAppShare> listTimeStampApp) {

		List<Integer> lstItemId = new ArrayList<>();
		listTimeStampApp.stream().forEach(data -> {
			// input. 臨時出退勤（List）でループする
			if (dailyApp.getTempTime().isPresent()) {
				// 日別勤怠(work）の[臨時出退勤]をチェック
				Optional<TimeLeavingWork> timeLeavOpt = dailyApp.getTempTime().get().getTimeLeavingWorks().stream()
						.filter(x -> x.getWorkNo().v() == data.getDestinationTimeApp().getEngraveFrameNo().intValue())
						.findFirst();
				if (!timeLeavOpt.isPresent()) {
					// 該当の打刻枠NOをキーに[臨時出退勤]を作成する
					// 臨時出退勤を日別勤怠(work）にセットする
					Pair<TimeLeavingWork, List<Integer>> result = createTimeLeav(data);
					dailyApp.getTempTime().get().getTimeLeavingWorks().add(result.getLeft());
					lstItemId.addAll(result.getRight());
					// dailyApp.getTempTime().get().setCountWorkTime();
				}else {
					lstItemId.addAll(updateTimeLeav(timeLeavOpt.get(), data));
				}
			} else {
				List<TimeLeavingWork> lst = new ArrayList<>();
				Pair<TimeLeavingWork, List<Integer>> result = createTimeLeav(data);
				lst.add(result.getLeft());
				lstItemId.addAll(result.getRight());
				dailyApp.setTempTime(Optional.of(new TemporaryTimeOfDailyAttd(new WorkTimes(0), lst)));
				// dailyApp.getTempTime().get().setCountWorkTime();
			}
			

			// 申請反映状態にする
			UpdateEditSttCreateBeforeAppReflect.update(dailyApp, lstItemId);

		});

		return lstItemId;
	}

	public static Pair<TimeLeavingWork, List<Integer>> createTimeLeav(TimeStampAppShare data) {
		List<Integer> itemIds = new ArrayList<Integer>();
		boolean start = (data.getDestinationTimeApp().getStartEndClassification() == StartEndClassificationShare.START);
		if (start) {
			itemIds.addAll(Arrays.asList(
					//[臨時出勤時刻1～3]
					CancelAppStamp.createItemId(51, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8),
					// [臨時出勤場所コード1～3]
					CancelAppStamp.createItemId(50, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
		} else {
			itemIds.addAll(Arrays.asList(
					//[臨時退勤時刻1～3]
					CancelAppStamp.createItemId(53, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8),
					// [臨時退勤場所コード1～3]
					CancelAppStamp.createItemId(52, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
		}
		return Pair.of(
				new TimeLeavingWork(new WorkNo(data.getDestinationTimeApp().getEngraveFrameNo().intValue()),
						start ? createTimeActualStamp(data) : null, //
						start ? null : createTimeActualStamp(data)), //
				itemIds);//
	}

	private static TimeActualStamp createTimeActualStamp(TimeStampAppShare data) {
		return new TimeActualStamp(null, new WorkStamp(
				new WorkTimeInformation(new ReasonTimeChange(TimeChangeMeans.APPLICATION, Optional.empty()), data.getTimeOfDay()),
				data.getWorkLocationCd()), 0);
	}
	
	public static List<Integer> updateTimeLeav(TimeLeavingWork timeLeav, TimeStampAppShare data) {

		List<Integer> lstItemId = new ArrayList<>();
		boolean start = (data.getDestinationTimeApp().getStartEndClassification() == StartEndClassificationShare.START);
		if (start) {
			if (!timeLeav.getAttendanceStamp().isPresent()) {
				timeLeav.setAttendanceStamp(
						Optional.of(TimeActualStamp.createDefaultWithReason(TimeChangeMeans.APPLICATION)));
			}
			if (!timeLeav.getAttendanceStamp().get().getStamp().isPresent()) {
				timeLeav.getAttendanceStamp().get().setStamp(Optional.of(WorkStamp.createDefault()));
			}
			val y = timeLeav.getAttendanceStamp().get().getStamp().get();
			if (data.getWorkLocationCd().isPresent()) {
				lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(50,
						data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
				y.setLocationCode(data.getWorkLocationCd());
			}
			y.getTimeDay().getReasonTimeChange().setTimeChangeMeans(TimeChangeMeans.APPLICATION);
			y.getTimeDay().setTimeWithDay(Optional.ofNullable(data.getTimeOfDay()));
			lstItemId.addAll(Arrays.asList(
					CancelAppStamp.createItemId(51, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
		} else {
			if (!timeLeav.getLeaveStamp().isPresent()) {
				timeLeav.setLeaveStamp(
						Optional.of(TimeActualStamp.createDefaultWithReason(TimeChangeMeans.APPLICATION)));
			}
			if(!timeLeav.getLeaveStamp().get().getStamp().isPresent()) {
				timeLeav.getLeaveStamp().get().setStamp(Optional.of(WorkStamp.createDefault()));
			}
			val y = timeLeav.getLeaveStamp().get().getStamp().get();
			if (data.getWorkLocationCd().isPresent()) {
				lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(52,
						data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
				y.setLocationCode(data.getWorkLocationCd());
			}
			y.getTimeDay().getReasonTimeChange().setTimeChangeMeans(TimeChangeMeans.APPLICATION);
			y.getTimeDay().setTimeWithDay(Optional.ofNullable(data.getTimeOfDay()));
			lstItemId.addAll(Arrays.asList(
					CancelAppStamp.createItemId(53, data.getDestinationTimeApp().getEngraveFrameNo().intValue(), 8)));
		}
		return lstItemId;
	}
}
