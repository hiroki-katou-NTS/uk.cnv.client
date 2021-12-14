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
 * 付与残数データから使用数を消化する（テスト）
 * @author yuri_tamakoshi
 */

@RunWith(JMockit.class)
public class GetTempAnnualLeaveMngsFromUsedNumberServiceTest {

	@Injectable
	private GetAnnualLeaveUsedNumberFromRemDataService.RequireM3 require;

	/** 付与残数データから使用数を消化する */
	// パターン１：月別実績の使用数3.0日と2:00の時
	// ※消化できる
	@Test
	public void AnnLeaMngsFromUsedNumber1(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(3.0, 120, 1.0, null, null); // 月別実績の使用数 // 3.0日と2:00

		// 使用数を暫定年休管理データに変換する
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// 年休付与残数データ：付与日数10.0日、付与時間8:00、使用数3.0日、使用時間2:00、残日数7.0日、残時間6:00
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

		// 消化
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// 期待値：付与日数10.0日、付与時間8:00、使用数3.0日、使用時間2:00、残日数7.0日、残時間6:00
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

	// パターン２：月別実績の使用数0.5日と0:00の時
	@Test
	public void AnnLeaMngsFromUsedNumber2(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(0.5, 0, 3.0, null, null); // 月別実績の使用数 // 0.5日と0:00

		// 使用数を暫定年休管理データに変換する
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// 年休付与残数データ：付与日数10.0日、付与時間8:00、使用数3.0日、使用時間2:00、残日数7.0日、残時間6:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 0d, 0, 0d, 0d, 10.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs2();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		// 消化
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// 期待値：付与日数10.0日、付与時間8:00、使用数0.5日、使用時間0:00、残日数9.5日、残時間8:00
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

	// パターン３：月別実績の使用数1.5日と1:00の時
	//  ※使い過ぎで消化できないとき
	@Test
	public void AnnLeaMngsFromUsedNumber3(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(1.5, 60, 0d, null, null); // 月別実績の使用数 // 1.5日と1:00　使い過ぎの場合

		// 使用数を暫定年休管理データに変換する
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// 年休付与残数データ：付与日数10.0日、付与時間8:00、使用数9.0日、使用時間0:00、残日数1.0日、残時間8:00
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

		// 消化
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// 期待値：[0]付与日数10.0日、付与時間8:00、残日数0.0日、残時間7:00、使用数10.0日、使用時間1:00
		// 期待値：[1]付与日数0.0日、付与時間0:00(empty)、残日数-0.5日、残時間0:00、使用数0.5日、使用時間0:00(empty)
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

	// パターン4：月別実績の使用数1.0日と0:00の時
	@Test
	public void AnnLeaMngsFromUsedNumber4(){

		String companyId = "0001";
		String employeeId = "000001";
		LeaveUsedNumber usedNumber = usedNumber(1.0, 0, 1.0, null, null); // 月別実績の使用数 // 3.0日と2:00

		// 使用数を暫定年休管理データに変換する
		val  tempAnn =GetTempAnnualLeaveMngsFromUsedNumberService.tempAnnualLeaveMngs(employeeId, usedNumber);

		// 年休付与残数データ：付与日数10.0日、付与時間8:00、使用数3.0日、使用時間2:00、残日数7.0日、残時間6:00
		val remainingData = leaveGrantRemainingData(10.0, 480, 0d, 0, 0d, 0d, 10.0, 480, null, new BigDecimal(0));

		val expect = tempAnnualLeaveMngs1();
		assertThat(tempAnn.get(0).getYmd()).isEqualTo(expect.get(0).getYmd());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedDayNumber()).isEqualTo(expect.get(0).getUsedNumber().getUsedDayNumber());
		assertThat(tempAnn.get(0).getUsedNumber().getUsedTime()).isEqualTo(expect.get(0).getUsedNumber().getUsedTime());

		// 消化
		val digestion = GetAnnualLeaveUsedNumberFromRemDataService.getAnnualLeaveGrantRemainingData(companyId, employeeId, remainingData, usedNumber, require);

		// 期待値：付与日数10.0日、付与時間8:00、使用数1.0日、使用時間0:00、残日数9.0日、残時間8:00
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

	// 休暇使用数
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

	// 休暇付与残数データ(年休付与残数データ：AnnualLeaveGrantRemainingData)
	private List<LeaveGrantRemainingData> leaveGrantRemainingData(double days, Integer minutes, double usedays, Integer useminutes, double stowageDays, double numberOverDays, double remdays, Integer remminutes, Integer timeOver, BigDecimal usedPercent) {
		List<LeaveGrantRemainingData> list = new ArrayList<>();
		list.add(
				LeaveGrantRemainingData.of( "900001",GeneralDate.ymd(2020, 10, 16) , GeneralDate.ymd(2020, 10, 16), LeaveExpirationStatus.AVAILABLE, GrantRemainRegisterType.MONTH_CLOSE,
						details(days, minutes, usedays, useminutes, stowageDays, numberOverDays, remdays, remminutes, timeOver, usedPercent)));
		return list;
	}

	// 休暇数情報（明細）
	private LeaveNumberInfo details(double days, Integer minutes, double usedays, Integer useminutes, double stowageDays, double numberOverDays, double remdays, Integer remminutes, Integer timeOver, BigDecimal usedPercent) {
		return LeaveNumberInfo.of(
				LeaveGrantNumber.of( //付与数
						new LeaveGrantDayNumber(days),
						minutes == null ? Optional.empty() : Optional.of(new LeaveGrantTime(minutes))),
				usedNumber(usedays, useminutes, stowageDays, numberOverDays, 0), // 使用数
				LeaveRemainingNumber.of(// 残数
						new LeaveRemainingDayNumber(remdays),
						remminutes == null ? Optional.empty() : Optional.of(new LeaveRemainingTime(remminutes))),
				new LeaveUsedPercent(usedPercent));	// 使用率
	}

	// 暫定年休管理データ
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 17), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 18), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK))) ),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 19), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(null, 120), Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))) );
	}

	// 暫定年休管理データ
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs1(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}

	// 暫定年休管理データ
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs2(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(0.5, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}
	// 暫定年休管理データ
	private List<TempAnnualLeaveMngs> tempAnnualLeaveMngs3(){
		return Arrays.asList(
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(1.0, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 16), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(0.5, null),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))),
				TempAnnualLeaveMngs.of("000001","900001", GeneralDate.ymd(2020, 10, 17), CreateAtr.RECORD, RemainType.ANNUAL, RemainAtr.SINGLE, new WorkTypeCode("1"), new TempAnnualLeaveUsedNumber(null, 60),  Optional.ofNullable(DigestionHourlyTimeType.of(false, Optional.of(AppTimeType.OFFWORK)))));
	}
}
