package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import java.util.Optional;

import nts.arc.error.BusinessException;
import nts.arc.task.tran.AtomTask;

/**
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務実績.作業管理.作業初期選択設定
 * 作業初期選択履歴を複写する
 * 
 * @author Hieult
 */
public class CopyTaskInitialSelHisService {
	
	/**
	 * [1] 複写する
	 * @param require
	 * @param empCopy
	 * @param empMain
	 * @return
	 */
	public static AtomTask copy(Require require, String empCopy, String empMain) {
		// $複写元設定 = require.作業初期選択履歴を取得する(複写元社員)
		Optional<TaskInitialSelHist> optTask = require.getById(empCopy);
		// if $複写元設定.isEmpty
		// BusinessException: Msg_1185
		AtomTask atomTaks = AtomTask.none();
		if (!optTask.isPresent()) {
			throw new BusinessException("Msg_1185");
		} 
		//$複写先の既存設定 = require.作業初期選択履歴を取得する(複写先社員)
		Optional<TaskInitialSelHist> mainTask = require.getById(empMain);
		// $登録履歴 = 作業初期選択履歴#作業初期選択履歴(複写先社員,$複写元設定.履歴リスト)
		TaskInitialSelHist data = new TaskInitialSelHist(empMain, optTask.get().getLstHistory());
		atomTaks = AtomTask.of(() -> {
			// $複写先の既存設定 : $登録対象.add(require.削除する(複写先社員)
			if (mainTask.isPresent()) {
				require.delete(empMain);
			}
			require.insert(data);
		});
		
		return atomTaks;
	}

	public static interface Require {
		/**
		 * [R-1] 作業初期選択履歴を取得する 作業初期選択履歴Repository.Get(社員ID)
		 */
		Optional<TaskInitialSelHist> getById(String empId);

		/**
		 * [R-2] 追加する 作業初期選択履歴Repository.Insert(作業初期選択履歴)
		 */
		void insert(TaskInitialSelHist taskInitialSelHist);

		/**
		 * [R-3] 削除する 作業初期選択履歴Repository.Delete(社員ID)
		 */
		void delete(String empId);
	}
}
