package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
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
import nts.gul.location.GeoCoordinate;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.AutoCreateStampCardNumberService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardCreateResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AudioType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonLayoutType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.CorrectionInterval;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.DisplaySettingsStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ResultDisplayTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SettingDateTimeColorOfStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageLayout;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SupportWplSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author chungnt
 *
 */
@RunWith(JMockit.class)
public class EnterStampFromSmartPhoneServiceTest {

	@Injectable
	private nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EnterStampFromSmartPhoneService.Require require;

	/**
	 * require.getSmartphoneStampSetting() isPresent()
	 * resutl : BusinessException("Msg_1632")
	 */
	@Test
	public void test_settingSmartPhoneStampOpt_null() {

		ContractCode contractCode = new ContractCode("DUMMY");
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		
		new Expectations() {
		{
			require.getSmartphoneStampSetting();
		}
	};
	
	NtsAssert.businessException("Msg_1632",
			() -> EnterStampFromSmartPhoneService.create(require, "", contractCode, employeeId,
					dateTime, stampButton,
					Optional.empty(), null));
	
	}

	/**
	 *  $ボタン詳細設定 = $スマホ打刻の打刻設定.ボタン詳細設定を取得する(打刻ボタン) == null
	 * resutl : BusinessException("Msg_1632")
	 */
	@Test
	public void test_buttonSettingOpt_null() {

		ContractCode contractCode = new ContractCode("DUMMY");
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		GeoCoordinate geoCoordinate = new GeoCoordinate(1, 1);
		DisplaySettingsStampScreen displaySettingsStamScreen = new DisplaySettingsStampScreen(new CorrectionInterval(1),
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1));
		List<StampPageLayout> stampPageLayouts = new ArrayList<>();
		SettingsSmartphoneStamp settingsSmartphoneStamp = new SettingsSmartphoneStamp(contractCode.v(), displaySettingsStamScreen, stampPageLayouts, true, NotUseAtr.USE, NotUseAtr.USE);
		
		new Expectations() {
		{
			require.getSmartphoneStampSetting();
			result = Optional.of(settingsSmartphoneStamp);
		}
	};
	
	NtsAssert.businessException("Msg_1632",
			() -> EnterStampFromSmartPhoneService.create(require, "", contractCode, employeeId,
					dateTime, stampButton,
					Optional.of(geoCoordinate), null));
	
	}
	
	
	/**
	 *  $打刻する方法 = 打刻する方法#打刻する方法(ID認証, スマホ打刻)
	 */
	@Test
	public void test() {

		ContractCode contractCode = new ContractCode("DUMMY");
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		GeoCoordinate geoCoordinate = new GeoCoordinate(1, 1);
		DisplaySettingsStampScreen displaySettingsStamScreen = new DisplaySettingsStampScreen(new CorrectionInterval(1),
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1));
		
		List<StampPageLayout> stampPageLayouts = new ArrayList<>();
		List<ButtonSettings> lstButtonSet = new ArrayList<>();

		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.NONE , Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.NONE , Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.NONE , Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE)));
		
		stampPageLayouts.add(new StampPageLayout(new PageNo(1), new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")), ButtonLayoutType.SMALL_8,
				lstButtonSet));
		
		SettingsSmartphoneStamp settingsSmartphoneStamp = new SettingsSmartphoneStamp(contractCode.v(), displaySettingsStamScreen, stampPageLayouts, true, NotUseAtr.USE, NotUseAtr.USE);
		
		new Expectations() {
		{
			require.getSmartphoneStampSetting();
			result = Optional.of(settingsSmartphoneStamp);
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
	
	TimeStampInputResult timeStampInputResult = EnterStampFromSmartPhoneService.create(require, "", contractCode, employeeId,
					dateTime, stampButton,
					Optional.of(geoCoordinate), null);
	
	assertThat(timeStampInputResult.at).isNotEmpty();
	assertThat(timeStampInputResult.stampDataReflectResult.getAtomTask()).isNotNull();
	}
}
