package nts.uk.ctx.at.record.dom.dailyperformanceformat.enums;

public enum SettingUnit {

	/*
	 * 設定単位
	 */
	// 0: 権限
	AUTHORITY(0),
	// 1: 勤務種別
	BUSINESSTYPE(1);
	
	public final int value;
	
	private SettingUnit(int type) {
		this.value = type;
	}
}
