package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.利用項目名称
 * @author NWS-DungDV
 *
 */
@StringMaxLength(20)
public class UsageItemName extends StringPrimitiveValue<UsageItemName> {

	private static final long serialVersionUID = 1L;

	public UsageItemName(String rawValue) {
		super(rawValue);
	}
}
