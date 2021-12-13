package nts.uk.ctx.at.schedule.app.command.task.taskpalette;

import java.util.List;

import lombok.Value;

/**
 * 
 * @author quytb
 *
 */
@Value
public class TaskPaletteCommand {
	
	private Integer unit;
	private String targetId;
	private Integer page;
	private String name;
	private String remarks;
	private List<Integer> position;
	private List<String> taskCode;
}
