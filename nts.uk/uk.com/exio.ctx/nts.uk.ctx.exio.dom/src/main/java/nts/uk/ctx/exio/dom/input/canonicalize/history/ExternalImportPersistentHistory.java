package nts.uk.ctx.exio.dom.input.canonicalize.history;

import java.util.List;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.history.strategic.PersistentHistory;

/**
 * 連続かつ永続する履歴の汎用クラス
 */
@AllArgsConstructor
public class ExternalImportPersistentHistory implements PersistentHistory<DateHistoryItem, DatePeriod, GeneralDate>{

	private List<DateHistoryItem> period;
	
	@Override
	public void exValidateIfCanAdd(DateHistoryItem itemToBeAdded) {
	}
	
	@Override
	public List<DateHistoryItem> items() {
		return period;
	}

}
