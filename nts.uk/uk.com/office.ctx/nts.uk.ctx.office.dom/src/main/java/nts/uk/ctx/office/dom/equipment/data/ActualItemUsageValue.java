package nts.uk.ctx.office.dom.equipment.data;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.利用実績項目値
 */
@StringMaxLength(200)
public class ActualItemUsageValue extends StringPrimitiveValue<ActualItemUsageValue> {

	private static final long serialVersionUID = 1L;
	
	public ActualItemUsageValue(String rawValue) {
		super(rawValue);
	}
}
