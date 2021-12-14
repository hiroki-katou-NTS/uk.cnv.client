package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import java.util.ArrayList;
import java.util.Arrays;
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
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
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
		for (AttendanceBeforeApplicationReflect hist : appHist.get().getLstAttBeforeAppReflect()) {

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
							appHist.get().getReflectionTime())
					.stream()
					.filter(x -> !x.getApplicationId().equals(appHist.get().getApplicationId()))
					.collect(Collectors.toList());
			// 取得した[反映前（List）]に、対象の勤怠項目があるかチェック
			if (!checkAgain.isEmpty() && checkAgain.stream().flatMap(x -> x.getLstAttBeforeAppReflect().stream())
					.filter(x -> x.getAttendanceId() == hist.getAttendanceId()).findFirst().isPresent()) {
				continue;
			}

			// 処理中の反映前.勤怠項目IDに該当する値を元に戻す

			lstId.add(hist.getAttendanceId());
			DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();
			List<ItemValue> lstCurrent = converter.setData(domainDaily).convert(Arrays.asList(hist.getAttendanceId()));
			lstCurrent.stream().map(x -> {
				x.value(hist.getValue().orElse(null));
				return x;
			}).collect(Collectors.toList());
			DailyRecordToAttendanceItemConverter converterNew = require.createDailyConverter().setData(domainDaily);
			converterNew.merge(lstCurrent);
			domainDaily = converterNew.toDomain();

			// 処理中の反映前.編集状態をチェック
			if (hist.getEditState().isPresent()) {
				// 日別勤怠(work）の編集状態を元に戻す
				val lstResult = new ArrayList<EditStateOfDailyAttd>();
				domainDaily.getEditState().removeIf(x -> x.getAttendanceItemId() == editStateInDom.get().getAttendanceItemId());
				lstResult.addAll(domainDaily.getEditState());
				editStateInDom.get().setEditStateSetting(hist.getEditState().get().getEditStateSetting());
				lstResult.add(editStateInDom.get());
				domainDaily.setEditState(lstResult);
			} else {
				// 日別勤怠(work）の編集状態から該当の編集状態を削除する
				domainDaily.getEditState().removeIf(x -> x.getAttendanceItemId() == editStateInDom.get().getAttendanceItemId());
			}
		}
		return new DailyAfterAppReflectResult(
				new DailyRecordOfApplication(new ArrayList<>(), classification, domainDaily), lstId);
	}

	public static interface Require extends AcquireAppReflectHistForCancel.Require {

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
