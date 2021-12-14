package nts.uk.ctx.office.dom.equipment.achievement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.error.ErrorMessage;
import nts.arc.error.I18NErrorMessage;
import nts.arc.i18n.I18NText;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ErrorItem;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.UsageItemName;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;

@RunWith(JMockit.class)
public class ItemInputControlTest {
	
	/**
	 * inv-1 項目分類　＝　文字　&&　桁数.isPresent()
	 */
	@Test
	public void constructorWithInv1() {
		NtsAssert.businessException("Msg_2248", () -> ItemInputControlTestHelper.caseInv1());
	}
	
	/**
	 * inv-2 （項目分類　＝　数字　||　時間）　&&　最大値.isPresent()
	 */
	@Test
	public void constructorWithInv2() {
		NtsAssert.businessException("Msg_2249", () -> ItemInputControlTestHelper.caseInv2(true));
		NtsAssert.businessException("Msg_2249", () -> ItemInputControlTestHelper.caseInv2(false));
	}
	
	/**
	 * inv-3 （項目分類　＝　数字　||　時間）　&&　最小値.isPresent()
	 */
	@Test
	public void constructorWithInv3() {
		NtsAssert.businessException("Msg_2250", () -> ItemInputControlTestHelper.caseInv3(true));
		NtsAssert.businessException("Msg_2250", () -> ItemInputControlTestHelper.caseInv3(false));
	}
	
	/**
	 * [C-1] 新規追加
	 * 項目分類　＝　文字
	 */
	@Test
	public void constructorWithText() {
		ItemInputControl itemText = ItemInputControlTestHelper.newAddition(ItemClassification.TEXT);
		NtsAssert.invokeGetters(itemText);
	}
	
	/**
	 * [C-1] 新規追加
	 * 項目分類　＝　数字
	 */
	@Test
	public void constructorWithNumber() {
		ItemInputControl itemNumber = ItemInputControlTestHelper.newAddition(ItemClassification.NUMBER);
		NtsAssert.invokeGetters(itemNumber);
	}
	
	/**
	 * [C-1] 新規追加
	 * 項目分類　＝　時間
	 */
	@Test
	public void constructorWithTime() {
		ItemInputControl itemTime = ItemInputControlTestHelper.newAddition(ItemClassification.TIME);
		NtsAssert.invokeGetters(itemTime);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * if　@必須　＝＝　true　&&　入力値.isEmpty()　＝＝　true
	 */
	@Test
	public void checkErrorsTest1() {
		ItemInputControl item = ItemInputControlTestHelper.caseRequire(true);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.empty();
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2228").addRaw(itemName.v()).build());
		
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * if　入力値.isEmpty()　＝＝　true
	 */
	@Test
	public void checkErrorsTest2() {
		ItemInputControl item = ItemInputControlTestHelper.caseRequire(false);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.empty();
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		assertThat(actual).isNotPresent();
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 文字
	 * if　＠桁数　＜　入力値.length()
	 */
	@Test
	public void checkErrorsTest3() {
		ItemInputControl item = ItemInputControlTestHelper.caseText(19);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("ActualItemUsageValue"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2229")
				.addRaw(itemName.v())
				.addRaw(item.getDigitsNo().get())
				.build());
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 文字
	 * if　＠桁数　>　入力値.length()
	 */
	@Test
	public void checkErrorsTest4() {
		ItemInputControl item = ItemInputControlTestHelper.caseText(21);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("ActualItemUsageValue"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		assertThat(actual).isNotPresent();
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 数字
	 * if　＠最大値　＜　入力値
	 */
	@Test
	public void checkErrorsTest5() {
		ItemInputControl item = ItemInputControlTestHelper.caseNumber(10, 1);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("11"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2246")
				.addRaw(itemName.v())
				.addRaw(item.getMinimum().get().v())
				.addRaw(item.getMaximum().get().v())
				.build());
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 数字
	 * if　＠最小値　>　入力値
	 */
	@Test
	public void checkErrorsTest6() {
		ItemInputControl item = ItemInputControlTestHelper.caseNumber(10, 1);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("0"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2246")
				.addRaw(itemName.v())
				.addRaw(item.getMinimum().get().v())
				.addRaw(item.getMaximum().get().v())
				.build());
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 数字
	 * if ＠最小値　<　入力値 < ＠最大値
	 */
	@Test
	public void checkErrorsTest7() {
		ItemInputControl item = ItemInputControlTestHelper.caseNumber(10, 1);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("9"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		assertThat(actual).isNotPresent();
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 時間
	 * if　＠最大値　＜　入力値
	 */
	@Test
	public void checkErrorsTest8() {
		ItemInputControl item = ItemInputControlTestHelper.caseTime(3600, 1000);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("4100"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2247")
				.addRaw(itemName.v())
				.addRaw(ItemInputControlTestHelper.formatTime(item.getMinimum().get().v()))
				.addRaw(ItemInputControlTestHelper.formatTime(item.getMaximum().get().v()))
				.build());
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 時間
	 * if　＠最小値　>　入力値
	 */
	@Test
	public void checkErrorsTest9() {
		ItemInputControl item = ItemInputControlTestHelper.caseTime(3601, 1000);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("900"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		ErrorMessage expect = new I18NErrorMessage(I18NText.main("Msg_2247")
				.addRaw(itemName.v())
				.addRaw(ItemInputControlTestHelper.formatTime(item.getMinimum().get().v()))
				.addRaw(ItemInputControlTestHelper.formatTime(item.getMaximum().get().v()))
				.build());
		assertThat(actual).isPresent();
		assertThat(actual.get().getErrorMessage()).isEqualByComparingTo(expect);	
	}
	
	/**
	 * [1] 項目制御のエラーをチェックする
	 * switch: @項目分類
	 * case 時間
	 * if ＠最小値　<　入力値 < ＠最大値
	 */
	@Test
	public void checkErrorsTest10() {
		ItemInputControl item = ItemInputControlTestHelper.caseTime(3600, 1000);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		Optional<ActualItemUsageValue> inputVal = Optional.of(new ActualItemUsageValue("3000"));
		Optional<ErrorItem> actual = item.checkErrors(itemNo, itemName, inputVal);
		
		assertThat(actual).isNotPresent();
	}
	
	/**
	 * [2] エラーメッセージを作成する
	 * case 文字
	 */
	@Test
	public void testCreateErrorItemCaseText() {
		// given
		ItemInputControl item = ItemInputControlTestHelper.caseText(5);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		
		// when
		ErrorItem errorItem = item.createErrorItem(itemNo, itemName);
		
		// then
		assertThat(errorItem.getErrorMessage().getMessage()).isNotBlank();
	}
	
	/**
	 * [2] エラーメッセージを作成する
	 * case 数字
	 */
	@Test
	public void testCreateErrorItemCaseNumber() {
		// given
		ItemInputControl item = ItemInputControlTestHelper.caseNumber(0, 3);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		
		// when
		ErrorItem errorItem = item.createErrorItem(itemNo, itemName);
		
		// then
		assertThat(errorItem.getErrorMessage().getMessage()).isNotBlank();
	}
	
	/**
	 * [2] エラーメッセージを作成する
	 * case 時間
	 */
	@Test
	public void testCreateErrorItemCaseTime() {
		// given
		ItemInputControl item = ItemInputControlTestHelper.caseTime(0, 300);
		EquipmentItemNo itemNo = new EquipmentItemNo("1");
		UsageItemName itemName = new UsageItemName("ItemName");
		
		// when
		ErrorItem errorItem = item.createErrorItem(itemNo, itemName);
		
		// then
		assertThat(errorItem.getErrorMessage().getMessage()).isNotBlank();
	}
}
