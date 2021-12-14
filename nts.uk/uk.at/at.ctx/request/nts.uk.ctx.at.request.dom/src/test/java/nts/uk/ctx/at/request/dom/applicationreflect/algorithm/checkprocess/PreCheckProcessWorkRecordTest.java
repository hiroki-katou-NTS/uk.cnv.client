package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.CheckAchievementConfirmation.ConfirmClsStatus;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class PreCheckProcessWorkRecordTest {

	@Injectable
	private PreCheckProcessWorkRecord.Require require;

	@Injectable
	private CheckAchievementConfirmation confirm;

	private String companyId;

	private Integer closureId;

	private GeneralDate dateRefer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		companyId = "cid";
		closureId = 1;
		dateRefer = GeneralDate.ymd(2020, 05, 10);
	}

	/*
	 * テストしたい内容
	 * 
	 * → 日別実績反映不可理由が「2：実績がロックされている」
	 * 
	 * 準備するデータ
	 * 
	 * → 実績がロックされている
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLockActual() {

		Application application = ReflectApplicationHelper.createApp(PrePostAtr.POSTERIOR);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = new ArrayList<>();
			}
		};
		PreCheckProcessResult check = PreCheckProcessWorkRecord.preCheck(require, companyId, application, closureId,
				false, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkRecord())
				.isEqualTo(ReasonNotReflectDaily.ACHIEVEMENTS_LOCKED);

	}


	/*
	 * テストしたい内容
	 * 
	 * → 勤務実績反映ができる
	 * 
	 * 準備するデータ
	 * 
	 * → 申請が事前の残業申請
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testBeforeZangyo() {

		Application application = ReflectApplicationHelper.createApp(ApplicationType.OVER_TIME_APPLICATION,
				PrePostAtr.PREDICT);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = Arrays.asList(new DatePeriod(GeneralDate.min(), GeneralDate.max()));
			}
		};
		PreCheckProcessResult check = PreCheckProcessWorkRecord.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.USE);
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務実績反映ができない
	 * 
	 * →実績反映不可理由が「2：本人確認、上司確認済」
	 * 
	 * 準備するデータ
	 * 
	 * → 対象期間内で本人確認をした
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDayConfirm() {

		Application application = ReflectApplicationHelper.createApp(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = Arrays.asList(new DatePeriod(GeneralDate.min(), GeneralDate.max()));
				
				require.getProcessingYMD(anyString, anyString, (DatePeriod) any);
				result = Arrays.asList(dateRefer);// 予定確定区分
			}
		};

		PreCheckProcessResult check = PreCheckProcessWorkRecord.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkRecord())
				.isEqualTo(ReasonNotReflectDaily.SELF_CONFIRMED_BOSS_CONFIRMED);

	}
	
	/*
	 * テストしたい内容
	 * 
	 * → 勤務実績反映ができない
	 * 
	 * →実績反映不可理由が「4： 締め処理が完了している」
	 * 
	 * 準備するデータ
	 * 
	 * → 実績の締め確定した
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAchievementConfirmation(@Mocked CheckAchievementConfirmation mockedConfirm) {

		Application application = ReflectApplicationHelper.createApp(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = Arrays.asList(new DatePeriod(GeneralDate.min(), GeneralDate.max()));
				
				//実績の締め確定した
				CheckAchievementConfirmation.check(require, companyId, anyString, (GeneralDate) any, anyInt);
				result = ConfirmClsStatus.Confirm;
			}
		};

		PreCheckProcessResult check = PreCheckProcessWorkRecord.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkRecord())
				.isEqualTo(ReasonNotReflectDaily.ACTUAL_CONFIRMED);

	}
	
	/*
	 * テストしたい内容
	 * 
	 * → 勤務実績反映ができる
	 * 
	 * 
	 * 準備するデータ
	 * 
	 * → すべて、ロックされていません
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAll(@Mocked CheckAchievementConfirmation mockedConfirm) {

		Application application = ReflectApplicationHelper.createApp(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = Arrays.asList(new DatePeriod(GeneralDate.min(), GeneralDate.max()));
				//実績の締め確定した
				CheckAchievementConfirmation.check(require, companyId, anyString, (GeneralDate) any, anyInt);
				result = ConfirmClsStatus.Pending;
			}
		};

		PreCheckProcessResult check = PreCheckProcessWorkRecord.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.USE);
	}
}
