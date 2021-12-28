package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;

/**
 * DS: 作業実績の確認を解除する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.作業実績の確認を解除する
 * @author ThanhPV
 */

public class CancelConfirmationWorkResultsService {
	
//■Public
	/**
	 * [1] 登録する
	 * @input require
	 * @input targetSid 対象者
	 * @input targetYMD 対象日
	 * @input confirmSid 確認者	
	 * @output Atomtask
	 */
	public static Optional<AtomTask> check(Require require, String targetSid, GeneralDate targetYMD, String confirmSid) {
		//$作業実績の確認 = require.作業実績の確認を取得する(対象者,対象日)
		Optional<ConfirmationWorkResults> confirmationWorkResults = require.get(targetSid, targetYMD);
		//if $作業実績の確認.isEmpty	
		if (!confirmationWorkResults.isPresent()) 
			//	return Optional.Empty
			return Optional.empty();
		//$作業実績の確認.解除する(確認者)
		confirmationWorkResults.get().release(confirmSid);
		//	if $削除後の確認状態.確認者一覧.isEmpty
		if(confirmationWorkResults.get().getConfirmers().isEmpty())
			//return Atom Task:																			
				//require.作業実績の確認を削除する($作業実績の確認)	
			return Optional.of(AtomTask.of(() -> {
				require.delete(confirmationWorkResults.get());
			}));
		//return Atom Task:																				
			//require.作業実績の確認を更新する($削除後の確認状態)
		return Optional.of(AtomTask.of(() -> {
			require.update(confirmationWorkResults.get());
		}));
	}
	
//■Require
	public static interface Require {
		// 	[R-1] 作業実績の確認を取得する
		//	作業実績の確認Repository.Get(対象者,対象日)														
		Optional<ConfirmationWorkResults> get(String targetSid, GeneralDate targetYMD);

		//  [R-2] 作業実績の確認を削除する
		//	作業実績の確認Repository.Delete(作業実績の確認)
		void delete(ConfirmationWorkResults confirmationWorkResults);

		// [R-3] 作業実績の確認を更新する
		//  作業実績の確認Repository.Update(作業実績の確認)	
		void update(ConfirmationWorkResults confirmationWorkResults);
	}

}
