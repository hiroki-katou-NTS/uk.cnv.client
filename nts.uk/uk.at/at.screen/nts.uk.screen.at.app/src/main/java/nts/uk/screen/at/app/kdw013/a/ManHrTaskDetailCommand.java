package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrTaskDetail;

@Getter
@AllArgsConstructor
public class ManHrTaskDetailCommand {
	/** 作業項目値 */
	private List<TaskItemValueCommand> taskItemValues;

	/** 応援勤務枠No */
	private Integer supNo;

	public static ManHrTaskDetail toDomain(ManHrTaskDetailCommand ht) {

		return new ManHrTaskDetail(
				ht.getTaskItemValues().stream().map(ti-> TaskItemValueCommand.toDomain(ti)).collect(Collectors.toList())
				, new SupportFrameNo(ht.getSupNo()));
	}
}
