package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;

import lombok.Getter;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * 公休繰越履歴データ
 * @author hayata_maekawa
 *
 */
@Getter
public class PublicHolidayCarryForwardHistory extends PublicHolidayCarryForwardData {
	
	// 年月
	private YearMonth histYearMonth;

	// 締めID
	private ClosureId closureId;

	// 締め日
	private ClosureDate closureDate;
	
	
	
	public PublicHolidayCarryForwardHistory(
			PublicHolidayCarryForwardData data, YearMonth yearMonth, ClosureId clousureId, ClosureDate closureDate){
		super(	data.getEmployeeId(),
				data.getNumberCarriedForward(),
				data.getGrantRemainRegisterType());
		this.histYearMonth = yearMonth;
		this.closureId = clousureId;
		this.closureDate = closureDate;
	}

}
