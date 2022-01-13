package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.history.strategic.PersistentHistory;

/**
 * @name UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.作業補足情報項目設定.作業補足情報の選択肢履歴
 * 
 * @author tutt
 * 
 */
@Getter
@AllArgsConstructor
public class TaskSupInfoChoicesHistory extends AggregateRoot
		implements PersistentHistory<DateHistoryItem, DatePeriod, GeneralDate> {

	/** 項目ID */
	private final int itemId;

	/** 履歴項目 */
	private final List<DateHistoryItem> dateHistoryItems;

	@Override
	public List<DateHistoryItem> items() {
		return this.dateHistoryItems;
	}
}
