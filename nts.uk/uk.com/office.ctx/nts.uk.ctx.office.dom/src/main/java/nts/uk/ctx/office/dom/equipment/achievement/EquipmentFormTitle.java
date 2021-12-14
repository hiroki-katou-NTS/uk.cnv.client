package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.設備帳票タイトル
 * @author NWS-DungDV
 *
 */
@StringMaxLength(20)
public class EquipmentFormTitle extends StringPrimitiveValue<EquipmentFormTitle> {

	private static final long serialVersionUID = 1L;

	public EquipmentFormTitle(String rawValue) {
		super(rawValue);
	}
}
