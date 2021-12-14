package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.ReflectAttendanceLeav;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;

@RunWith(JMockit.class)
public class ReflectAttendanceLeavTest {
	
	@Injectable
	private ReflectAttendanceLeav.Require require;

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の出退勤.出勤」を更新する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の出退勤に勤務NOを存在する
	 * 
	 * 開始終了区分が開始
	 */

	@Test
	public void testUpdateStart() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeav(ScheduleRecordClassifi.SCHEDULE, 1);//打刻NO= 1
		List<Integer> actualResult = ReflectAttendanceLeav.reflect(require, "", dailyApp,
				ReflectApplicationHelper.createlstTimeStamp(StartEndClassificationShare.START, //開始終了区分
						540));//時刻

		assertThat(actualResult).isEqualTo(Arrays.asList(30, 31));

		// compare 時刻
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getAttendanceStamp().get()
				.getStamp().get().getTimeDay().getTimeWithDay().get().v()).isEqualTo(540);

		// compare 時刻変更手段
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getAttendanceStamp().get()
				.getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())
						.isEqualTo(TimeChangeMeans.APPLICATION);

		// compare 場所コード
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getAttendanceStamp().get()
				.getStamp().get().getLocationCode().get().v()).isEqualTo("0001");

	}

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の出退勤.出勤」を作成する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の出退勤に勤務NOを存在しない
	 * 
	 * 開始終了区分が開始
	 */
	@Test
	public void testCreateStart() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeav(ScheduleRecordClassifi.SCHEDULE, 
						1);//打刻NO= 1
		
		List<Integer> actualResult = ReflectAttendanceLeav.reflect(require, "", dailyApp,
				ReflectApplicationHelper.createlstTimeStamp(StartEndClassificationShare.START, //開始終了区分
						540, //時刻
						2));// 打刻枠NO

		assertThat(actualResult).isEqualTo(Arrays.asList(40, 41));

		// compare 時刻
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getAttendanceStamp().get()
				.getStamp().get().getTimeDay().getTimeWithDay().get().v()).isEqualTo(540);

		// compare 時刻変更手段
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getAttendanceStamp().get()
				.getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())
						.isEqualTo(TimeChangeMeans.APPLICATION);

		// compare 場所コード
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getAttendanceStamp().get()
				.getStamp().get().getLocationCode().get().v()).isEqualTo("0001");
	}

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の出退勤.退勤」を更新する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の出退勤に勤務NOを存在する
	 * 
	 * 開始終了区分が終了
	 */
	@Test
	public void testUpdateEnd() {
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeav(ScheduleRecordClassifi.SCHEDULE, 1);//打刻NO= 1
		
		List<Integer> actualResult = ReflectAttendanceLeav.reflect(require, "", dailyApp,
				ReflectApplicationHelper.createlstTimeStamp(StartEndClassificationShare.END, //開始終了区分
						540));//時刻

		assertThat(actualResult).isEqualTo(Arrays.asList(33, 34));

		// compare 時刻
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getLeaveStamp().get()
				.getStamp().get().getTimeDay().getTimeWithDay().get().v()).isEqualTo(540);

		// compare 時刻変更手段
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getLeaveStamp().get()
				.getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())
						.isEqualTo(TimeChangeMeans.APPLICATION);

		// compare 場所コード
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(1).get().getLeaveStamp().get()
				.getStamp().get().getLocationCode().get().v()).isEqualTo("0001");
	}

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の出退勤.退勤」をを作成する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の出退勤に勤務NOを存在しない
	 * 
	 * 開始終了区分が終了
	 */
	@Test
	public void testCreateEnd() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeav(ScheduleRecordClassifi.SCHEDULE, 
						1);//打刻NO= 1
		
		List<Integer> actualResult = ReflectAttendanceLeav.reflect(require, "", dailyApp,
				ReflectApplicationHelper.createlstTimeStamp(StartEndClassificationShare.END, //開始終了区分
						540, //時刻
						2));// 打刻枠NO

		assertThat(actualResult).isEqualTo(Arrays.asList(43, 44));

		// compare 時刻
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getLeaveStamp().get()
				.getStamp().get().getTimeDay().getTimeWithDay().get().v()).isEqualTo(540);

		// compare 時刻変更手段
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getLeaveStamp().get()
				.getStamp().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())
						.isEqualTo(TimeChangeMeans.APPLICATION);

		// compare 場所コード
		assertThat(dailyApp.getAttendanceLeave().get().getAttendanceLeavingWork(2).get().getLeaveStamp().get()
				.getStamp().get().getLocationCode().get().v()).isEqualTo("0001");
	}

}
