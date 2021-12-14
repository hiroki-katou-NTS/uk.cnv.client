package nts.uk.ctx.sys.auth.dom.wkpmanager;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
@Getter
public class WorkplaceManager extends AggregateRoot {
	/**
	 * 職場管理者ID
	 */
	private String workplaceManagerId;
	/**
	 * 社員ID
	 */
	private String employeeId;
	/**
	 * 職場ID
	 */
	private String workplaceId;
	/**
	 * 履歴期間
	 */
	private DatePeriod historyPeriod;
	
	public WorkplaceManager(String workplaceManagerId, String employeeId, String workplaceId, DatePeriod historyPeriod) {
		super();
		this.workplaceManagerId = workplaceManagerId;
		this.employeeId = employeeId;
		this.workplaceId = workplaceId;
		this.historyPeriod = historyPeriod;
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
