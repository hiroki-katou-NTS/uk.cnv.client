package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.表示幅
 * @author NWS-DungDV
 *
 */
@IntegerRange(min = 1, max = 999)
public class DisplayWidth  extends IntegerPrimitiveValue<DisplayWidth> {

	private static final long serialVersionUID = 1L;

	public DisplayWidth(Integer no) {
		super(no);
	}
}
