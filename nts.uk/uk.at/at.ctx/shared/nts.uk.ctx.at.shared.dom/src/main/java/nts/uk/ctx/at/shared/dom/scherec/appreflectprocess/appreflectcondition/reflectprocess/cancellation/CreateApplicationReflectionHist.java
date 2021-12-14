package nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;

/**
 * @author thanh_nx
 *
 *         申請反映履歴を作成する
 */
public class CreateApplicationReflectionHist {

	public static void create(Require require, String appId, ScheduleRecordClassifi classifi,
			DailyRecordOfApplication dailyRecordApp, IntegrationOfDaily domainBefore, GeneralDateTime reflectTime) {

		// 申請反映前のデータを取得
		addDataBeforeAppReflect(require, dailyRecordApp.getAttendanceBeforeReflect(), domainBefore);

		// 申請反映履歴を追加する
		require.insertAppReflectHist(new ApplicationReflectHistory(domainBefore.getEmployeeId(), domainBefore.getYmd(), appId,
				reflectTime, classifi, false, dailyRecordApp.getAttendanceBeforeReflect()));
	}

	public static interface Require {

		public void insertAppReflectHist(ApplicationReflectHistory hist);
		
		public DailyRecordToAttendanceItemConverter createDailyConverter();
	}

	/**
	 * @author thanh_nx
	 *
	 *         申請反映前のデータを取得
	 */
	private static void addDataBeforeAppReflect(Require require, List<AttendanceBeforeApplicationReflect> attendanceBeforeReflect,
			IntegrationOfDaily domainBefore) {

		DailyRecordToAttendanceItemConverter converter = require.createDailyConverter();
		converter.setData(domainBefore);
		Map<Integer, ItemValue> itemValue = converter
				.convert(attendanceBeforeReflect.stream().map(x -> x.getAttendanceId()).collect(Collectors.toList()))
				.stream().collect(Collectors.toMap(x -> x.getItemId(), x -> x, (x, y) -> x));

		// 反映前の勤怠（List）でループする
		for (AttendanceBeforeApplicationReflect data : attendanceBeforeReflect) {

			// [反映前の日別勤怠(Work）]から、該当する値を取得
			String value = itemValue.containsKey(data.getAttendanceId())
					? itemValue.get(data.getAttendanceId()).getValue()
					: "";

			// 取得した値を該当する[反映前の勤怠（List）]に追加する
			data.setValue(Optional.ofNullable(value));

			// [反映前の日別勤怠(Work）]から、該当する編集状態を取得
			EditStateOfDailyAttd state = domainBefore.getEditState().stream()
					.filter(x -> x.getAttendanceItemId() == data.getAttendanceId()).findFirst().orElse(null);
			data.setEditState(Optional.ofNullable(state));

		}
	}
}
