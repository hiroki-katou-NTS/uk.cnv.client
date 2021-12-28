package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;

/**
 * DS: 作業実績を確認する	
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.作業実績を確認する
 * @author ThanhPV
 */

public class CheckWorkPerformanceService {
	
//■Public
	/**
	 * [1] 登録する
	 * @input require
	 * @input targetSid 対象者
	 * @input targetYMD 対象日
	 * @input confirmSid 確認者	
	 * @output Atomtask
	 */
	public static AtomTask check(Require require, String targetSid, GeneralDate targetYMD, String confirmSid) {
		
		//$作業実績の確認 = require.作業実績の確認を取得する(対象者,対象日)
		Optional<ConfirmationWorkResults> confirmationWorkResults = require.get(targetSid, targetYMD);
		//if $作業実績の確認.isEmpty	
		if (!confirmationWorkResults.isPresent()) {
			//	$作業実績の確認 = 作業実績の確認#新規作成(対象者,対象日,確認者)		
			ConfirmationWorkResults confirmationWorkResultsNew = ConfirmationWorkResults.createNew(targetSid, targetYMD, confirmSid);
			//return Atom Task:																			
				//require.作業実績の確認を追加する($作業実績の確認)
			return AtomTask.of(() -> {
				require.insert(confirmationWorkResultsNew);
			});
		}
		//	$作業実績の確認.確認する(確認者)
		confirmationWorkResults.get().confirm(confirmSid);
		//return Atom Task:																				
			//require.作業実績の確認を更新する($作業実績の確認)
		return AtomTask.of(() -> {
			require.update(confirmationWorkResults.get());
		});
	}
	
//■Require
	public static interface Require {
		// [R-1] 作業実績の確認を取得する
		//	作業実績の確認Repository.Get(対象者,対象日)	
		Optional<ConfirmationWorkResults> get(String targetSid, GeneralDate targetYMD);

		// [R-2] 作業実績の確認を追加する
		// 作業実績の確認Repository.Insert(作業実績の確認)	
		void insert(ConfirmationWorkResults confirmationWorkResults);

		// [R-3] 作業実績の確認を更新する
		//	作業実績の確認Repository.Update(作業実績の確認)	
		void update(ConfirmationWorkResults confirmationWorkResults);
	}

}
