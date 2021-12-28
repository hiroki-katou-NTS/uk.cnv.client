package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;

@Getter
@AllArgsConstructor
public class ManHrRecordConvertResultCommand {
	/** 年月日 */
	private GeneralDate ymd;

	/** 作業リスト */
	private List<ManHrTaskDetailCommand> taskList;

	/** 実績内容 */
	private List<ItemValueCommand> manHrContents;

	public static ManHrRecordConvertResult toDomain(ManHrRecordConvertResultCommand mh) {
		
		return new ManHrRecordConvertResult(mh.getYmd()
				, mh.getTaskList().stream().map(ht-> ManHrTaskDetailCommand.toDomain(ht)).collect(Collectors.toList())
				, mh.getManHrContents().stream().map(ht-> ItemValueCommand.toDomain(ht)).collect(Collectors.toList())
				);
	}
}
