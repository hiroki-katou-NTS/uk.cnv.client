package nts.uk.ctx.at.shared.dom.worktime.filtercriteria;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.絞り込み条件.就業時間帯の絞り込み条件NO
 */
@IntegerRange(min = 1, max = 10)
public class WorkHoursFilterConditionNo extends IntegerPrimitiveValue<WorkHoursFilterConditionNo> {

	private static final long serialVersionUID = 1L;

	public WorkHoursFilterConditionNo(Integer rawValue) {
		super(rawValue);
	}
}
