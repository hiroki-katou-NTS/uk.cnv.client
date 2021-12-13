package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampHelper;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetStampTypeToSuppressService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;

/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class GetStampTypeToSuppressServiceTest {

	@Injectable
	private Require require;
	
	/**
	 * stampMeans != StampMeans.INDIVITION;
	 */
	@Test
	public void testGetStampTypeToSuppressService_1() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.PORTAL;
		new Expectations() {
			{
				require.getPotalSettings();
				result = Optional.empty();
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isFalse();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() is empty
	 */
	@Test
	public void testGetStampTypeToSuppressService_2() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
			}
		};	
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isFalse();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == false
	 */
	@Test
	public void testGetStampTypeToSuppressService_3() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY_buttonEmphasisArt_false);
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isFalse();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,
				GeneralDate.today()) is empty
	 */
	@Test
	public void testGetStampTypeToSuppressService_4() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
			}
		};
		NtsAssert.businessException("Msg_430", () -> GetStampTypeToSuppressService.get(require, employeeId, stampMeans));
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime is empty	
	 */
	@Test
	public void testGetStampTypeToSuppressService_5() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeIsNull());
				
			}
		};
		NtsAssert.businessException("Msg_1142", () -> GetStampTypeToSuppressService.get(require, employeeId, stampMeans));
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) is empty
	 * 
	 */
	@Test
	public void testGetStampTypeToSuppressService_6() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				
			}
		};
		NtsAssert.businessException("Msg_1142", () -> GetStampTypeToSuppressService.get(require, employeeId, stampMeans));
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() < startDateClock.v()
	 * 
	 */
	@Test
	public void testGetStampTypeToSuppressService_7() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(2000));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isFalse();
		assertThat(stampToSuppress.isDeparture()).isTrue();
		assertThat(stampToSuppress.isGoOut()).isTrue();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * 
	 */
	@Test
	public void testGetStampTypeToSuppressService_8() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isFalse();
		assertThat(stampToSuppress.isDeparture()).isTrue();
		assertThat(stampToSuppress.isGoOut()).isTrue();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * list Stamp not null
	 * have stamp :ChangeClockArt = ChangeClockArt.GOING_TO_WORK
	 * have stamp :ChangeClockArt = ChangeClockArt.GO_OUT
	 * have stamp :ChangeClockArt = ChangeClockArt.RETURN
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetStampTypeToSuppressService_9() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
//				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
//				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampByChangeClockArt("stampCardId1",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now()),
						StampHelper.getStampByChangeClockArt("stampCardId2",ChangeClockAtr.GO_OUT,GeneralDateTime.now().addMinutes(3)),
						StampHelper.getStampByChangeClockArt("stampCardId3",ChangeClockAtr.RETURN,GeneralDateTime.now().addMinutes(4)),
						StampHelper.getStampByChangeClockArt("stampCardId4",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(5)));
				
			}
		};
		
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		
		assertThat(stampToSuppress.isGoingToWork()).isTrue();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isTrue();
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * list Stamp not null
	 * have stamp :ChangeClockArt = ChangeClockArt.GOING_TO_WORK
	 * have stamp :ChangeClockArt = ChangeClockArt.GO_OUT
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetStampTypeToSuppressService_10() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
//				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
//				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampByChangeClockArt("stampCardId1",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(3)),
						StampHelper.getStampByChangeClockArt("stampCardId2",ChangeClockAtr.GO_OUT,GeneralDateTime.now().addMinutes(5)),
						StampHelper.getStampByChangeClockArt("stampCardId4",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(7)));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isTrue();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isTrue();
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * list Stamp not null
	 * have stamp :ChangeClockArt = ChangeClockArt.GO_OUT
	 * have stamp :ChangeClockArt = ChangeClockArt.RETURN
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetStampTypeToSuppressService_11() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
//				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
//				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(
						StampHelper.getStampByChangeClockArt("stampCardId2",ChangeClockAtr.GO_OUT,GeneralDateTime.now().addMinutes(5)),
						StampHelper.getStampByChangeClockArt("stampCardId3",ChangeClockAtr.RETURN,GeneralDateTime.now().addMinutes(4)));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isTrue();
		assertThat(stampToSuppress.isDeparture()).isTrue();
		assertThat(stampToSuppress.isGoOut()).isTrue();
		assertThat(stampToSuppress.isTurnBack()).isFalse();
	}
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * list Stamp not null
	 * have stamp :ChangeClockArt = ChangeClockArt.GOING_TO_WORK
	 * have stamp :ChangeClockArt = ChangeClockArt.RETURN
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetStampTypeToSuppressService_12() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
//				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
//				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampByChangeClockArt("stampCardId1",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(5)),
						StampHelper.getStampByChangeClockArt("stampCardId3",ChangeClockAtr.RETURN,GeneralDateTime.now().addMinutes(7)),
						StampHelper.getStampByChangeClockArt("stampCardId4",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(9)));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isTrue();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isTrue();
	}
	
	/**
	 * stampMeans == StampMeans.INDIVITION;
	 * require.getStampSet() not empty
	 * 
	 * 打刻ボタンを抑制する 
	 * buttonEmphasisArt == true
	 * 
	 * require.findWorkConditionByEmployee(employeeId,GeneralDate.today()) not empty
	 * WorkTime not empty	
	 * 
	 * require.findByWorkTimeCode(workTimeCode.get().v()) not empty
	 * clockHourMinute.v() > startDateClock.v()
	 * list Stamp not null
	 * have stamp :ChangeClockArt = ChangeClockArt.GOING_TO_WORK
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetStampTypeToSuppressService_13() {
		String employeeId = "employeeId";//dummy
		StampMeans stampMeans = StampMeans.INDIVITION;
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(StampSettingPersonHelper.DUMMY);
				
				require.findWorkConditionByEmployee(anyString,
						(GeneralDate) any);
				result = Optional.of(DomainServiceHeplper.getWorkingCondWorktimeNotNull());
				
				require.findByWorkTimeCode(anyString);
				result = Optional.of(DomainServiceHeplper.getPredetemineTimeSettingByTime(1));
				
				require.getListStampCard(anyString);
				result = Arrays.asList(
						StampHelper.getStampCardByInput("stampCardId1", "stampNumber1", GeneralDate.today()),
						StampHelper.getStampCardByInput("stampCardId2", "stampNumber2",
								GeneralDate.today().addDays(-10)),
						StampHelper.getStampCardByInput("stampCardId3", "stampNumber3",
								GeneralDate.today().addDays(-3)));
//				require.getStampRecord((List<StampNumber>) any, (GeneralDate) any);
//				result = Arrays.asList(StampRecordHelper.getStampRecord());
				
				require.getStamp((List<StampNumber>) any, (GeneralDate) any);
				result = Arrays.asList(StampHelper.getStampByChangeClockArt("stampCardId1",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(3)),
						StampHelper.getStampByChangeClockArt("stampCardId4",ChangeClockAtr.GOING_TO_WORK,GeneralDateTime.now().addMinutes(5)));
				
			}
		};
		StampToSuppress stampToSuppress = GetStampTypeToSuppressService.get(require, employeeId, stampMeans);
		assertThat(stampToSuppress.isGoingToWork()).isTrue();
		assertThat(stampToSuppress.isDeparture()).isFalse();
		assertThat(stampToSuppress.isGoOut()).isFalse();
		assertThat(stampToSuppress.isTurnBack()).isTrue();
	}

}
