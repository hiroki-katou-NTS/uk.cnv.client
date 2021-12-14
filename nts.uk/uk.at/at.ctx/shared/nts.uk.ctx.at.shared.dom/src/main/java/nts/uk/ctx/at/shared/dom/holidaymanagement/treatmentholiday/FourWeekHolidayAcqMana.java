package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
/**
 * 4週間単位の休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.4週間単位の休日取得管理
 * @author tutk
 *
 */
public interface FourWeekHolidayAcqMana extends HolidayAcquisitionManagement {
	
	/**
	 * [1] 管理期間の単位を取得する
	 */
	default HolidayCheckUnit getUnitManagementPeriod() {
		return HolidayCheckUnit.FOUR_WEEK;
	}
	
	/**
	 * [3] 起算日の種類を取得する
	 * @return
	 */
	StartDateClassification getStartDateType(); 
	
	/**
	 * [prv-1] 4週間を作る
	 * @param startDate 起算日	
	 * @param referenceDate 基準日		
	 */
	default DatePeriod make4Weeks(GeneralDate startDate, GeneralDate referenceDate) {
		int cycleDay = 7*4;
		int currentNumberOfCycles = (new DatePeriod(startDate, referenceDate).datesBetween().size() -1)/cycleDay; 
		startDate = startDate.addDays(currentNumberOfCycles*cycleDay);
		return new DatePeriod(startDate, startDate.addDays(cycleDay-1));
	}
}
