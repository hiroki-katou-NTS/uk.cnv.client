package nts.uk.ctx.at.request.dom.cancelapplication.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.applicationreflect.object.PreApplicationWorkScheReflectAttr;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class SCApplicationCancellationProcessTest {

	@Injectable
	private SCApplicationCancellationProcess.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → 反映状態が取消済
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事後
	 */
	@Test
	public void test1() {

		val applicationCom = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.POSTERIOR, // 事前事後区分 = 事後
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCom);

		SCCancelProcessOneDayOutput actualResult = SCApplicationCancellationProcess.processSchedule(require, "",
				application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.REFLECTED), NotUseAtr.NOT_USE);
		assertThat(actualResult.getStatusWorkSchedule().getReflectStatus()).isEqualTo(ReflectedState.CANCELED);
	}

	/*
	 * テストしたい内容
	 * 
	 * → 反映状態のまま
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事前
	 * 
	 * →反映状態!=反映済み
	 */
	@Test
	public void test2() {

		val applicationCom = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT, // 事前事後区分 = 事前
				GeneralDate.ymd(2021, 4, 20), ReflectedState.NOTREFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCom);

		SCCancelProcessOneDayOutput actualResult = SCApplicationCancellationProcess.processSchedule(require, "",
				application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.NOTREFLECTED), // 反映状態=未反映
				NotUseAtr.NOT_USE);

		assertThat(actualResult.getStatusWorkSchedule().getReflectStatus()).isEqualTo(ReflectedState.NOTREFLECTED);
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定の取消の処理を呼ぶ
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事前
	 * 
	 * →反映状態=反映済み
	 * 
	 * →DB登録するか区分＝しない
	 */
	@Test
	public void test3() {

		Application applicationCommon = ReflectApplicationHelper.createAppWithDate(
				ApplicationType.BUSINESS_TRIP_APPLICATION, PrePostAtr.PREDICT, // 事前事後区分 = 事前
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCommon);

		SCApplicationCancellationProcess.processSchedule(require, "", application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.REFLECTED), // 反映状態=反映済み
				NotUseAtr.NOT_USE);

		new Verifications() {
			{
				require.process((ApplicationShare) any, (GeneralDate) any, (ReflectStatusResult) any,
						(NotUseAtr) any);
				times = 1;
			}
		};
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定の取消の処理を呼ぶ
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事前
	 * 
	 * →反映状態=反映済み
	 * 
	 * →DB登録するか区分＝する
	 * 
	 * →申請反映実行条件.[勤務予定が確定状態でも反映する]= する
	 */
	@Test
	public void test4() {

		val applicationCommon = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT, // 事前事後区分 = 事前
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCommon);

		new Expectations() {
			{
				require.findAppReflectExecCond(anyString);
				result = Optional.of(
						new AppReflectExecutionCondition("", PreApplicationWorkScheReflectAttr.REFLECT, NotUseAtr.USE, // 務予定が確定状態でも反映する
								NotUseAtr.NOT_USE));
			}
		};
		SCApplicationCancellationProcess.processSchedule(require, "", application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.REFLECTED), // 反映状態=未反映
				NotUseAtr.USE);

		new Verifications() {
			{
				require.process((ApplicationShare) any, (GeneralDate) any, (ReflectStatusResult) any,
						(NotUseAtr) any);
				times = 1;
			}
		};
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定の取消の処理を呼ぶ
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事前
	 * 
	 * →反映状態=反映済み
	 * 
	 * →DB登録するか区分＝する
	 * 
	 * →申請反映実行条件.[勤務予定が確定状態でも反映する]= しない
	 * 
	 * →事前申請の処理ができる
	 * 
	 */
	@Test
	public void test5(@Mocked PreCheckProcessWorkSchedule pre) {

		val applicationCommon = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT, // 事前事後区分 = 事前
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCommon);

		new Expectations() {
			{
				require.findAppReflectExecCond(anyString);
				result = Optional.of(new AppReflectExecutionCondition("", PreApplicationWorkScheReflectAttr.REFLECT,
						NotUseAtr.NOT_USE, // 務予定が確定状態でも反映する
						NotUseAtr.NOT_USE));
				PreCheckProcessWorkSchedule.preCheck(require, "", (Application) any, anyInt, anyBoolean,
						(ReflectStatusResult) any, (GeneralDate) any);
				result = new PreCheckProcessResult(NotUseAtr.USE, null);// 前申請の処理ができる

			}
		};
		SCApplicationCancellationProcess.processSchedule(require, "", application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.REFLECTED), // 反映状態=未反映
				NotUseAtr.USE);

		new Verifications() {
			{
				require.process((ApplicationShare) any, (GeneralDate) any, (ReflectStatusResult) any,
						(NotUseAtr) any);
				times = 1;
			}
		};
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定の取消の処理を呼ばない
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事前
	 * 
	 * →反映状態=反映済み
	 * 
	 * →DB登録するか区分＝する
	 * 
	 * →申請反映実行条件.[勤務予定が確定状態でも反映する]= しない
	 * 
	 * →事前申請の処理ができない
	 * 
	 */
	@Test
	public void test6(@Mocked PreCheckProcessWorkSchedule pre) {

		val applicationCommon = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT, // 事前事後区分 = 事前
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		BusinessTrip application = new BusinessTrip(new ArrayList<>(), null, null, applicationCommon);

		new Expectations() {
			{
				require.findAppReflectExecCond(anyString);
				result = Optional.of(new AppReflectExecutionCondition("", PreApplicationWorkScheReflectAttr.REFLECT,
						NotUseAtr.NOT_USE, // 務予定が確定状態でも反映する
						NotUseAtr.NOT_USE));
				PreCheckProcessWorkSchedule.preCheck(require, "", (Application) any, anyInt, anyBoolean,
						(ReflectStatusResult) any, (GeneralDate) any);
				result = new PreCheckProcessResult(NotUseAtr.NOT_USE, ReflectApplicationHelper
						.createReflectStatusResult(ReflectedState.REFLECTED, ReasonNotReflect.ACHIEVEMENTS_LOCKED));// 事前申請の処理ができない

			}
		};
		SCCancelProcessOneDayOutput actualResult = SCApplicationCancellationProcess.processSchedule(require, "",
				application, GeneralDate.ymd(2021, 4, 20), 1,
				ReflectApplicationHelper.createReflectStatusResult(ReflectedState.REFLECTED), // 反映状態=未反映
				NotUseAtr.USE);

		assertThat(actualResult.getStatusWorkSchedule().getReasonNotReflectWorkSchedule())
				.isEqualTo(ReasonNotReflect.ACHIEVEMENTS_LOCKED);
		new Verifications() {
			{
				require.process((ApplicationShare) any, (GeneralDate) any, (ReflectStatusResult) any,
						(NotUseAtr) any);
				times = 0;
			}
		};
	}
}
