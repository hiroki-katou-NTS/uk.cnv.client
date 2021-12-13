package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.reflectprocess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.testing.assertion.NtsAssert.Invoke;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.reasonappdaily.ApplicationReasonInfo;
import nts.uk.ctx.at.request.dom.reasonappdaily.ApplicationTypeReason;
import nts.uk.ctx.at.request.dom.reasonappdaily.ReasonApplicationDailyResult;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;

@RunWith(JMockit.class)
public class ReflectApplicationReasonTest {

	@Injectable
	private ReflectApplicationReason.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 申請理由の反映ができない
	 * 
	 * 準備するデータ
	 * 
	 * → 理由の反映がない
	 * 
	 */
	@Test
	public void test() {

		Application application = ReflectApplicationHelper.createApp(PrePostAtr.POSTERIOR);

		val actualResult = ReflectApplicationReason.reflectReason(require, application, GeneralDate.today(), GeneralDateTime.FAKED_NOW, "1", ReflectedState.WAITREFLECTION);

		assertThat(actualResult).isEmpty();
	}

	/*
	 * テストしたい内容
	 * 
	 * → 申請理由の反映ができる（新規追加）
	 * 
	 * 準備するデータ
	 * 
	 * → 理由の反映がある
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAdd() {

		Application application = ReflectApplicationHelper.createAppWithReason(ApplicationType.ABSENCE_APPLICATION, 1,
				"AAAA");

		new Expectations() {
			{
				require.findReasonAppDaily(anyString, GeneralDate.today(), (PrePostAtr) any, (ApplicationType) any,
						(Optional<OvertimeAppAtr>) any);
				result = Arrays.asList();

				// addメソッドを呼び出すかどうかを確認します
				new MockUp<ReflectApplicationReason>() {
					@Mock(invocations = 1)
					public ReasonApplicationDailyResult createReason(Application application, GeneralDate date) {
						return null;
					}
				};
			}
		};

		val actualResult = ReflectApplicationReason.reflectReason(require, application, GeneralDate.today(), GeneralDateTime.FAKED_NOW, "1", ReflectedState.WAITREFLECTION);
		NtsAssert.atomTask(() -> actualResult.get(), any -> require.addUpdateReason(any.get()),
				any -> require.processCreateHist(any.get(), any.get(), any.get(), any.get(), any.get(), any.get(), any.get(), any.get()));

	}

	/*
	 * テストしたい内容
	 * 
	 * → 申請理由の反映ができる（更新）
	 * 
	 * 準備するデータ
	 * 
	 * → 理由の反映がある
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdate() {

		Application application = ReflectApplicationHelper.createAppWithReason(ApplicationType.ABSENCE_APPLICATION, 1,
				"AAAA");

		new Expectations() {
			{
				require.findReasonAppDaily(anyString, GeneralDate.today(), (PrePostAtr) any, (ApplicationType) any,
						(Optional<OvertimeAppAtr>) any);
				result = Arrays.asList(new ReasonApplicationDailyResult("1", GeneralDate.today(),
						new ApplicationTypeReason(ApplicationType.ABSENCE_APPLICATION, Optional.empty()),
						PrePostAtr.POSTERIOR,
						new ApplicationReasonInfo(new AppStandardReasonCode(1), new AppReason("AAAA"))));
				// addメソッドを呼び出すかどうかを確認します
				new MockUp<ReflectApplicationReason>() {
					@Mock(invocations = 0)
					public ReasonApplicationDailyResult createReason(Application application, GeneralDate date) {
						return null;
					}
				};

			}
		};

		val actualResult = ReflectApplicationReason.reflectReason(require, application, GeneralDate.today(), GeneralDateTime.FAKED_NOW, "1", ReflectedState.WAITREFLECTION);
		NtsAssert.atomTask(() -> actualResult.get(), any -> require.addUpdateReason(any.get()),
				any -> require.processCreateHist(any.get(), any.get(), any.get(), any.get(), any.get(), any.get(), any.get(), any.get()));

	}

	// 反映前の申請理由情報と申請から値と勤怠項目IDを作成します
	@Test
	public void testCreateId() {

		// 申請.申請理由<>null の場合 → 値 ＝ 反映前の申請理由情報.申請理由
		// 事前事後区分＝事前の場合 → 勤怠項目ID ← 下記の勤怠項目（869~881）の該当する勤怠項目ID
		List<ReasonApplicationDailyResult> apps = Arrays
				.asList(create(GeneralDate.ymd(2020, 05, 01), ApplicationType.ABSENCE_APPLICATION, // 休暇申請
						PrePostAtr.PREDICT, // 事後
						11, "AA"));

		Application application = ReflectApplicationHelper.createAppWithReason(ApplicationType.ABSENCE_APPLICATION,
				null, "1111");
		Map<Integer, String> actualResult = Invoke.staticMethod(ReflectApplicationReason.class, "createMap", apps,
				application);
		// 勤怠項目ID: 873, 値 = "AA"
		assertTrue(actualResult.keySet().contains(873));
		assertTrue(actualResult.values().contains("AA"));

		// 申請.申請理由<>null の場合 → 値 ＝ 反映前の申請理由情報.申請理由
		// 事前事後区分＝事後の場合 → 勤怠項目ID ← 下記の勤怠項目（895~907）の該当する勤怠項目ID
		apps = Arrays.asList(create(GeneralDate.ymd(2020, 05, 01), ApplicationType.ABSENCE_APPLICATION, // 休暇申請
				PrePostAtr.POSTERIOR, // 事後
				11, "AA"));

		application = ReflectApplicationHelper.createAppWithReason(ApplicationType.ABSENCE_APPLICATION, null, "1111");
		actualResult = Invoke.staticMethod(ReflectApplicationReason.class, "createMap", apps, application);
		// 勤怠項目ID: 899, 値 = "AA"
		assertTrue(actualResult.keySet().contains(899));
		assertTrue(actualResult.values().contains("AA"));

		// 申請..定型理由コード<>null の場合 → 値 ＝ 反映前の申請理由情報.定型
		// 事前事後区分＝事前の場合 → 勤怠項目ID ← 下記の勤怠項目（882~894）の該当する勤怠項目ID
		apps = Arrays.asList(create(GeneralDate.ymd(2020, 05, 01), ApplicationType.OVER_TIME_APPLICATION, // 残業申請
				1, // 残業申請区分=通常残業
				PrePostAtr.PREDICT, // 事前
				10, "AA"));

		application = ReflectApplicationHelper.createAppWithReason(ApplicationType.OVER_TIME_APPLICATION, 1, null);
		actualResult = Invoke.staticMethod(ReflectApplicationReason.class, "createMap", apps, application);
		// 勤怠項目ID: 899, 値 = 10
		assertTrue(actualResult.keySet().contains(883));
		assertTrue(actualResult.values().contains("10"));

		// 申請..定型理由コード<>null の場合 → 値 ＝ 反映前の申請理由情報.定型
		// 事前事後区分＝事後の場合 → 勤怠項目ID ← 下記の勤怠項目（908~920）の該当する勤怠項目ID
		apps = Arrays.asList(create(GeneralDate.ymd(2020, 05, 01), ApplicationType.OVER_TIME_APPLICATION, // 残業申請
				1, // 残業申請区分=通常残業
				PrePostAtr.POSTERIOR, // 事後
				10, "AA"));

		application = ReflectApplicationHelper.createAppWithReason(ApplicationType.OVER_TIME_APPLICATION, 1, null);
		actualResult = Invoke.staticMethod(ReflectApplicationReason.class, "createMap", apps, application);
		// 勤怠項目ID: 909, 値 = 10
		assertTrue(actualResult.keySet().contains(909));
		assertTrue(actualResult.values().contains("10"));

	}

	private ReasonApplicationDailyResult create(GeneralDate date, ApplicationType appType, PrePostAtr prePostAtr,
			Integer code, String appReason) {
		return create(date, appType, null, prePostAtr, code, appReason);
	}

	private ReasonApplicationDailyResult create(GeneralDate date, ApplicationType appType, Integer overType,
			PrePostAtr prePostAtr, Integer code, String appReason) {
		return new ReasonApplicationDailyResult("1", date,
				new ApplicationTypeReason(appType,
						Optional.ofNullable(
								overType == null ? null : EnumAdaptor.valueOf(overType, OvertimeAppAtr.class))),
				prePostAtr, new ApplicationReasonInfo(new AppStandardReasonCode(code), new AppReason(appReason)));
	}
}
