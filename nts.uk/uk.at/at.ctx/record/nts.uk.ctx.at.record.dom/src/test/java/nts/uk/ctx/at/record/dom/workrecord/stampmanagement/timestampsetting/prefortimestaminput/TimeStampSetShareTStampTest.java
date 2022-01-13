package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author chungnt
 *
 */

public class TimeStampSetShareTStampTest {

	@Test
	public void getter() {
		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();
		NtsAssert.invokeGetters(setShareTStamp);
	}

	@Test
	public void testStampPageLayoutNull() {
		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();

		StampButton stampButton = new StampButton(new PageNo(2), new ButtonPositionNo(2));
		Optional<ButtonSettings> optional = setShareTStamp.getDetailButtonSettings(stampButton);

		assertThat(optional).isEmpty();
	}

	@Test
	public void testStampPageLayout() {

		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();

		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		Optional<ButtonSettings> optional = setShareTStamp.getDetailButtonSettings(stampButton);

		assertThat(optional.get().getUsrArt().value).isEqualTo(NotUseAtr.USE.value);
		assertThat(optional.get().getAudioType().value).isEqualTo(AudioType.NONE.value);
		assertThat(optional.get().getButtonPositionNo().v()).isEqualTo(1);
//		assertThat(optional.get().getButtonType().getReservationArt().value).isEqualTo(ReservationArt.RESERVATION.value);
//		assertThat(optional.get().getButtonType().getStampType()).isEmpty();
		
	}
	
	@Test 
	public void testInsert() {
		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(2, 2);
		
		setShareTStamp.addPage(pageLayout);
		assertThat(setShareTStamp.getLstStampPageLayout().stream().filter(x -> x.getPageNo().v() == 2)).isNotEmpty();
	}
	
	@Test
	public void update_succes() {
		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();
		StampPageLayout pageLayout = StampSettingPersonHelper.crePageLayout(1, 1);

		setShareTStamp.updatePage(pageLayout);
		assertThat(setShareTStamp.getLstStampPageLayout().stream().filter(x -> x.getStampPageName().v().equals("NEW_DUMMY2"))).isNotEmpty();
	}

	@Test
	public void delete() {
		StampSetCommunal setShareTStamp = TimeStampSetShareTStampHelper.getDefault();

		setShareTStamp.deletePage(new PageNo(1));
		assertThat(setShareTStamp.getLstStampPageLayout()).isEmpty();
	}

}
