package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.i18n.TextResource;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class StampTest {

	@Test
	public void getters() {
		Stamp stamp = StampHelper.getStampDefault();
		NtsAssert.invokeGetters(stamp);
	}

	/**
	 * Test C1 初回打刻データを作成する
	 */
	
	@Test
	public void testStamp_contructor_C1() {
		StampNumber cardNumber = new StampNumber("cardNumber");//dummy
		GeneralDateTime stampDateTime = GeneralDateTime.now();
		Relieve relieve =  StampHelper.getRelieveDefault();
		StampType type = StampHelper.getStampTypeDefault();
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();
		Optional<GeoCoordinate> locationInfor = Optional.ofNullable(StampHelper.getGeoCoordinateDefault()) ;
		ContractCode contactCode = new ContractCode("DUMMY");
		Stamp stamp = new Stamp(contactCode, cardNumber, stampDateTime, relieve, type, refActualResults, locationInfor);
		assertThat(stamp.getImprintReflectionStatus().isReflectedCategory()).isFalse();
		NtsAssert.invokeGetters(stamp);
	}
	
	/**
	 * Test AttendanceTime
	 */
	@Test
	public void testSetAttendanceTime() {
		AttendanceTime attendanceTime = new AttendanceTime(10);//dummy
		Stamp stamp = StampHelper.getStampDefault();
		stamp.setAttendanceTime(attendanceTime);
		assertThat(stamp.getAttendanceTime().get()).isEqualTo(attendanceTime);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case1 
	 * ①打刻の年月日 = 処理中年月日
	 * 時刻はそのまま
	 */
	@Test
	public void testConvertToAttendanceStamp0() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 3);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 0, 0, 10);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(0);
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case1 
	 * ①打刻の年月日 = 処理中年月日
	 * 時刻+8:20:10
	 */
	@Test
	public void testConvertToAttendanceStamp1() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 3);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 8, 20, 10);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(500); // 8h20 => 500p
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case2 
	 * ②打刻の年月日 = 処理中年月日の翌日
	 * 時刻+24:00 +8:20:10
	 */
	@Test
	public void testConvertToAttendanceStamp2() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 2);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 8, 20, 10);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(1940); // = 24*60 + 8*60+20 
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case2.1 
	 * ②打刻の年月日 = 処理中年月日の翌日
	 * 時刻+26:00
	 */
	@Test
	public void testConvertToAttendanceStamp21() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 2);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 2, 0, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(1560); // = 26*60
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case2.2 
	 * ②打刻の年月日 = 処理中年月日の翌日
	 * 時刻+26:30
	 */
	@Test
	public void testConvertToAttendanceStamp22() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 2);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 2, 30, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(1590); // = 26*60 + 30
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case3 
	 * ②打刻の年月日 = 処理中年月日の翌日
	 * 時刻+48:00 +8:20:00
	 */
	@Test
	public void testConvertToAttendanceStamp3() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 2);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 4, 8, 20, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(2880+500); // = 48*60 + 8*60+20
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case3.1 
	 * ②打刻の年月日 = 処理中年月日の翌日
	 * 時刻+48:30
	 */
	@Test
	public void testConvertToAttendanceStamp31() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 2);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 4, 0, 30, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(2880+30); // = 48*60 + 30
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case4
	 * ④打刻の年月日 = 処理中年月日の前日
	 * 時刻ー24:00
	 */
	@Test
	public void testConvertToAttendanceStamp4() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 4);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 0, 0, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(-1440); // = 24*60 
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	/**
	 * Test func convertToAttendanceStamp
	 * case4.1
	 * ④打刻の年月日 = 処理中年月日の前日
	 * 時刻ー2:00
	 */
	@Test
	public void testConvertToAttendanceStamp41() {
		GeneralDate dateInput = GeneralDate.ymd(2021, 1, 4);
		GeneralDateTime stampDateTime = GeneralDateTime.ymdhms(2021, 1, 3, 22, 0, 0);
		
		Relieve relieve =  StampHelper.getRelieveDefault();
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null);
		
		// result
		WorkTimeInformation wt = stamp.convertToAttendanceStamp(dateInput);
		assertThat(wt.getTimeWithDay().get().valueAsMinutes()).isEqualTo(-120); // = 2*60 
		assertThat(wt.getReasonTimeChange().getTimeChangeMeans()).isEqualTo(TimeChangeMeans.REAL_STAMP);
		assertThat(wt.getReasonTimeChange().getEngravingMethod().get()).isEqualTo(EngravingMethod.TIME_RECORD_ID_INPUT);
	}
	
	@Test
	public void testC2() {
		
		StampNumber cardNumber = new StampNumber("cardNumber");//dummy
		GeneralDateTime stampDateTime = GeneralDateTime.now();
		Relieve relieve =  StampHelper.getRelieveDefault();
		StampType type = StampHelper.getStampTypeDefault();
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();
		Optional<GeoCoordinate> locationInfor = Optional.ofNullable(StampHelper.getGeoCoordinateDefault()) ;
		ContractCode contactCode = new ContractCode("DUMMY");
//		StampTypeDisplay stampTypeDisplay = new StampTypeDisplay("DUMMY");
		Stamp stamp = new Stamp(contactCode, cardNumber, stampDateTime, relieve, type, refActualResults, locationInfor);
		//assertThat(stamp.isReflectedCategory()).isFalse();
		NtsAssert.invokeGetters(stamp);
	}

	// return TextResource.localize("KDP011_35");
	
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
        		ChangeClockAtr.valueOf(3), //dummy
        		ChangeCalArt.valueOf(4));//dummy
        
        Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_35"));
		
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
        		ChangeClockAtr.valueOf(3), //dummy
        		ChangeCalArt.valueOf(4));//dummy
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_36"));
	}
	
	/**
	 * this.changeCalArt == ChangeCalArt.EARLY_APPEARANCE && this.changeClockArt.equals(ChangeClockAtr.GOING_TO_WORK)
	 */
	@Test
	public void testCreateStampTypeDisplay_3(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_37");
            	result =  "KDP011_37";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockAtr.valueOf(0), //dummy
        		ChangeCalArt.valueOf(1));//dummy
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_37"));
	}
	
	/**
	 * this.changeCalArt == ChangeCalArt.EARLY_APPEARANCE && this.changeClockArt.equals(ChangeClockAtr.START_OF_SUPPORT
	 */
	@Test
	public void testCreateStampTypeDisplay_4(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_40");
            	result =  "KDP011_40";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockAtr.valueOf(6), //dummy
        		ChangeCalArt.valueOf(1));//dummy
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_40"));
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt == ChangeCalArt.EARLY_APPEARANCE
	 */
	@Test
	public void testCreateStampTypeDisplay_5(@Mocked final TextResource tr) {
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
        		ChangeClockAtr.valueOf(3), //dummy
        		ChangeCalArt.valueOf(1));
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo("退門(公用)+早出+KDP011_39");
	}
	
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt == ChangeCalArt.BRARK
	 */
	@Test
	public void testCreateStampTypeDisplay_6(@Mocked final TextResource tr) {
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
        		ChangeClockAtr.valueOf(3), //dummy
        		ChangeCalArt.valueOf(3));
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_38"));
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
	public void testCreateStampTypeDisplay_7() {
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		false, 
        		null, 
        		SetPreClockArt.valueOf(0),
        		ChangeClockAtr.valueOf(3), //dummy
        		ChangeCalArt.valueOf(0));
		String stampAtr = stampType.getChangeClockArt().nameId;
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr);
	}
	
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt != null
//	 * changeCalArt == ChangeCalArt.NONE
//	 * changeHalfDay = false;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_8() {
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//        		false, 
//        		GoingOutReason.valueOf(1), //dummy
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(0));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")");
//	}
	
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt == null
//	 * changeCalArt != ChangeCalArt.NONE
//	 * changeHalfDay = false;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_9() {
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//        		false, 
//        		null,
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(2));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "+" + stampType.getChangeCalArt().nameId);
//	}
	/**
	 * setPreClockArt == SetPreClockArt.NONE
	 * changeCalArt != ChangeCalArt.BRARK
	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
	 * 
	 * goOutArt == null
	 * changeCalArt == ChangeCalArt.NONE
	 * changeHalfDay = true;
	 */
//	@Test
//	public void testCreateStampTypeDisplay_10(@Mocked final TextResource tr) {
//		new Expectations() {
//            {
//            	TextResource.localize("KDP011_39");
//            	result =  "KDP011_39";
//            }
//        };
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//        		true, 
//        		null,
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(0));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "+" + TextResource.localize("KDP011_39"));
//	}
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt != null
//	 * changeCalArt != ChangeCalArt.NONE
//	 * changeHalfDay = false;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_11() {
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//        		false, 
//        		GoingOutReason.valueOf(1),//dummy
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(2));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"+ "+" + stampType.getChangeCalArt().nameId);
//	}
//	
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt != null
//	 * changeCalArt == ChangeCalArt.NONE
//	 * changeHalfDay = true;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_12(@Mocked final TextResource tr) {
//		new Expectations() {
//            {
//            	TextResource.localize("KDP011_39");
//            	result =  "KDP011_39";
//            }
//        };
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//				true, 
//        		GoingOutReason.valueOf(1),//dummy
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(0));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"+ "+" + TextResource.localize("KDP011_39"));
//	}
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt == null
//	 * changeCalArt != ChangeCalArt.NONE
//	 * changeHalfDay = true;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_13(@Mocked final TextResource tr) {
//		new Expectations() {
//            {
//            	TextResource.localize("KDP011_39");
//            	result =  "KDP011_39";
//            }
//        };
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//				true, 
//        		null,
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(2));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "+" + stampType.getChangeCalArt().nameId+ "+" + TextResource.localize("KDP011_39"));
//	}
	
//	/**
//	 * setPreClockArt == SetPreClockArt.NONE
//	 * changeCalArt != ChangeCalArt.BRARK
//	 * changeCalArt != ChangeCalArt.EARLY_APPEARANCE
//	 * 
//	 * goOutArt != null
//	 * changeCalArt != ChangeCalArt.NONE
//	 * changeHalfDay = true;
//	 */
//	@Test
//	public void testCreateStampTypeDisplay_14(@Mocked final TextResource tr) {
//		StampType stampType = StampHelper.getStampTypeHaveInput(
//        		true, 
//        		GoingOutReason.valueOf(1),//dummy
//        		SetPreClockArt.valueOf(0),
//        		ChangeClockAtr.valueOf(3), //dummy
//        		ChangeCalArt.valueOf(2));
//		String stampAtr = stampType.getChangeClockArt().nameId;
//		
//		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
//		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(stampAtr+ "(" + stampType.getGoOutArt().get().nameId + ")"
//				+ "+" + stampType.getChangeCalArt().nameId 
//				+ "+" + TextResource.localize("KDP011_39") );
//	}
	
	/**
	 * this.changeCalArt == ChangeCalArt.BRARK && this.changeClockArt.equals(ChangeClockAtr.GOING_TO_WORK)
	 */
	@Test
	public void testCreateStampTypeDisplay_15(@Mocked final TextResource tr) {
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
        		ChangeClockAtr.valueOf(0), //dummy
        		ChangeCalArt.valueOf(3));//dummy
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_38"));
	}
	
	/**
	 * this.changeCalArt == ChangeCalArt.BRARK && this.changeClockArt.equals(ChangeClockAtr.START_OF_SUPPORT)
	 */
	@Test
	public void testCreateStampTypeDisplay_16(@Mocked final TextResource tr) {
		new Expectations() {
            {
            	TextResource.localize("KDP011_41");
            	result =  "KDP011_41";
            }
        };
		StampType stampType = StampHelper.getStampTypeHaveInput(
        		true, //dummy
        		GoingOutReason.valueOf(1), //dummy
        		SetPreClockArt.valueOf(0),
        		ChangeClockAtr.valueOf(6), //dummy
        		ChangeCalArt.valueOf(3));//dummy
		
		Stamp stamp = new Stamp(null, null, null, null, stampType, null, null);
		assertThat(stamp.createStampDivisionDisplayed()).isEqualTo(TextResource.localize("KDP011_41"));
	}
}
