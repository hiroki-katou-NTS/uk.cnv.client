package nts.uk.screen.at.app.ksu001.getsendingperiod;

import javax.ejb.Stateless;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 	送る期間を取得する
 */
@Stateless
public class GetSendingPeriodScreenQuery {
	
    public DatePeriod getSendingPeriod(DatePeriod currentPeriod, boolean isNextMonth, boolean cycle28Day) {
    	DatePeriod result = null;
    	YearMonth yearMonth = YearMonth.of(currentPeriod.start().year(), currentPeriod.start().month());
    	
		if (cycle28Day) {
			// 28日周期か == true <call> 年月日 現在の期間．終了日+1
			// result = currentPeriod.newSpan(currentPeriod.start(),currentPeriod.end().addDays(1));
		} else {
			// 期間の開始日、終了日にそれぞれ「月を足す」を実行する
			if (isNextMonth) {
				if (currentPeriod.start().day() == 1) {
					result = DatePeriod.daysFirstToLastIn(yearMonth.nextMonth());
				} else {
					GeneralDate strRes = currentPeriod.start().addMonths(1);
					GeneralDate endRes = currentPeriod.end().addMonths(1);
					GeneralDate endDateOfNextMonth = yearMonth.nextMonth().lastGeneralDate();
					if (endRes.before(endDateOfNextMonth)) {
						endRes = endDateOfNextMonth;
					}
					result = new DatePeriod(strRes, endRes);
				}
			} else {
				if (currentPeriod.start().day() == 1) {
					result = DatePeriod.daysFirstToLastIn(yearMonth.previousMonth());
				} else {
					GeneralDate strRes = currentPeriod.start().addMonths(-1);
					GeneralDate endRes = currentPeriod.end().addMonths(-1);
					GeneralDate endDateOfPreMonth = yearMonth.previousMonth().lastGeneralDate();
					if (endRes.before(endDateOfPreMonth)) {
						endRes = endDateOfPreMonth;
					}
					result = new DatePeriod(strRes, endRes);
				}
			}
		}
		return result;
	}
}
