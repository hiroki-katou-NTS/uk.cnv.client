package nts.uk.ctx.sys.portal.dom.flowmenu;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.ポータル.トップページの部品.フローメニュー作成.表示名称
 */
@StringMaxLength(20)
public class DisplayName extends StringPrimitiveValue<DisplayName> {

	private static final long serialVersionUID = 1L;

	public DisplayName(String rawValue) {
		super(rawValue);
	}
}
