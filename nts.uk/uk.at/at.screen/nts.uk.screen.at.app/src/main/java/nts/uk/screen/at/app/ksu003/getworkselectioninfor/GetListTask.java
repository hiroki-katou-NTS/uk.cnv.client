package nts.uk.screen.at.app.ksu003.getworkselectioninfor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto.TaskData;
import nts.uk.shr.com.context.AppContexts;
/**
 * 使用できる作業マスタを取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).A：個人スケジュール修正(日付別).メニュー別OCD.
 * @author HieuLt
 *
 */
@Stateless
public class GetListTask {
	@Inject
	private TaskingRepository taskingRepository;
	
	public List<TaskData> getListTask(GeneralDate referenceDate, int taskFrameNo) {
		String cid = AppContexts.user().companyId();

		List<TaskFrameNo> list = new ArrayList<>();
		list.add(new TaskFrameNo(taskFrameNo));
		// 取得する(1, 基準日):List<作業>
		List<Task> data = taskingRepository.getListTask(cid, referenceDate, list);
		List<TaskData> dtos = data.stream().map( c -> {
			TaskData dto = TaskData.toDto(c);
			return dto;
		}).collect(Collectors.toList());
		return dtos;

	}


}
