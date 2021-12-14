package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

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
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.AutoCreateStampCardNumberService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardCreateResult;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.EnterStampForSharedStampService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.TimeStampInputResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSetCommunal;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.TimeStampSetShareTStampHelper;

/**
 * 
 * @author chungnt
 *
 */
//TODO : Chungnt
@RunWith(JMockit.class)
public class EnterStampForSharedStampServiceTest {

	@Injectable
	private Require require;
	
	/**
	 *  require.gets(); isPresent()
	 *  result : BusinessException("Msg_1632");
	 */
	@Test
	public void test_setShareTStamp_null() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		StampNumber stampNumber = new StampNumber("DUMMY");
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		
		Relieve relieve = new Relieve(AuthcMethod.ID_AUTHC, StampMeans.INDIVITION);
		
		new Expectations() {
			{
				require.gets();
				result = Optional.empty();
			}
		};
		
		NtsAssert.businessException("Msg_1632", 
				() -> EnterStampForSharedStampService.create(require ,"", contractCode, employeeId, Optional.of(stampNumber),
				relieve, dateTime, stampButton , null));
		
	}
	
	/**
	 *  require.gets(); isPresent()
	 *  result : BusinessException("Msg_1632");
	 */
	@Test
	public void test_buttonSetting_null() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		StampNumber stampNumber = new StampNumber("DUMMY");
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		Relieve relieve = new Relieve(AuthcMethod.ID_AUTHC, StampMeans.INDIVITION);
		
		StampSetCommunal timeStampSetShareTStamp = TimeStampSetShareTStampHelper.get_list_empty();
		
		new Expectations() {
			{
				require.gets();
				result = Optional.of(timeStampSetShareTStamp);
			}
		};
		
		NtsAssert.businessException("Msg_1632", () -> EnterStampForSharedStampService.create(require ,"", contractCode, employeeId, Optional.of(stampNumber),
				relieve, dateTime, stampButton , null));
		
	}
	
	/**
	 *  return CreateStampDataForEmployeesService.create(require, new ContractCode(conteactCode), employeeID,
	 *  StampNumber, stmapDateTime, relieve, buttonSetting.get().getButtonType(), refActualResult, null);
	 */
	@Test
	public void test() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		StampNumber stampNumber = new StampNumber("DUMMY");
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		Relieve relieve = new Relieve(AuthcMethod.ID_AUTHC, StampMeans.INDIVITION);
		
		StampSetCommunal timeStampSetShareTStamp = TimeStampSetShareTStampHelper.getDefault();
		
		new Expectations() {
			{
				require.gets();
				result = Optional.of(timeStampSetShareTStamp);
			}
		};
		
		AtomTask atomTask = AtomTask.of(() -> {});// dummy
		StampCardCreateResult stampCardCreateResult = new StampCardCreateResult("1", Optional.of(atomTask));
		
		new MockUp<AutoCreateStampCardNumberService>() {
			@Mock
			public Optional<StampCardCreateResult> create(AutoCreateStampCardNumberService.Require require,
					String employeeID, StampMeans stampMeanss) {
				return Optional.of(stampCardCreateResult);
			}
		}; 
		
		TimeStampInputResult timeStampInputResult = EnterStampForSharedStampService.create(require ,"", contractCode, employeeId, Optional.of(stampNumber),
				relieve, dateTime, stampButton , null);
		
		assertThat(timeStampInputResult.at).isNotEmpty();
		assertThat(timeStampInputResult.stampDataReflectResult.getAtomTask()).isNotNull();
	}
}
