package nts.uk.ctx.at.shared.dom.worktype;

import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.勤務種類.日別実績の日数カウント
 */
@HalfIntegerRange(min = 0, max = 1.0)
public class DailyActualDayCount extends HalfIntegerPrimitiveValue<DailyActualDayCount> {

	private static final long serialVersionUID = 1L;

	public DailyActualDayCount(Double rawValue) {
		super(rawValue);
	}
}
