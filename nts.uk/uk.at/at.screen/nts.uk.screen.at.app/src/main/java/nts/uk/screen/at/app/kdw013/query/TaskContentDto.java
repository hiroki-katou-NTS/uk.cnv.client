package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class TaskContentDto {
	
	/** 項目ID: 工数実績項目ID*/
	public int itemId;
	
	/** 作業コード*/
	public String taskCode;
	
	public TaskContentDto(TaskContent domain) {
		this.itemId = domain.getItemId();
		this.taskCode = domain.getTaskCode().v();
	}
}
