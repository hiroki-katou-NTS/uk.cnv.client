package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

public class TimeItemTypeAtrTest {

	@Test
	public void isNomalType_NORMAL_TYPE() {
		assertThat(TimeItemTypeAtr.NORMAL_TYPE.isNomalType()).isTrue();
	}
	
	@Test
	public void isNomalType_SPECIAL_TYPE() {
		assertThat(TimeItemTypeAtr.SPECIAL_TYPE.isNomalType()).isFalse();
	}
	
	@Test
	public void isSpecialType_SPECIAL_TYPE() {
		assertThat(TimeItemTypeAtr.SPECIAL_TYPE.isSpecialType()).isTrue();
	}
	
	@Test
	public void isSpecialType_NORMAL_TYPE() {
		assertThat(TimeItemTypeAtr.NORMAL_TYPE.isSpecialType()).isFalse();
	}
}
