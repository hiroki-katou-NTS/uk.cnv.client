package nts.uk.ctx.office.dom.equipment.achievement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;

@RunWith(JMockit.class)
public class EquipmentUsageRecordItemSettingTest {
	
	@Test
	public void getters() {
		EquipmentUsageRecordItemSetting domain = EquipmentUsageRecordItemSettingTestHelper.createDoamin();
		
		NtsAssert.invokeGetters(domain);
	}
	
	/**
	 * [1] 入力した値の制御をチェックする
	 * エラー:Empty
	 */
	@Test
	public void testCheck() {
		EquipmentUsageRecordItemSetting domain = EquipmentUsageRecordItemSettingTestHelper.createDoamin();
		ActualItemUsageValue inputVal = new ActualItemUsageValue("input value");
		
		Optional<ErrorItem> actual = domain.check(Optional.of(inputVal));
		assertThat(actual).isNotPresent();
	}
	
	/**
	 * [1] 入力した値の制御をチェックする
	 * エラー:Present
	 */
	@Test
	public void testCheck2() {
		EquipmentUsageRecordItemSetting domain = EquipmentUsageRecordItemSettingTestHelper.createDoamin();
		ActualItemUsageValue inputVal = new ActualItemUsageValue("input value mock to error");
		
		Optional<ErrorItem> actual = domain.check(Optional.of(inputVal));
		assertThat(actual).isPresent();
	}
}
