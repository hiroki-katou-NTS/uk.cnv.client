package nts.uk.ctx.at.record.dom.applicationcancel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.applicationcancel.ReflectTimeStamp.ReflectTimeStampResult;
import nts.uk.ctx.at.record.dom.applicationcancel.ReflectTimeStamp.Require;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.AppRecordImageShare;
import nts.uk.ctx.at.shared.dom.scherec.application.stamp.EngraveShareAtr;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.OutputTimeReflectForWorkinfo;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.AttendanceClock;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class TimeStampApplicationNRModeTest {

	@Injectable
	private TimeStampApplicationNRMode.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 申請反映状態にする
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻区分 = 外出
	 * 
	 */
	@Test
	public void testGooutComeback(@Mocked ReflectGoOutReturn reflectGoOutReturn) {

		AppRecordImageShare appNr = ReflectApplicationHelper.createAppRecord(EngraveShareAtr.GO_OUT);
		DailyRecordOfApplication dailyRecordApp = ReflectApplicationHelper
				.createDailyRecord(ScheduleRecordClassifi.RECORD, 1);
		Optional<Stamp> stamp = Optional.of(createStamp());

		new Expectations() {
			{
				ReflectGoOutReturn.process(require, anyString, dailyRecordApp, (OutputTimeReflectForWorkinfo) any,
						(AttendanceClock) any, (EngraveShareAtr) any, stamp, (ChangeDailyAttendance) any);
				result = new ReflectTimeStampResult(dailyRecordApp.getDomain(), true, new WorkNo(1));
			}
		};

		val actualResult = TimeStampApplicationNRMode.process(require, "", GeneralDate.ymd(2020, 01, 02), appNr,
				dailyRecordApp, stamp, new ChangeDailyAttendance(false, false, false, false, ScheduleRecordClassifi.RECORD, false));

		assertThat(actualResult).isEqualTo(Arrays.asList(91, 86));

	}

	/*
	 * テストしたい内容
	 * 
	 * → 申請反映状態にしない
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻区分 = 外出
	 *      
	 * → 反映フラグチェック = false;
	 */
	@Test
	public void testNoGooutComeback(@Mocked ReflectGoOutReturn reflectGoOutReturn) {

		AppRecordImageShare appNr = ReflectApplicationHelper.createAppRecord(EngraveShareAtr.GO_OUT);
		DailyRecordOfApplication dailyRecordApp = ReflectApplicationHelper
				.createDailyRecord(ScheduleRecordClassifi.RECORD, 1);
		Optional<Stamp> stamp = Optional.of(createStamp());

		new Expectations() {
			{
				ReflectGoOutReturn.process(require, anyString, dailyRecordApp, (OutputTimeReflectForWorkinfo) any,
						(AttendanceClock) any, (EngraveShareAtr) any, stamp, (ChangeDailyAttendance) any);
				result = new ReflectTimeStampResult(dailyRecordApp.getDomain(), false, new WorkNo(1));
			}
		};

		val actualResult = TimeStampApplicationNRMode.process(require, "", GeneralDate.ymd(2020, 01, 02), appNr,
				dailyRecordApp, stamp, new ChangeDailyAttendance(false, false, false, false, ScheduleRecordClassifi.RECORD, false));

		assertThat(actualResult).isEmpty();

	}
	
	/*
	 * テストしたい内容
	 * 
	 * →申請反映状態にする
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻区分 = 出勤
	 * 
	 */
	@Test
	public void testAtt() {

		AppRecordImageShare appNr = ReflectApplicationHelper.createAppRecord(EngraveShareAtr.ATTENDANCE);
		DailyRecordOfApplication dailyRecordApp = ReflectApplicationHelper
				.createDailyRecord(ScheduleRecordClassifi.RECORD, 1);
		Optional<Stamp> stamp = Optional.of(createStamp());

		new Expectations() {
			{

				new MockUp<ReflectTimeStamp>() {
					@Mock
					public ReflectTimeStampResult reflect(Require require, DailyRecordOfApplication dailyRecordApp,
							OutputTimeReflectForWorkinfo timeReflectWork, AttendanceClock attendanceTime,
							TimeWithDayAttr attr, EngraveShareAtr appStampComAtr, Optional<Stamp> stamp) {
						return new ReflectTimeStampResult(dailyRecordApp.getDomain(), true, new WorkNo(1));
					}
				};
			}
		};

		val actualResult = TimeStampApplicationNRMode.process(require, "",  GeneralDate.ymd(2020, 01, 02), appNr,
				dailyRecordApp, stamp, new ChangeDailyAttendance(false, false, false, false, ScheduleRecordClassifi.RECORD, false));

		assertThat(actualResult).isEqualTo(Arrays.asList(31));

	}

	/*
	 * テストしたい内容
	 * 
	 * →申請反映状態にしない
	 * 
	 * 準備するデータ
	 * 
	 * → 打刻区分 = 出勤
	 * 
	 */
	@Test
	public void testNoAtt() {

		AppRecordImageShare appNr = ReflectApplicationHelper.createAppRecord(EngraveShareAtr.ATTENDANCE);
		DailyRecordOfApplication dailyRecordApp = ReflectApplicationHelper
				.createDailyRecord(ScheduleRecordClassifi.RECORD, 1);
		Optional<Stamp> stamp = Optional.of(createStamp());

		new Expectations() {
			{

				new MockUp<ReflectTimeStamp>() {
					@Mock
					public ReflectTimeStampResult reflect(Require require, DailyRecordOfApplication dailyRecordApp,
							OutputTimeReflectForWorkinfo timeReflectWork, AttendanceClock attendanceTime,
							TimeWithDayAttr attr, EngraveShareAtr appStampComAtr, Optional<Stamp> stamp) {
						return new ReflectTimeStampResult(dailyRecordApp.getDomain(), false, new WorkNo(1));
					}
				};
			}
		};

		val actualResult = TimeStampApplicationNRMode.process(require, "", GeneralDate.ymd(2020, 01, 02), appNr,
				dailyRecordApp, stamp, new ChangeDailyAttendance(false, false, false, false, ScheduleRecordClassifi.RECORD, false));

		assertThat(actualResult).isEmpty();

	}
	
	private Stamp createStamp() {
		return new Stamp(new ContractCode("1"), // 契約コード
				new StampNumber("1"), // 打刻カード
				GeneralDateTime.ymdhms(2020, 01, 01, 10, 0, 0), // 打刻日時
				new Relieve(AuthcMethod.ID_AUTHC, // 打刻する方法. 認証方法
						StampMeans.NAME_SELECTION), /// 打刻する方法. 打刻手段
				new StampType(false, GoingOutReason.valueOf(1), // 外出理由
						SetPreClockArt.NONE, // 所定時刻セット区分
						ChangeClockArt.GOING_TO_WORK, // 時刻変更区分
						ChangeCalArt.NONE), // 計算区分変更対象
				new RefectActualResult( null, null, null), Optional.empty(),
				"DUMMY");
	}
}
