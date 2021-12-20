package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

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
	
	public void addConfirmer(Confirmer confirmer) {
		this.confirmers.add(confirmer);
	}
	
	public void removeConfirmer(String confirmSid) {
		this.confirmers.removeIf(c->c.getConfirmSID().equals(confirmSid));
	}

}
