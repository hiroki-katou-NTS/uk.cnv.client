package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.calculationattribute.enums.DivergenceTimeAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalFlexOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.deviationtime.AutoCalcSetOfDivergenceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.HTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.OTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.TimeItemTypeAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.WTCalSettingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;

public class BPTimeItemSettingTest {

	@Test
	public void testGetters() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.CALCULATION, TimeItemTypeAtr.NORMAL_TYPE);
		NtsAssert.invokeGetters(target);
	}
	
	@Test
	public void isCalc_CalAttrOfDailyAttd_True() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //全て計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.WithinWorkTime, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_CalAttrOfDailyAttd_False() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //全て計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.WithinWorkTime, CalAttrOfDailyAttd.defaultData()); //全て計算しない
		assertThat(result).isFalse();
	}
	
	@Test
	public void isCalc_WithinWorkTime() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.NON_CALCULATION, WTCalSettingAtr.CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //就内のみ計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.WithinWorkTime, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_HolidayWork() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.CALCULATION, OTCalSettingAtr.NON_CALCULATION, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //休出のみ計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.HolidayWork, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_EarlyWork() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業のみ計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.EarlyWork, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_OverTimeWork() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業のみ計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.OverTimeWork, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_StatutoryOverTimeWork() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.CALCULATION, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業のみ計算する
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.StatutoryOverTimeWork, helper.calcAll()); //全て計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_HolidayWork_FOLLOW_HOLIDAY_SETTING() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.FOLLOW_HOLIDAY_SETTING, OTCalSettingAtr.NON_CALCULATION, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //休出の自動計算の設定に従う
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.HolidayWork, helper.calcOnlyHolidayWork()); //休出のみ計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_EarlyWork_FOLLOW_OVERTIME_SETTING() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.FOLLOW_OVERTIME_SETTING, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業の自動計算の設定に従う
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.EarlyWork, helper.calcOnlyEarlyWork()); //早出残業のみ計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_OverTimeWork_FOLLOW_OVERTIME_SETTING() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.FOLLOW_OVERTIME_SETTING, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業の自動計算の設定に従う
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.OverTimeWork, helper.calcOnlyOverTimeWork()); //普通残業のみ計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_StatutoryOverTimeWork_FOLLOW_OVERTIME_SETTING() {
		BPTimeItemSetting target  = new BPTimeItemSetting("companyId", 1,
				HTCalSettingAtr.NON_CALCULATION, OTCalSettingAtr.FOLLOW_OVERTIME_SETTING, WTCalSettingAtr.NON_CALCULATION, TimeItemTypeAtr.NORMAL_TYPE); //残業の自動計算の設定に従う
		boolean result = target.isCalc(ActualWorkTimeSheetAtr.StatutoryOverTimeWork, helper.calcOnlyStatutoryOverTimeWork()); //法内残業のみ計算する
		assertThat(result).isTrue();
	}
	
	private static class helper {
		private static CalAttrOfDailyAttd calcAll() {
			AutoCalFlexOvertimeSetting flexExcessTime = new AutoCalFlexOvertimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS));
			AutoCalRaisingSalarySetting rasingSalarySetting = new AutoCalRaisingSalarySetting(true, true);
			AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS));
			AutoCalOvertimeSetting overtimeSetting = new AutoCalOvertimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS));
			AutoCalcOfLeaveEarlySetting leaveEarlySetting = new AutoCalcOfLeaveEarlySetting(true, true);
			AutoCalcSetOfDivergenceTime divergenceTime = new AutoCalcSetOfDivergenceTime(DivergenceTimeAttr.USE);
			return new CalAttrOfDailyAttd(flexExcessTime, rasingSalarySetting, holidayTimeSetting, overtimeSetting, leaveEarlySetting, divergenceTime);
		}
		
		private static CalAttrOfDailyAttd calcOnlyHolidayWork() {
			AutoCalRaisingSalarySetting rasingSalarySetting = new AutoCalRaisingSalarySetting(true, true);
			AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER));
			return new CalAttrOfDailyAttd(null, rasingSalarySetting, holidayTimeSetting, null, null, null);
		}
		
		private static CalAttrOfDailyAttd calcOnlyEarlyWork() {
			AutoCalRaisingSalarySetting rasingSalarySetting = new AutoCalRaisingSalarySetting(true, true);
			AutoCalOvertimeSetting overtimeSetting = new AutoCalOvertimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER));
			return new CalAttrOfDailyAttd(null, rasingSalarySetting, null, overtimeSetting, null, null);
		}
		
		private static CalAttrOfDailyAttd calcOnlyOverTimeWork() {
			AutoCalRaisingSalarySetting rasingSalarySetting = new AutoCalRaisingSalarySetting(true, true);
			AutoCalOvertimeSetting overtimeSetting = new AutoCalOvertimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER));
			return new CalAttrOfDailyAttd(null, rasingSalarySetting, null, overtimeSetting, null, null);
		}
		
		private static CalAttrOfDailyAttd calcOnlyStatutoryOverTimeWork() {
			AutoCalRaisingSalarySetting rasingSalarySetting = new AutoCalRaisingSalarySetting(true, true);
			AutoCalOvertimeSetting overtimeSetting = new AutoCalOvertimeSetting(
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS),
					new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER));
			return new CalAttrOfDailyAttd(null, rasingSalarySetting, null, overtimeSetting, null, null);
		}
	}
}
