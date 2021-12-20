package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AttendanceBeforeApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.condition.DailyAfterAppReflectResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

/**
 * @author thanh_nx
 *
 *         [RQ671]申請の取消
 */
public class CancellationOfApplication {

	public static DailyAfterAppReflectResult process(Require require, ApplicationShare app, GeneralDate baseDate,
			ScheduleRecordClassifi classification, IntegrationOfDaily domainDaily) {
		// 取消すために必要な申請反映履歴を取得する
		val appHist = AcquireAppReflectHistForCancel.process(require, app, baseDate, classification);

		if (!appHist.isPresent()) {
			return new DailyAfterAppReflectResult(
					new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily), new ArrayList<>());
		}

		List<Integer> lstId = new ArrayList<>();
		// 取得した[元に戻すための申請反映履歴].反映前（List）でループ
		for (AttendanceBeforeApplicationReflect hist : appHist.get().getAppHistPrev().getLstAttBeforeAppReflect()) {

			// 処理中の勤怠項目IDの編集状態が[申請反映]かチェック
			Optional<EditStateOfDailyAttd> editStateInDom = domainDaily.getEditState().stream()
					.filter(x -> x.getAttendanceItemId() == hist.getAttendanceId()
							&& x.getEditStateSetting() == EditStateSetting.REFLECT_APPLICATION)
					.findFirst();
			if (!editStateInDom.isPresent()) {
				continue;
			}

			// 取消す申請より後の申請反映履歴があるかチェックする
			val checkAgain = require
					.findAppReflectHistAfterMaxTime(app.getEmployeeID(), baseDate, classification, false,
							appHist.get().getAppHistLastest().getAppExecInfo().getReflectionTime())
					.stream()
					.filter(x -> !x.getApplicationId().equals(appHist.get().getAppHistPrev().getApplicationId()))
					.collect(Collectors.toList());
			// 取得した[反映前（List）]に、対象の勤怠項目があるかチェック
			if (!checkAgain.isEmpty() && checkAgain.stream().flatMap(x -> x.getLstAttBeforeAppReflect().stream())
					.filter(x -> x.getAttendanceId() == hist.getAttendanceId()).findFirst().isPresent()) {
				continue;
			}

			// 申請を反映する前の状態に戻す
			domainDaily = ReturnStateBeforeReflectApp.process(require, domainDaily, appHist.get().getAppHistPrev(), classification,
					hist.getAttendanceId());
			
			//元に戻した勤怠項目ID(List)にadd
			lstId.add(hist.getAttendanceId());
			
		}
		return new DailyAfterAppReflectResult(
				new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily), lstId);
	}

	public static interface Require extends AcquireAppReflectHistForCancel.Require, ReturnStateBeforeReflectApp.Require {

		/**
		 * 申請反映履歴.社員ID＝input.申請.申請者 申請反映履歴.年月日＝input.対象日 申請反映履歴.予定実績区分＝input.予定実績区分
		 * 申請反映履歴.反映時刻 > 取得した[反映前の情報を持つ申請反映履歴].反映時刻 申請反映履歴.取消区分＝false
		 */
		public List<ApplicationReflectHistory> findAppReflectHistAfterMaxTime(String sid, GeneralDate baseDate,
				ScheduleRecordClassifi classification, boolean flgRemove, GeneralDateTime reflectionTime);

		//DailyRecordConverter
		public DailyRecordToAttendanceItemConverter createDailyConverter();
	}
}
