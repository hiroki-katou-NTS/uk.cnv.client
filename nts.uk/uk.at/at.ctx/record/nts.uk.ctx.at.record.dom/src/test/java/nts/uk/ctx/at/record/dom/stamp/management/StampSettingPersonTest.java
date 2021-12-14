package nts.uk.ctx.at.record.dom.stamp.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.experimental.var;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.Layout;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.Layout.Button;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.Layout.Comment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.StampFunctionAvailableService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AudioType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonLayoutType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageLayout;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SupportWplSet;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.StampScreen;

/**
 * 
 * @author phongtq
 *
 */
@RunWith(JMockit.class)
public class StampSettingPersonTest {

	
	//TODO:Chungnt example ut insert update

//	@Test
//	public void testGetButtonSet_false() {
//
//		StampSettingPerson settingPerson = StampSettingPersonHelper.DUMMY;
//		assertThat(settingPerson.getButtonSet(0, 0)).isNotPresent();
//	}
//
//	@Test
//	public void testGetButtonSet_succes() {
//
//		StampSettingPerson settingPerson = StampSettingPersonHelper.DUMMY;
//		assertThat(settingPerson.getButtonSet(1, 1)).isPresent();
//	}
//
//	@Test
//	public void testGetButtonSet_succes2() {
//
//		StampSettingPerson settingPerson = StampSettingPersonHelper.DUMMY;
//		assertThat(settingPerson.getButtonSet(1, 0)).isNotPresent();
//	}

	/**
	 * Optional<StampPageLayout> pageLayout = lstStampPageLayout.stream().filter(x -> x.getPageNo().v() == stambutton.getPageNo().v()).findFirst(){
		if(!pageLayout.isPresent()){
			return Optional.empty();
		}
	 */
//	@Test void testStampSettingPerson() {
//		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson();
//		Optional<StampPageLayout> pageLayout = Optional.empty();
//		
//		Optional<ButtonSettings> data = settingPerson.getButtonSet(new StampButton(new PageNo(1), new ButtonPositionNo(1)));
//		assertThat(data).isEqualTo(Optional.empty());
//		
//	}
	
	/**
	 * Optional<StampPageLayout> pageLayout !
	 */
	@Test
	public void testNull() {
		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPersonNull();
		
		Optional<ButtonSettings> stambutton = settingPerson.getButtonSet(new StampButton(new PageNo(1), new ButtonPositionNo(1)));
		
		assertThat(stambutton).isEmpty();
	}
	
	@Test
	public void testNull1() {
		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson1();
		
		Optional<ButtonSettings> stambutton = settingPerson.getButtonSet(new StampButton(new PageNo(2), new ButtonPositionNo(2)));
		
		assertThat(stambutton).isEmpty();
	}
	
	
	/**
	 * Optional<ButtonSettings> buttonSettings = pageLayout.get().getLstButtonSet().stream().filter(x -> x.getButtonPositionNo().v() == stambutton.getButtonPositionNo().v()).findFirst();
	 */
	@Test
	public void testNotNull() {
		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson1();
		
		Optional<ButtonSettings> stambutton = settingPerson
				.getButtonSet(new StampButton(new PageNo(1), new ButtonPositionNo(0)));
		
		Optional<ButtonSettings> buttonSettings = Optional.of(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.RESERVATION, null),
				NotUseAtr.USE,
				AudioType.GOOD_MORNING,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE)));
		
		assertThat(stambutton).isNotEqualTo(buttonSettings);

	}
	
	
	/**
	 * Optional<ButtonSettings> buttonSettings = pageLayout.get().getLstButtonSet().stream().filter(x -> x.getButtonPositionNo().v() == stambutton.getButtonPositionNo().v()).findFirst();
	 */

	
	@Test
	public void insert() {

		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(2, 3);

		settingPerson.addPage(pageLayout);
		assertThat(settingPerson.getLstStampPageLayout().stream().filter(x -> x.getPageNo().v() == 3)).isNotEmpty();
	}
	
	@Test
	public void update_succes() {

		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(1, 1);

		settingPerson.updatePage(pageLayout);
		assertThat(settingPerson.getLstStampPageLayout().stream().filter(x -> x.getStampPageName().v().equals("NEW_DUMMY2"))).isNotEmpty();
	}

	@Test
	public void delete() {

		StampSettingPerson settingPerson = StampSettingPersonHelper.settingPerson();

		settingPerson.delete(1);
		assertThat(settingPerson.getLstStampPageLayout().stream().filter(x -> x.getPageNo().v() == 1)).isEmpty();
	}

	@Test
	public void getters() {
		StampSettingPerson settingPerson = StampSettingPersonHelper.DUMMY;
		NtsAssert.invokeGetters(settingPerson);
	}

	@Test
	public void cons() {
		StampSettingPerson person = new StampSettingPerson("000000000000-0001", true, StampScreen.DUMMY, null,null);
		NtsAssert.invokeGetters(person);
	}
}
