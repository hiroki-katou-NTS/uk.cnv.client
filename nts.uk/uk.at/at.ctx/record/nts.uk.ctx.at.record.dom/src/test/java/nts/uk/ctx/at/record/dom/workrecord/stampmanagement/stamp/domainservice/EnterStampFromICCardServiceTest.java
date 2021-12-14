package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.EnterStampForSharedStampService;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.WorkInformationStamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EnterStampFromICCardService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.support.SupportCardNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.OvertimeDeclaration;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;

/**
 * 
 * @author chungnt
 *
 */
@RunWith(JMockit.class)
public class EnterStampFromICCardServiceTest {

	@Injectable
	private Require require;

	@Injectable
	private nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.EnterStampForSharedStampService.Require require1;

	@Test
	public void getters() {
		EnterStampFromICCardService cardService = new EnterStampFromICCardService();
		NtsAssert.invokeGetters(cardService);
	}

	/**
	 * if (!stampCardOpt.isPresent()) { throw new BusinessException("Msg_433"); }
	 */
	@Test
	public void testCreate1() {
		ContractCode contractCode = new ContractCode("DUMMY");
		StampNumber stampNumber = new StampNumber("DUMMY");
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		OvertimeDeclaration declaration = new OvertimeDeclaration(new AttendanceTime(1), new AttendanceTime(1));
		RefectActualResult result = new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
				Optional.of(new WorkLocationCD("workLocationCD")), 
				Optional.of(new SupportCardNumber(9999))),
				new WorkTimeCode("DUMMY"), declaration);
		
		new Expectations() {
			{
				require.getByCardNoAndContractCode(anyString, anyString);
			}
		};

		NtsAssert.businessException("Msg_433", () -> EnterStampFromICCardService.create(require, "", contractCode,
				stampNumber, dateTime, stampButton, result));
	}

	@Test
	public void test_Create() {
		ContractCode contractCode = new ContractCode("DUMMY");
		StampNumber stampNumber = new StampNumber("DUMMY");
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(2));
		OvertimeDeclaration declaration = new OvertimeDeclaration(new AttendanceTime(1), new AttendanceTime(1));
		RefectActualResult result = new RefectActualResult(new WorkInformationStamp(Optional.empty(), Optional.empty(),
				Optional.of(new WorkLocationCD("workLocationCD")), 
				Optional.of(new SupportCardNumber(9999))),
				new WorkTimeCode("DUMMY"), declaration);
		AtomTask atomTask = AtomTask.of(() -> {});// dummy

		TimeStampInputResult inputResult = new TimeStampInputResult(
				new StampDataReflectResult(Optional.of(GeneralDate.today()), atomTask), Optional.of(atomTask));

		new MockUp<EnterStampForSharedStampService>() {
			@Mock
			public TimeStampInputResult create(EnterStampForSharedStampService.Require require, String cid, String conteactCode,
					String employeeID, Optional<StampNumber> StampNumber, Relieve relieve,
					GeneralDateTime stmapDateTime, StampButton stampButton, RefectActualResult refActualResult) {
				return inputResult;
			}
		};

		new Expectations() {
			{
				require.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
				result = Optional.of(new StampCard("000001", "1111101010", "eadf-adf9-adfg4"));

			}
		};

		StampingResultEmployeeId stampingResultEmployeeId = EnterStampFromICCardService.create(require, "", contractCode,
				stampNumber, dateTime, stampButton, result);

		assertThat(stampingResultEmployeeId.employeeId).isEqualTo("eadf-adf9-adfg4");

	}

}
