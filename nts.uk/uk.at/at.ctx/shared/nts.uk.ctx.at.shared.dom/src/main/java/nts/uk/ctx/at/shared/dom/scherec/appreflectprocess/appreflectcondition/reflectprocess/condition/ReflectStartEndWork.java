package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.CancelAppStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;

/**
 * @author thanh_nx
 *
 *         始業終業の反映
 */
public class ReflectStartEndWork {

	// 反映する
	public static List<Integer> reflect(Require require, String cid, DailyRecordOfApplication dailyApp,
			List<TimeZoneWithWorkNo> timeZoneWithWorkNoLst) {
		List<Integer> itemIds = new ArrayList<Integer>();
		// 日別勤怠(work)の予定勤務時間帯に[input:申請勤務時間帯]をセットする
		itemIds.addAll(ReflectAttendance.reflect(require, cid, timeZoneWithWorkNoLst, ScheduleRecordClassifi.SCHEDULE,
				dailyApp, Optional.of(true), Optional.of(true), Optional.empty()));

		return itemIds;
	}

	// 反映する
	public static List<Integer> reflect(Require require, String cid, DailyRecordOfApplication dailyApp,
			List<TimeZoneWithWorkNo> timeZoneWithWorkNoLst, boolean att, boolean leav) {
		return ReflectAttendance.reflect(require, cid, timeZoneWithWorkNoLst, ScheduleRecordClassifi.SCHEDULE, dailyApp,
				Optional.of(att), Optional.of(leav), Optional.empty());
	}

	// 時間帯を反映する
	public static List<Integer> reflectTimeZone(String cid, DailyRecordOfApplication dailyApp,
			TimeZoneWithWorkNo timeZone) {
		List<Integer> lstItemId = new ArrayList<Integer>();
		// [日別勤怠(work)の始業終業時間帯]をチェック
		Optional<ScheduleTimeSheet> scheduleTimeSheet = dailyApp.getWorkInformation()
				.getScheduleTimeSheet(timeZone.getWorkNo());
		if (!scheduleTimeSheet.isPresent()) {
			// 予定時間帯を追加する
			dailyApp.getWorkInformation().getScheduleTimeSheets().add(new ScheduleTimeSheet(timeZone.getWorkNo().v(),
					timeZone.getTimeZone().getStartTime().v(), timeZone.getTimeZone().getEndTime().v()));
			lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(3, timeZone.getWorkNo().v(), 2),
					CancelAppStamp.createItemId(4, timeZone.getWorkNo().v(), 2)));
		} else {
			// 予定時間帯を更新する
			scheduleTimeSheet.get().setAttendance(timeZone.getTimeZone().getStartTime());
			scheduleTimeSheet.get().setLeaveWork(timeZone.getTimeZone().getEndTime());
			lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(3, timeZone.getWorkNo().v(), 2),
					CancelAppStamp.createItemId(4, timeZone.getWorkNo().v(), 2)));
		}

		// 編集状態の更新と申請反映前リストの作成
		UpdateEditSttCreateBeforeAppReflect.update(dailyApp, lstItemId);
		return lstItemId;
	}

	// 時刻を反映する
	public static List<Integer> reflectTimeAtr(String cid, DailyRecordOfApplication dailyApp,
			TimeReflectFromApp reflectTime) {
		List<Integer> lstItemId = new ArrayList<Integer>();
		// [日別勤怠(work)の始業終業時間帯]をチェック
		Optional<ScheduleTimeSheet> scheduleTimeSheet = dailyApp.getWorkInformation()
				.getScheduleTimeSheet(reflectTime.getWorkNo());
		if(!scheduleTimeSheet.isPresent()) {
			return lstItemId;
		}
		
		//input.時刻.開始終了時刻 = "開始"
		if (reflectTime.getStartEndClassification() == StartEndClassificationShare.START) {
			scheduleTimeSheet.get().setAttendance(reflectTime.getTimeOfDay());
			lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(3, reflectTime.getWorkNo().v(), 2)));
		} else {
			//input.時刻.開始終了時刻 = "終了"
			scheduleTimeSheet.get().setLeaveWork(reflectTime.getTimeOfDay());
			lstItemId.addAll(Arrays.asList(CancelAppStamp.createItemId(4, reflectTime.getWorkNo().v(), 2)));
		}
		
		// 編集状態の更新と申請反映前リストの作成
		UpdateEditSttCreateBeforeAppReflect.update(dailyApp, lstItemId);
		return lstItemId;
	}

	public static interface Require extends ReflectAttendance.Require {

	}
}
