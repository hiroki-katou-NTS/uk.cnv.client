package nts.uk.screen.at.app.kdw013.e;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.screen.at.app.kdw013.a.TaskDto;
import nts.uk.screen.at.app.kdw013.c.GetAvailableWorking;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.E：時刻なし作業内容.メニュー別OCD.作業項目を選択する
 * 
 * @author tutt
 *
 */
@Stateless
public class SelectTaskItem {

	@Inject
	private GetAvailableWorking getAvailableWorking;

	public List<TaskDto> selectTaskItem(GetAvailableWorkingCommand command) {
		List<Task> tasks = getAvailableWorking.get(command.getSId(), command.getRefDate(),
				new TaskFrameNo(command.getTaskFrameNo()), Optional.of(new TaskCode(command.getTaskCode())));

		return tasks.stream().map(TaskDto::toDto).collect(Collectors.toList());
	}

}
