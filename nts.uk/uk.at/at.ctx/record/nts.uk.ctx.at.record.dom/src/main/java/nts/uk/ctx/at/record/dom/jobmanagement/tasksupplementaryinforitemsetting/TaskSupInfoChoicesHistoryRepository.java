package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;

/**
 * RP: 作業補足情報の選択肢履歴Repository
 * 
 * @author tutt
 *
 */
public interface TaskSupInfoChoicesHistoryRepository {
	
	/**
	 * @name [1] insert(作業補足情報の選択肢履歴, 作業補足情報の選択肢詳細)
	 */
	void insert(TaskSupInfoChoicesHistory history, TaskSupInfoChoicesDetail detail);
	
	/**
	 * @name [2] insert(作業補足情報の選択肢詳細)
	 */
	void insert(TaskSupInfoChoicesDetail detail);
	
	void insert(TaskSupInfoChoicesHistory history);
	
	/**
	 * @name [3] update(作業補足情報の選択肢履歴)
	 */
	void update(TaskSupInfoChoicesHistory history);
	
	/**
	 * @name [4] update(作業補足情報の選択肢詳細)
	 */
	void update(TaskSupInfoChoicesDetail detail);
	
	/**
	 * @name [5] delete(履歴ID)
	 */
	void delete(String historyId);
	
	/**
	 * @name [6] delete(履歴ID,コード)
	 */
	void delete(String historyId, ChoiceCode code);
	
	/**
	 * @name [7] 全ての履歴を取得する
	 * @input 会社ID
	 * @output List<作業補足情報の選択肢履歴>
	 */
	List<TaskSupInfoChoicesHistory> getAll(String companyId);
	
	/**
	 * @name [8] 作業補足情報の選択肢詳細を取得する		
	 * @input 履歴ID
	 * @output List<作業補足情報の選択肢詳細>
	 */
	List<TaskSupInfoChoicesDetail> get(String historyId);
	
	/**
	 * @name [9] 作業補足情報の選択肢詳細を取得する
	 * @input 会社ID, 項目ID, 基準日
	 * @output List<作業補足情報の選択肢詳細>
	 */
	List<TaskSupInfoChoicesDetail> get(String companyId, int itemId, GeneralDate refDate);
	
	
	Optional<TaskSupInfoChoicesDetail> get(String historyId, int itemId, ChoiceCode code);
	
	List<TaskSupInfoChoicesDetail> get(List<String> historyIds);
	
	Optional<TaskSupInfoChoicesHistory> get(String historyId, int itemId);
	
	List<TaskSupInfoChoicesDetail> getListForCid(String cid);
}
