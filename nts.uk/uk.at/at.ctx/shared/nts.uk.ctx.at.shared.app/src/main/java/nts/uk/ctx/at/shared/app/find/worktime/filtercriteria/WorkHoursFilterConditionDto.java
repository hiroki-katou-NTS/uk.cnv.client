package nts.uk.ctx.at.shared.app.find.worktime.filtercriteria;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterCondition;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionName;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkHoursFilterConditionDto {

	/**
	 * NO
	 */
	private int no;

	/**
	 * 使用区分
	 */
	private boolean notUseAtr;

	/**
	 * 名称
	 */
	private String name;

	public static WorkHoursFilterConditionDto fromDomain(WorkHoursFilterCondition domain) {
		return new WorkHoursFilterConditionDto(domain.getNo().v(), domain.getNotUseAtr().isUse(),
				domain.getName().map(WorkHoursFilterConditionName::v).orElse(""));
	}
}
