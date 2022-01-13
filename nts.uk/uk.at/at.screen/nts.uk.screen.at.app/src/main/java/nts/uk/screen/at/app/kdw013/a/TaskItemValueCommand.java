package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.TaskItemValue;

@Getter
@AllArgsConstructor
public class TaskItemValueCommand {
	/** 工数実績項目ID */
	private Integer itemId;

	/** 値 */
	private String value;

	public static TaskItemValue toDomain(TaskItemValueCommand ti) {

		return new TaskItemValue(ti.getItemId(), ti.getValue());
	}
}
