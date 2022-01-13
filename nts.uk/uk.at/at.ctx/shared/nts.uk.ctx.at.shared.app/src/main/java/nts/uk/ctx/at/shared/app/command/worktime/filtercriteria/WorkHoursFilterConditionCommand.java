package nts.uk.ctx.at.shared.app.command.worktime.filtercriteria;

import java.util.Optional;

import lombok.Value;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterCondition;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionName;
import nts.uk.ctx.at.shared.dom.worktime.filtercriteria.WorkHoursFilterConditionNo;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Value
public class WorkHoursFilterConditionCommand {

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

	public WorkHoursFilterCondition toDomain(String cid) {
		return new WorkHoursFilterCondition(cid, new WorkHoursFilterConditionNo(this.no),
				NotUseAtr.valueOf(this.notUseAtr),
				Optional.ofNullable(this.name).map(WorkHoursFilterConditionName::new));
	}
}
