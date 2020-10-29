package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper;

/**
 * 
 * @author chungnt
 *
 */

public class SettingsSmartphoneStampTest {

	@Test
	public void getters() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStampDefault();
		NtsAssert.invokeGetters(settingsSmartphoneStamp);
	}
	
	@Test
	public void testLayoutSettingsNull() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStampDefault();
		
		 Optional<ButtonSettings> optButtonSetting = settingsSmartphoneStamp.getDetailButtonSettings(new StampButton(new PageNo(1), new ButtonPositionNo(1)));
		 
		 assertThat(optButtonSetting).isEmpty();
	}
	
	@Test
	public void testLayoutSettings() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStamp();
		
		Optional<ButtonSettings> optButtonSetting = settingsSmartphoneStamp.getDetailButtonSettings(new StampButton(new PageNo(1), new ButtonPositionNo(1)));
		Optional<ButtonSettings> optButtonSetting1 = SettingsSmartphoneStampHelper.getOPTButtonSeting();
		
		assertThat(optButtonSetting.get().getAudioType().value).isNotEqualTo(optButtonSetting1.get().getAudioType().value);
		assertThat(optButtonSetting.get().getButtonDisSet().getBackGroundColor().v()).isNotEqualTo(optButtonSetting1.get().getButtonDisSet().getBackGroundColor().v());
		assertThat(optButtonSetting.get().getButtonDisSet().getButtonNameSet()).isNotEqualTo(optButtonSetting1.get().getButtonDisSet().getButtonNameSet());
		assertThat(optButtonSetting.get().getButtonPositionNo()).isNotEqualTo(optButtonSetting1.get().getButtonPositionNo());
		assertThat(optButtonSetting.get().getButtonType().getReservationArt().value).isNotEqualTo(optButtonSetting1.get().getButtonType().getReservationArt().value);
		assertThat(optButtonSetting.get().getButtonType().getStampTypeDisplay()).isNotEqualTo(optButtonSetting1.get().getButtonType().getStampTypeDisplay());
	}
	
	
	@Test
	public void insert() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStampDefault();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(1, 2);
		
		settingsSmartphoneStamp.addPage(pageLayout);
		assertThat(settingsSmartphoneStamp.getPageLayoutSettings().stream().filter(x -> x.getPageNo().v() == 2)).isNotEmpty();
	}
	
	@Test
	public void update_succes() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStamp();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(1, 2);

		settingsSmartphoneStamp.updatePage(pageLayout);
		assertThat(settingsSmartphoneStamp.getPageLayoutSettings().stream().filter(x -> x.getStampPageName().v().equals("NEW_DUMMY2"))).isNotEmpty();
	}

	@Test
	public void delete() {
		SettingsSmartphoneStamp settingsSmartphoneStamp = SettingsSmartphoneStampHelper.getSettingsSmartphoneStamp();

		settingsSmartphoneStamp.deletePage();
		assertThat(settingsSmartphoneStamp.getPageLayoutSettings()).isEmpty();
	}
	
}
