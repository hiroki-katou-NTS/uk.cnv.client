package nts.uk.ctx.office.dom.equipment.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.domain.EquipmentUsageRecordItemSettingTestHelper;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ResultData.Require;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;

@RunWith(JMockit.class)
public class EquipmentDataTest {

	@Injectable
	private Require require;

	@Test
	public void getters() {
		// when
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(GeneralDate.today());

		// then
		NtsAssert.invokeGetters(domain);
	}

	/**
	 * [C-1] 新規登録 
	 */
	@Test
	public void testCreateTempEquipmentData() {
		// given
		GeneralDate useDate = GeneralDate.today();
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(useDate);

		new Expectations() {
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "1");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "4");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("4", ItemClassification.NUMBER, 1000));
			};
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "7");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("7", ItemClassification.TIME, 1000));
			};
		};

		// when
		EquipmentData equipmentData = EquipmentData.createData(require, EquipmentDataTestHelper.CID,
				new EquipmentClassificationCode(EquipmentDataTestHelper.EQUIPMENT_CLS_CD),
				new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD), EquipmentDataTestHelper.SID, useDate,
				EquipmentDataTestHelper.mockValueMap());

		// then
		assertThat(equipmentData.getEquipmentCode()).isEqualTo(domain.getEquipmentCode());
		assertThat(equipmentData.getResultDatas()).isEqualTo(domain.getResultDatas());
	}

	/**
	 * [1] 変更登録 
	 */
	@Test
	public void testUpdateItemDatasTest() {
		// given
		GeneralDate useDate = GeneralDate.today();
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(useDate);
		Map<EquipmentItemNo, ActualItemUsageValue> valuesMap = EquipmentDataTestHelper.mockValueMap();
		valuesMap.entrySet().forEach(e -> {
			switch (e.getKey().v()) {
			case "1":
				e.setValue(new ActualItemUsageValue("xyzz"));
				break;
			case "4":
				e.setValue(new ActualItemUsageValue("1"));
				break;
			case "7":
				e.setValue(new ActualItemUsageValue("720"));
				break;
			}
		});
		List<String> valuesToChange = valuesMap.values().stream().map(ActualItemUsageValue::v)
				.collect(Collectors.toList());

		// when
		domain.updateResultDatas(valuesMap.entrySet().stream()
				.map(e -> new ItemData(e.getKey(), e.getValue())).collect(Collectors.toList()));
		List<String> newValues = domain.getResultDatas().stream()
				.map(data -> data.getActualValue().map(ActualItemUsageValue::v).orElse(null)).filter(Objects::nonNull)
				.collect(Collectors.toList());

		// then
		assertThat(newValues).isEqualTo(valuesToChange);
	}
}
