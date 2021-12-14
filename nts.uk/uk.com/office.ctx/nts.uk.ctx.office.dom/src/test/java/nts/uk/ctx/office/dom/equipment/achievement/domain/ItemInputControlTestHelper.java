package nts.uk.ctx.office.dom.equipment.achievement.domain;

import java.util.Optional;

import nts.uk.ctx.office.dom.equipment.achievement.DigitsNumber;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.MaximumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.MinimumUsageRecord;

public class ItemInputControlTestHelper {
	
	/**
	 * inv-1
	 */
	public static ItemInputControl caseInv1() {
		// 項目分類　＝　文字
		ItemClassification itemCls = ItemClassification.TEXT;
		boolean require = true;
		// 桁数.isPresent()
		Optional<DigitsNumber> digitsNo = Optional.empty();
		Optional<MaximumUsageRecord> maximum = Optional.empty();
		Optional<MinimumUsageRecord> minimum = Optional.empty();
		return new ItemInputControl(itemCls, require, digitsNo, maximum, minimum);
	}
	
	/**
	 * inv-2
	 * @param isNumber 項目分類　＝　数字
	 * @param isPresentMax 最大値.isPresent()
	 */
	public static ItemInputControl caseInv2(boolean isNumber) {
		// 項目分類　＝　数字　||　時間
		ItemClassification itemCls = isNumber
				? ItemClassification.NUMBER
				: ItemClassification.TIME;
		boolean require = true;
		Optional<DigitsNumber> digitsNo = Optional.empty();
		// 最大値.isPresent()
		Optional<MaximumUsageRecord> maximum = Optional.empty();
		Optional<MinimumUsageRecord> minimum = Optional.empty();
		return new ItemInputControl(itemCls, require, digitsNo, maximum, minimum);
	}
	
	/**
	 * inv-3
	 * @param isNumber 項目分類　＝　数字
	 */
	public static ItemInputControl caseInv3(boolean isNumber) {
		// 項目分類　＝　数字　||　時間
		ItemClassification itemCls = isNumber
				? ItemClassification.NUMBER
				: ItemClassification.TIME;
		boolean require = true;
		Optional<DigitsNumber> digitsNo = Optional.empty();
		Optional<MaximumUsageRecord> maximum = Optional.of(new MaximumUsageRecord(10));
		// 最小値.isPresent()
		Optional<MinimumUsageRecord> minimum = Optional.empty();
		return new ItemInputControl(itemCls, require, digitsNo, maximum, minimum);
	}
	
	/**
	 * 新規追加
	 */
	public static ItemInputControl newAddition(ItemClassification classification) {
		ItemClassification itemCls = classification;
		boolean require = true;
		Optional<DigitsNumber> digitsNo = Optional.of(new DigitsNumber(10));
		Optional<MaximumUsageRecord> maximum = Optional.of(new MaximumUsageRecord(10));
		Optional<MinimumUsageRecord> minimum = Optional.of(new MinimumUsageRecord(1));
		return new ItemInputControl(itemCls, require, digitsNo, maximum, minimum);
	}
	
	/**
	 * @必須: true | false
	 */
	public static ItemInputControl caseRequire(boolean require) {
		ItemClassification itemCls = ItemClassification.TEXT;
		Optional<DigitsNumber> digitsNo = Optional.of(new DigitsNumber(10));
		Optional<MaximumUsageRecord> maximum = Optional.of(new MaximumUsageRecord(10));
		Optional<MinimumUsageRecord> minimum = Optional.of(new MinimumUsageRecord(1));
		return new ItemInputControl(itemCls, require, digitsNo, maximum, minimum);
	}
	
	/**
	 * case 文字
	 */
	public static ItemInputControl caseText(Integer digitsNumber) {
		ItemClassification itemCls = ItemClassification.TEXT;
		Optional<DigitsNumber> digitsNo = Optional.of(new DigitsNumber(digitsNumber));
		Optional<MaximumUsageRecord> maximum = Optional.empty();
		Optional<MinimumUsageRecord> minimum = Optional.empty();
		return new ItemInputControl(itemCls, true, digitsNo, maximum, minimum);
	}
	
	/**
	 * case 数字
	 */
	public static ItemInputControl caseNumber(Integer max, Integer min) {
		ItemClassification itemCls = ItemClassification.NUMBER;
		Optional<DigitsNumber> digitsNo = Optional.empty();
		Optional<MaximumUsageRecord> maximum = Optional.of(new MaximumUsageRecord(max));
		Optional<MinimumUsageRecord> minimum = Optional.of(new MinimumUsageRecord(min));
		return new ItemInputControl(itemCls, true, digitsNo, maximum, minimum);
	}
	
	/**
	 * case 時間
	 */
	public static ItemInputControl caseTime(Integer max, Integer min) {
		ItemClassification itemCls = ItemClassification.TIME;
		Optional<DigitsNumber> digitsNo = Optional.empty();
		Optional<MaximumUsageRecord> maximum = Optional.of(new MaximumUsageRecord(max));
		Optional<MinimumUsageRecord> minimum = Optional.of(new MinimumUsageRecord(min));
		return new ItemInputControl(itemCls, true, digitsNo, maximum, minimum);
	}
	
	/**
	 * Format data to H:MM
	 * @param minute
	 * @return H:MM
	 */
	public static String formatTime(Integer minute) {
		int h = Math.abs(minute) / 60;
		int m = Math.abs(minute) % 60;
		return h + ":" + (m < 10 ? "0" + m : m);
	}
}
