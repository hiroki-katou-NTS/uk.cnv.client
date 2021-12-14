package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.i18n.TextResource;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class StampTypeTest {
	
	@Injectable
	private StampType.Require require;

	@Test
	public void getters() {
		StampType stampType = StampHelper.getStampTypeDefault();
		NtsAssert.invokeGetters(stampType);
	}
	
	@Test
	public void testStampType() {
		boolean changeHalfDay = true;
		GoingOutReason goOutArt = GoingOutReason.valueOf(1);
		SetPreClockArt setPreClockArt = SetPreClockArt.valueOf(1);
		ChangeClockArt changeClockArt = ChangeClockArt.valueOf(3);
		ChangeCalArt changeCalArt = ChangeCalArt.valueOf(4);
		StampType stampType = new StampType(changeHalfDay, goOutArt, setPreClockArt, changeClockArt, changeCalArt);
		NtsAssert.invokeGetters(stampType);
	}
    @Test
    public void testMethodinClassTextResource() {
    	new MockUp<TextResource>() {
            @Mock
            public String localize(String resourceId, String... params) {
            	return resourceId;
            }
        };
    }
	
	/**
	 * setPreClockArt == SetPreClockArt.DIRECT
	 */
	@Test
	public void testCreateStampTypeDisplay_1(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_35");
            	result =  "KDP011_35";
            }
        };
        StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1),//dummy 
        		SetPreClockArt.valueOf(1),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(4));//dummy
		
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(TextResource.localize("KDP011_35"));
		
	}
	/**
	 * setPreClockArt == SetPreClockArt.BOUNCE
	 */
	@Test
	public void testCreateStampTypeDisplay_2(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_36");
            	result =  "KDP011_36";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(2),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(4));//dummy
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(TextResource.localize("KDP011_36"));
	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt == ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCreateStampTypeDisplay_3(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_39");
            	result =  "KDP011_39";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(1));
		assertThat(stampType.createStampTypeDisplay()).isEqualTo("退門(公用)+早出+KDP011_39");
	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt == ChangeCalArt.BRARK
	 */
	@Test
	public void testCreateStampTypeDisplay_4(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_38");
            	result =  "KDP011_38";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(3));
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(TextResource.localize("KDP011_38"));
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt == null
	 * changeCalArt == ChangeCalArt.NONE
	 * changeHalfDay = false;
	 */
	@Test
	public void testCreateStampTypeDisplay_5() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		false, 
        		null, 
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(0));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr);
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt != null
	 * changeCalArt == ChangeCalArt.NONE
	 * changeHalfDay = false;
	 */
	@Test
	public void testCreateStampTypeDisplay_6() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		false, 
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(0));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")");
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt == null
	 * changeCalArt != ChangeCalArt.NONE
	 * changeHalfDay = false;
	 */
	@Test
	public void testCreateStampTypeDisplay_7() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		false, 
        		null,
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "+" + stampType.getChangeCalArt().nameId);
	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt == null
	 * changeCalArt == ChangeCalArt.NONE
	 * changeHalfDay = true;
	 */
	@Test
	public void testCreateStampTypeDisplay_8(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_39");
            	result =  "KDP011_39";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, 
        		null,
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(0));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "+" + TextResource.localize("KDP011_39"));
	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt != null
	 * changeCalArt != ChangeCalArt.NONE
	 * changeHalfDay = false;
	 */
	@Test
	public void testCreateStampTypeDisplay_9() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		false, 
        		GoingOutReason.valueOf(1),//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"+ "+" + stampType.getChangeCalArt().nameId);
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt != null
	 * changeCalArt == ChangeCalArt.NONE
	 * changeHalfDay = true;
	 */
	@Test
	public void testCreateStampTypeDisplay_10(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_39");
            	result =  "KDP011_39";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
				true, 
        		GoingOutReason.valueOf(1),//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(0));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"+ "+" + TextResource.localize("KDP011_39"));
	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt == null
	 * changeCalArt != ChangeCalArt.NONE
	 * changeHalfDay = true;
	 */
	@Test
	public void testCreateStampTypeDisplay_11(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_39");
            	result =  "KDP011_39";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
				true, 
        		null,
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "+" + stampType.getChangeCalArt().nameId+ "+" + TextResource.localize("KDP011_39"));
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt != null
	 * changeCalArt != ChangeCalArt.NONE
	 * changeHalfDay = true;
	 */
	@Test
	public void testCreateStampTypeDisplay_12(@Mocked final TextResource tr) {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, 
        		GoingOutReason.valueOf(1),//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));
		String stampAtr = stampType.getChangeClockArt().nameId;
		assertThat(stampType.createStampTypeDisplay()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"
				+ "+" + stampType.getChangeCalArt().nameId 
				+ "+" + TextResource.localize("KDP011_39") );
	}
	
	/**
	 * changeHalfDay == true;
	 */
	@Test
	public void testCheckBookAuto_1() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				true, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),//dummy
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));//dummy
		assertThat(stampType.checkBookAuto()).isFalse();
	}
	/**
	 * changeHalfDay = false;
	 * setPreClockArt != SetPreClockArt.NONE
	 */
	@Test
	public void testCheckBookAuto_2() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(1),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));//dummy
		assertThat(stampType.checkBookAuto()).isFalse();
	}
	
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt != ChangeClockArt.GOING_TO_WORK
	 * changeClockArt != ChangeClockArt.START_OF_SUPPORT  
	 * changeCalArt != ChangeCalArt.NONE
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCheckBookAuto_3() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(3), //dummy
        		ChangeCalArt.valueOf(2));
		assertThat(stampType.checkBookAuto()).isFalse();
	}
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt == ChangeClockArt.GOING_TO_WORK 
	 * changeCalArt == ChangeCalArt.NONE
	 */
	@Test
	public void testCheckBookAuto_4() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(0), 
        		ChangeCalArt.valueOf(0));
		assertThat(stampType.checkBookAuto()).isTrue();
	}
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt == ChangeClockArt.GOING_TO_WORK 
	 * changeCalArt == ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCheckBookAuto_5() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(0), 
        		ChangeCalArt.valueOf(1));
		assertThat(stampType.checkBookAuto()).isTrue();
	}
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt == ChangeClockArt.START_OF_SUPPORT 
	 * changeCalArt == ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCheckBookAuto_6() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(6), 
        		ChangeCalArt.valueOf(1));
		assertThat(stampType.checkBookAuto()).isTrue();
	}
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt == ChangeClockArt.START_OF_SUPPORT 
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCheckBookAuto_7() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(6), 
        		ChangeCalArt.valueOf(3));
		assertThat(stampType.checkBookAuto()).isFalse();
	}
	
	/**
	 * changeHalfDay == false;
	 * setPreClockArt == SetPreClockArt.NONE
	 * 
	 * changeClockArt == ChangeClockArt.GOING_TO_WORK
	 * changeCalArt != ChangeCalArt.NONE
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCheckBookAuto_8() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, 
        		null,//dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockArt.valueOf(0), 
        		ChangeCalArt.valueOf(2));
		assertThat(stampType.checkBookAuto()).isFalse();
	}
	
	/**
	 * if 勤務種類を取得する is empty
	 */
	@Test
	public void testChangeWorkOnHolidays_1() {
		StampType stampType = StampHelper.getStampTypeDefault();
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER);
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER);
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		new Expectations() {
			{
				require.findByPK(workTypeCode);
			}
		};
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isFalse();
	}
	
	/**
	 * if 勤務種類を取得する not empty
	 * 一日休日かを確認する not holiday
	 */
	@Test
	public void testChangeWorkOnHolidays_2() {
		StampType stampType = StampHelper.getStampTypeDefault();
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER);//dummy
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER);//dummy
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		WorkType wt = new WorkType("companyId", new WorkTypeCode(workTypeCode), new ArrayList<>());
		new Expectations() {
			{
				require.findByPK(workTypeCode);
				result = Optional.of(wt);
			}
		};
		
		new MockUp<WorkType>() {
			@Mock
			public boolean isHoliday(){
				return false;
			}
		};
		
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isFalse();
	}
	
	/**
	 * if 勤務種類を取得する not empty
	 * 一日休日かを確認する is holiday
	 * 日別勤怠の計算区分の休出時間の計算区分を確認する  == 打刻から計算する
	 */
	@Test
	public void testChangeWorkOnHolidays_3() {
		StampType stampType = StampHelper.getStampTypeDefault();
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,//dummy
				AutoCalAtrOvertime.CALCULATEMBOSS);
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,////dummy 
				AutoCalAtrOvertime.APPLYMANUALLYENTER);////dummy
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		WorkType wt = new WorkType("companyId", new WorkTypeCode(workTypeCode), new ArrayList<>());
		new Expectations() {
			{
				require.findByPK(workTypeCode);
				result = Optional.of(wt);
			}
		};
		
		new MockUp<WorkType>() {
			@Mock
			public boolean isHoliday(){
				return true;
			}
		};
		
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isTrue();
	}
	
	/**
	 * if 勤務種類を取得する not empty
	 * 一日休日かを確認する is holiday
	 * 日別勤怠の計算区分の休出時間の計算区分を確認する  == 申請または手入力
	 */
	@Test
	public void testChangeWorkOnHolidays_4() {
		StampType stampType = StampHelper.getStampTypeDefault();
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,//dummy
				AutoCalAtrOvertime.APPLYMANUALLYENTER);
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,////dummy 
				AutoCalAtrOvertime.APPLYMANUALLYENTER);////dummy
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		WorkType wt = new WorkType("companyId", new WorkTypeCode(workTypeCode), new ArrayList<>());
		new Expectations() {
			{
				require.findByPK(workTypeCode);
				result = Optional.of(wt);
			}
		};
		
		new MockUp<WorkType>() {
			@Mock
			public boolean isHoliday(){
				return true;
			}
		};
		
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isFalse();
	}
	
	/**
	 * if 勤務種類を取得する not empty
	 * 一日休日かを確認する is holiday
	 * 日別勤怠の計算区分の休出時間の計算区分を確認する  == タイムレコーダーで選択
	 * 打刻の計算区分変更対象を確認する == 休出
	 */
	@Test
	public void testChangeWorkOnHolidays_5() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, //dummy
        		null,//dummy
        		SetPreClockArt.valueOf(0),//dummy
        		ChangeClockArt.valueOf(0), //dummy
        		ChangeCalArt.valueOf(3));
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,//dummy
				AutoCalAtrOvertime.TIMERECORDER);
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,////dummy 
				AutoCalAtrOvertime.APPLYMANUALLYENTER);////dummy
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		WorkType wt = new WorkType("companyId", new WorkTypeCode(workTypeCode), new ArrayList<>());
		new Expectations() {
			{
				require.findByPK(workTypeCode);
				result = Optional.of(wt);
			}
		};
		
		new MockUp<WorkType>() {
			@Mock
			public boolean isHoliday(){
				return true;
			}
		};
		
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isTrue();
	}
	
	/**
	 * if 勤務種類を取得する not empty
	 * 一日休日かを確認する is holiday
	 * 日別勤怠の計算区分の休出時間の計算区分を確認する  == タイムレコーダーで選択
	 * 打刻の計算区分変更対象を確認する != 休出
	 */
	@Test
	public void testChangeWorkOnHolidays_6() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
				false, //dummy
        		null,//dummy
        		SetPreClockArt.valueOf(0),//dummy
        		ChangeClockArt.valueOf(0), //dummy
        		ChangeCalArt.valueOf(2));
		String workTypeCode = "workType";
		
		AutoCalSetting restTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,//dummy
				AutoCalAtrOvertime.TIMERECORDER);
		AutoCalSetting lateNightTime = new AutoCalSetting(TimeLimitUpperLimitSetting.INDICATEDYIMEUPPERLIMIT,////dummy 
				AutoCalAtrOvertime.APPLYMANUALLYENTER);////dummy
		AutoCalRestTimeSetting holidayTimeSetting = new AutoCalRestTimeSetting(restTime, lateNightTime);
		
		WorkType wt = new WorkType("companyId", new WorkTypeCode(workTypeCode), new ArrayList<>());
		new Expectations() {
			{
				require.findByPK(workTypeCode);
				result = Optional.of(wt);
			}
		};
		
		new MockUp<WorkType>() {
			@Mock
			public boolean isHoliday(){
				return true;
			}
		};
		
		boolean result = stampType.changeWorkOnHolidays(require, holidayTimeSetting, workTypeCode);
		assertThat(result).isFalse();
	}
	

}
