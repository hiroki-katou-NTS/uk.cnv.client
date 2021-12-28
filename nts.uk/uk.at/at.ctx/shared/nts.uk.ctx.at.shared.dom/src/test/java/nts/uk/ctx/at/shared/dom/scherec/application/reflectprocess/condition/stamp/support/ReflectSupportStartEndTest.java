package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.stamp.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.StartEndClassificationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.TimeStampAppShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.algorithm.support.ReflectSupportStartEnd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;

@RunWith(JMockit.class)
public class ReflectSupportStartEndTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void close() throws Exception {
	}

	@Injectable
	private ReflectSupportStartEnd.Require require;

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の応援作業時間帯.開始.時刻と時刻変更手段と勤務場所」を更新する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の応援作業時間帯に応援勤務枠Noを存在する
	 * 
	 * 開始終了区分が開始
	 */

	@Test
	public void testUpdateStart() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// no = 1

		List<TimeStampAppShare> lstStampAppShare = ReflectApplicationHelper.createlstTimeStamp(
				StartEndClassificationShare.START, // 開始終了区分
				666, // 時刻
				1, // 応援勤務枠No
				"lo");// 勤務場所

		new Expectations() {
			{

			}
		};
		List<Integer> actualResult = ReflectSupportStartEnd.reflect(require, dailyApp, lstStampAppShare);

		assertThat(actualResult).isEqualTo(Arrays.asList(929, 921));

		assertThat(dailyApp.getOuenTimeSheet().get(0).getTimeSheet().getStart().get().getTimeWithDay().get().v())
				.isEqualTo(666);// 時刻

		assertThat(dailyApp.getOuenTimeSheet().get(0).getTimeSheet().getStart().get().getReasonTimeChange()
				.getTimeChangeMeans()).isEqualTo(TimeChangeMeans.APPLICATION);// 時刻変更手段

		assertThat(dailyApp.getOuenTimeSheet().get(0).getWorkContent().getWorkplace().getWorkLocationCD().get().v())
				.isEqualTo("lo");// 勤務場所コード
	}

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の応援作業時間帯.開始.時刻.と時刻変更手段と勤務場所と職場と勤務枠No」を作成する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の応援作業時間帯に応援勤務枠Noを存在する
	 * 
	 * 開始終了区分が開始
	 * 
	 * [社員の作業データ設定]がある
	 */
	@Test
	public void testCreateStart() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// no = 1

		List<TimeStampAppShare> lstStampAppShare = ReflectApplicationHelper.createlstTimeStampWpl(
				StartEndClassificationShare.START, // 開始終了区分
				666, // 時刻
				2, // 応援勤務枠No
				"lo", "wplId");// 勤務場所

		List<Integer> actualResult = ReflectSupportStartEnd.reflect(require, dailyApp, lstStampAppShare);

		assertThat(actualResult).isEqualTo(Arrays.asList(939, 931));

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkNo().v()).isEqualTo(2);// 応援勤務枠No

		assertThat(dailyApp.getOuenTimeSheet().get(1).getTimeSheet().getStart().get().getTimeWithDay().get().v())
				.isEqualTo(666);// 時刻

		assertThat(dailyApp.getOuenTimeSheet().get(1).getTimeSheet().getStart().get().getReasonTimeChange()
				.getTimeChangeMeans()).isEqualTo(TimeChangeMeans.APPLICATION);// 時刻変更手段

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkContent().getWorkplace().getWorkLocationCD().get().v())
				.isEqualTo("lo");// 勤務場所コード

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkContent().getWorkplace().getWorkplaceId().v())
				.isEqualTo("wplId");// 職場
	}
	
	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の応援作業時間帯.終了.時刻と時刻変更手段と勤務場所」を更新する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の応援作業時間帯に応援勤務枠Noを存在する
	 * 
	 * 開始終了区分が終了
	 */

	@Test
	public void testUpdateEnd() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// no = 1

		List<TimeStampAppShare> lstStampAppShare = ReflectApplicationHelper.createlstTimeStamp(
				StartEndClassificationShare.END, // 開始終了区分
				666, // 時刻
				1, // 応援勤務枠No
				"lo");// 勤務場所

		new Expectations() {
			{

			}
		};
		List<Integer> actualResult = ReflectSupportStartEnd.reflect(require, dailyApp, lstStampAppShare);

		assertThat(actualResult).isEqualTo(Arrays.asList(930, 921));

		assertThat(dailyApp.getOuenTimeSheet().get(0).getTimeSheet().getEnd().get().getTimeWithDay().get().v())
				.isEqualTo(666);// 時刻

		assertThat(dailyApp.getOuenTimeSheet().get(0).getTimeSheet().getEnd().get().getReasonTimeChange()
				.getTimeChangeMeans()).isEqualTo(TimeChangeMeans.APPLICATION);// 時刻変更手段

		assertThat(dailyApp.getOuenTimeSheet().get(0).getWorkContent().getWorkplace().getWorkLocationCD().get().v())
				.isEqualTo("lo");// 勤務場所コード
	}

	/*
	 * テストしたい内容
	 * 
	 * 申請から「日別勤怠の応援作業時間帯.終了.時刻.と時刻変更手段と勤務場所と職場と勤務枠No」を作成する。
	 * 
	 * 準備するデータ
	 * 
	 * 日別勤怠の応援作業時間帯に応援勤務枠Noを存在する
	 * 
	 * 開始終了区分が終了
	 * 
	 * [社員の作業データ設定]がある
	 */
	@Test
	public void testCreateEnd() {

		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD,
				1);// no = 1

		List<TimeStampAppShare> lstStampAppShare = ReflectApplicationHelper.createlstTimeStampWpl(
				StartEndClassificationShare.END, // 開始終了区分
				666, // 時刻
				2, // 応援勤務枠No
				"lo", "wplId");// 勤務場所

		List<Integer> actualResult = ReflectSupportStartEnd.reflect(require, dailyApp, lstStampAppShare);

		assertThat(actualResult).isEqualTo(Arrays.asList(940, 931));

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkNo().v()).isEqualTo(2);// 応援勤務枠No

		assertThat(dailyApp.getOuenTimeSheet().get(1).getTimeSheet().getEnd().get().getTimeWithDay().get().v())
				.isEqualTo(666);// 時刻

		assertThat(dailyApp.getOuenTimeSheet().get(1).getTimeSheet().getEnd().get().getReasonTimeChange()
				.getTimeChangeMeans()).isEqualTo(TimeChangeMeans.APPLICATION);// 時刻変更手段

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkContent().getWorkplace().getWorkLocationCD().get().v())
				.isEqualTo("lo");// 勤務場所コード

		assertThat(dailyApp.getOuenTimeSheet().get(1).getWorkContent().getWorkplace().getWorkplaceId().v())
				.isEqualTo("wplId");// 職場
	}
}
