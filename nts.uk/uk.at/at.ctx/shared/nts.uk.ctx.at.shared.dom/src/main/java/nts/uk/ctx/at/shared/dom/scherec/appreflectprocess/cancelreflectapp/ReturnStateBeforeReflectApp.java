package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.cancelreflectapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AttendanceBeforeApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;

/**
 * @author thanh_nx
 *
 *         申請を反映する前の状態に戻す
 */
public class ReturnStateBeforeReflectApp {

	// [1] 実行
	public static IntegrationOfDaily process(Require require, IntegrationOfDaily domainDaily,
			ApplicationReflectHistory hist, ScheduleRecordClassifi classification, Integer itemId) {

		// $日別勤怠
		IntegrationOfDaily dailyRestore = restoreValueFromApp(require, domainDaily, hist, classification, itemId);
		// $反映履歴
		ApplicationReflectHistory restoreHistResult = hist;
		while (true) {
			// $同時に値を戻す反映履歴
			Optional<ApplicationReflectHistory> restoreHist = restoreHistResult.getReflectHistNeedReturn(require,
					classification, itemId, hist.getApplicationId() );
			if (!restoreHist.isPresent()) {
				break;
			}
			// $日別勤怠
			dailyRestore = restoreValueFromApp(require, dailyRestore, restoreHist.get(), classification, itemId);
			restoreHistResult = restoreHist.get();
		}
		return dailyRestore;
	}

	// [prv-1] 申請反映履歴から値を元に戻す
	private static IntegrationOfDaily restoreValueFromApp(Require require, IntegrationOfDaily domainDaily,
			ApplicationReflectHistory hist, ScheduleRecordClassifi classification, Integer itemId) {
		Optional<AttendanceBeforeApplicationReflect> correspondHist = hist.getBeforeSpecifiAttReflected(itemId);
		if (!correspondHist.isPresent()) {
			return domainDaily;
		}
		return restoreFirstValue(require, domainDaily, classification, itemId, hist);
	}

	// [prv-2] 値を元に戻す
	private static IntegrationOfDaily restoreFirstValue(Require require, IntegrationOfDaily domainDaily,
			ScheduleRecordClassifi classification, Integer itemId, ApplicationReflectHistory histApp) {

		Optional<AttendanceBeforeApplicationReflect> histOpt = histApp.determineValueUndoSpecifi(require,
				classification, itemId);
		if (!histOpt.isPresent())
			return domainDaily;

		DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();
		List<ItemValue> lstCurrent = converter.setData(domainDaily)
				.convert(Arrays.asList(histOpt.get().getAttendanceId()));
		lstCurrent.stream().map(x -> {
			x.value(histOpt.get().getValue().orElse(null));
			return x;
		}).collect(Collectors.toList());
		DailyRecordToAttendanceItemConverter converterNew = require.createDailyConverter().setData(domainDaily);
		converterNew.merge(lstCurrent);
		domainDaily = converterNew.toDomain();

		// 処理中の反映前.編集状態をチェック
		Optional<EditStateOfDailyAttd> editStateInDom = domainDaily.getEditState().stream()
				.filter(x -> x.getAttendanceItemId() == histOpt.get().getAttendanceId()).findFirst();
		if (histOpt.get().getEditState().isPresent()) {
			// 日別勤怠(work）の編集状態を元に戻す
			val lstResult = new ArrayList<EditStateOfDailyAttd>();
			domainDaily.getEditState().removeIf(x -> x.getAttendanceItemId() == histOpt.get().getAttendanceId());
			lstResult.addAll(domainDaily.getEditState());

			lstResult.add(new EditStateOfDailyAttd(histOpt.get().getAttendanceId(),
					histOpt.get().getEditState().get().getEditStateSetting()));
			domainDaily.setEditState(lstResult);
		} else {
			// 日別勤怠(work）の編集状態から該当の編集状態を削除する
			if(editStateInDom.isPresent()) domainDaily.getEditState()
					.removeIf(x -> x.getAttendanceItemId() == editStateInDom.get().getAttendanceItemId());
		}
		return domainDaily;
	}

	public static interface Require extends ApplicationReflectHistory.Require {
		// DailyRecordConverter
		public DailyRecordToAttendanceItemConverter createDailyConverter();
	}
}
