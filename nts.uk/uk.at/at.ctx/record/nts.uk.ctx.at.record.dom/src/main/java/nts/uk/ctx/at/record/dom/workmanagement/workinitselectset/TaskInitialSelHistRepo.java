package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * 作業初期選択履歴Repository
 * @author HieuLt
 *
 */
public interface TaskInitialSelHistRepo {

	/**	[1] Insert(作業初期選択履歴) **/
	public void insert(TaskInitialSelHist taskInitialSelHist);
	
	/** [2] Update(作業初期選択履歴)**/
	public void update(TaskInitialSelHist taskInitialSelHist);
	
	/** [3] Delete(社員ID) **/
	public void delete(String empId);
	
	/** [4] Get**/
	public Optional<TaskInitialSelHist> getById(String empId);
	
	/** [5] 基準日時点の初期選択する作業項目を取得する **/
	public Optional<TaskItem> getByBaseDate(String empId , GeneralDate baseDate );
	
	/** [6] Get* **/
	public List<TaskInitialSelHist> getByCid (String companyId);
	
}
