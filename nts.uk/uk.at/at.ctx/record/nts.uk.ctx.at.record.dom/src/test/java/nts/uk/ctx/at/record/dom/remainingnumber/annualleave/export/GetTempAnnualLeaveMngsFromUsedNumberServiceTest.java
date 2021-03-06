package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveOverNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedPercent;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.CreateAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.primitive.RemainType;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.usenumber.DayNumberOver;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.usenumber.TimeOver;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.AppTimeType;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.DigestionHourlyTimeType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * ??????????????????????????????????????????????????????????????????
 * @author yuri_tamakoshi
 */

@RunWith(JMockit.class)
public class GetTempAnnualLeaveMngsFromUsedNumberServiceTest {

	@Injectable
	private GetAnnualLeaveUsedNumberFromRemDataService.RequireM3 require;

	/** ??????????????????????????????????????????????????? */
	// ??????????????????????????????????????????3.0??????2:00??????
	// ??????????????????
	@Test
	public void AnnLeaMngsFromUsedNumber1(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(3.0, 120, 1.0, null, null); // ???????????????????????? // 3.0??????2:00

		// ??????????????????????????????????????????????????????
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// ??????????????????????????????????????????10.0??????????????????8:00????????????3.0??????????????????2:00????????????7.0???????????????6:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 0d, 0, 0d, 0d, 10.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());

		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		assertThat(tempAnn.get(1).getYmd()).isEqualTo(expect.get(1).getYmd());
		assertThat(tempAnn.get(1).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(1).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(1).getUsedNumber().getUsedTime()).isEqualTo(expect.get(1).getUsedNumber().getUsedTime());

		assertThat(tempAnn.get(2).getYmd()).isEqualTo(expect.get(2).getYmd());
		assertThat(tempAnn.get(2).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(2).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(2).getUsedNumber().getUsedTime()).isEqualTo(expect.get(2).getUsedNumber().getUsedTime());

		assertThat(tempAnn.get(3).getYmd()).isEqualTo(expect.get(3).getYmd());
		assertThat(tempAnn.get(3).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(3).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(3).getUsedNumber().getUsedTime()).isEqualTo(expect.get(3).getUsedNumber().getUsedTime());

		// ??????
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// ????????????????????????10.0??????????????????8:00????????????3.0??????????????????2:00????????????7.0???????????????6:00
		val expect2 = leaveGrantRemainingData(10.0, 480, 3.0, 120, 0d, 0d, 7.0, 360,  null, new BigDecimal(0));

		assertThat(digestion.get(0).getGrantDate()).isEqualTo(expect2.get(0).getGrantDate());
		assertThat(digestion.get(0).getDeadline()).isEqualTo(expect2.get(0).getDeadline());
		assertThat(digestion.get(0).getExpirationStatus()).isEqualTo(expect2.get(0).getExpirationStatus());
		assertThat(digestion.get(0).getRegisterType()).isEqualTo(expect2.get(0).getRegisterType());

		assertThat(digestion.get(0).getDetails().getGrantNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getDays());
		assertThat(digestion.get(0).getDetails().getGrantNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getDays());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getDays());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedPercent()).isEqualTo(expect2.get(0).getDetails().getUsedPercent());

	}

	// ??????????????????????????????????????????0.5??????0:00??????
	@Test
	public void AnnLeaMngsFromUsedNumber2(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(0.5, 0, 3.0, null, null); // ???????????????????????? // 0.5??????0:00

		// ??????????????????????????????????????????????????????
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// ??????????????????????????????????????????10.0??????????????????8:00????????????3.0??????????????????2:00????????????7.0???????????????6:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 0d, 0, 0d, 0d, 10.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs2();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		// ??????
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// ????????????????????????10.0??????????????????8:00????????????0.5??????????????????0:00????????????9.5???????????????8:00
		val expect2 = leaveGrantRemainingData(10.0, 480, 0.5, 0, 0d, 0d, 9.5, 480,  null, new BigDecimal(0));

		assertThat(digestion.get(0).getGrantDate()).isEqualTo(expect2.get(0).getGrantDate());
		assertThat(digestion.get(0).getDeadline()).isEqualTo(expect2.get(0).getDeadline());
		assertThat(digestion.get(0).getExpirationStatus()).isEqualTo(expect2.get(0).getExpirationStatus());
		assertThat(digestion.get(0).getRegisterType()).isEqualTo(expect2.get(0).getRegisterType());

		assertThat(digestion.get(0).getDetails().getGrantNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getDays());
		assertThat(digestion.get(0).getDetails().getGrantNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getDays());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getDays());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedPercent()).isEqualTo(expect2.get(0).getDetails().getUsedPercent());

	}

	// ??????????????????????????????????????????1.5??????1:00??????
	//  ??????????????????????????????????????????
	@Test
	public void AnnLeaMngsFromUsedNumber3(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(1.5, 60, 0d, null, null); // ???????????????????????? // 1.5??????1:00????????????????????????

		// ??????????????????????????????????????????????????????
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// ??????????????????????????????????????????10.0??????????????????8:00????????????9.0??????????????????0:00????????????1.0???????????????8:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 9.0, 0, 0d, 0d, 1.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs3();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		assertThat(tempAnn.get(1).getYmd()).isEqualTo(expect.get(1).getYmd());
		assertThat(tempAnn.get(1).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(1).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(1).getUsedNumber().getUsedTime()).isEqualTo(expect.get(1).getUsedNumber().getUsedTime());

		assertThat(tempAnn.get(2).getYmd()).isEqualTo(expect.get(2).getYmd());
		assertThat(tempAnn.get(2).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(2).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(2).getUsedNumber().getUsedTime()).isEqualTo(expect.get(2).getUsedNumber().getUsedTime());

		// ??????
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// ????????????[0]????????????10.0??????????????????8:00????????????0.0???????????????7:00????????????10.0??????????????????1:00
		// ????????????[1]????????????0.0??????????????????0:00(empty)????????????-0.5???????????????0:00????????????0.5??????????????????0:00(empty)
		val expect2 = leaveGrantRemainingData(10.0, 480, 10.0, 60, 0d, 0d, 0.0, 420,  null, new BigDecimal(0));

		assertThat(digestion.get(0).getGrantDate()).isEqualTo(expect2.get(0).getGrantDate());
		assertThat(digestion.get(0).getDeadline()).isEqualTo(expect2.get(0).getDeadline());
		assertThat(digestion.get(0).getExpirationStatus()).isEqualTo(expect2.get(0).getExpirationStatus());
		assertThat(digestion.get(0).getRegisterType()).isEqualTo(expect2.get(0).getRegisterType());

		assertThat(digestion.get(0).getDetails().getGrantNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getDays());
		assertThat(digestion.get(0).getDetails().getGrantNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getDays());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getDays());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedPercent()).isEqualTo(expect2.get(0).getDetails().getUsedPercent());

	}

	// ????????????4???????????????????????????1.0??????0:00??????
	@Test
	public void AnnLeaMngsFromUsedNumber4(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(1.0, 0, 1.0, null, null); // ???????????????????????? // 3.0??????2:00

		// ??????????????????????????????????????????????????????
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// ??????????????????????????????????????????10.0??????????????????8:00????????????3.0??????????????????2:00????????????7.0???????????????6:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 0d, 0, 0d, 0d, 10.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs1();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		// ??????
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// ????????????????????????10.0??????????????????8:00????????????1.0??????????????????0:00????????????9.0???????????????8:00
		val expect2 = leaveGrantRemainingData(10.0, 480, 1.0, 0, 0d, 0d, 9.0, 480, null,  new BigDecimal(0));

		assertThat(digestion.get(0).getGrantDate()).isEqualTo(expect2.get(0).getGrantDate());
		assertThat(digestion.get(0).getDeadline()).isEqualTo(expect2.get(0).getDeadline());
		assertThat(digestion.get(0).getExpirationStatus()).isEqualTo(expect2.get(0).getExpirationStatus());
		assertThat(digestion.get(0).getRegisterType()).isEqualTo(expect2.get(0).getRegisterType());

		assertThat(digestion.get(0).getDetails().getGrantNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getDays());
		assertThat(digestion.get(0).getDetails().getGrantNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getGrantNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getDays());
		assertThat(digestion.get(0).getDetails().getRemainingNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getRemainingNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getDays()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getDays());
		assertThat(digestion.get(0).getDetails().getUsedNumber().getMinutes()).isEqualTo(expect2.get(0).getDetails().getUsedNumber().getMinutes());
		assertThat(digestion.get(0).getDetails().getUsedPercent()).isEqualTo(expect2.get(0).getDetails().getUsedPercent());

	}

	// ???????????????
	private LeaveUsedNumber usedNumber(double days, Integer minutes, double stowageDays, Double numberOverDays, Integer timeOver) {
		return LeaveUsedNumber.of(
				new LeaveUsedDayNumber(days),
				minutes == null ? Optional.empty() : Optional.of(new LeaveUsedTime(minutes)),
				Optional.of(new LeaveUsedDayNumber(stowageDays)),
				//stowageDays == null ? Optional.empty() : Optional.of(new LeaveUsedTime(stowageDays)),
				Optional.of(LeaveOverNumber.of(
						new DayNumberOver(numberOverDays),
						timeOver == null ? Optional.empty() : Optional.of(new TimeOver(timeOver)))));
	}

	// ???????????????????????????(??????????????????????????????AnnualLeaveGrantRemainingData)
	private List<LeaveGrantRemainingData> leaveGrantRemainingData(double days, Integer minutes, double usedays, Integer useminutes, double stowageDays, double numberOverDays, double remdays, Integer remminutes, Integer timeOver, BigDecimal usedPercent) {
		List<LeaveGrantRemainingData> list = new ArrayList<>();
		list.add(
				LeaveGrantRemainingData.of( "900001",GeneralDate.ymd(2020, 10, 16) , GeneralDate.ymd(2020, 10, 16), LeaveExpirationStatus.AVAILABLE, GrantRemainRegisterType.MONTH_CLOSE,
						details(days, minutes, usedays, useminutes, stowageDays, numberOverDays, remdays, remminutes, timeOver, usedPercent)));
		return list;
	}

	// ???????????????????????????
	private LeaveNumberInfo details(double days, Integer minutes, double usedays, Integer useminutes, double stowageDays, double numberOverDays, double remdays, Integer remminutes, Integer timeOver, BigDecimal usedPercent) {
		return LeaveNumberInfo.of(
				LeaveGrantNumber.of( //?????????
						new LeaveGrantDayNumber(days),
						minutes == null ? Optional.empty() : Optional.of(new LeaveGrantTime(minutes))),
				usedNumber(usedays, useminutes, stowageDays, numberOverDays, 0), // ?????????
				LeaveRemainingNumber.of(// ??????
						new LeaveRemainingDayNumber(remdays),
						remminutes == null ? Optional.empty() : Optional.of(new LeaveRemainingTime(remminutes))),
				new LeaveUsedPercent(usedPercent));	// ?????????
	}

	// ???????????????????????????
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 17), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 18), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 19), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(null, 120), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))) );
	}

	// ???????????????????????????
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs1(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}

	// ???????????????????????????
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs2(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(0.5, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}
	// ???????????????????????????
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs3(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(0.5, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 17), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(null, 60),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}
}
