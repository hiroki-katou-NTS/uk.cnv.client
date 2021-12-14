package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.EngravingMethod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
/**
 * 
 * @author tutk
 *
 */
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
		Stamp stamp = new Stamp(contactCode, cardNumber, stampDateTime, relieve, type, refActualResults, locationInfor, "DUMMY");
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		Stamp stamp = new Stamp(null, null, stampDateTime, relieve, null, null, null, "DUMMY");
		
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
		StampTypeDisplay stampTypeDisplay = new StampTypeDisplay("DUMMY");
		StampRecord stampRecord = new StampRecord(contactCode, cardNumber, stampDateTime, stampTypeDisplay);
		Stamp stamp = new Stamp(stampRecord, relieve, type, refActualResults, locationInfor);
		//assertThat(stamp.isReflectedCategory()).isFalse();
		NtsAssert.invokeGetters(stamp);
	}

}
