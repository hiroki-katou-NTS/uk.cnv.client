package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;

@RunWith(JMockit.class)
public class ShiftMasterDisInforTest {

	@Test
	public void getters() {
		ShiftMasterDisInfor shiftMasterDisInfor = ShiftMasterHelper.DispInfo.createDummy();
		NtsAssert.invokeGetters(shiftMasterDisInfor);
	}

	// if remarks == null
	@Test
	public void test_create_remarksIsEmpty() {

		ShiftMasterDisInfor displayInfor = new ShiftMasterDisInfor(
						new ShiftMasterName("name a")
					,	new ColorCodeChar6("color a")
					,	new ColorCodeChar6("colorsp a")
					,	Optional.empty()
				);

		assertThat( displayInfor.getName() ).isEqualTo( new ShiftMasterName("name a") );
		assertThat( displayInfor.getColor() ).isEqualTo( new ColorCodeChar6("color a") );
		assertThat( displayInfor.getColorSmartPhone() ).isEqualTo( new ColorCodeChar6("colorsp a") );
		assertThat( displayInfor.getRemarks() ).isEmpty();

	}

	// if remarks != null
	@Test
	public void test_create_remarksIsNotEmpty() {

		ShiftMasterDisInfor displayInfor = new ShiftMasterDisInfor(
						new ShiftMasterName("a name")
					,	new ColorCodeChar6("a color")
					,	new ColorCodeChar6("a colorsp")
					,	Optional.of( new Remarks("a remarks") )
				);

		assertThat( displayInfor.getName() ).isEqualTo( new ShiftMasterName("a name") );
		assertThat( displayInfor.getColor() ).isEqualTo( new ColorCodeChar6("a color") );
		assertThat( displayInfor.getColorSmartPhone() ).isEqualTo( new ColorCodeChar6("a colorsp") );
		assertThat( displayInfor.getRemarks() ).isPresent().get().isEqualTo( new Remarks("a remarks") );

	}

}
