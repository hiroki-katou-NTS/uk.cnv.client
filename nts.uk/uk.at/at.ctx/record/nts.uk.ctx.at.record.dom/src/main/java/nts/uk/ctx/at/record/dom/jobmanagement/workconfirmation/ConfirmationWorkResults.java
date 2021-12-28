package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;

/**
 * @author thanhpv
 * @name 作業実績の確認
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.作業実績の確認
 */
@Getter
@AllArgsConstructor
public class ConfirmationWorkResults extends AggregateRoot {
	
	/** 対象者 */
	private String targetSID;
	/** 対象日 */
	private GeneralDate targetYMD;
	/** 確認者一覧 */
	private List<Confirmer> confirmers;
	
	//[C-1] 新規作成
	/**
	 *対象者	社員ID	 targetSID						
	 *対象日	年月日	targetYMD					
	 *確認者	社員ID 	confirmerId
	 * @return 作業実績の確認
	 */
	public static ConfirmationWorkResults createNew(String targetSID, GeneralDate targetYMD, String confirmerId) {
		// $確認者 = 確認者#新規作成(確認者)
		// return 作業実績の確認#作業実績の確認(対象者,対象日,$確認者)
		return new ConfirmationWorkResults(targetSID, targetYMD, Arrays.asList(Confirmer.createNew(confirmerId)));
	}
	
	// [1] 確認する
	/**
	 * 確認者 社員ID confirmerId
	 */
	public void confirm(String confirmerId) {
		// $確認者 = 確認者#新規作成(確認者)
		// @確認者一覧.追加($確認者)
		this.confirmers.add(Confirmer.createNew(confirmerId));
	}
	
	// [2] 解除する
	/**
	 * 確認者 社員ID confirmerId
	 */
	public void release(String confirmerId) {
		this.confirmers.removeIf(c -> c.getConfirmSID().equals(confirmerId));
	}

}
