package nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.condition.overtimeholiday.holiday.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.CreateWorkMaxTimeZone;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubstituteTransferProcess;
import nts.uk.ctx.at.shared.dom.scherec.application.holidayworktime.AppHolidayWorkShare;
import nts.uk.ctx.at.shared.dom.scherec.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.BreakApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.OthersReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.hdworkapply.AfterHdWorkAppReflect;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author thanh_nx
 *
 *         事後休日出勤申請の反映
 */
@RunWith(JMockit.class)
public class AfterHdWorkAppReflectTest {

	@Injectable
	private AfterHdWorkAppReflect.Require require;

	/*
	 * テストしたい内容
	 * 
	 * →勤務情報の反映
	 * 
	 * →①日別勤怠と申請の勤務種類、就業時間帯が同じ：勤務情報を反映する
	 * 
	 * →②日別勤怠と申請の勤務種類、就業時間帯が同じじゃない：勤務情報を反映しない
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * 勤務情報データ
	 * 
	 */
	@Test
	public void test1() {

		AppHolidayWorkShare holidayApp = ReflectApplicationHelper.createAppHoliday("005", // 勤務種類コード
				"006" // 就業時間帯コード
		);
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);

		AfterHdWorkAppReflect after = new AfterHdWorkAppReflect(new OthersReflect(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE), // その他項目を反映する
				new BreakApplication(NotUseAtr.NOT_USE), // 休憩・外出を申請反映する
				NotUseAtr.NOT_USE);// 出退勤を反映する
		
		// ②日別勤怠と申請の勤務種類、就業時間帯が同じじゃない：勤務情報を反映しない
		after.process(require, "", holidayApp, dailyApp);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("005");
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo("006");

		// ①日別勤怠と申請の勤務種類、就業時間帯が同じ：勤務情報を反映する
		dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);
		String workTypeBefore = dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v();
		String workTimeBefore = dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v();
		holidayApp = ReflectApplicationHelper.createAppHoliday(workTypeBefore, // 勤務種類コード
				workTimeBefore // 就業時間帯コード
		);

		after.process(require, "", holidayApp, dailyApp);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo(workTypeBefore);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo(workTimeBefore);
		assertThat(dailyApp.getEditState()).extracting(x -> x.getAttendanceItemId()).doesNotContain(28, 29);
	}

	/*
	 * テストしたい内容
	 * 
	 * →直行直帰区分の反映
	 * 
	 * →①休日出勤申請.直行区分 = false、休日出勤申請.直帰区分 = false：直行直帰区分を反映しない
	 * 
	 * →②休日出勤申請.直行区分 = true、休日出勤申請.直帰区分 = true : 直行直帰区分を反映する
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * 直行直帰区分の設定
	 * 
	 */
	@Test
	public void test2() {
		// ①休日出勤申請.直行区分 = false、休日出勤申請.直帰区分 = false：直行直帰区分を反映しない
		AppHolidayWorkShare holidayApp = ReflectApplicationHelper.createAppHoliday(false, false);
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);

		AfterHdWorkAppReflect after = new AfterHdWorkAppReflect(new OthersReflect(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE), // その他項目を反映する
				new BreakApplication(NotUseAtr.NOT_USE), // 休憩・外出を申請反映する
				NotUseAtr.NOT_USE);// 出退勤を反映する
		after.process(require, "", holidayApp, dailyApp);
		assertThat(dailyApp.getWorkInformation().getGoStraightAtr()).isEqualTo(NotUseAttribute.Not_use);
		assertThat(dailyApp.getWorkInformation().getBackStraightAtr()).isEqualTo(NotUseAttribute.Not_use);

		// ②休日出勤申請.直行区分 = true、休日出勤申請.直帰区分 = true : 直行直帰区分を反映する
		holidayApp = ReflectApplicationHelper.createAppHoliday(true, true);
		dailyApp = ReflectApplicationHelper.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);
		after.process(require, "", holidayApp, dailyApp);
		assertThat(dailyApp.getWorkInformation().getGoStraightAtr()).isEqualTo(NotUseAttribute.Use);
		assertThat(dailyApp.getWorkInformation().getBackStraightAtr()).isEqualTo(NotUseAttribute.Use);

	}

	/*
	 * テストしたい内容
	 * 
	 * →出退勤の反映
	 * 
	 * →①[出退勤を反映する] = しない：出退勤を反映しない
	 * 
	 * →②[出退勤を反映する] = する : 出退勤を反映する
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * [出退勤を反映する]の設定
	 * 
	 */
	@Test
	public void test3() {
		// ①休日出勤申請.直行区分 = false、休日出勤申請.直帰区分 = false：直行直帰区分を反映しない
		AppHolidayWorkShare holidayApp = ReflectApplicationHelper.createAppHoliday(1, // No
				488, // 勤務時間帯-開始時刻
				1088 // 勤務時間帯-終了時刻
		);
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);
		int attBefore = dailyApp.getAttendanceLeave().get().getTimeLeavingWorks().get(0).getStampOfAttendance().get()
				.getTimeDay().getTimeWithDay().get().v();
		int leavBefore = dailyApp.getAttendanceLeave().get().getTimeLeavingWorks().get(0).getStampOfLeave().get()
				.getTimeDay().getTimeWithDay().get().v();

		AfterHdWorkAppReflect after = new AfterHdWorkAppReflect(new OthersReflect(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE), // その他項目を反映する
				new BreakApplication(NotUseAtr.NOT_USE), // 休憩・外出を申請反映する
				NotUseAtr.NOT_USE);// 出退勤を反映しない
		after.process(require, "", holidayApp, dailyApp);
		// ①[出退勤を反映する] = しない：出退勤を反映しない
		assertThat(dailyApp.getAttendanceLeave().get().getTimeLeavingWorks())
				.extracting(x -> x.getWorkNo().v(),
						x -> x.getStampOfAttendance().get().getTimeDay().getTimeWithDay().get().v(), // 勤務時間帯-開始時刻
						x -> x.getStampOfAttendance().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 時刻変更理由
						x -> x.getStampOfLeave().get().getTimeDay().getTimeWithDay().get().v(), // 勤務時間帯-終了時刻
						x -> x.getStampOfLeave().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())// 時刻変更理由
				.containsExactly(Tuple.tuple(1, attBefore, TimeChangeMeans.AUTOMATIC_SET, leavBefore,
						TimeChangeMeans.AUTOMATIC_SET));

		after = new AfterHdWorkAppReflect(new OthersReflect(NotUseAtr.NOT_USE, NotUseAtr.NOT_USE), // その他項目を反映する
				new BreakApplication(NotUseAtr.NOT_USE), // 休憩・外出を申請反映する
				NotUseAtr.USE);// 出退勤を反映する
		after.process(require, "", holidayApp, dailyApp);
		// ②[出退勤を反映する] = する : 出退勤を反映する
		assertThat(dailyApp.getAttendanceLeave().get().getTimeLeavingWorks())
				.extracting(x -> x.getWorkNo().v(),
						x -> x.getStampOfAttendance().get().getTimeDay().getTimeWithDay().get().v(), // 勤務時間帯-開始時刻
						x -> x.getStampOfAttendance().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans(), // 時刻変更理由
						x -> x.getStampOfLeave().get().getTimeDay().getTimeWithDay().get().v(), // 勤務時間帯-終了時刻
						x -> x.getStampOfLeave().get().getTimeDay().getReasonTimeChange().getTimeChangeMeans())// 時刻変更理由
				.containsExactly(Tuple.tuple(1, 488, TimeChangeMeans.APPLICATION, 1088, TimeChangeMeans.APPLICATION));
	}

	/*
	 * テストしたい内容
	 * 
	 * →休出時間の反映処理を呼ぶ
	 * 
	 * → [その他項目の反映]＝する：その他項目の反映処理を呼ぶ
	 * 
	 * →休憩・外出の申請反映処理を呼ぶ
	 * 
	 * 準備するデータ
	 * 
	 *
	 * 
	 */
	@Test
	public void test4(@Mocked CreateWorkMaxTimeZone createMaxTime, @Mocked SubstituteTransferProcess sub) {
		AppHolidayWorkShare holidayApp = ReflectApplicationHelper.createAppHolidayBreak(1, // No
				600, // 休憩時間帯-開始時刻
				660, // 休憩時間帯-終了時刻
				111, // 残業時間
				"ReasonName", // 乖離理由
				"ReasonCode");// 乖離理由コード
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper
				.createRCWithTimeLeavFull(ScheduleRecordClassifi.RECORD, 1);

		AfterHdWorkAppReflect after = new AfterHdWorkAppReflect(new OthersReflect(NotUseAtr.USE, NotUseAtr.USE), // その他項目を反映する
				new BreakApplication(NotUseAtr.USE), // 休憩・外出を申請反映する
				NotUseAtr.NOT_USE);// 出退勤を反映する

		after.process(require, "", holidayApp, dailyApp);

		// 休出時間の反映がある
		assertThat(
				dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime()
						.getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getOverTimeWorkFrameTime())
								.extracting(x -> x.getOverWorkFrameNo().v(), x -> x.getOverTimeWork().getTime().v())
								.containsExactly(Tuple.tuple(1, 111));// 日別勤怠の残業時間.残業枠時間.残業時間.時間

		// 休憩・外出の申請反映処理を呼ぶ
		assertThat(dailyApp.getBreakTime().getBreakTimeSheets())
				.extracting(x -> x.getBreakFrameNo().v(), x -> x.getStartTime().v(), // 休憩時間帯-開始時刻
						x -> x.getEndTime().v()) // 休憩時間帯-終了時刻
				.containsExactly(Tuple.tuple(1, 600, 660));

		// その他項目の反映処理を呼ぶ
		assertThat(dailyApp.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getDivTime()
				.getDivergenceTime()).extracting(x -> x.getDivReason().get().v(), x -> x.getDivResonCode().get().v())
						.contains(Tuple.tuple("ReasonName", "ReasonCode"));

	}
}
