package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.RefectActualResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Relieve;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.CreateStampDataForEmployeesService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class CreateStampDataForEmployeesServiceTest {

	@Injectable
	private Require require;
	
	/**
	 * require.getLstStampCardBySidAndContractCd(employeeId) is empty
	 * Throw Msg_433
	 */
	@Test
	public void testCreateStampDataForEmployee_1() {
		ContractCode contractCd =  new ContractCode("DUMMY");//dummy
		String employeeId = "employeeId";//dummy
		Optional<StampNumber> stampNumber =  Optional.ofNullable(new StampNumber(""));
		GeneralDateTime stampDateTime = GeneralDateTime.now();//dummy
		Relieve relieve = StampHelper.getRelieveDefault();//dummy
		ButtonType buttonType = DomainServiceHeplper.getButtonTypeDefault();//dummy
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();//dummy
		Optional<GeoCoordinate> stampLocationInfor = Optional.ofNullable(StampHelper.getGeoCoordinateDefault());//dummy
		
		new Expectations() {
			{
				require.getLstStampCardBySidAndContractCd(employeeId);
			}
		};
		
		NtsAssert.businessException("Msg_433", () -> CreateStampDataForEmployeesService.create(
				require, "", contractCd, employeeId, stampNumber, stampDateTime,
				relieve, buttonType, refActualResults, stampLocationInfor));
	}
	
	/**
	 * require.getLstStampCardBySidAndContractCd(employeeId) not empty
	 * 打刻区分を取得する
	 * ButtonType buttonType.getStampType().isPensent() == true;
	 * positionInfo.isPresent() == true
	 */
	@Test
	public void testCreateStampDataForEmployee_2() {
		
		ContractCode contractCd =  new ContractCode("DUMMY");//dummy
		String employeeId = "employeeId";//dummy
		String cid = "cid";//dummy
		Optional<StampNumber> stampNumber =  Optional.ofNullable(new StampNumber(""));
		GeneralDateTime stampDateTime = GeneralDateTime.now();//dummy
		Relieve relieve = StampHelper.getRelieveDefault();//dummy
		ButtonType buttonType = DomainServiceHeplper.getButtonTypeDefault();//dummy
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();//dummy
		Optional<GeoCoordinate> stampLocationInfor = Optional.ofNullable(StampHelper.getGeoCoordinateDefault());//dummy
		
		
		new Expectations() {
			{
				require.getLstStampCardBySidAndContractCd(employeeId);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
			}
		};
		
		TimeStampInputResult stampDataReflectResult = CreateStampDataForEmployeesService.create(
				require, cid, contractCd, employeeId, stampNumber, stampDateTime,
				relieve, buttonType, refActualResults, stampLocationInfor);
		
		assertThat(stampDataReflectResult.getStampDataReflectResult().getReflectDate().isPresent()).isFalse();
		
		NtsAssert.atomTask(
				() -> stampDataReflectResult.getStampDataReflectResult().getAtomTask(),
				any -> require.insert((StampRecord) any.get()),
				any -> require.insert((Stamp) any.get())
		);
	}
//	
//	/**
//	 * require.getLstStampCardBySidAndContractCd(employeeId) not empty
//	 * 打刻区分を取得する
//	 * ButtonType buttonType.getStampType().isPensent() == true;
//	 * positionInfo.isPresent() == false
//	 */
	@Test
	public void testCreateStampDataForEmployee_3() {
		
		ContractCode contractCd =  new ContractCode("DUMMY");//dummy
		String employeeId = "employeeId";//dummy
		Optional<StampNumber> stampNumber =  Optional.ofNullable(new StampNumber(""));
		GeneralDateTime stampDateTime = GeneralDateTime.now();//dummy
		Relieve relieve = StampHelper.getRelieveDefault();//dummy
		ButtonType buttonType = DomainServiceHeplper.getButtonTypeDefault();//dummy
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();//dummy
		Optional<GeoCoordinate> stampLocationInfor = Optional.ofNullable(null);//dummy
		
		new Expectations() {
			{
				require.getLstStampCardBySidAndContractCd(employeeId);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
			}
		};
		
		TimeStampInputResult stampDataReflectResult = CreateStampDataForEmployeesService.create(
				require, "", contractCd, employeeId, stampNumber, stampDateTime,
				relieve, buttonType, refActualResults, stampLocationInfor);
		
		assertThat(stampDataReflectResult.getStampDataReflectResult().getReflectDate().isPresent()).isFalse();
		
		NtsAssert.atomTask(
				() -> stampDataReflectResult.getStampDataReflectResult().getAtomTask(),
				any -> require.insert((StampRecord) any.get()),
				any -> require.insert((Stamp) any.get())
		);
		
	}
//	
//	/**
//	 * require.getLstStampCardBySidAndContractCd(employeeId) not empty
//	 * 打刻区分を取得する
//	 * ButtonType buttonType.getStampType().isPensent() == false;
//	 */
	@Test
	public void testCreateStampDataForEmployee_4() {
		
		ContractCode contractCd =  new ContractCode("DUMMY");//dummy
		String employeeId = "employeeId";//dummy
		Optional<StampNumber> stampNumber =  Optional.ofNullable(new StampNumber(""));
		GeneralDateTime stampDateTime = GeneralDateTime.now();//dummy
		Relieve relieve = StampHelper.getRelieveDefault();//dummy
		ButtonType buttonType = DomainServiceHeplper.getButtonTypeDefault();//dummy
		RefectActualResult refActualResults = StampHelper.getRefectActualResultDefault();//dummy
		Optional<GeoCoordinate> stampLocationInfor = Optional.ofNullable(StampHelper.getGeoCoordinateDefault());//dummy
		
		new Expectations() {
			{
				require.getLstStampCardBySidAndContractCd(employeeId);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
			}
		};
		
		TimeStampInputResult stampDataReflectResult = CreateStampDataForEmployeesService.create(
				require, "", contractCd, employeeId, stampNumber, stampDateTime,
				relieve, buttonType, refActualResults, stampLocationInfor);
		
		assertThat(stampDataReflectResult.getStampDataReflectResult().getReflectDate().isPresent()).isFalse();
		NtsAssert.atomTask(
				() -> stampDataReflectResult.getStampDataReflectResult().getAtomTask(),
				any -> require.insert((StampRecord) any.get())
		);
	}

}
