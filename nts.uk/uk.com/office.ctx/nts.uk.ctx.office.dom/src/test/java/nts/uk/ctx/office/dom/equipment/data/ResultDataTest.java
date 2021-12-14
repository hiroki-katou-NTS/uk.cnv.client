package nts.uk.ctx.office.dom.equipment.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.domain.EquipmentUsageRecordItemSettingTestHelper;
import nts.uk.ctx.office.dom.equipment.data.ResultData.Require;

@RunWith(JMockit.class)
public class ResultDataTest {

	@Injectable
	private Require require;

	@Test
	public void getters() {
		// when
		ResultData domain = EquipmentDataTestHelper.createItemData("1", ItemClassification.TEXT, "test");

		// then
		NtsAssert.invokeGetters(domain);
	}

	/**
	 * [C-1] 新規追加 
	 */
	@Test
	public void testCreateTempItemDataNoErr() {
		// given
		ResultData domain = EquipmentDataTestHelper.createItemData("1", ItemClassification.TEXT, "test");

		new Expectations() {
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "1");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
		};

		// when
		ResultData resultData = ResultData.createData(require, EquipmentDataTestHelper.CID,
				new EquipmentItemNo("1"), new ActualItemUsageValue("test"));
		
		// then
		assertThat(resultData).isEqualTo(domain);
	}
	
	/**
	 * [C-1] 新規追加 
	 * $項目設定.isPresent() == false
	 */
	@Test
	public void testCreateTempItemDataNoItemSetting() {
		// when
		ResultData resultData = ResultData.createData(require, EquipmentDataTestHelper.CID,
				new EquipmentItemNo("15"), new ActualItemUsageValue("test"));
		
		// then
		assertThat(resultData).isNull();
	}
	
	/**
	 * [1] 項目値を変更する
	 */
	@Test
	public void testUpdateValue() {
		// given
		ResultData resultData = new ResultData(new EquipmentItemNo("1"), ItemClassification.TEXT, Optional.empty());
		List<ItemData> itemValues = Arrays.asList(
				new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("new")),
				new ItemData(new EquipmentItemNo("3"), new ActualItemUsageValue("none")));
		
		// when
		resultData.updateValue(itemValues);
		
		// then
		assertThat(resultData.getActualValue()).isPresent();
		assertThat(resultData.getActualValue().get().v()).isEqualTo("new");
	}
}
