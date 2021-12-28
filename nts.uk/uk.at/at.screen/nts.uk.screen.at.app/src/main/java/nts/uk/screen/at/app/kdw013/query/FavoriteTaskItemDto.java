package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;

/**
 * 
 * @author tutt
 *
 */
@NoArgsConstructor
@Getter
public class FavoriteTaskItemDto {
	
	private String employeeId;
	
	private String favoriteId;
	
	private String taskName;
	
	private List<TaskContentDto> favoriteContents;

	public FavoriteTaskItemDto(FavoriteTaskItem domain) {
		this.employeeId = domain.getEmployeeId();
		this.favoriteId = domain.getFavoriteId();
		this.taskName = domain.getTaskName().v();
		this.favoriteContents = domain.getFavoriteContents().stream().map(m -> new TaskContentDto(m)).collect(Collectors.toList());
	}
}
