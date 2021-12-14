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
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport.ConfirmedAtrImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.CheckAchievementConfirmation.ConfirmClsStatus;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.PreCheckProcessWorkSchedule.PreCheckProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class PreCheckProcessWorkScheduleTest {

	@Injectable
	private PreCheckProcessWorkSchedule.Require require;

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
	 * →予定反映不可理由が「2：実績がロックされている」
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
		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				false, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkSchedule())
				.isEqualTo(ReasonNotReflect.ACHIEVEMENTS_LOCKED);

	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定反映ができる
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
		
		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.USE);
	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定反映ができない
	 * 
	 * →予定反映不可理由が「2：勤務スケジュール確定済」
	 * 
	 * 準備するデータ
	 * 
	 * →勤務スケジュール確定済
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testScheduleReflected() {

		Application application = ReflectApplicationHelper.createApp(ApplicationType.BUSINESS_TRIP_APPLICATION,
				PrePostAtr.PREDICT);

		ReflectStatusResult reflectStatus = ReflectApplicationHelper.createReflectStatusResult();

		new Expectations() {
			{
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = Arrays.asList(new DatePeriod(GeneralDate.min(), GeneralDate.max()));
				
				require.findConfirmById((List<String>) any, (DatePeriod) any);
				result = Arrays.asList(new BasicScheduleConfirmImport("1", // 社員ID
						dateRefer, // 年月日
						ConfirmedAtrImport.CONFIRMED));// 予定確定区分
			}
		};

		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkSchedule()).isEqualTo(ReasonNotReflect.WORK_FIXED);

	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定反映ができない
	 * 
	 * →予定反映不可理由が「3：本人確認、上司確認済」
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

		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkSchedule())
				.isEqualTo(ReasonNotReflect.SELF_CONFIRMED_BOSS_CONFIRMED);

	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定反映ができない
	 * 
	 * →予定反映不可理由が「4： 締め処理が完了している」
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

		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.NOT_USE);

		assertThat(check.getReflectStatus().getReasonNotReflectWorkSchedule())
				.isEqualTo(ReasonNotReflect.TIGHTENING_PROCESS_COMPLETED);

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

		PreCheckProcessResult check = PreCheckProcessWorkSchedule.preCheck(require, companyId, application, closureId,
				true, reflectStatus, dateRefer, new ArrayList<>());

		assertThat(check.getProcessFlag()).isEqualTo(NotUseAtr.USE);
	}
}
