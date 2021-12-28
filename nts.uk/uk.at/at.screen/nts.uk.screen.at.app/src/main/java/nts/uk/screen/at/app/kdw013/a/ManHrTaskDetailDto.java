package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrTaskDetail;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.TaskItemValue;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ManHrTaskDetailDto {
	/** 作業項目値 */
	private List<TaskItemValue> taskItemValues;

	/** 応援勤務枠No */
	private int supNo;

	public static ManHrTaskDetailDto fromDomain(ManHrTaskDetail taskDetail) {
		
		return new ManHrTaskDetailDto(taskDetail.getTaskItemValues(), taskDetail.getSupNo().v());
	}
}
