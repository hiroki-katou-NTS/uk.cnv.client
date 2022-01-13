package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.kdw013.a.TaskDto;
import nts.uk.screen.at.app.kdw013.c.StartWorkInputPanelResult;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StartWorkInputPanelDto {

	/** 利用可能作業1リスト */
	private List<TaskDto> taskListDto1;

	/** 利用可能作業2リスト */
	private List<TaskDto> taskListDto2;

	/** 利用可能作業3リスト */
	private List<TaskDto> taskListDto3;

	/** 利用可能作業4リスト */
	private List<TaskDto> taskListDto4;

	/** 利用可能作業5リスト */
	private List<TaskDto> taskListDto5;

	public static StartWorkInputPanelDto toDto(StartWorkInputPanelResult result) {

		return new StartWorkInputPanelDto(setTaskListDto(result.getTaskList1()), 
				setTaskListDto(result.getTaskList2()),
				setTaskListDto(result.getTaskList3()), 
				setTaskListDto(result.getTaskList4()),
				setTaskListDto(result.getTaskList5()));
	}

	public static List<TaskDto> setTaskListDto(List<Task> taskList) {
		return taskList.stream().map(m -> TaskDto.toDto(m))
				.collect(Collectors.toList());
	}
}
