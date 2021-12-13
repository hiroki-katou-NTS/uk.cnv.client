package nts.uk.ctx.sys.auth.dom.wkpmanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.IdentifierUtil;
/**
 * 職場管理者
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.職場管理者.職場管理者
 * @author lan_lt
 *
 */
@AllArgsConstructor
@Getter
public class WorkplaceManager implements DomainAggregate{
	/** ID */
	private final String workplaceManagerId;
	
	/** 職場ID */
	private final String workplaceId;
	
	/** 社員ID */
	private final String employeeId;

	/** 履歴期間 */
	@Setter
	private DatePeriod historyPeriod;
	
	/**
	 * 新規作成する
	 * @param workplaceId 職場ID
	 * @param employeeId 社員ID
	 * @param historyPeriod 履歴期間
	 * @return
	 */
	public static WorkplaceManager createNew(String workplaceId, String employeeId, DatePeriod historyPeriod) {
		return new WorkplaceManager(IdentifierUtil.randomUniqueId(), workplaceId, employeeId, historyPeriod);
	}

	public void validate() {
		/*
		 * 対象時間（開始日：終了日）大小チェック
		 */
		if (historyPeriod.start().after(historyPeriod.end())){
			throw new BusinessException("Msg_136");
		} else {
			/*
			 * 終了日はシステム日付と比較する
			 */
			if (historyPeriod.end().before(GeneralDate.today())) {
				throw new BusinessException("Msg_11");
			}
		}
	}
}
