package nts.uk.screen.at.app.ksu003.getworkselectioninfor;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.app.query.task.TaskData;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * 指定された作業情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).A：個人スケジュール修正(日付別).メニュー別OCD.指定された作業情報を取得する
 * 
 * @author HieuLt
 *
 */
@Stateless
public class GetOneTask {
	@Inject
	private TaskingRepository taskingRepository;

	public Optional<TaskData> get(String companyID, int taskFrameNo, String taskCode) {
		Optional<Task> opt = taskingRepository.getOptionalTask(companyID, new TaskFrameNo(taskFrameNo),
				new TaskCode(taskCode));
		if (opt.isPresent()) {
			TaskData data = TaskData.toDto(opt.get());
			return Optional.of(data);
		} else {
			return Optional.empty();
		}
	}
}
