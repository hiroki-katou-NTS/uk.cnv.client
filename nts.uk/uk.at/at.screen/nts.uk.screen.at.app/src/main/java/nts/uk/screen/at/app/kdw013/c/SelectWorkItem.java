package nts.uk.screen.at.app.kdw013.c;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.C:作業入力パネル.メニュー別OCD.作業項目を選択する
 * 
 * @author tutt
 *
 */
@Stateless
public class SelectWorkItem {

	@Inject
	private GetAvailableWorking getAvailableWorking;

	/**
	 * 
	 * @param sId         社員ID
	 * @param refDate     基準日
	 * @param taskFrameNo 作業枠NO
	 * @param taskCode    上位枠作業コード
	 * @return
	 */
	public List<Task> select(String sId, GeneralDate refDate, TaskFrameNo taskFrameNo, Optional<TaskCode> taskCode) {

		return getAvailableWorking.get(sId, refDate, taskFrameNo, taskCode);
	}

}