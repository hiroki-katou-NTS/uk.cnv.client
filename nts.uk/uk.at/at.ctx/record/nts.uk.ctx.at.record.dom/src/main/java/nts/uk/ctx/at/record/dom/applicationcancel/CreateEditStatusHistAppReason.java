package nts.uk.ctx.at.record.dom.applicationcancel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.AttendanceBeforeApplicationReflect;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

/**
 * @author thanh_nx
 *
 *         申請理由の編集状態と履歴を作成する
 */
public class CreateEditStatusHistAppReason {

	public static List<AttendanceBeforeApplicationReflect> process(Require require, String employeeId, GeneralDate date, String appId,
			ScheduleRecordClassifi classification, Map<Integer, String> mapValue, GeneralDateTime reflectTime) {
		List<AttendanceBeforeApplicationReflect> lstAttBeforeReflect = new ArrayList<>();
		// 日別実績から、該当する編集状態を取得する
		List<EditStateOfDailyPerformance> lstEditState = require.findByKey(employeeId, date);

		mapValue.forEach((key, value) -> {
			EditStateOfDailyPerformance edit = lstEditState.stream()
					.filter(x -> x.getEditState().getAttendanceItemId() == key).findFirst().orElse(null);
			if (edit == null) {
				// 申請反映前の勤怠へセットする
				lstAttBeforeReflect.add(new AttendanceBeforeApplicationReflect(key, Optional.ofNullable(value), Optional.empty()));

				// 日別実績の更新（日別勤怠の編集状態を追加する）
				require.addAndUpdate(Arrays.asList(
						new EditStateOfDailyPerformance(employeeId, key, date, EditStateSetting.REFLECT_APPLICATION)));
			} else {

				// 申請反映前の勤怠へセット
				lstAttBeforeReflect
						.add(new AttendanceBeforeApplicationReflect(key, Optional.ofNullable(value), Optional.of(edit.getEditState())));

				// 日別実績の更新（日別勤怠の編集状態を更新する）
				require.addAndUpdate(Arrays.asList(
						new EditStateOfDailyPerformance(employeeId, key, date, EditStateSetting.REFLECT_APPLICATION)));
			}
		});

		// 申請反映履歴を作成する
		require.insertAppReflectHist(new ApplicationReflectHistory(employeeId, date, appId, reflectTime,
				classification, false, lstAttBeforeReflect));

		return lstAttBeforeReflect;
	}

	public static interface Require {
		// EditStateOfDailyPerformanceRepository
		List<EditStateOfDailyPerformance> findByKey(String employeeId, GeneralDate ymd);

		// EditStateOfDailyPerformanceRepository
		void addAndUpdate(List<EditStateOfDailyPerformance> editStates);

		void insertAppReflectHist(ApplicationReflectHistory hist);
	}

}
