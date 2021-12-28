package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.history.strategic.PersistentResidentHistory;

/** 社員単価履歴 */
@Getter
@RequiredArgsConstructor
public class EmployeeUnitPriceHistory extends AggregateRoot implements PersistentResidentHistory<DateHistoryItem, DatePeriod, GeneralDate>{

	/** 社員ID */
	private final String employeeId;

	/** 履歴 */
	private final List<DateHistoryItem> historyItems;
	
	@Override
	public List<DateHistoryItem> items() {
		return this.historyItems;
	}

}
