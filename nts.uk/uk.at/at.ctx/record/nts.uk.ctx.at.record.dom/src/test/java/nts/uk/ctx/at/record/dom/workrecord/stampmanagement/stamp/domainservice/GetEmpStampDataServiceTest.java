package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetEmpStampDataService.Require;

/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class GetEmpStampDataServiceTest {
	
	@Injectable
	private Require require;

	/**
	 * require.getListStampCard(employeeId) is empty()
	 */
	@Test
	public void testGetEmpStampDataService_1() {
		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.today();
		
		new Expectations() {
			{
				require.getListStampCard(employeeId);
				result = new ArrayList<>();
				
			}
		};
		
		assertThat(GetEmpStampDataService.get(require, employeeId, date)).isNotPresent();
	}
	
//	/**
//	 * require.getListStampCard(employeeId) not empty()
//	 * require.getStampRecord(listCard, date) is empty()
//	 */
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testGetEmpStampDataService_2() {
//		String employeeId = "employeeId";
//		GeneralDate date = GeneralDate.today();
//		new Expectations() {
//			{
//				require.getListStampCard(anyString);
//				result = Arrays.asList(
//						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()));
//				result = new ArrayList<>();
//			}
//		};
//		
//		assertThat(GetEmpStampDataService.get(require, employeeId, date)).isNotPresent();
//	}
	
	/**
	 * require.getListStampCard(employeeId) not empty()
	 * require.getStampRecord(listCard, date) not empty()
	 * require.getStamp(listCard, date) is empty()
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetEmpStampDataService_3() {
		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = new ArrayList<>();
			}
		};
		
		assertThat(GetEmpStampDataService.get(require, employeeId, date)).isPresent();
	}
	
	/**
	 * require.getListStampCard(employeeId) not empty()
	 * require.getStampRecord(listCard, date) not empty()
	 * require.getStamp(listCard, date) not empty()
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetEmpStampDataService_4() {
		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.today();
		new Expectations() {
			{
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampDefault());
			}
		};
		
		assertThat(GetEmpStampDataService.get(require, employeeId, date)).isPresent();
	}

}
