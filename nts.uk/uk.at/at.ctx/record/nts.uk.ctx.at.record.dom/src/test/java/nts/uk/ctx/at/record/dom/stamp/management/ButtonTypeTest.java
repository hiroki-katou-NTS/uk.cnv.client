package nts.uk.ctx.at.record.dom.stamp.management;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.Layout.Stamp;
import nts.uk.ctx.at.record.dom.stamp.management.StampSettingPersonHelper.Layout.Type;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ReservationArt;
import nts.uk.shr.com.i18n.TextResource;

@RunWith(JMockit.class)
public class ButtonTypeTest {
		
	@Test
	public void getters(@Mocked final TextResource tr) {
		ButtonType buttonType = Type.DUMMY;
		
		new Expectations() {
            {
            	TextResource.localize("KDP011_39");
            	result =  "KDP011_39";
            }
        };
        
		NtsAssert.invokeGetters(buttonType);
	}
	
	@Test
	public void checkStampTypeFalse(){
		ButtonType buttonType = new ButtonType(
				EnumAdaptor.valueOf(0, ReservationArt.class), //dummy
				Optional.empty());
		assertThat(buttonType.checkStampType()).isFalse();
	}
	
	@Test
	public void checkStampTypeTrue(){
		ButtonType buttonType = new ButtonType(
				EnumAdaptor.valueOf(0, ReservationArt.class), //dummy
				Optional.of(Stamp.DUMMY));
		assertThat(buttonType.checkStampType()).isTrue();
	}
	
}
