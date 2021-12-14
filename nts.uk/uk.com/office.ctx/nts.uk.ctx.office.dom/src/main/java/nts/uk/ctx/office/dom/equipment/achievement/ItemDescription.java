package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目の説明
 * @author NWS-DungDV
 *
 */
@StringMaxLength(200)
public class ItemDescription extends StringPrimitiveValue<ItemDescription> {

	private static final long serialVersionUID = 1L;

	public ItemDescription(String rawValue) {
		super(rawValue);
	}
}
