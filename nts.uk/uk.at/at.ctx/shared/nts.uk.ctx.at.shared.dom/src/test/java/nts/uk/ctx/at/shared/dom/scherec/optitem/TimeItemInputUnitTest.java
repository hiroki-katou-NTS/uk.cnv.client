package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class TimeItemInputUnitTest {

	@Test
	public void getters() {
		TimeItemInputUnit timeItemInputUnit = TimeItemInputUnit.valueOf(0);
		NtsAssert.invokeGetters(timeItemInputUnit);
	}

	@Test
	public void test() {
		TimeItemInputUnit timeItemInputUnit = TimeItemInputUnit.valueOf(0);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.ONE_MINUTE);
		timeItemInputUnit = TimeItemInputUnit.valueOf(1);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.FIVE_MINUTES);
		timeItemInputUnit = TimeItemInputUnit.valueOf(2);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.TEN_MINUTES);
		timeItemInputUnit = TimeItemInputUnit.valueOf(3);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.FIFTEEN_MINUTES);
		timeItemInputUnit = TimeItemInputUnit.valueOf(4);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.THIRTY_MINUTES);
		timeItemInputUnit = TimeItemInputUnit.valueOf(5);
		assertThat(timeItemInputUnit).isEqualTo(TimeItemInputUnit.SIXTY_MINUTES);
	}

	@Test
	public void testValueEnum() {
		TimeItemInputUnit timeItemInputUnit = TimeItemInputUnit.valueOf(0);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(1);
		timeItemInputUnit = TimeItemInputUnit.valueOf(1);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(5);
		timeItemInputUnit = TimeItemInputUnit.valueOf(2);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(10);
		timeItemInputUnit = TimeItemInputUnit.valueOf(3);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(15);
		timeItemInputUnit = TimeItemInputUnit.valueOf(4);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(30);
		timeItemInputUnit = TimeItemInputUnit.valueOf(5);
		assertThat(timeItemInputUnit.valueEnum()).isEqualTo(60);
	}
}
