package nts.uk.screen.at.app.kdw013.c;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.C:作業入力パネル.メニュー別OCD.作業入力パネルを起動する
 * 
 * @author tutt
 *
 */
@Stateless
public class StartWorkInputPanel {

	@Inject
	private GetAvailableWorking getAvailableWorking;

	/**
	 * 
	 * @param sId       社員ID
	 * @param refDate   基準日
	 * @param workGroup 作業グループ
	 * @return 利用可能作業
	 */
	public StartWorkInputPanelResult startPanel(String sId, GeneralDate refDate, WorkGroup workGroup) {

		// 1: <call>()
		List<Task> taskList1 = getAvailableWorking.get(sId, refDate, new TaskFrameNo(1), Optional.empty());

		// 2: <call>()
		List<Task> taskList2 = getAvailableWorking.get(sId, refDate, new TaskFrameNo(2),
				Optional.of(new TaskCode(workGroup.getWorkCD1().v())));
		// 3: <call>()
		List<Task> taskList3 = getAvailableWorking.get(sId, refDate, new TaskFrameNo(3),
				Optional.ofNullable(workGroup.getWorkCD2().map(x -> new TaskCode(x.v())).orElse(null)));

		// 4: <call>()
		List<Task> taskList4 = getAvailableWorking.get(sId, refDate, new TaskFrameNo(4),
				Optional.ofNullable(workGroup.getWorkCD3().map(x -> new TaskCode(x.v())).orElse(null)));

		// 5: <call>()
		List<Task> taskList5 = getAvailableWorking.get(sId, refDate, new TaskFrameNo(5),
				Optional.ofNullable(workGroup.getWorkCD4().map(x -> new TaskCode(x.v())).orElse(null)));

		return new StartWorkInputPanelResult(taskList1, taskList2, taskList3, taskList4, taskList5);

	}
}
