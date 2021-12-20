package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

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

}
