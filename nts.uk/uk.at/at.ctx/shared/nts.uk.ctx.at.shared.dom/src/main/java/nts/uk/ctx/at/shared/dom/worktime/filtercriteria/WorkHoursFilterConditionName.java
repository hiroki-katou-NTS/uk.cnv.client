package nts.uk.ctx.at.shared.dom.worktime.filtercriteria;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.就業時間帯.絞り込み条件.就業時間帯の絞り込み条件名称
 */
@StringMaxLength(12)
public class WorkHoursFilterConditionName extends StringPrimitiveValue<WorkHoursFilterConditionName> {

	private static final long serialVersionUID = 1L;

	public WorkHoursFilterConditionName(String rawValue) {
		super(rawValue);
	}
}
