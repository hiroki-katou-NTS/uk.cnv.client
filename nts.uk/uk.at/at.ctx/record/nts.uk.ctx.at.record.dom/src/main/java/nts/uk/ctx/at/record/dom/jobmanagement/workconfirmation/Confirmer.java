package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

import org.eclipse.persistence.internal.xr.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDateTime;

/**
 * @author thanhpv
 * @name 確認者
 * @path UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業確認.確認者
 */
@Getter
@AllArgsConstructor
public class Confirmer extends ValueObject {
	
	/** 社員ID */
	private String confirmSID;
	
	/** 確認日時 */
	private GeneralDateTime confirmDateTime;
	
	// [C-1] 新規作成
	/**
	 * confirmSID
	 * 
	 * @return 確認者
	 */
	public static Confirmer createNew(String confirmSID) {
		// $確認日時 = 日時#今()
		// return 確認者#確認者(社員ID,$確認日時)
		return new Confirmer(confirmSID, GeneralDateTime.now());
	}

}
