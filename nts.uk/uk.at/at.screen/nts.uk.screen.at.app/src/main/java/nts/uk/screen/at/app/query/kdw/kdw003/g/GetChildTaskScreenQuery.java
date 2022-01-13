package nts.uk.screen.at.app.query.kdw.kdw003.g;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;


import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.shr.com.context.AppContexts;

/**
 * 子作業を取得する
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.G：初期作業選択設定.メニュー別OCD.子作業を取得する.子作業を取得する
 * @author quytb
 *
 */

@Stateless
public class GetChildTaskScreenQuery {	
	@Inject
	private TaskingRepository taskingRepository;
	public List<TaskItemDto> getChildTasks(TaskFrameNo taskFrameNo, TaskCode code){
		List<TaskItemDto> results = new ArrayList<TaskItemDto>();
		String cid = AppContexts.user().companyId();
		List<Task> listTask = taskingRepository.getListChildTask(cid, taskFrameNo, code);
		
		results = listTask.stream().map(task -> {
			return  TaskItemDto.builder().frameNo(task.getTaskFrameNo().v())
					.taskCode(task.getCode().v())
					.taskName(task.getDisplayInfo().getTaskName().v())
					.taskAbName(task.getDisplayInfo().getTaskAbName().v())
					.startDate(task.getExpirationDate().start().toString())
					.endDate(task.getExpirationDate().end().toString())
//					.listFrameNoUseAtr(listFrameUseAtr)
//					.listFrameName(listFrameName)
					.build();
		}).collect(Collectors.toList());	
		return results;
	}
}
