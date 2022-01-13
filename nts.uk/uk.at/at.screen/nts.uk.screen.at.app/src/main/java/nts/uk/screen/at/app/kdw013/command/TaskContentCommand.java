package nts.uk.screen.at.app.kdw013.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
public class TaskContentCommand {
	
	/** 項目ID: 工数実績項目ID*/
	private int itemId;
	
	/** 作業コード*/
	private String taskCode;
	
	public TaskContent toDomain() {
		return new TaskContent(this.itemId, new WorkCode(this.taskCode));
	}
}
