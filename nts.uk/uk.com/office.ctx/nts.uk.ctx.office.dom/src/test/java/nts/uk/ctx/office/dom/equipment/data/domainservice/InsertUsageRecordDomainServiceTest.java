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
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationCode;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentDataTestHelper;
import nts.uk.ctx.office.dom.equipment.data.ItemData;
import nts.uk.ctx.office.dom.equipment.data.RegisterResult;
import nts.uk.ctx.office.dom.equipment.data.domainservice.InsertUsageRecordDomainService.Require;
import nts.uk.ctx.office.dom.equipment.information.EquipmentCode;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformationTestHelper;

@RunWith(JMockit.class)
public class InsertUsageRecordDomainServiceTest {

	@Injectable
	private Require require;

	/**
	 * [1] 新規登録する
	 * エラーがない
	 */
	@Test
	public void testInsertDataNoErr() {
		// given
		GeneralDate useDate = GeneralDate.today();
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abc")));

		new Expectations() {
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "1");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
			{
				require.getItemSettings(EquipmentDataTestHelper.CID, Arrays.asList(new EquipmentItemNo("1")));
				result = Arrays
						.asList(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
			{
				require.getEquipmentInfo(EquipmentDataTestHelper.CID,
						new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD));
				result = Optional.of(EquipmentInformationTestHelper.mockDomain(""));
			}
		};

		// when
		RegisterResult result = InsertUsageRecordDomainService.insert(require, EquipmentDataTestHelper.CID,
				new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD), useDate, EquipmentDataTestHelper.SID,
				new EquipmentClassificationCode(EquipmentDataTestHelper.EQUIPMENT_CLS_CD), itemDatas);

		// then
		assertThat(result.isHasError()).isFalse();
	}

	/**
	 * [1] 新規登録する
	 * Msg_2233
	 */
	@Test
	public void testInsertDataBusinessErr() {
		// given
		GeneralDate useDate = GeneralDate.today().addDays(1);
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abc")));

		new Expectations() {
			{
				require.getEquipmentInfo(EquipmentDataTestHelper.CID,
						new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD));
				result = Optional.of(EquipmentInformationTestHelper.mockDomain(""));
			}
		};

		// then
		NtsAssert.businessException("Msg_2233",
				() -> InsertUsageRecordDomainService.insert(require, EquipmentDataTestHelper.CID,
						new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD), useDate, EquipmentDataTestHelper.SID,
						new EquipmentClassificationCode(EquipmentDataTestHelper.EQUIPMENT_CLS_CD), itemDatas));
	}

	/**
	 * [1] 新規登録する
	 * エラーがある
	 */
	@Test
	public void testInsertDataWithErr() {
		// given
		GeneralDate useDate = GeneralDate.today();
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abczzzzzz")));

		new Expectations() {
			{
				require.getItemSettings(EquipmentDataTestHelper.CID, Arrays.asList(new EquipmentItemNo("1")));
				result = Arrays
						.asList(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
			{
				require.getEquipmentInfo(EquipmentDataTestHelper.CID,
						new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD));
				result = Optional.of(EquipmentInformationTestHelper.mockDomain(""));
			}
		};

		// when
		RegisterResult result = InsertUsageRecordDomainService.insert(require, EquipmentDataTestHelper.CID,
				new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD), useDate, EquipmentDataTestHelper.SID,
				new EquipmentClassificationCode(EquipmentDataTestHelper.EQUIPMENT_CLS_CD), itemDatas);

		// then
		assertThat(result.isHasError()).isTrue();
		assertThat(result.getErrorItems().isEmpty()).isFalse();
	}

	/**
	 * [1] 新規登録する
	 * $設備情報 == null
	 */
	@Test
	public void testInsertDataNoInfo() {
		// given
		GeneralDate useDate = GeneralDate.today();
		List<ItemData> itemDatas = Arrays
				.asList(new ItemData(new EquipmentItemNo("1"), new ActualItemUsageValue("abc")));

		new Expectations() {
			{
				require.getItemSetting(EquipmentDataTestHelper.CID, "1");
				result = Optional
						.of(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
			{
				require.getItemSettings(EquipmentDataTestHelper.CID, Arrays.asList(new EquipmentItemNo("1")));
				result = Arrays
						.asList(EquipmentUsageRecordItemSettingTestHelper.mockDomain("1", ItemClassification.TEXT, 5));
			};
		};

		// when
		RegisterResult result = InsertUsageRecordDomainService.insert(require, EquipmentDataTestHelper.CID,
				new EquipmentCode(EquipmentDataTestHelper.EQUIPMENT_CD), useDate, EquipmentDataTestHelper.SID,
				new EquipmentClassificationCode(EquipmentDataTestHelper.EQUIPMENT_CLS_CD), itemDatas);

		// then
		assertThat(result.isHasError()).isFalse();
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
		Optional<ErrorItem> optErrorItem = NtsAssert.Invoke.staticMethod(InsertUsageRecordDomainService.class, 
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
		Optional<ErrorItem> optErrorItem = NtsAssert.Invoke.staticMethod(InsertUsageRecordDomainService.class, 
				"validateItemLimit", itemData, itemSettings);
		
		// then
		assertThat(optErrorItem).isPresent();
	}
}
