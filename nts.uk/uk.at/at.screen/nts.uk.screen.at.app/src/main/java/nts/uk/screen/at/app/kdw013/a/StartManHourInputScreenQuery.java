package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.工数入力を起動する
 * 
 * @author tutt 
 * <<ScreenQuery>>
 */
@Stateless
public class StartManHourInputScreenQuery {

	@Inject
	private TaskFrameUsageSettingRepository taskFrameUsageSettingRepo;

	@Inject
	private TaskingRepository taskingRepo;

	@Inject
	private WorkLocationRepository workLocationRepo;

	public StartManHourInput startManHourInput() {
		StartManHourInput startManHourInput = new StartManHourInput();
		String cid = AppContexts.user().companyId();

		// 1: get()
		TaskFrameUsageSetting taskFrameUsageSetting = taskFrameUsageSettingRepo.getWorkFrameUsageSetting(cid);
		startManHourInput.setTaskFrameUsageSetting(taskFrameUsageSetting);

		// 2: get()
		List<Task> tasks = taskingRepo.getListTask(cid);
		startManHourInput.setTasks(tasks);

		// 3: 契約コード条件として勤務場所を取得する(ログイン契約コード)
		List<WorkLocation> workLocations = workLocationRepo.findAll(AppContexts.user().contractCode());
		startManHourInput.setWorkLocations(workLocations);

		return startManHourInput;
	}

}
