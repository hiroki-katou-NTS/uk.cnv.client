package nts.uk.ctx.at.request.dom.cancelapplication.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.GetClosureStartForEmployee;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class ApplicationCancellationProcessTest {

	@Injectable
	private ApplicationCancellationProcess.Require require;

	/*
	 * テストしたい内容
	 * 
	 * → ①期間の申請: 期間と申請の反映を消す
	 * 
	 * → ②時間の申請: 時間と申請の反映を消す
	 * 
	 * 準備するデータ
	 * 
	 * → 期間の申請or時間の申請
	 * 
	 * →申請の反映が反映済
	 */
	@Test
	public void test1(@Mocked SCApplicationCancellationProcess processSC,
			@Mocked RCApplicationCancellationProcess processRC, @Mocked GetClosureStartForEmployee getCls) {

		// ①期間の申請: 期間と申請の反映を消す
		Application application = ReflectApplicationHelper.createAppWithPeriod(
				ApplicationType.BUSINESS_TRIP_APPLICATION,
				new DatePeriod(GeneralDate.ymd(2021, 4, 20), GeneralDate.ymd(2021, 4, 21)), ReflectedState.REFLECTED);

		new Expectations() {
			{
				GetClosureStartForEmployee.algorithm(require, (CacheCarrier) any, anyString);
				result = Optional.of(GeneralDate.ymd(2021, 01, 01));

				require.findEmpHistbySid(anyString);
				result = Arrays.asList(
						new EmploymentHistShareImport("1", "1", new DatePeriod(GeneralDate.min(), GeneralDate.max())));

				require.findByEmploymentCD(anyString, anyString);
				result = Optional.of(new ClosureEmployment("", "1", 1));

				SCApplicationCancellationProcess.processSchedule(require, anyString, (Application) any,
						(GeneralDate) any, anyInt, (ReflectStatusResult) any, (NotUseAtr) any, (EmploymentHistShareImport) any);
				result = new SCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null, null);

				RCApplicationCancellationProcess.processRecord(require, anyString, (Application) any, (GeneralDate) any,
						anyInt, (ReflectStatusResult) any, (NotUseAtr) any , (EmploymentHistShareImport)any);
				result = new RCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null, null);
			}
		};

		ApplicationCancellationProcess.cancelProcess(require, "", application, NotUseAtr.NOT_USE);

		assertThat(application.getReflectionStatus().getListReflectionStatusOfDay())
				.extracting(x -> x.getTargetDate(), x -> x.getActualReflectStatus(), x -> x.getScheReflectStatus())
				.containsExactly(
						Tuple.tuple(GeneralDate.ymd(2021, 4, 20), ReflectedState.CANCELED, ReflectedState.CANCELED),
						Tuple.tuple(GeneralDate.ymd(2021, 4, 21), ReflectedState.CANCELED, ReflectedState.CANCELED));

		// ②時間の申請: 時間と申請の反映を消す
		application = ReflectApplicationHelper.createAppWithDate(ApplicationType.BUSINESS_TRIP_APPLICATION,
				GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED);
		ApplicationCancellationProcess.cancelProcess(require, "", application, NotUseAtr.NOT_USE);

		assertThat(application.getReflectionStatus().getListReflectionStatusOfDay())
				.extracting(x -> x.getTargetDate(), x -> x.getActualReflectStatus(), x -> x.getScheReflectStatus())
				.containsExactly(
						Tuple.tuple(GeneralDate.ymd(2021, 4, 20), ReflectedState.CANCELED, ReflectedState.CANCELED));
	}

	/*
	 * テストしたい内容
	 * 
	 * → ①申請日＜社員の締め開始日場合反映した申請は削除が出来ない
	 * 
	 * → ②申請日=社員の締め開始日場合反映した申請は削除が出来る
	 * 
	 * → ③申請日>=社員の締め開始日場合反映した申請は削除が出来る
	 * 
	 * 準備するデータ
	 * 
	 * → 申請開始日＜社員の締め開始日＜申請終了日
	 */
	@Test
	public void test2(@Mocked SCApplicationCancellationProcess processSC,
			@Mocked RCApplicationCancellationProcess processRC, @Mocked GetClosureStartForEmployee getCls) {

		Application application = ReflectApplicationHelper.createAppWithPeriod(
				ApplicationType.BUSINESS_TRIP_APPLICATION,
				new DatePeriod(GeneralDate.ymd(2021, 4, 20), GeneralDate.ymd(2021, 4, 22)), ReflectedState.REFLECTED);

		new Expectations() {
			{
				GetClosureStartForEmployee.algorithm(require, (CacheCarrier) any, anyString);
				result = Optional.of(GeneralDate.ymd(2021, 04, 21));// 社員の締め開始日

				require.findEmpHistbySid(anyString);
				result = Arrays.asList(
						new EmploymentHistShareImport("1", "1", new DatePeriod(GeneralDate.min(), GeneralDate.max())));

				require.findByEmploymentCD(anyString, anyString);
				result = Optional.of(new ClosureEmployment("", "1", 1));

				SCApplicationCancellationProcess.processSchedule(require, anyString, (Application) any,
						(GeneralDate) any, anyInt, (ReflectStatusResult) any, (NotUseAtr) any, (EmploymentHistShareImport) any);
				result = new SCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null, null);

				RCApplicationCancellationProcess.processRecord(require, anyString, (Application) any, (GeneralDate) any,
						anyInt, (ReflectStatusResult) any, (NotUseAtr) any, (EmploymentHistShareImport)any);
				result = new RCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null, null);
			}
		};

		ApplicationCancellationProcess.cancelProcess(require, "", application, NotUseAtr.NOT_USE);

		assertThat(application.getReflectionStatus().getListReflectionStatusOfDay())
				.extracting(x -> x.getTargetDate(), x -> x.getActualReflectStatus(), x -> x.getScheReflectStatus(),
						x -> x.getOpUpdateStatusAppCancel().flatMap(y -> y.getOpReasonScheCantReflect()).orElse(null),
						x -> x.getOpUpdateStatusAppCancel().flatMap(y -> y.getOpReasonActualCantReflect())
								.orElse(null))
				.containsExactly(
						Tuple.tuple(GeneralDate.ymd(2021, 4, 20), ReflectedState.REFLECTED, ReflectedState.REFLECTED,
								ReasonNotReflect.TIGHTENING_PROCESS_COMPLETED,
								ReasonNotReflectDaily.TIGHTENING_PROCESS_COMPLETED), // ①申請日＜社員の締め開始日場合反映した申請は削除が出来ない
						Tuple.tuple(GeneralDate.ymd(2021, 4, 21), ReflectedState.CANCELED, ReflectedState.CANCELED,
								null, null), // ②申請日=社員の締め開始日場合反映した申請は削除が出来る
						Tuple.tuple(GeneralDate.ymd(2021, 4, 22), ReflectedState.CANCELED, ReflectedState.CANCELED,
								null, null));// ③申請日>=社員の締め開始日場合反映した申請は削除が出来る
	}

	/*
	 * テストしたい内容
	 * 
	 * → ①DB登録するか区分=しない場合申請の反映状態の更新がない
	 * 
	 * → ②DB登録するか区分=する場合申請の反映状態の更新がある
	 * 
	 * 準備するデータ
	 * 
	 * → DB登録するか区分
	 */
	@Test
	public void test3(@Mocked SCApplicationCancellationProcess processSC,
			@Mocked RCApplicationCancellationProcess processRC, @Mocked GetClosureStartForEmployee getCls) {

		Application application = ReflectApplicationHelper.createAppWithPeriod(
				ApplicationType.BUSINESS_TRIP_APPLICATION,
				new DatePeriod(GeneralDate.ymd(2021, 4, 20), GeneralDate.ymd(2021, 4, 21)), ReflectedState.REFLECTED);

		new Expectations() {
			{
				GetClosureStartForEmployee.algorithm(require, (CacheCarrier) any, anyString);
				result = Optional.of(GeneralDate.ymd(2021, 01, 01));// 社員の締め開始日

				require.findEmpHistbySid(anyString);
				result = Arrays.asList(
						new EmploymentHistShareImport("1", "1", new DatePeriod(GeneralDate.min(), GeneralDate.max())));

				require.findByEmploymentCD(anyString, anyString);
				result = Optional.of(new ClosureEmployment("", "1", 1));

				SCApplicationCancellationProcess.processSchedule(require, anyString, (Application) any,
						(GeneralDate) any, anyInt, (ReflectStatusResult) any, (NotUseAtr) any, (EmploymentHistShareImport) any);
				result = new SCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null,
						AtomTask.none());

				RCApplicationCancellationProcess.processRecord(require, anyString, (Application) any, (GeneralDate) any,
						anyInt, (ReflectStatusResult) any, (NotUseAtr) any, (EmploymentHistShareImport)any);
				result = new RCCancelProcessOneDayOutput(
						ReflectApplicationHelper.createReflectStatusResult(ReflectedState.CANCELED), null,
						AtomTask.none());
			}
		};

		NtsAssert.atomTask(
				() -> ApplicationCancellationProcess.cancelProcess(require, "", application, NotUseAtr.USE)
						.getAtomTask(), // DB登録するか区分
				any -> require.updateApp(any.get()), any -> require.registerRemain(any.get(), any.get(), any.get()));

	}
}
