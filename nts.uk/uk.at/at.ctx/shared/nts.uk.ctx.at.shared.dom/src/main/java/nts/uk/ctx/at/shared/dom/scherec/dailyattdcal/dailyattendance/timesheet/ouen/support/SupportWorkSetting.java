package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/** 応援・作業設定 仮実装。ドメインが確定したら削除する。*/
@Getter
@AllArgsConstructor
public class SupportWorkSetting implements DomainAggregate {
	
	/** 会社ID */
	private String companyId;
	
	/** 利用する */
	private NotUseAtr isUse;
	
	/** 移動時間の計上先 */
	private AccountingOfMoveTime accountingOfMoveTime;
	
	/**
	 * 仮実装。ドメインが確定したら削除する。
	 * @return
	 */
	public static SupportWorkSetting defaultValue() {
		return new SupportWorkSetting(AppContexts.user().companyId(), NotUseAtr.USE, AccountingOfMoveTime.DESTINATION);
	}
}
