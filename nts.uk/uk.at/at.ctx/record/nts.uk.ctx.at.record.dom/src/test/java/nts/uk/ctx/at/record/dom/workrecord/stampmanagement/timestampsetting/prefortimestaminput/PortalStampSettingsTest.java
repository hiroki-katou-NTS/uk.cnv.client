package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.ot.frame.NotUseAtr;

public class PortalStampSettingsTest {

	@Test
	public void getter() {
		PortalStampSettings stampSettings = PortalStampSettingsHelper.getDefault();
		NtsAssert.invokeGetters(stampSettings);
	}
	
	@Test
	public void testDetailButtonSettings() {
		PortalStampSettings stampSettings = PortalStampSettingsHelper.getDefault();
		
		Optional<ButtonSettings> optButtonSettings = stampSettings.getDetailButtonSettings(new ButtonPositionNo(1));
		
		assertThat(optButtonSettings.get().getUsrArt().value).isEqualTo(NotUseAtr.NOT_USE.value);
		assertThat(optButtonSettings.get().getButtonType().getReservationArt().value).isEqualTo(ReservationArt.CANCEL_RESERVATION.value);
	}
}
