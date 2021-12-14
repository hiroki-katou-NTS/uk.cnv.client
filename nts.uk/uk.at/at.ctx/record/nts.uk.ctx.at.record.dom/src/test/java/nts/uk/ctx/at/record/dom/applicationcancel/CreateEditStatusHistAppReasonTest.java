package nts.uk.ctx.at.record.dom.applicationcancel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.cancellation.ApplicationReflectHistory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

@RunWith(JMockit.class)
public class CreateEditStatusHistAppReasonTest {

	@Injectable
	private CreateEditStatusHistAppReason.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 申請理由の編集状態と履歴を作成する
	 * 
	 * 準備するデータ
	 * 
	 * → 日別実績から、該当する編集状態がある
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testPresentEdit() {

		new Expectations() {
			{
				require.findByKey(anyString, (GeneralDate) any);
				result = new EditStateOfDailyPerformance("2", GeneralDate.today(),
						new EditStateOfDailyAttd(869, EditStateSetting.HAND_CORRECTION_MYSELF));
			}
		};

		Map<Integer, String> mapValue = new HashMap<Integer, String>();
		mapValue.put(869, "600");

		new Expectations() {
			{
				require.addAndUpdate((List<EditStateOfDailyPerformance>) any);
				times = 1;

				require.insertAppReflectHist((ApplicationReflectHistory) any);
				times = 1;
			}
		};

		val actualResult = CreateEditStatusHistAppReason.process(require, "2", // 社員ID
				GeneralDate.today(), // 対象日
				"appId", // 申請ID
				ScheduleRecordClassifi.SCHEDULE, // 予定実績区分
				mapValue, GeneralDateTime.FAKED_NOW);// Map<勤怠項目ID,値>
		assertThat(actualResult).extracting(x -> x.getEditState().get().getEditStateSetting())
				.containsExactly(EditStateSetting.HAND_CORRECTION_MYSELF);

	}

	/*
	 * テストしたい内容
	 * 
	 * → 申請理由の編集状態と履歴を作成する
	 * 
	 * 準備するデータ
	 * 
	 * → 日別実績から、該当する編集状態がない
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNotPresentEdit() {

		new Expectations() {
			{
				require.findByKey(anyString, (GeneralDate) any);
				result = Arrays.asList();
			}
		};

		Map<Integer, String> mapValue = new HashMap<Integer, String>();
		mapValue.put(869, "600");

		new Expectations() {
			{
				require.addAndUpdate((List<EditStateOfDailyPerformance>) any);
				times = 1;

				require.insertAppReflectHist((ApplicationReflectHistory) any);
				times = 1;
			}
		};

		val actualResult = CreateEditStatusHistAppReason.process(require, "2", // 社員ID
				GeneralDate.today(), // 対象日
				"appId", // 申請ID
				ScheduleRecordClassifi.SCHEDULE, // 予定実績区分
				mapValue, GeneralDateTime.FAKED_NOW);// Map<勤怠項目ID,値>
		assertThat(actualResult).extracting(x -> x.getEditState()).containsExactly(Optional.empty());
	}

}
