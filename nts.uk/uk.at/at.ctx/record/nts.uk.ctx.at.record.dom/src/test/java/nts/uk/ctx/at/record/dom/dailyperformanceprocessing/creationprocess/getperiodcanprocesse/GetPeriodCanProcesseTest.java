package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.closegetunlockedperiod.ClosingGetUnlockedPeriod;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.shr.com.time.calendar.date.ClosureDate;


@RunWith(JMockit.class)
public class GetPeriodCanProcesseTest {
	
	@Injectable
	private GetPeriodCanProcesse.Require require;
	
	/**
	 * 最新のドメインモデル「締め状態管理」を取得する is empty
	 * (⁂注意：締め状態管理取得できない場合、パラメータ。期間を使う)
	 * 雇用履歴に含まれる期間を取得る not empty
	 */
	@Test
	public void test_get_1() {
		String employeeId = "employeeId";// dummy
		DatePeriod period = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 2, 1)); 
		IgnoreFlagDuringLock ignoreFlagDuringLock = IgnoreFlagDuringLock.CAN_CAL_LOCK;
		AchievementAtr achievementAtr = AchievementAtr.DAILY;
		
		List<EmploymentHistoryImported> listEmploymentHis = new ArrayList<>();
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode1", new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31))));
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode2", new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 28))));
		
		new MockUp<ClosingGetUnlockedPeriod>() {
			@Mock
			public List<DatePeriod> get(GetPeriodCanProcesse.Require require, DatePeriod period, String employmentCode,
					IgnoreFlagDuringLock ignoreFlagDuringLock, AchievementAtr achievementAtr) {
				return new ArrayList<>();
			}
		};
		new Expectations() {
			{
				require.getAllByEmpId(anyString);
			}
		};
		
		List<DatePeriod> result = GetPeriodCanProcesse.get(require, employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		assertThat(result).isEmpty();
	}
	
	/**
	 * 最新のドメインモデル「締め状態管理」を取得する is empty
	 * (⁂注意：締め状態管理取得できない場合、パラメータ。期間を使う)
	 * 雇用履歴に含まれる期間を取得る is empty
	 */
	@Test
	public void test_get_2() {
		String employeeId = "employeeId";// dummy
		DatePeriod period = new DatePeriod(GeneralDate.ymd(2021, 3, 1), GeneralDate.ymd(2021, 4, 1)); 
		IgnoreFlagDuringLock ignoreFlagDuringLock = IgnoreFlagDuringLock.CAN_CAL_LOCK;
		AchievementAtr achievementAtr = AchievementAtr.DAILY;
		
		List<EmploymentHistoryImported> listEmploymentHis = new ArrayList<>();
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode1", new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31))));
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode2", new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 28))));
		
		new Expectations() {
			{
				require.getAllByEmpId(anyString);
			}
		};
		
		List<DatePeriod> result = GetPeriodCanProcesse.get(require, employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		assertThat(result).isEmpty();
	}

	/**
	 * 最新のドメインモデル「締め状態管理」を取得する not empty
	 * 締めされていない期間を求める is empty
	 */
	@Test
	public void test_get_3() {
		String employeeId = "employeeId";// dummy
		DatePeriod period = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 2, 1)); 
		IgnoreFlagDuringLock ignoreFlagDuringLock = IgnoreFlagDuringLock.CAN_CAL_LOCK;
		AchievementAtr achievementAtr = AchievementAtr.DAILY;
		
		List<EmploymentHistoryImported> listEmploymentHis = new ArrayList<>();
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode1", new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31))));
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode2", new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 28))));
		
		List<ClosureStatusManagement> listClosureStatus = new ArrayList<>();
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202101), employeeId, 1,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 22))));
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202102), employeeId, 2,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 23))));
		
		new MockUp<ClosureStatusManagement>() {
			@Mock
			public Optional<DatePeriod> closureStateManagenent(DatePeriod period) {
				return Optional.empty();
			}
		};
		new Expectations() {
			{
				require.getAllByEmpId(anyString);
				result = listClosureStatus;
			}
		};
		
		List<DatePeriod> result = GetPeriodCanProcesse.get(require, employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		assertThat(result).isEmpty();
	}
	
	/**
	 * 最新のドメインモデル「締め状態管理」を取得する not empty
	 * 締めされていない期間を求める not empty
	 * 実績締めロックされない期間を取得する is empty
	 */
	@Test
	public void test_get_4() {
		String employeeId = "employeeId";// dummy
		DatePeriod period = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 2, 1)); 
		IgnoreFlagDuringLock ignoreFlagDuringLock = IgnoreFlagDuringLock.CAN_CAL_LOCK;
		AchievementAtr achievementAtr = AchievementAtr.DAILY;
		
		List<EmploymentHistoryImported> listEmploymentHis = new ArrayList<>();
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode1", new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31))));
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode2", new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 28))));
		
		List<ClosureStatusManagement> listClosureStatus = new ArrayList<>();
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202101), employeeId, 1,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 22))));
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202102), employeeId, 2,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 23))));
	
		new MockUp<ClosingGetUnlockedPeriod>() {
			@Mock
			public List<DatePeriod> get(GetPeriodCanProcesse.Require require, DatePeriod period, String employmentCode,
					IgnoreFlagDuringLock ignoreFlagDuringLock, AchievementAtr achievementAtr) {
				return new ArrayList<>();
			}
		};
		new Expectations() {
			{
				require.getAllByEmpId(anyString);
				result = listClosureStatus;
			}
		};
		
		List<DatePeriod> result = GetPeriodCanProcesse.get(require, employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		assertThat(result).isEmpty();
	}
	/**
	 * 最新のドメインモデル「締め状態管理」を取得する not empty
	 * 締めされていない期間を求める not empty
	 * 実績締めロックされない期間を取得する not empty
	 */
	@Test
	public void test_get_5() {
		String employeeId = "employeeId";// dummy
		DatePeriod period = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 2, 1)); 
		IgnoreFlagDuringLock ignoreFlagDuringLock = IgnoreFlagDuringLock.CAN_CAL_LOCK;
		AchievementAtr achievementAtr = AchievementAtr.DAILY;
		
		List<EmploymentHistoryImported> listEmploymentHis = new ArrayList<>();
		listEmploymentHis.add(new EmploymentHistoryImported(employeeId, "employmentCode1", new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31))));
		
		List<ClosureStatusManagement> listClosureStatus = new ArrayList<>();
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202101), employeeId, 1,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 22))));
		listClosureStatus.add(new ClosureStatusManagement(new YearMonth(202102), employeeId, 2,new ClosureDate(1, true), new DatePeriod(GeneralDate.ymd(2021, 2, 1), GeneralDate.ymd(2021, 2, 23))));
	
		DatePeriod dateP = new DatePeriod(GeneralDate.ymd(2021, 1,10), GeneralDate.ymd(2021, 2, 5));
		new MockUp<ClosureStatusManagement>() {
			@Mock
			public Optional<DatePeriod> closureStateManagenent(DatePeriod period) {
				return Optional.of(dateP);
			}
		};
		List<DatePeriod> listP = new ArrayList<>(); 
		listP.add(new DatePeriod(GeneralDate.ymd(2021, 1,10), GeneralDate.ymd(2021, 1, 20)));
		listP.add(new DatePeriod(GeneralDate.ymd(2021, 1,25), GeneralDate.ymd(2021, 1, 30)));
		new MockUp<ClosingGetUnlockedPeriod>() {
			@Mock
			public List<DatePeriod> get(GetPeriodCanProcesse.Require require, DatePeriod period, String employmentCode,
					IgnoreFlagDuringLock ignoreFlagDuringLock, AchievementAtr achievementAtr) {
				return listP;
			}
		};
		new Expectations() {
			{
				require.getAllByEmpId(anyString);
				result = listClosureStatus;
			}
		};
		
		List<DatePeriod> result = GetPeriodCanProcesse.get(require, employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		assertThat( result )
		.extracting( 
				d -> d.start(),
				d -> d.end() )
		.containsExactly(
				tuple( listP.get(0).start(), listP.get(0).end()),
				tuple( listP.get(1).start(), listP.get(1).end()));
	}
	
	
	
}
