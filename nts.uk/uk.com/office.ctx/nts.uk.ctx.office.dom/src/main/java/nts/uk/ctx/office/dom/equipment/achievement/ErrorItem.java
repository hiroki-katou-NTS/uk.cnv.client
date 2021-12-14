package nts.uk.ctx.office.dom.equipment.achievement;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.error.ErrorMessage;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.エラー項目
 */
@Data
@AllArgsConstructor
public class ErrorItem {

	/**
	 * 項目NO
	 */
	private EquipmentItemNo itemNo;
	
	/**
	 * Msgエラー
	 */
	private ErrorMessage errorMessage;
}
