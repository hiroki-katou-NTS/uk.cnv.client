package nts.uk.ctx.sys.portal.dom.toppagealarm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDateTime;

public class ToppageAlarmDataTest {

	/**
	 * Getter test
	 */
	@Test
	public void getters() {
		// then
		NtsAssert.invokeGetters(ToppageAlarmDataHelper.mockUnResolveDomain());
	}

	/**
	 * Builder test
	 */
	@Test
	public void testBuilder() {
		String domainBuilder = ToppageAlarmData.builder().toString();
		assertThat(domainBuilder.isEmpty()).isFalse();
	}
	
	/*
	 * [1] 解消済みに状態を変更する test
	 */
	@Test
	public void changeResolvedStatusTest() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper.mockUnResolveDomain();
		
		//when
		domain.changeResolvedStatus();
		
		//then
		assertThat(domain.getIsResolved()).isTrue();
	}
	
	/**
	 * [2] 発生日時を更新する test
	 */
	@Test
	public void updateOccurrenceDateTimeTest() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper.mockResolvedDomain();
		
		//when
		domain.updateOccurrenceDateTime(GeneralDateTime.FAKED_NOW);
		
		//then
		assertThat(domain.getIsResolved()).isFalse();
		assertThat(domain.getOccurrenceDateTime()).isEqualTo(GeneralDateTime.FAKED_NOW);
		assertThat(domain.getReadDateTime()).isEmpty();
	}
	
	/**
	 * [3] 部下の社員IDを変更する 
	 * 表示社員区分 = 上長
	 * domain.SubSids : 1,2,3,4
	 * 部下のエラーがある員ID = 1,2,3,5
	 * 部下のエラーがない社員ID == empty
	 */
	@Test
	public void changeSubSidsTest() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(Arrays.asList("1", "2", "3", "4"), DisplayAtr.SUPERIOR);
		List<String> newSubSid = Stream.of("1", "2", "3", "5").collect(Collectors.toList());
		List<String> noErrSids = Collections.emptyList();
		
		//when
		domain.changeSubSids(newSubSid, noErrSids);
		
		//then
		assertThat(domain.getSubSids()).isEqualTo(Arrays.asList("1", "2", "3", "4", "5"));
	}
	
	/**
	 * [3] 部下の社員IDを変更する 
	 * 表示社員区分 != 上長
	 * 部下のエラーがある員ID = 1,2
	 * 部下のエラーがない社員ID == empty
	 */
	@Test
	public void changeSubSidsTest2() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper.mockDomainWithDispAtr(DisplayAtr.PRINCIPAL);
		List<String> newSubSid = Stream.of("1", "2").collect(Collectors.toList());
		List<String> noErrSids = Collections.emptyList();
		
		//when
		domain.changeSubSids(newSubSid, noErrSids);
		
		//then
		assertThat(domain.getSubSids()).isEmpty();
	}
	
	/**
	 * [3] 部下の社員IDを変更する 
	 * domain.SubSids : 1,2,3,4
	 * 表示社員区分 = 上長
	 * 部下のエラーがある員ID = 1,2,3,5
	 * 部下のエラーがない社員ID = 4
	 */
	@Test
	public void changeSubSidsTest3() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(Arrays.asList("1", "2", "3", "4"), DisplayAtr.SUPERIOR);
		List<String> newSubSid = Stream.of("1", "2", "3", "5").collect(Collectors.toList());
		List<String> noErrSids = Arrays.asList("4");
		
		//when
		domain.changeSubSids(newSubSid, noErrSids);
		
		//then
		assertThat(domain.getSubSids()).isEqualTo(Arrays.asList("1", "2", "3", "5"));
	}
	
	/**
	 * [3] 部下の社員IDを変更する 
	 * domain.SubSids : 1,2,3,4
	 * 表示社員区分 = 上長
	 * 部下のエラーがある員ID = empty
	 * 部下のエラーがない社員ID = 1,2,3,4
	 */
	@Test
	public void changeSubSidsTest4() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(Arrays.asList("1", "2", "3", "4"), DisplayAtr.SUPERIOR);
		List<String> newSubSid = Collections.emptyList();
		List<String> noErrSids = Arrays.asList("1", "2", "3", "4");
		
		//when
		domain.changeSubSids(newSubSid, noErrSids);
		
		//then
		assertThat(domain.getSubSids()).isEmpty();
	}
	
	/**
	 * [4]上長の場合はエラーが解消済みになるか
	 * 表示社員区分 == 上長
	 */
	@Test
	public void isErrResolvedTest() {
		//give
		String sid = "sid";
		List<String> newSubSid = Stream.of(sid).collect(Collectors.toList());
		List<String> noErrSids = newSubSid;
		
		//when
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(newSubSid, DisplayAtr.SUPERIOR);
		
		//then
		assertThat(domain.isErrorResolved(new ArrayList<>())).isFalse();
		assertThat(domain.isErrorResolved(noErrSids)).isTrue();
	}
	
	/**
	 * [4]上長の場合はエラーが解消済みになるか
	 * 表示社員区分 != 上長
	 */
	@Test
	public void isErrResolvedTest2() {
		//give
		String sid = "sid";
		List<String> newSubSid = Stream.of(sid).collect(Collectors.toList());
		List<String> noErrSids = newSubSid;
		
		//when
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(newSubSid, DisplayAtr.PRINCIPAL);
		
		//then
		assertThat(domain.isErrorResolved(noErrSids)).isFalse();
	}
	
	/**
	 * [4]上長の場合はエラーが解消済みになるか
	 * 表示社員区分 = 上長
	 */
	@Test
	public void isErrResolvedTest3() {
		//give
		List<String> newSubSid = Stream.of("1", "2", "3").collect(Collectors.toList());
		List<String> noErrSids = Arrays.asList("1");
		
		//when
		ToppageAlarmData domain = ToppageAlarmDataHelper
				.mockDomainWithSubSidsAndDispAtr(newSubSid, DisplayAtr.SUPERIOR);
		
		//then
		assertThat(domain.isErrorResolved(noErrSids)).isFalse();
	}
	
	/**
	 * 既読日時を更新する test
	 */
	@Test
	public void updateReadDateTimeTest() {
		//give
		ToppageAlarmData domain = ToppageAlarmDataHelper.mockResolvedDomain();
		GeneralDateTime dateTime = GeneralDateTime.now().addDays(10);
		
		//when
		domain.updateReadDateTime(dateTime);
		
		//then
		assertThat(domain.getReadDateTime().get()).isEqualTo(dateTime);
	}

	/**
	 * AllArgsConstructor test
	 */
	@Test
	public void testAllArgsConstructor() {
		
		//give
		String cid = "000000000000-0001";
		AlarmClassification alarmClassification = AlarmClassification.ALARM_LIST;
		String displaySId = "ae7fe82e-a7bd-4ce3-adeb-5cd403a9d570";
		DisplayAtr displayAtr = DisplayAtr.PIC;
		Boolean isResolved = true;
		GeneralDateTime occurrenceDateTime = GeneralDateTime.now();
		DisplayMessage displayMessage = new DisplayMessage("test message");
		List<String> subSids = new ArrayList<>();
		Optional<LinkURL> linkURL = Optional.of(new LinkURL("http://google.com.vn"));
		Optional<GeneralDateTime> readDateTime = Optional.of(GeneralDateTime.now());
		Optional<AlarmListPatternCode> patternCode = Optional.of(new AlarmListPatternCode("patternCode"));
		Optional<NotificationId> notificationId = Optional.of(new NotificationId("notificationId"));
		
		//then
		ToppageAlarmData domain = new ToppageAlarmData(cid, alarmClassification, displaySId, displayAtr, isResolved,
				occurrenceDateTime, displayMessage, subSids, linkURL, readDateTime, patternCode, notificationId);

		//when
		assertThat(domain.getCid()).isEqualTo(cid);
		assertThat(domain.getAlarmClassification()).isEqualTo(alarmClassification);
		assertThat(domain.getDisplaySId()).isEqualTo(displaySId);
		assertThat(domain.getDisplayAtr()).isEqualTo(displayAtr);
		assertThat(domain.getIsResolved()).isEqualTo(isResolved);
		assertThat(domain.getOccurrenceDateTime()).isEqualTo(occurrenceDateTime);
		assertThat(domain.getDisplayMessage()).isEqualTo(displayMessage);
		assertThat(domain.getLinkUrl()).isEqualTo(linkURL);
		assertThat(domain.getReadDateTime()).isEqualTo(readDateTime);
		assertThat(domain.getPatternCode()).isEqualTo(patternCode);
		assertThat(domain.getNotificationId()).isEqualTo(notificationId);
	}

}
