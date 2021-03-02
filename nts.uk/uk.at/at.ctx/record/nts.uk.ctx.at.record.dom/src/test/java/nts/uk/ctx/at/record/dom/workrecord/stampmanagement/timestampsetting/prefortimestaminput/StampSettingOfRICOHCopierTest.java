package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.stampsettingofRICOHcopier.StampSettingOfRICOHCopier;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author ThanhPV
 *
 */

public class StampSettingOfRICOHCopierTest {

	@Test
	public void getters() {
		StampSettingOfRICOHCopier settingsSmartphoneStamp = StampSettingOfRICOHCopierHelper.CreateStampSettingOfRICOHCopier();
		NtsAssert.invokeGetters(settingsSmartphoneStamp);
	}
	
	@Test
	public void testGetDetailButtonSettings() {
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		StampButton stampButton1 = new StampButton(new PageNo(2), new ButtonPositionNo(1));
		StampSettingOfRICOHCopier settingsSmartphoneStamp = StampSettingOfRICOHCopierHelper.CreateStampSettingOfRICOHCopier();
		assertThat(settingsSmartphoneStamp.getDetailButtonSettings(stampButton)).isNotEmpty();
		assertThat(settingsSmartphoneStamp.getDetailButtonSettings(stampButton1)).isEmpty();
	}
	
	@Test
	public void testAddPage() {
		List<ButtonSettings> buttonSettings = new ArrayList<>();
		
		buttonSettings.add(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.CANCEL_RESERVATION, Optional.empty()),
				NotUseAtr.NOT_USE,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		StampPageLayout pageLayoutSetting = new StampPageLayout(new PageNo(1),
				new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")),
				ButtonLayoutType.LARGE_2_SMALL_4,
				buttonSettings);
		
		StampSettingOfRICOHCopier stampSettingOfRICOHCopier = StampSettingOfRICOHCopierHelper.CreateStampSettingOfRICOHCopier();
		stampSettingOfRICOHCopier.updatePage(pageLayoutSetting);
		stampSettingOfRICOHCopier.deletePage(new PageNo(1));
		stampSettingOfRICOHCopier.addPage(pageLayoutSetting);		
		assertThat(stampSettingOfRICOHCopier.getPageLayoutSettings()).isNotEmpty();
	}
}
