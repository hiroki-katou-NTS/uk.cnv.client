package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import nts.arc.enums.EnumAdaptor;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.stampsettingofRICOHcopier.StampSettingOfRICOHCopier;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
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
		StampButton stampButton1 = new StampButton(new PageNo(3), new ButtonPositionNo(1));
		StampSettingOfRICOHCopier settingsSmartphoneStamp = StampSettingOfRICOHCopierHelper.CreateStampSettingOfRICOHCopier();
		assertThat(settingsSmartphoneStamp.getDetailButtonSettings(stampButton)).isNotEmpty();
		assertThat(settingsSmartphoneStamp.getDetailButtonSettings(stampButton1)).isEmpty();
	}
	
	@Test
	public void testAddPage() {
		List<ButtonSettings> buttonSettings = new ArrayList<>();
		
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class), 
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));
		
		buttonSettings.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));

		buttonSettings.add(new ButtonSettings(new ButtonPositionNo(2),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		StampPageLayout pageLayoutSetting = new StampPageLayout(new PageNo(1),
				new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")),
				ButtonLayoutType.LARGE_2_SMALL_4,
				buttonSettings);
		
		StampSettingOfRICOHCopier stampSettingOfRICOHCopier = StampSettingOfRICOHCopierHelper.CreateStampSettingOfRICOHCopier();
		
		stampSettingOfRICOHCopier.updatePage(pageLayoutSetting);
		assertEquals(stampSettingOfRICOHCopier.getPageLayoutSettings().get(0).getLstButtonSet().size(), 2);
		
		stampSettingOfRICOHCopier.deletePage(new PageNo(1));
		assertFalse(stampSettingOfRICOHCopier.getPageLayoutSettings().contains(pageLayoutSetting));
		
		stampSettingOfRICOHCopier.addPage(pageLayoutSetting);
		assertTrue(stampSettingOfRICOHCopier.getPageLayoutSettings().contains(pageLayoutSetting));
	}
}
