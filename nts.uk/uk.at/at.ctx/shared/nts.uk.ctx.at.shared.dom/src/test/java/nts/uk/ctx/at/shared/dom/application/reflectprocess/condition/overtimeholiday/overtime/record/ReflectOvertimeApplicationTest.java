package nts.uk.ctx.at.shared.dom.application.reflectprocess.condition.overtimeholiday.overtime.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.shared.dom.application.common.PrePostAtrShare;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.DailyRecordOfApplication;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.application.reflectprocess.common.ReflectApplicationHelper;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.AfterOtWorkAppReflect;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.BeforeOtWorkAppReflect;
import nts.uk.ctx.at.shared.dom.workcheduleworkrecord.appreflectprocess.appreflectcondition.overtimeholidaywork.otworkapply.OtWorkAppReflect;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class ReflectOvertimeApplicationTest {

	@Injectable
	private ReflectOvertimeApplication.Require require;

	private String cid = "1";

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →実績の勤務情報へ反映 ①[実績の勤務情報へ反映する] = しない
	 * 
	 * →反映しない ②[実績の勤務情報へ反映する] = する →反映する
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * →[実績の勤務情報へ反映する] = しないorする
	 * 
	 */

	@Test
	public void test1() {

		val overTimeApp = ReflectApplicationHelper.createOverTimeAppNone();// 勤務情報 = ("003", "003")
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeav(ScheduleRecordClassifi.RECORD,
				1, true);// 勤務情報 = ("001", "001")
		// ①[実績の勤務情報へ反映する] = しない →反映しない
		OtWorkAppReflect createOtWorkRfl = createOtWorkRfl(NotUseAtr.NOT_USE);
		ReflectOvertimeApplication.process(require, cid, overTimeApp, dailyApp, createOtWorkRfl);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo("001");// 就業時間帯コード
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("001");// 勤務種類コード

		// ②[実績の勤務情報へ反映する] = する →反映する
		createOtWorkRfl = createOtWorkRfl(NotUseAtr.USE);
		ReflectOvertimeApplication.process(require, cid, overTimeApp, dailyApp, createOtWorkRfl);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo("003");// 就業時間帯コード
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("003");// 勤務種類コード

	}

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →事前残業申請の反映ができる
	 * 
	 * 準備するデータ
	 * 
	 * →[実績の勤務情報へ反映する] = しない
	 * 
	 * →事前事後区分＝事前
	 * 
	 * →残業申請.事前.勤務情報、出退勤を反映する＝する
	 * 
	 * →残業申請.事後.出退勤を反映する＝する
	 * 
	 */

	@Test
	public void test2() {

		val overTimeApp = ReflectApplicationHelper.createOverTimeAppBeforeAfter(PrePostAtrShare.PREDICT);// 勤務情報 =
																											// ("003",
																											// "003")
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeav(ScheduleRecordClassifi.RECORD,
				1, true);// 勤務情報 = ("001", "001")
		// [実績の勤務情報へ反映する] = しない
		OtWorkAppReflect createOtWorkRfl = otWorkRflBeforeAfter(NotUseAtr.USE);// 残業申請.事前.勤務情報、出退勤を反映する
		ReflectOvertimeApplication.process(require, cid, overTimeApp, dailyApp, createOtWorkRfl);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo("003");// 就業時間帯コード
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("003");// 勤務種類コード

	}

	/*
	 * テストしたい内容
	 * 
	 * 
	 * →事後残業申請の反映ができる
	 * 
	 * 準備するデータ
	 * 
	 * →[実績の勤務情報へ反映する] = しない
	 * 
	 * →事前事後区分＝事後
	 * 
	 * →残業申請.事前.勤務情報、出退勤を反映する＝する
	 * 
	 * →残業申請.事後.出退勤を反映する＝する
	 * 
	 */

	@Test
	public void test3() {

		val overTimeApp = ReflectApplicationHelper.createOverTimeAppBeforeAfter(PrePostAtrShare.POSTERIOR);// 勤務情報 =
																											// ("003",
																											// "003")
		DailyRecordOfApplication dailyApp = ReflectApplicationHelper.createRCWithTimeLeav(ScheduleRecordClassifi.RECORD,
				1, true);// 勤務情報 = ("001", "001")
		// [実績の勤務情報へ反映する] = しない
		OtWorkAppReflect createOtWorkRfl = otWorkRflBeforeAfter(NotUseAtr.USE);// 残業申請.事前.勤務情報、出退勤を反映する
		ReflectOvertimeApplication.process(require, cid, overTimeApp, dailyApp, createOtWorkRfl);
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTimeCode().v()).isEqualTo("001");// 就業時間帯コード
		assertThat(dailyApp.getWorkInformation().getRecordInfo().getWorkTypeCode().v()).isEqualTo("001");// 勤務種類コード

	}

	private OtWorkAppReflect createOtWorkRfl(NotUseAtr reflectWork) {
		return new OtWorkAppReflect(null, null, reflectWork);
	}

	private OtWorkAppReflect otWorkRflBeforeAfter(NotUseAtr reflectWork) {
		return new OtWorkAppReflect(BeforeOtWorkAppReflect.create(reflectWork.value, 0, 0),
				AfterOtWorkAppReflect.create(1, 1, 1, 1), NotUseAtr.NOT_USE);
	}
}
