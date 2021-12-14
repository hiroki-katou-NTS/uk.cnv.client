package nts.uk.ctx.office.dom.equipment.achievement;

import java.util.Optional;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.eclipse.persistence.internal.xr.ValueObject;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.error.I18NErrorMessage;
import nts.arc.i18n.I18NText;
import nts.gul.text.StringUtil;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目入力制御
 * 
 * @author NWS-DungDV
 *
 */
@Getter
public class ItemInputControl extends ValueObject {
	// 項目分類
	@Required
	private ItemClassification itemCls;

	// 必須
	private boolean require;

	// 桁数
	private Optional<DigitsNumber> digitsNo;

	// 最大値
	private Optional<MaximumUsageRecord> maximum;

	// 最小値
	private Optional<MinimumUsageRecord> minimum;

	/**
	 * [C-1] 新規追加
	 * 
	 * @param itemCls   項目分類
	 * @param mandatory 必須
	 * @param digitsNo  桁数
	 * @param maximum   最大値
	 * @param minimum   最小値
	 */
	public ItemInputControl(ItemClassification itemCls, boolean require, Optional<DigitsNumber> digitsNo,
			Optional<MaximumUsageRecord> maximum, Optional<MinimumUsageRecord> minimum) {
		// inv-1 項目分類 ＝ 文字 && 桁数.isPresent()
		if (itemCls.equals(ItemClassification.TEXT) && !digitsNo.isPresent()) {
			throw new BusinessException("Msg_2248", "itemNo");
		}
		// inv-2 （項目分類 ＝ 数字 || 時間） && 最大値.isPresent()
		if ((itemCls.equals(ItemClassification.NUMBER) || itemCls.equals(ItemClassification.TIME))
				&& !maximum.isPresent()) {
			throw new BusinessException("Msg_2249", "itemNo");
		}
		// inv-3 （項目分類 ＝ 数字 || 時間） && 最小値.isPresent()
		if ((itemCls.equals(ItemClassification.NUMBER) || itemCls.equals(ItemClassification.TIME))
				&& !minimum.isPresent()) {
			throw new BusinessException("Msg_2250", "itemNo");
		}

		// [mapping]
		this.itemCls = itemCls;
		this.require = require;
		this.digitsNo = digitsNo;
		this.maximum = maximum;
		this.minimum = minimum;
	}

	/**
	 * [1] 項目制御のエラーをチェックする
	 * 
	 * @param itemNo   項目NO
	 * @param itemName 項目名称
	 * @param inputVal 入力値
	 * @return Optional<エラー項目>
	 */
	public Optional<ErrorItem> checkErrors(EquipmentItemNo itemNo, UsageItemName itemName,
			Optional<ActualItemUsageValue> optInputVal) {
		if (this.require && !this.isValidInput(optInputVal)) {
			I18NText errorMessage = I18NText.main("Msg_2228").addRaw(itemName.v()).build();
			return Optional.of(new ErrorItem(itemNo, new I18NErrorMessage(errorMessage)));
		}

		if (!this.isValidInput(optInputVal)) {
			return Optional.empty();
		}
		String inputVal = optInputVal.get().v();

		if ((this.digitsNo.isPresent() && this.digitsNo.get().lessThan(inputVal.length()))
				|| (this.maximum.isPresent() && this.maximum.get().v() < Double.valueOf(inputVal))
				|| (this.minimum.isPresent() && this.minimum.get().v() > Double.valueOf(inputVal))) {
			return Optional.of(this.createErrorItem(itemNo, itemName));
		}
		return Optional.empty();
	}

	/**
	 * [2] エラーメッセージを作成する
	 * @param itemNo		項目NO
	 * @param itemName		項目名称
	 * @return エラー項目
	 */
	public ErrorItem createErrorItem(EquipmentItemNo itemNo, UsageItemName itemName) {
		I18NText errorMessage = null;
		switch (this.itemCls) {
		case TEXT:
			errorMessage = I18NText.main("Msg_2229").addRaw(itemName.v()).addRaw(this.digitsNo.get()).build();
			break;
		case NUMBER:
			errorMessage = I18NText.main("Msg_2246").addRaw(itemName.v()).addRaw(this.minimum.get().v())
					.addRaw(this.maximum.get().v()).build();
			break;
		case TIME:
			errorMessage = I18NText.main("Msg_2247").addRaw(itemName.v())
					.addRaw(this.formatTime(this.minimum.get().v())).addRaw(this.formatTime(this.maximum.get().v()))
					.build();
			break;
		}
		return new ErrorItem(itemNo, new I18NErrorMessage(errorMessage));
	}

	/**
	 * Format data to H:MM
	 * 
	 * @param minute
	 * @return H:MM
	 */
	private String formatTime(Integer minute) {
		int h = Math.abs(minute) / 60;
		int m = Math.abs(minute) % 60;
		return h + ":" + (m < 10 ? "0" + m : m);
	}

	private boolean isValidInput(Optional<ActualItemUsageValue> optValue) {
		return optValue.isPresent() && !StringUtil.isNullOrEmpty(optValue.get().v(), true);
	}
}
