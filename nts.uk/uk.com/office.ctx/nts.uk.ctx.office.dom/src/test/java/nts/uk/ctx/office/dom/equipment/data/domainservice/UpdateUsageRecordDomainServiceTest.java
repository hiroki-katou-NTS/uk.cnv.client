package nts.uk.ctx.office.dom.equipment.data.domainservice;

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
import nts.arc.time.GeneralDate;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.domain.EquipmentUsageRecordItemSettingTestHelper;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataTestHelper;
import nts.uk.ctx.office.dom.equipment.data.ItemData;
import nts.uk.ctx.office.dom.equipment.data.RegisterResult;
import nts.uk.ctx.office.dom.equipment.data.domainservice.UpdateUsageRecordDomainService.Require;

@RunWith(JMockit.class)
public class UpdateUsageRecordDomainServiceTest {

	@Injectable
	private Require require;

	/**
	 * [1]変更する
	 */
	@Test
	public void testUpdateDataNoErr() {
		// given
		GeneralDate useDate = GeneralDate.today();
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(useDate);
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("new")));

		new Expectations() {
			{
				require.getEquipmentData(EquipmentDataTestHelper.CID, domain.getEquipmentCode(), useDate,
						EquipmentDataTestHelper.SID, domain.getInputDate());
				result = Optional.of(domain);
			};
			{
				require.getItemSettings(EquipmentDataTestHelper.CID, Arrays.asList(new EquipmentItemNo("1")));
				result = Arrays
						.asList(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
		};

		// when
		RegisterResult result = UpdateUsageRecordDomainService.update(require, EquipmentDataTestHelper.CID,
				domain.getEquipmentCode(), domain.getInputDate(), domain.getEquipmentClassificationCode(),
				EquipmentDataTestHelper.SID, useDate, itemDatas);

		// then
		assertThat(result.isHasError()).isFalse();
	}

	/**
	 * [1]変更する
	 * エラーがある
	 */
	@Test
	public void testUpdateDataWithErr() {
		// given
		GeneralDate useDate = GeneralDate.today();
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(useDate);
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("new value")));

		new Expectations() {
			{
				require.getEquipmentData(EquipmentDataTestHelper.CID, domain.getEquipmentCode(), useDate,
						EquipmentDataTestHelper.SID, domain.getInputDate());
				result = Optional.of(domain);
			};
			{
				require.getItemSettings(EquipmentDataTestHelper.CID, Arrays.asList(new EquipmentItemNo("1")));
				result = Arrays
						.asList(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
		};

		// when
		RegisterResult result = UpdateUsageRecordDomainService.update(require, EquipmentDataTestHelper.CID,
				domain.getEquipmentCode(), domain.getInputDate(), domain.getEquipmentClassificationCode(),
				EquipmentDataTestHelper.SID, useDate, itemDatas);

		// then
		assertThat(result.isHasError()).isTrue();
		assertThat(result.getErrorItems().isEmpty()).isFalse();
	}

	/**
	 * [1]変更する
	 * Msg_2319
	 */
	@Test
	public void testUpdateDataBusinessError() {
		// given
		GeneralDate useDate = GeneralDate.today();
		EquipmentData domain = EquipmentDataTestHelper.mockDomain(useDate);
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("new")));

		// then
		NtsAssert.businessException("Msg_2319",
				() -> UpdateUsageRecordDomainService.update(require, EquipmentDataTestHelper.CID,
						domain.getEquipmentCode(), domain.getInputDate(), domain.getEquipmentClassificationCode(),
						EquipmentDataTestHelper.SID, useDate, itemDatas));
	}
	
	/**
	 * [prv-1]項目制限をチェックする結果を取得する
	 * エラーがない
	 */
	@Test
	public void testValidateItemLimitNoErr() {
		// given
		ItemData itemData = new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abc"));
		List<EquipmentUsageRecordItemSetting> itemSettings = Arrays.asList(
				EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 3),
				EquipmentUsageRecordItemSettingTestHelper.mockDomain("2", ItemClassification.NUMBER, 10));
		
		// when
		Optional<ErrorItem> optErrorItem = NtsAssert.Invoke.staticMethod(UpdateUsageRecordDomainService.class, 
				"validateItemLimit", itemData, itemSettings);
		
		// then
		assertThat(optErrorItem).isEmpty();
	}
	
	/**
	 * [prv-1]項目制限をチェックする結果を取得する
	 * エラーがある
	 */
	@Test
	public void testValidateItemLimitHasError() {
		// given
		ItemData itemData = new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abcdef"));
		List<EquipmentUsageRecordItemSetting> itemSettings = Arrays.asList(
				EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 3),
				EquipmentUsageRecordItemSettingTestHelper.mockDomain("2", ItemClassification.NUMBER, 10));
		
		// when
		Optional<ErrorItem> optErrorItem = NtsAssert.Invoke.staticMethod(UpdateUsageRecordDomainService.class, 
				"validateItemLimit", itemData, itemSettings);
		
		// then
		assertThat(optErrorItem).isPresent();
	}
}
