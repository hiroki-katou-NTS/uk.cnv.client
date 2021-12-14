package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.利用実績単位
 * @author NWS-DungDV
 *
 */
@StringMaxLength(4)
public class UsageRecordUnit extends StringPrimitiveValue<UsageRecordUnit> {
	private static final long serialVersionUID = 1L;

	public UsageRecordUnit(String value) {
		super(value);
	}
}
