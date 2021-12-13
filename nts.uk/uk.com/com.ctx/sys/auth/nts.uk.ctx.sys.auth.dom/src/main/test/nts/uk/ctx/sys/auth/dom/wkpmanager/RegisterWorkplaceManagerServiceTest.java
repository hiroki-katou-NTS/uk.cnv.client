package nts.uk.ctx.sys.auth.dom.wkpmanager;

import static mockit.Deencapsulation.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmpEnrollPeriodImport;
import nts.uk.ctx.sys.auth.dom.adapter.employee.SecondSituation;
@RunWith(JMockit.class)
public class RegisterWorkplaceManagerServiceTest {
	
	@Injectable
	private RegisterWorkplaceManagerService.Require require;
	
	/**
	 * 期間重複をチェックする
	 * input:
	 * 職場管理者の履歴期間 historyPeriods:[	2012/01/01 ~ 2018/12/31
	 * 						2019/01/01 ~ 2030/12/31]
	 * 履歴期間 period：	「2021/01/01 ~ 2021/12/31」
	 * output： Msg_619
	 */
	@Test
	public void checkError_Msg_619() {
		String sid = "sid";
		val historyPeriods = new ArrayList<DatePeriod>(Arrays.asList(
					new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2030, 12, 31))));
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, historyPeriods);
		val period = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 12, 31));

		 NtsAssert.businessException("Msg_619", ()->{
				invoke(RegisterWorkplaceManagerService.class, 
						"checkError", require, sid, period, wpkManageRegistered);
				});
	}
	
	/**
	 * 入社前・退職後をチェックする
	 * 職場管理者の履歴期間 wpkManageRegistered: empty
	 * 社員の所属履歴 periods:[	2015/01/01 ~ 2020/12/31
	 * 							2024/01/01 ~ 2030/12/31]
	 * 履歴期間 period：	「2019/01/01 ~ 2023/12/31」
	 * output： Msg_2199
	 */
	@Test
	public void checkError_Msg_2199_case_1() {
		val sid = "sid";
		val period = new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2023, 12, 31));
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, Collections.emptyList());
		val periods = new ArrayList<DatePeriod>(Arrays.asList(
						new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2020, 12, 31))
					,	new DatePeriod(GeneralDate.ymd(2024, 1, 1), GeneralDate.ymd(2030, 12, 31))));
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, periods);
		
		new Expectations() {{
			require.getHistoryCompanyBySidAndDatePeriod((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		 NtsAssert.businessException("Msg_2199", ()->{
				invoke(RegisterWorkplaceManagerService.class, 
						"checkError", require, sid, period, wpkManageRegistered);
		 });
		
	}
	
	/**
	 * 入社前・退職後をチェックする
	 * 職場管理者の履歴期間 wpkManageRegistered: empty
	 * 社員の所属履歴 comHistories:[	2015/01/01 ~ 2020/12/31
	 * 					2024/01/01 ~ 2030/12/31]
	 * 履歴期間 period：	「2010/01/01 ~ 2014/12/31」
	 * output： Msg_2199
	 */
	@Test
	public void checkError_Msg_2199_case_2() {
		val sid = "sid";
		val period = new DatePeriod(GeneralDate.ymd(2010, 1, 1), GeneralDate.ymd(2014, 12, 31));
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, Collections.emptyList());
		val periods = new ArrayList<DatePeriod>(Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2020, 12, 31))
			,	new DatePeriod(GeneralDate.ymd(2024, 1, 1), GeneralDate.ymd(2030, 12, 31))));
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, periods);
		
		new Expectations() {{
			require.getHistoryCompanyBySidAndDatePeriod((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		 NtsAssert.businessException("Msg_2199", ()->{
				invoke(RegisterWorkplaceManagerService.class, 
						"checkError", require, sid, period, wpkManageRegistered);
		 });
		
	}
	
	/**
	 * 入社前・退職後をチェックする
	 * 職場管理者の履歴期間 wpkManageRegistered: empty
	 * 社員の所属履歴 comHistories:[	2015/01/01 ~ 2020/12/31
	 * 								2024/01/01 ~ 2030/12/31]
	 * 履歴期間 period：	「2019/01/01 ~ 2025/12/31」
	 * output： Msg_2199
	 */
	@Test
	public void checkError_Msg_2199_case_3() {
		val sid = "sid";
		val period = new DatePeriod(GeneralDate.ymd(2010, 1, 1), GeneralDate.ymd(2014, 12, 31));
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, Collections.emptyList());
		val periods = new ArrayList<DatePeriod>(Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2020, 12, 31))
			,	new DatePeriod(GeneralDate.ymd(2024, 1, 1), GeneralDate.ymd(2030, 12, 31))));
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, periods);
		
		new Expectations() {{
			require.getHistoryCompanyBySidAndDatePeriod((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		 NtsAssert.businessException("Msg_2199", ()->{
				invoke(RegisterWorkplaceManagerService.class, 
						"checkError", require, sid, period, wpkManageRegistered);
		 });
		
	}
	
	/**
	 * 追加: susscess
	 */
	@Test
	public void add() {
		String sid = "sid";
		String workplaceId = "workplaceId";
		
		val historyPeriods = new ArrayList<DatePeriod>(Arrays.asList(
					new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2022, 12, 31))));
		val comHistperiods = new ArrayList<DatePeriod>(Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2050, 12, 31))));
		
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, historyPeriods);
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, comHistperiods);
		val period = new DatePeriod(GeneralDate.ymd(2023, 1, 1), GeneralDate.ymd(2026, 12, 31));
		val workPlaceManager = WorkplaceManager.createNew(workplaceId, sid, period);
		
		new Expectations() {{
			require.getWorkplaceMangagerByWorkplaceIdAndSid((String) any, (String) any);
			result = wpkManageRegistered;
			
			require.getHistoryCompanyBySidAndDatePeriod((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		NtsAssert.atomTask(() -> RegisterWorkplaceManagerService.add(require, workplaceId, sid, period),
				any -> require.insert(workPlaceManager));
	}
	
	/**
	 * 期間を変更する: susscess
	 */	
	@Test
	public void changePeriod() {
		String sid = "sid";
		String workplaceId = "workplaceId";
		
		val historyPeriods = new ArrayList<DatePeriod>(Arrays.asList(
					new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2022, 12, 31))));
		val comHistperiods = new ArrayList<DatePeriod>(Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2050, 12, 31))));
		
		val wpkManageRegistered = Helper.createWorkplaceManagers(sid, historyPeriods);
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, comHistperiods);
		val period = new DatePeriod(GeneralDate.ymd(2023, 1, 1), GeneralDate.ymd(2026, 12, 31));
		val workPlaceManager = WorkplaceManager.createNew(workplaceId, sid, period);
		
		new Expectations() {{
			require.getWorkplaceMangagerByWorkplaceIdAndSid((String) any, (String) any);
			result = wpkManageRegistered;
			
			require.getHistoryCompanyBySidAndDatePeriod((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		NtsAssert.atomTask(() -> RegisterWorkplaceManagerService.changePeriod(require, workPlaceManager),
				any -> require.update(workPlaceManager));
	}
	
	
	
	public static class Helper{
		
		public static List<WorkplaceManager> createWorkplaceManagers(String sid, List<DatePeriod> periods) {
			return periods.stream()
					.map(c -> WorkplaceManager.createNew("workplaceId", sid, c))
					.collect(Collectors.toList());

		}
		
		public static WorkplaceManager createWorkplaceManager(String sid, DatePeriod period) {
			return WorkplaceManager.createNew("workplaceId", "sid", period);
		}
		
		public static EmpEnrollPeriodImport createEmpEnrollPeriodImport(String sid, DatePeriod period) {
			return new EmpEnrollPeriodImport(sid, period, SecondSituation.ACCEPTING);
		}
		
		public static List<EmpEnrollPeriodImport> createEmpEnrollPeriodImports(String sid, List<DatePeriod> periods) {
			return periods.stream().map(c -> createEmpEnrollPeriodImport(sid, c)).collect(Collectors.toList());
		}
		
	}
	

}
