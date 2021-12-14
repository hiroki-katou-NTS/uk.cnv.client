package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;

/**
 * @author thanh_nx
 *
 *         開始終了時刻の反映
 */
public class ReflectAttendance {

	//時間帯を反映する
	public static List<Integer> reflect(Require require, String cid, List<TimeZoneWithWorkNo> timeZoneWithWorkNoLst,
			ScheduleRecordClassifi classification, DailyRecordOfApplication dailyApp, Optional<Boolean> reflectAtt,
			Optional<Boolean> reflectLeav, Optional<TimeChangeMeans> timeChangeMeanOpt) {
		List<Integer> lstItemId = new ArrayList<Integer>();

		if (!reflectAtt.orElse(false) && !reflectLeav.orElse(false)) {
			return lstItemId;
		}

		// [input. 勤務時間帯(List）]をループ
		for (TimeZoneWithWorkNo timeZone : timeZoneWithWorkNoLst) {
			//[input. 申請の反映先]をチェック
			if (classification == ScheduleRecordClassifi.SCHEDULE) {
				//出勤、退勤を両方反映するかどうか確認
				if (!reflectAtt.orElse(false) || !reflectLeav.orElse(false)) {
					return lstItemId;
				}
				// 時間帯を反映する
				lstItemId.addAll(ReflectStartEndWork.reflectTimeZone(cid, dailyApp, timeZone));
			} else {
				//申請から反映する時刻を作成する
				List<TimeReflectFromApp> reflectTimeLst = new ArrayList<>();
				if(reflectAtt.orElse(false)) {
					reflectTimeLst.add(new TimeReflectFromApp(timeZone.getWorkNo(),
						        StartEndClassificationShare.START, timeZone.getTimeZone().getStartTime(), Optional.empty()));
				};
				if(reflectLeav.orElse(false)) {
					reflectTimeLst.add(new TimeReflectFromApp(timeZone.getWorkNo(),
					        StartEndClassificationShare.END, timeZone.getTimeZone().getEndTime(), Optional.empty()));
				}
				
				//時間帯を反映する
				lstItemId
						.addAll(reflectTime(require, cid, dailyApp, classification, reflectTimeLst, timeChangeMeanOpt));
			}
		}

		return lstItemId;
	}
	
	// 時刻を反映する
	public static List<Integer> reflectTime(Require require, String cid, DailyRecordOfApplication dailyApp,
			ScheduleRecordClassifi classification, List<TimeReflectFromApp> reflectTimeLst,
			Optional<TimeChangeMeans> timeChangeMeanOpt) {
		List<Integer> lstItemId = new ArrayList<Integer>();
		//[input. 時刻(List）]をループ
		for (TimeReflectFromApp reflectTime : reflectTimeLst) {
			if (classification == ScheduleRecordClassifi.SCHEDULE) {
				//時刻を反映する
				lstItemId.addAll(ReflectStartEndWork.reflectTimeAtr(cid, dailyApp, reflectTime));
			}else {
				//時刻を反映する
				lstItemId.addAll(ReflectAttLeavTime.reflect(require, cid, dailyApp, reflectTime, timeChangeMeanOpt));
			}
		}
		return lstItemId;
	}
	
	public static interface Require extends ReflectAttLeavTime.Require {

	}
}
