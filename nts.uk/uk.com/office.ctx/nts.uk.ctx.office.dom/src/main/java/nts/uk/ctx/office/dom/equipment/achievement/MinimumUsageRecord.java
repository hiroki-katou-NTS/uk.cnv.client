package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.利用実績最小値
 * @author NWS-DungDV
 *
 */
@IntegerRange(min = -99999999, max = 99999999)
public class MinimumUsageRecord extends IntegerPrimitiveValue<MinimumUsageRecord> {

	private static final long serialVersionUID = 1L;

	public MinimumUsageRecord(Integer no) {
		super(no);
	}
}
