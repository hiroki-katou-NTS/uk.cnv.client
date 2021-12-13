package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;


public interface PublicHolidayCarryForwardHistoryRepository {

	/**
	 * 登録および更新
	 * @param domain
	 */
	void persistAndUpdate(PublicHolidayCarryForwardHistory domain);
	
}
