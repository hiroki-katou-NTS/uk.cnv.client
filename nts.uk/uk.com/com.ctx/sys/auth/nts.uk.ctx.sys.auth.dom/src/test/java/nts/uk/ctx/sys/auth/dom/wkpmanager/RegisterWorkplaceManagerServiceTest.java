package nts.uk.ctx.sys.auth.dom.wkpmanager;

import static mockit.Deencapsulation.invoke;

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
import nts.uk.ctx.sys.shared.dom.employee.EmpEnrollPeriodImport;
import nts.uk.ctx.sys.shared.dom.employee.SecondSituation;
@RunWith(JMockit.class)
public class RegisterWorkplaceManagerServiceTest {
	
	@Injectable
	private RegisterWorkplaceManagerService.Require require;

	/**
	 * target:		エラーをチェックする
	 * patterns:	期間重複をチェックする	
	 */
	@Test
	public void testCheckError_Msg_619() {
		String sid = "sid";
		val historyPeriods =  Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
			,	new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2030, 12, 31)));
		val registeredWorkplaceManagers = Helper.createWorkplaceManagers(sid, historyPeriods);
		
		val checkPeriod = Arrays.asList(
					new DatePeriod(GeneralDate.ymd(2011, 1, 1), GeneralDate.ymd(2015, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2017, 1, 1), GeneralDate.ymd(2021, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2028, 1, 1), GeneralDate.ymd(2031, 12, 31))
					);
		
		checkPeriod.forEach(period ->{
			 NtsAssert.businessException("Msg_619", ()->{
					invoke(	RegisterWorkplaceManagerService.class 
						,	"checkError", require, sid, period, registeredWorkplaceManagers);
					});
		});
	}
	
	/**
	 * target:		エラーをチェックする
	 * patterns:	入社前・退職後をチェックする
	 * 職場管理者の履歴期間 registeredWorkplaceManagers: empty
	 * 社員の所属履歴 comHistories:[2015/01/01 ~ 2020/12/31]
	 */
	@Test
	public void testCheckError_Msg_2199() {
		val sid = "sid";
		val periods = Arrays.asList(new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2020, 12, 31)));
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, periods);
		
		new Expectations() {{
			require.getEmployeeCompanyHistory((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		/**
		 * case1: Msg_2199
		 * 			<----------->	社員の所属履歴
		 * <--> 				 	追加、変更期間
		 * 	<------>
		 * 					<------>
		 *						<---->
		 *								<------>
		 */
		{
			val addPeriodErrors = Arrays.asList(	new DatePeriod(GeneralDate.ymd(2010, 1, 1), GeneralDate.ymd(2014, 12, 31))
												,	new DatePeriod(GeneralDate.ymd(2014, 1, 1), GeneralDate.ymd(2015, 1, 1))
												,	new DatePeriod(GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2021, 1, 1))
												,	new DatePeriod(GeneralDate.ymd(2020, 12, 31), GeneralDate.ymd(2021, 12, 31))
												,	new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 12, 31))
												);
			
			addPeriodErrors.stream().forEach(addperiod ->{
				 NtsAssert.businessException("Msg_2199", ()->{
						invoke(	RegisterWorkplaceManagerService.class 
							,	"checkError", require, sid, addperiod, Collections.emptyList());
				 });
			});
			
		}
		
		/**
		 * case2: エラーがない
		 * 			<----------->	社員の所属履歴
		 * 			<--->			追加.変更期間
		 */
		
		{
			val addperiods = Arrays.asList(		new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2016, 12, 31))
											,	new DatePeriod(GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2020, 12, 31))
											);
			
			addperiods.stream().forEach(addperiod -> {
				NtsAssert.Invoke.staticMethod(	RegisterWorkplaceManagerService.class
											,	"checkError", require, sid, addperiod, Collections.emptyList());
			});
		}
	}
	
	/**
	 * target:		追加する
	 * patterns:	エラーがない
	 * expected:	success
	 */
	@Test
	public void testAdd() {
		val sid = "sid";
		val workplaceId = "workplaceId";
		
		val historyPeriods = Arrays.asList(
					new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
				,	new DatePeriod(GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2022, 12, 31)));
		val comHistperiods = Arrays.asList(
				new DatePeriod(GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2050, 12, 31)));
		
		val registeredWorkplaceManagers = Helper.createWorkplaceManagers(sid, historyPeriods);
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, comHistperiods);
		val period = new DatePeriod(GeneralDate.ymd(2023, 1, 1), GeneralDate.ymd(2026, 12, 31));
		val workPlaceManager = WorkplaceManager.createNew(workplaceId, sid, period);
		
		new Expectations() {{
			require.getWorkplaceMangager((String) any, (String) any);
			result = registeredWorkplaceManagers;
			
			require.getEmployeeCompanyHistory((String) any, (DatePeriod) any);
			result = comHistories;
			
		}};
		
		NtsAssert.atomTask(() -> RegisterWorkplaceManagerService.add(require, workplaceId, sid, period)
				,	any -> require.insert(workPlaceManager));
	}
	
	/**
	 * target:		期間を変更する
	 * patterns:	
	 * 			<----------->		<---------------->	既に登録される職場管理者リスト: [wkplaceManagerId_1, wkplaceManagerId_2]
	 * 					<--------->						職場管理者.履歴期間のwkplaceManagerId_2
	 * expected:	Msg_619
	 * 
	 */	
	@Test
	public void testChangePeriod_period_duplicate() {
		val id1 = "wkplaceManagerId_1";
		val id2 = "wkplaceManagerId_2";
		
		val registeredWorkplaceManagers = Arrays.asList(
						Helper.createWorkplaceManager( id1, GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2018, 12, 31))
					,	Helper.createWorkplaceManager( id2, GeneralDate.ymd(2020, 1, 1), GeneralDate.ymd(2022, 12, 31))
						);
		
		val workPlaceManager = Helper.createWorkplaceManager( id2, GeneralDate.ymd(2016, 1, 1), GeneralDate.ymd(2019, 06, 30));
		
		new Expectations() {{
			require.getWorkplaceMangager((String) any, (String) any);
			result = registeredWorkplaceManagers;
			
		}};

		 NtsAssert.businessException("Msg_619"
				 , ()->{RegisterWorkplaceManagerService.changePeriod(require, workPlaceManager);
		 });
	}

	/**
	 * target:		期間を変更する
	 * patterns:	
	 * 			<----------->	<-------->		<---------------->	既に登録される職場管理者リスト: [id_1, id_2, id_3]
	 * 										<-------------->		職場管理者.履歴期間のid_3
	 * expected:	success
	 * 
	 */	
	@Test
	public void testChangePeriod_update() {
		val sid = "sid";
		val comHistperiods = Arrays.asList(new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(2050, 12, 31)));
	
		val registeredWorkplaceManagers = Arrays.asList(
				Helper.createWorkplaceManager( "id_1", GeneralDate.ymd(2015, 1, 1), GeneralDate.ymd(2016, 12, 31) )
			,	Helper.createWorkplaceManager( "id_2", GeneralDate.ymd(2019, 1, 1), GeneralDate.ymd(2020, 12, 31) )
			,	Helper.createWorkplaceManager( "id_3", GeneralDate.ymd(2022, 1, 1), GeneralDate.ymd(2025, 12, 31) )
				);
				
		val comHistories = Helper.createEmpEnrollPeriodImports(sid, comHistperiods);
		//id3 update
		val workplaceManager = Helper.createWorkplaceManager( "id_3", GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2023, 12, 31) );

		
		new Expectations() {{
			require.getWorkplaceMangager( (String) any, (String) any);
			result = registeredWorkplaceManagers;
			
			require.getEmployeeCompanyHistory( (String) any, (DatePeriod) any );
			result = comHistories;
			
		}};
		
		NtsAssert.atomTask(() -> RegisterWorkplaceManagerService.changePeriod(require, workplaceManager)
				,	any -> require.update(workplaceManager));
		
	}
	
	private static class Helper{
		/**
		 * create list workplaceMananger
		 * @param sid
		 * @param periods
		 * @return
		 */
		public static List<WorkplaceManager> createWorkplaceManagers(String sid, List<DatePeriod> periods) {
			return periods.stream()
					.map(c -> WorkplaceManager.createNew("workplaceId", sid, c))
					.collect(Collectors.toList());
		}
		
		/**
		 * create workplaceManager
		 * @param workplaceManagerId
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		public static WorkplaceManager createWorkplaceManager(String workplaceManagerId, GeneralDate startDate, GeneralDate endDate) {
			return new WorkplaceManager(workplaceManagerId, "workplaceId", "sid", new DatePeriod(startDate, endDate));
		}
		
		/**
		 * create company employee history
		 * @param sid
		 * @param period
		 * @return
		 */
		public static EmpEnrollPeriodImport createEmpEnrollPeriodImport(String sid, DatePeriod period) {
			return new EmpEnrollPeriodImport(sid, period, SecondSituation.ACCEPTING);
		}
		
		/**
		 * create list company employee history
		 * @param sid
		 * @param periods
		 * @return
		 */
		public static List<EmpEnrollPeriodImport> createEmpEnrollPeriodImports(String sid, List<DatePeriod> periods) {
			return periods.stream().map(c -> createEmpEnrollPeriodImport(sid, c)).collect(Collectors.toList());
		}	
	}
	
}
