package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.休日取得管理
 * @author tutk
 *
 */
public interface HolidayAcquisitionManagement {
	
	/**
	 * [1] 管理期間の単位を取得する
	 * @return
	 */
	public HolidayCheckUnit getUnitManagementPeriod();
	
	/**
	 * [2] 管理期間を取得する
	 * @param require
	 * @param ymd
	 * @return
	 */
	public HolidayAcqManaPeriod getManagementPeriod(Require require,GeneralDate ymd);
	
	/**
	 * 28日間を取得する
	 * @param require
	 * @param ymd 年月日
	 * @return
	 */
	public DatePeriod get28Days(Require require, GeneralDate ymd);
	
	public static interface Require extends WeeklyHolidayAcqMana.Require {
	}
}
