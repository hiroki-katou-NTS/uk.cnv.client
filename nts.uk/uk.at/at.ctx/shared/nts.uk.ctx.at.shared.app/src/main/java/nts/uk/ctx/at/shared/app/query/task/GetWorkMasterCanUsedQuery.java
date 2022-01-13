package nts.uk.ctx.at.shared.app.query.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.shr.com.context.AppContexts;

/**
 * 使用できる作業マスタを取得する
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.shared(勤務予定、勤務実績).作業管理.作業マスタ.App.使用できる作業マスタを取得する
 * 
 * @author HieuLt
 *
 */
@Stateless
public class GetWorkMasterCanUsedQuery {

	@Inject
	TaskingRepository repo;

	List<TaskData> get(int taskFrameNo, GeneralDate referenceDate) {
		//1:get(ログイン会社ID,基準日, 作業枠NO): List<作業>
		String cid = AppContexts.user().companyId();
		List<TaskFrameNo> listTaskFrameNo = new ArrayList<>();
		listTaskFrameNo.add(new TaskFrameNo(taskFrameNo));
		List<Task> data = repo.getListTask(cid, referenceDate, listTaskFrameNo);
		List<TaskData> dtos = data.stream().map(c -> {
			TaskData dto = TaskData.toDto(c);
			return dto;
		}).collect(Collectors.toList());
		return dtos;
	}
}
