package nts.uk.ctx.at.request.dom.applicationreflect.algorithm.reflectprocess;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.checkprocess.CheckProcessDuringLock;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.common.ReflectApplicationHelper;
import nts.uk.ctx.at.request.dom.applicationreflect.object.PreApplicationWorkScheReflectAttr;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class ProcessReflectWorkScheduleTest {

	@Injectable
	private ProcessReflectWorkSchedule.Require require;

	private String companyId;

	private Integer closureId;

	private ReflectStatusResult statusWorkSchedule;

	private GeneralDate dateRefer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		companyId = "cid";
		closureId = 1;
		statusWorkSchedule = new ReflectStatusResult(ReflectedState.NOTREFLECTED, null, null);
		dateRefer = GeneralDate.ymd(2020, 05, 10);
	}

	/*
	 * テストしたい内容
	 * 
	 * →勤務予定の反映状態が「反映済み」
	 * 
	 * 準備するデータ
	 * 
	 * → 事前事後区分 = 事後
	 * 
	 */

	@Test
	public void checkAppType() {

		Application application = ReflectApplicationHelper.createApp(PrePostAtr.POSTERIOR);

		val actualResult = ProcessReflectWorkSchedule.processReflect(require, companyId, closureId, application,
				false, dateRefer, statusWorkSchedule, new ArrayList<>());
		assertThat(actualResult.getLeft().getReflectStatus()).isEqualTo(ReflectedState.REFLECTED);

	}

	/*
	 * テストしたい内容
	 * 
	 * →勤務予定の反映状態が「反映済み」
	 * 
	 * 準備するデータ
	 * 
	 * → 「事前申請を勤務予定に反映する」の設定がしない
	 * 
	 */

	@Test
	public void checkBeforeScheduleConfirm() {

		Application application = ReflectApplicationHelper.createApp(PrePostAtr.PREDICT);

		new Expectations() {
			{
				require.findAppReflectExecCond(companyId);
				result = Optional.of(new AppReflectExecutionCondition(companyId, PreApplicationWorkScheReflectAttr.NOT_REFLECT, // 事前申請を勤務予定に反映する
						NotUseAtr.NOT_USE, NotUseAtr.USE));
			}
		};
		val actualResult = ProcessReflectWorkSchedule.processReflect(require, companyId, closureId, application,
				false, dateRefer, statusWorkSchedule, new ArrayList<>());
		assertThat(actualResult.getLeft().getReflectStatus()).isEqualTo(ReflectedState.REFLECTED);

	}

	/*
	 * テストしたい内容
	 * 
	 * → 勤務予定反映がない
	 * 
	 * 準備するデータ
	 * 
	 * → 「勤務予定が確定状態でも反映する」の設定が する
	 * 
	 * → 「事前申請を勤務予定に反映する」の設定がしない
	 * 
	 * →実績がロックされている
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void checkScheduleConfirm(@Mocked CheckProcessDuringLock lockActual) {

		Application application = ReflectApplicationHelper.createApp(PrePostAtr.PREDICT);

		new Expectations() {
			{
				require.findAppReflectExecCond(anyString);
				result = Optional
						.of(new AppReflectExecutionCondition(companyId, PreApplicationWorkScheReflectAttr.REFLECT, NotUseAtr.NOT_USE, NotUseAtr.USE));
				
				require.getPeriodProcess(anyString, (DatePeriod) any, (List<EmploymentHistoryImported>) any,
						(IgnoreFlagDuringLockImport) any, (AchievementAtrImport) any);
				result = new ArrayList<>();
			}
		};
		val actualResult = ProcessReflectWorkSchedule.processReflect(require, companyId, closureId, application,
				 false, dateRefer, statusWorkSchedule, new ArrayList<>());
		assertThat(actualResult.getLeft().getReflectStatus()).isEqualTo(ReflectedState.NOTREFLECTED);
		assertThat(actualResult.getLeft().getReasonNotReflectWorkSchedule()).isEqualTo(ReasonNotReflect.ACHIEVEMENTS_LOCKED);

	}
}
