package nts.uk.ctx.at.schedule.dom.shift.management.workavailability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.GetUsingShiftTableRuleOfEmployeeService;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityRule;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;
@SuppressWarnings("static-access")
@RunWith(JMockit.class)
public class RegisterWorkAvailabilityTest {
	@Injectable
	RegisterWorkAvailability.Require require;	
	
	@Injectable
	WorkAvailability.Require workRequire;
	
	@Mocked 
	private ShiftTableRule shiftRule;
	
	@Mocked 
	private WorkAvailabilityRule setting;
	
	@Mocked
	private GetUsingShiftTableRuleOfEmployeeService service;
	
	private DatePeriod datePeriod;
	
	private List<WorkAvailabilityOfOneDay> workOneDays = new ArrayList<>();
	
	@Before
	public void createDatePeriod(){
		this.datePeriod = new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 31));
	}
	
	@Before
	public void createWorkAvaiOfOneDays() {
		ShiftMasterCode shiftMasterCode = new ShiftMasterCode("001");
		
		new Expectations() {
			{
				workRequire.shiftMasterIsExist(shiftMasterCode);
				result = true;
			}
		};

		this.workOneDays = Helper.createWorkAvaiOfOneDays(workRequire, shiftMasterCode);
		
	}
	/**
	 * input : シフト表のルール = empty
	 * excepted: Msg_2049
	 */
	@Test
	public void testCheckError_throw_Msg_2049() {
		
		new Expectations() {
			{
				service.get(require, (String) any, (GeneralDate) any);
			}
		};

		NtsAssert.businessException("Msg_2049",
				() -> RegisterWorkAvailability.register(require, workOneDays, this.datePeriod));
	}

	/**
	 * input : シフト表のルール.勤務希望運用区分 ==しない									
	 * excepted: Msg_2052
	 */
	@Test
	public void testCheckError_throw_Msg_2052() {
		
		new Expectations() {{
			service.get(require, (String) any, (GeneralDate) any);
			result = Optional.of(shiftRule);
			
			shiftRule.getUseWorkAvailabilityAtr();
			result = NotUseAtr.NOT_USE;
		}};

		NtsAssert.businessException("Msg_2052",
				() -> RegisterWorkAvailability.register(require, workOneDays, this.datePeriod));
	}
	
	/**
	 * input : シフト表のルール.シフト表の設定.締切日を過ぎているか= true
	 * excepted: Msg_2050
	 */
	@Test
	public void testCheckError_throw_Msg_2050() {
		
		new Expectations() {
			{			
				service.get(require, (String) any, (GeneralDate) any);
				result = Optional.of(shiftRule);
				
				shiftRule.getShiftTableSetting();
				result = Optional.of(setting);
				
				setting.isOverDeadline(workOneDays.get(0).getWorkAvailabilityDate());
				result = true;
			}
		};

		NtsAssert.businessException("Msg_2050",
				() -> RegisterWorkAvailability.register(require, workOneDays, this.datePeriod));
	}
	
	/**
	 * input: 希望＝休日,  フト表のルール.シフト表の設定.休日日数の上限日数を超えているか = true
	 * excepted: Msg_2051
	 */
	@Test
	public void testCheckError_throw_Msg_2051() {
		
		List<WorkAvailabilityOfOneDay> workOneDays = Helper.createWorkAvaiHolidays(workRequire);
		
		new Expectations() {{
			service.get(require, (String) any, (GeneralDate) any);
			result = Optional.of(shiftRule);
				
		    shiftRule.getShiftTableSetting();
			result = Optional.of(setting);
		
			setting.isOverHolidayMaxDays(require, workOneDays);
			result = true;
				
		}};

		NtsAssert.businessException("Msg_2051",
				() -> RegisterWorkAvailability.register(require, workOneDays, this.datePeriod));
	}
	
	
	@Test
	public void testRegister_success_1() {
		
		new Expectations() {
			{				
				service.get(require, (String) any, (GeneralDate) any);
				result = Optional.of(shiftRule);
				
				shiftRule.getShiftTableSetting();
				result = Optional.of(setting);
				
				setting.isOverHolidayMaxDays(require, workOneDays);
				result = false;
				
			}
		};
		
		NtsAssert.atomTask(
				() -> RegisterWorkAvailability.register(
						require,
						workOneDays,
						this.datePeriod), 
				any -> require.deleteAllWorkAvailabilityOfOneDay(workOneDays.get(0).getEmployeeId(), this.datePeriod),
				any -> require.insertAllWorkAvailabilityOfOneDay(workOneDays));
	}
	

	public static class Helper {
		/**
		 * 希望＝シフト
		 * @return
		 */
		public static List<WorkAvailabilityOfOneDay> createWorkAvaiOfOneDays(@Injectable WorkAvailability.Require require, ShiftMasterCode shiftMasterCode) {
			return Arrays.asList(
					WorkAvailabilityOfOneDay.create(require, "sid", GeneralDate.today(), new WorkAvailabilityMemo("memo"),
							AssignmentMethod.SHIFT, Arrays.asList(shiftMasterCode), Collections.emptyList()));
		}
		
		/**
		 *  希望＝休日
		 * @return
		 */
		public static List<WorkAvailabilityOfOneDay> createWorkAvaiHolidays(@Injectable WorkAvailability.Require require) {
			return Arrays.asList(
					WorkAvailabilityOfOneDay.create(require, "sid", GeneralDate.today(), new WorkAvailabilityMemo("memo"),
							AssignmentMethod.HOLIDAY, Collections.emptyList(), Collections.emptyList()));
		}

	}
}
