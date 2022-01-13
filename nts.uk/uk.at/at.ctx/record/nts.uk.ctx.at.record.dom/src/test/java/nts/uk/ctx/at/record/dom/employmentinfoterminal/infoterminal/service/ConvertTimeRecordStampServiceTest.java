package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.CreateStampInfo;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerSerialNo;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminal;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminal.EmpInfoTerminalBuilder;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalName;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.MacAddress;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.ModelEmpInfoTer;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.MonitorIntervalTime;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.NRConvertInfo;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.OutPlaceConvert;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.TimeRecordReqSetting;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.TimeRecordReqSetting.ReqSettingBuilder;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData.StampDataBuilder;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.net.Ipv4Address;

/**
 * @author ThanhNX
 *
 *         データタイムレコードを打刻に変換するTest
 */
@RunWith(JMockit.class)
public class ConvertTimeRecordStampServiceTest {
	private static EmpInfoTerminalCode empInfoTerCode;

	private static ContractCode contractCode;

	private static Optional<EmpInfoTerminal> empInfoTer;

	@Injectable
	private ConvertTimeRecordStampService.Require require;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		empInfoTerCode = new EmpInfoTerminalCode("1");
		contractCode = new ContractCode("1");
	}

	@Before
	public void setUp() throws Exception {
		CreateStampInfo temFix = new CreateStampInfo(
				new NRConvertInfo(new OutPlaceConvert(NotUseAtr.NOT_USE, Optional.of(GoingOutReason.PRIVATE)),
						NotUseAtr.NOT_USE),
				Optional.empty(), Optional.empty());
		
		empInfoTer = Optional.of(new EmpInfoTerminalBuilder(Optional.of(Ipv4Address.parse("192.168.1.1")),
				new MacAddress("AABBCCDD"), new EmpInfoTerminalCode("1"), Optional.of(new EmpInfoTerSerialNo("1")),
				new EmpInfoTerminalName(""), new ContractCode("1"))
						.createStampInfo(temFix)
						.modelEmpInfoTer(ModelEmpInfoTer.NRL_1).intervalTime((new MonitorIntervalTime(1))).build());
	}

	@Test
	public void testEmpInfoTerNoPresent() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "A", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime("1201").build();
		val resultActual = ConvertTimeRecordStampService
				.convertData(require, empInfoTerCode, contractCode, dataNR);
		assertThat(resultActual).isEmpty();

	}

	@Test
	public void testExistHistory() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "A", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime("1201").build();

		Optional<TimeRecordReqSetting> timeRecordReqSetting = Optional
				.of(new ReqSettingBuilder(empInfoTerCode, contractCode, new CompanyId("1"), null, null, null, null).build());

//		Optional<StampRecord> stampRecord = Optional
//				.of(new StampRecord(contractCode, new StampNumber("1"), GeneralDateTime.FAKED_NOW, new StampTypeDisplay("")));
//				Optional.of(new StampRecord(new StampNumber("1"), GeneralDateTime.now(),
//				true, ReservationArt.NONE, Optional.empty()));

		new Expectations() {
			{
				require.getEmpInfoTerminal((EmpInfoTerminalCode) any, (ContractCode) any);
				result = empInfoTer;

//				require.existsStamp((ContractCode) any, (StampNumber) any,
//						(GeneralDateTime) any, (ChangeClockAtr) any);
//				result = true;
	
			}
		};

		val resultActual = ConvertTimeRecordStampService
				.convertData(require, empInfoTerCode, contractCode, dataNR);
		assertThat(resultActual.get().getReflectDate()).isEmpty();

	}

	@Test
	public void testNotExistStampCard() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "A", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime("1201").build();

		Optional<TimeRecordReqSetting> timeRecordReqSetting = Optional
				.of(new ReqSettingBuilder(empInfoTerCode, contractCode, new CompanyId("1"), null, null, null, null).build());

		new Expectations() {
			{
				require.getEmpInfoTerminal((EmpInfoTerminalCode) any, (ContractCode) any);
				result = empInfoTer;
			}
		};

		val resultActual = ConvertTimeRecordStampService
				.convertData(require, empInfoTerCode, contractCode, dataNR);
		assertThat(resultActual.get().getReflectDate()).isEmpty();

	}

	@Test
	public void testRegistDoneData() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "A", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime("1201").build();

		Optional<TimeRecordReqSetting> timeRecordReqSetting = Optional
				.of(new ReqSettingBuilder(empInfoTerCode, contractCode, new CompanyId("1"), null, null, null, null).build());

		new Expectations() {
			{
				require.getEmpInfoTerminal((EmpInfoTerminalCode) any, (ContractCode) any);
				result = empInfoTer;

				require.getByCardNoAndContractCode(contractCode, (StampNumber) any);
				result = Optional.of(new StampCard(contractCode, new StampNumber("1"), "1", GeneralDate.today(), "2"));
				
//				require.existsStamp((ContractCode) any, (StampNumber) any,
//						(GeneralDateTime) any, (ChangeClockAtr) any);
//				result = false;
			}
		};

		val resultActual = ConvertTimeRecordStampService
				.convertData(require, empInfoTerCode, contractCode, dataNR);
//		NtsAssert.atomTask(() -> resultActual.get().getAtomTask(),
//				any -> require.insert((StampRecord) (any.get())), any -> require.insert((Stamp) (any.get())));
	}
}
