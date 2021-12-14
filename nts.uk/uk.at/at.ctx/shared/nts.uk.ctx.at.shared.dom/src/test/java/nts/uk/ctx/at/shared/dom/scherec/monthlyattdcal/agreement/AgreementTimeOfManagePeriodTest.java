package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.converter.MonthlyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.SettingRequiredByReg;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.AgreementOneMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting.BasicAgreementSettingForCalc;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.OutsideOTCalMed;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.OutsideOTSetting;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.UseClassification;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.breakdown.BreakdownItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.breakdown.OutsideOTBRDItem;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.breakdown.ProductNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.holiday.PremiumExtra60HRate;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.holiday.PremiumRate;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.outsideot.overtime.OvertimeNo;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameNo;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRole;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSet;
import nts.uk.shr.com.context.AppContexts;

public class AgreementTimeOfManagePeriodTest {

	@Injectable
	private AgreementTimeOfManagePeriod.RequireM2 require;
	
	private String sid = "sid";
	private GeneralDate ymd = GeneralDate.today();
	private YearMonth ym = ymd.yearMonth();
	private String cid = "cid";
	private DatePeriod period = new DatePeriod(GeneralDate.ymd(ym, 1), GeneralDate.ymd(ym, ym.lastDateInMonth()));

	private BasicAgreementSettingForCalc agreementSet = new BasicAgreementSettingForCalc(
			new BasicAgreementSetting(new AgreementOneMonth(
						OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(50 * 60), 
								new AgreementOneMonthTime(40* 60)), new AgreementOneMonthTime(60* 60)), 
						OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(80* 60), 
								new AgreementOneMonthTime(75* 60)), new AgreementOneMonthTime(100* 60))), 
					null, null, null), false);

	private BasicAgreementSettingForCalc agreementSetWithEmpSet = new BasicAgreementSettingForCalc(
			new BasicAgreementSetting(new AgreementOneMonth(
						OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(50 * 60), 
								new AgreementOneMonthTime(40* 60)), new AgreementOneMonthTime(60* 60)), 
						OneMonthTime.createWithCheck(OneMonthErrorAlarmTime.of(new AgreementOneMonthTime(80* 60), 
								new AgreementOneMonthTime(75* 60)), new AgreementOneMonthTime(100* 60))), 
					null, null, null), true);
	
	List<OutsideOTBRDItem> osItems = Arrays.asList(new OutsideOTBRDItem(UseClassification.UseClass_Use, 
			BreakdownItemNo.ONE, null, ProductNumber.ONE, Arrays.asList(35),
			Arrays.asList(new PremiumExtra60HRate(new PremiumRate(1), OvertimeNo.ONE))));
	
	OutsideOTSetting otSet = new OutsideOTSetting(cid, null, osItems,
			OutsideOTCalMed.TIME_SERIES, new ArrayList<>(), Optional.empty());
	
	Map<Integer, WorkdayoffFrame> holWorkRole = new HashMap<Integer, WorkdayoffFrame>(){{
		put(1, new WorkdayoffFrame(cid, new WorkdayoffFrameNo(BigDecimal.ONE), NotUseAtr.USE, null, null, WorkdayoffFrameRole.STATUTORY_HOLIDAYS));
		put(2, new WorkdayoffFrame(cid, new WorkdayoffFrameNo(new BigDecimal(2)), NotUseAtr.USE, null, null, WorkdayoffFrameRole.NON_STATUTORY_HOLIDAYS));
	}};
	
	Map<Integer, WorkdayoffFrame> holWorkRoleWithMix = new HashMap<Integer, WorkdayoffFrame>(){{
		put(1, new WorkdayoffFrame(cid, new WorkdayoffFrameNo(new BigDecimal(1)), NotUseAtr.USE, null, null, WorkdayoffFrameRole.MIX_WITHIN_OUTSIDE_STATUTORY));
		put(2, new WorkdayoffFrame(cid, new WorkdayoffFrameNo(new BigDecimal(2)), NotUseAtr.USE, null, null, WorkdayoffFrameRole.NON_STATUTORY_HOLIDAYS));
	}};
	
	@Mocked
	private WorkType workType;
	@Mocked
	private WorkTypeSet workTypeSet;
	@Mocked
	private MonthlyCalculation monthlyCalc;
	@Mocked
	private SettingRequiredByReg settingRequiredByReg;
	
	@Injectable
	private MonthlyRecordToAttendanceItemConverter converter;
	
    @Mocked
    AppContexts appContexts;
	
    @Before
    public void init() {
        new Expectations() {{
            AppContexts.user().companyId();
            result = cid;
        }};
    }
    
    private List<IntegrationOfDaily> getDailyWithHoliWork(int holiWork) {
    	List<IntegrationOfDaily> dailyRecords = new ArrayList<>();
    	int day = 1;
    	while (true) {

        	int hwTime = holiWork > 48 ? 48 : holiWork;
    		
        	val attendanceTime = new AttendanceTimeOfDailyAttendance(null, 
        			new ActualWorkingTimeOfDaily(null, null, null, new TotalWorkingTime(null, null, null, null, 
        					new ExcessOfStatutoryTimeOfDaily(null, null, Optional.of(new HolidayWorkTimeOfDaily(null, 
        							Arrays.asList(new HolidayWorkFrameTime(new HolidayWorkFrameNo(1), 
        									Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(hwTime * 60), new AttendanceTime(hwTime * 60))),
        									Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0))), null),
        									new HolidayWorkFrameTime(new HolidayWorkFrameNo(2), 
        	    									Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(hwTime * 60), new AttendanceTime(hwTime * 60))),
        	    									Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0), new AttendanceTime(0))), null)), null, null))), null, null, null, null, null, null, null, null, null, null), null, null), null, null, null);
        	dailyRecords.add(
        			new IntegrationOfDaily(sid, GeneralDate.ymd(ym, day), new WorkInfoOfDailyAttendance(new WorkInformation("aa", "aa"), null, null, null, null, null, null),
        					null, null, null, null, null, null, Optional.of(attendanceTime), null, null, null, null, null, null, null, null, null));
    		
        	if (holiWork > 48) {
        		holiWork -= 48;
        		day++;
        	} else {
        		break;
        	}
    	}
    	return dailyRecords;
    }

	@SuppressWarnings("unchecked")
	private void defaultExpect(int overTime, int holidayWork,
			BasicAgreementSettingForCalc agreementSet,
			Map<Integer, WorkdayoffFrame> workdayoffFrame) {
		
		List<ItemValue> itemValues = Arrays.asList(ItemValue.builder().itemId(35).valueType(ValueType.TIME).value(overTime * 60));
		
		new Expectations() {{
			require.outsideOTSetting(cid);
			result = Optional.of(otSet);
			
			require.monthRoundingSet(cid);
			result = Optional.empty();
			
			require.createMonthlyConverter();
			result = converter;
			
			converter.withAttendanceTime((AttendanceTimeOfMonthly) any);
			
			converter.convert((List<Integer>) any);
			result = itemValues;
			
			require.basicAgreementSetting(cid, sid, ym, ymd);
			result = agreementSet;
			
			monthlyCalc.getWorkingSystem();
			result = WorkingSystem.REGULAR_WORK;
            
            monthlyCalc.getSettingsByReg();
            result = settingRequiredByReg;
            
            settingRequiredByReg.getRoleHolidayWorkFrameMap();
            result = workdayoffFrame;
            
            monthlyCalc.getProcPeriod();
            result = period;
            
            monthlyCalc.getEmployeeId();
            result = sid;
            
            monthlyCalc.getMonthlyCalculatingDailys().getDailyWorks(sid);
            result = getDailyWithHoliWork(holidayWork);
		}};
	}
	
	private void defaultExpect(int overTime, int holidayWork,
			BasicAgreementSettingForCalc agreementSet,
			Map<Integer, WorkdayoffFrame> workdayoffFrame, HolidayAtr holidayAtr) {
		
		defaultExpect(overTime, holidayWork, agreementSet, workdayoffFrame);
		
		new Expectations() {{
            
            require.workType(cid, anyString);
            result = Optional.of(workType);
            
            workType.getWorkTypeSet();
            result = workTypeSet;
            
            workTypeSet.getHolidayAtr();
            result = holidayAtr;
		}};
	}

	@Test
	public void test1() {
		
		new Expectations() {{
			require.outsideOTSetting(cid);
			result = Optional.empty();
		}};
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("時間外超過設定が取得できないイケース")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
	}
	
	@Test
	public void test2() {
		int overTime = 1;
		int holidayWork = 0;
		
		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月アラームより小さい")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test3() {
		int overTime = 42;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月アラーム以上エラーより小さい")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test4() {
		int overTime = 55;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月エラー以上上限より小さい")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test5() {
		int overTime = 70;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月上限以上特例アラームより小さい")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}

	@Test
	public void test6() {
		int overTime = 76;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例アラーム以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test7() {
		int overTime = 82;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例エラー以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test8() {
		int overTime = 102;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例上限以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test9() {
		int overTime = 1;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定1か月アラーム以下")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test10() {
		int overTime = 1;
		int holidayWork = 41;
		
		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本アラームより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test11() {
		int overTime = 1;
		int holidayWork = 54;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);

		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本エラーより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test12() {
		int overTime = 1;
		int holidayWork = 69;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本上限より大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}

	@Test
	public void test13() {
		int overTime = 1;
		int holidayWork = 75;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定1か月特例アラーム以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}

	@Test
	public void test14() {
		int overTime = 1;
		int holidayWork = 81;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定1か月特例エラー以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test15() {
		int overTime = 1;
		int holidayWork = 101;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在するケース　かつ　36協定1か月特例上限以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test16() {
		int overTime = 1;
		int holidayWork = 0;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, holWorkRole);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月アラーム以下　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL_SPECIAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test17() {
		int overTime = 42;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());

		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月アラーム以上エラー以下　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM_SP);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test18() {
		int overTime = 55;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月エラー以上上限以下　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test19() {
		int overTime = 70;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月上限以上特例アラーム以下　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR_SP);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}

	@Test
	public void test20() {
		int overTime = 76;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例アラーム以上　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test21() {
		int overTime = 82;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例エラー以上　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test22() {
		int overTime = 102;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSetWithEmpSet, new HashMap<>());
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("法定休出が存在しないケース　かつ　36協定1か月特例上限以上　かつ　社員設定あり")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test23() {
		int overTime = 1;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定1か月アラーム以下")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test24() {
		int overTime = 1;
		int holidayWork = 41;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本アラームより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test25() {
		int overTime = 1;
		int holidayWork = 54;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);

		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本エラーより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test26() {
		int overTime = 1;
		int holidayWork = 69;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本上限より大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}

	@Test
	public void test27() {
		int overTime = 1;
		int holidayWork = 75;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定1か月特例アラーム以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}

	@Test
	public void test28() {
		int overTime = 1;
		int holidayWork = 81;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定1か月特例エラー以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test29() {
		int overTime = 1;
		int holidayWork = 101;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法内)ケース　かつ　36協定1か月特例上限以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + holidayWork*60);
	}
	
	@Test
	public void test30() {
		int overTime = 1;
		int holidayWork = 1;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定1か月アラーム以下")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test31() {
		int overTime = 1;
		int holidayWork = 41;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本アラームより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test32() {
		int overTime = 1;
		int holidayWork = 54;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);

		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本エラーより大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test33() {
		int overTime = 1;
		int holidayWork = 69;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定対象時間は小さい、法定上限時間が大きい（基本上限より大きい）")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}

	@Test
	public void test34() {
		int overTime = 1;
		int holidayWork = 75;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定1か月特例アラーム以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}

	@Test
	public void test35() {
		int overTime = 1;
		int holidayWork = 81;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定1か月特例エラー以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
	
	@Test
	public void test36() {
		int overTime = 1;
		int holidayWork = 101;

		defaultExpect(overTime, holidayWork, agreementSet, holWorkRoleWithMix, HolidayAtr.NON_STATUTORY_HOLIDAYS);
		
		val result = AgreementTimeOfManagePeriod.aggregate(require, sid, ymd, ym, monthlyCalc);
		
		assertThat(result.getStatus()).as("休出枠混在　かつ　法定休出が存在する(法外)ケース　かつ　36協定1か月特例上限以上")
				.isEqualTo(AgreementTimeStatusOfMonthly.NORMAL);
		
		assertThat(result.getBreakdown().calcAgreementTime().valueAsMinutes()).isEqualTo(overTime*60);
		
		assertThat(result.getBreakdown().calcLegalLimitTime().valueAsMinutes()).isEqualTo(overTime*60 + 0*60);
	}
}
