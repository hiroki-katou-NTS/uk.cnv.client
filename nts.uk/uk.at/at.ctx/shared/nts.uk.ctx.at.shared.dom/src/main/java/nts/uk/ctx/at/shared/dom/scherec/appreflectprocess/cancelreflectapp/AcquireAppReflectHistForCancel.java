package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;

/**
 * @author thanh_nx
 *
 *         取消すために必要な申請反映履歴を取得する
 */
public class AcquireAppReflectHistForCancel {

	public static Optional<ApplicationReflectHistory> process(Require require, ApplicationShare app,
			GeneralDate baseDate, ScheduleRecordClassifi classification) {

		// 取消す申請の最新の反映履歴を取得
		List<ApplicationReflectHistory> appHists = require.findAppReflectHist(app.getEmployeeID(), app.getAppID(),
				baseDate, classification, false);
		if (appHists.isEmpty())
			return Optional.empty();

		// 取得した1件目の申請反映履歴を変数にセットする
		ApplicationReflectHistory appHistLastest = appHists.get(0);
		ApplicationReflectHistory appHistPrev = appHists.get(0);

		// 最新の申請反映履歴より前の申請反映履歴を取得する
		List<ApplicationReflectHistory> appHistsBeforeLastest = require.findAppReflectHistDateCond(app.getEmployeeID(),
				baseDate, classification, false, appHistLastest.getReflectionTime());
		for (ApplicationReflectHistory appHistBLast : appHistsBeforeLastest) {
			// 申請IDをチェック
			if (!appHistBLast.getApplicationId().equals(app.getAppID()))
				continue;
			// 処理中の申請反映履歴を変数[反映前の情報を持つ申請反映履歴]にセット
			appHistPrev = appHistBLast;
		}
		// [最新の申請反映履歴]と[反映前の情報を持つ申請反映履歴]を返す
		return Optional.of(appHistPrev);
	}

	public static interface Require {

		/**
		 * [条件] 社員ID＝input.申請.申請者 申請ID＝input.申請.申請ID 年月日＝input.対象日 予定実績区分＝input.予定実績区分
		 * 取消フラグ＝false
		 * 
		 * 反映時刻（DESC）
		 */
		public List<ApplicationReflectHistory> findAppReflectHist(String sid, String appId, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove);

		/**
		 * [条件] 社員ID＝input.申請.申請者 年月日＝input.対象日 予定実績区分＝input.予定実績区分 取消フラグ＝false
		 * 反映時刻＜=取得した[最新の申請反映履歴].反映時刻 ]
		 * 
		 * 反映時刻（DESC）
		 */
		public List<ApplicationReflectHistory> findAppReflectHistDateCond(String sid, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime);
	}
}
