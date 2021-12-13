package nts.uk.screen.at.app.ksu001.getsendingperiod;

public enum ModePeriod {
	
	/**抽出期間を表示する：A3_2_1選択時				 */
	extractionPeriod(1),

	/** 28日周期で表示する：A3_2_2選択時 */
	mode28Days(2),
	
	/** 1日～末日で表示する	：A3_2_3選択時 */
	from1stToLastDay(3);

	public int value;
	
	private ModePeriod(int value) {
		this.value = value;
	}

}
