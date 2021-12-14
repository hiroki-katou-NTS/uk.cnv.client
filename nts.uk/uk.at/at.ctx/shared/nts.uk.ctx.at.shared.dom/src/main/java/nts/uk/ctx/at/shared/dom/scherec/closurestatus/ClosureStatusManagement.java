package nts.uk.ctx.at.shared.dom.scherec.closurestatus;

import java.util.Optional;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author HungTT - 締め状態管理
 *
 */

@Getter
public class ClosureStatusManagement extends AggregateRoot {

	// 年月
	private YearMonth yearMonth;

	// 社員ID
	private String employeeId;

	// 締めID
	private ClosureId closureId;

	// 締め日
	private ClosureDate closureDate;

	// 期間
	private DatePeriod period;

	public ClosureStatusManagement(YearMonth yearMonth, String employeeId, int closureId, ClosureDate closureDate,
			DatePeriod period) {
		super();
		this.yearMonth = yearMonth;
		this.employeeId = employeeId;
		this.closureId = EnumAdaptor.valueOf(closureId, ClosureId.class);
		this.closureDate = closureDate;
		this.period = period;
	}
	
	/**
	 * 	[１] 締めされていない期間を求める
	 * @param period
	 * @return 	締めされていない期間
	 */
	public Optional<DatePeriod> closureStateManagenent(DatePeriod period) {
		GeneralDate endDateForCheck = this.period.end() .addDays(1);
		if(period.end().before(endDateForCheck)) { 
			return Optional.empty();
		}
		if(period.start().after(endDateForCheck)) {
			return Optional.of(period);
		}
		return Optional.of(new DatePeriod(endDateForCheck, period.end())); 
	}

}
