package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting;
/**
 * 実働時間帯区分
 * @author keisuke_hoshina
 *
 */
public enum ActualWorkTimeSheetAtr {
	WithinWorkTime,//就業時間内
	OverTimeWork,//残業
	EarlyWork,//早出残業
	StatutoryOverTimeWork,//法定内残業
	HolidayWork;//休出
	
	/**
	 * 就内時間であるか判定する
	 * @return　就業時間である
	 */
	public boolean isWithinWorkTime() {
		return this.equals(WithinWorkTime);
	}
	
	/**
	 * 残業か
	 * @return 残業である
	 */
	public boolean isOverTimeWork() {
		return this.equals(OverTimeWork);
	}
	
	/**
	 * 早出残業か
	 * @return 早出残業である
	 */
	public boolean isEarlyWork() {
		return this.equals(EarlyWork);
	}
	
	/**
	 * 法定内残業か
	 * @return 法定内残業である
	 */
	public boolean isStatutoryOverTimeWork() {
		return this.equals(StatutoryOverTimeWork);
	}
	
	/**
	 * 休出か
	 * @return 休出である
	 */
	public boolean isHolidayWork() {
		return this.equals(HolidayWork);
	}
}
