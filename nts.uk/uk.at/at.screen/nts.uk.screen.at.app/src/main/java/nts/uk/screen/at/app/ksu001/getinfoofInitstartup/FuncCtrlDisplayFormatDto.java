package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

/**
 * 表示形式
 *
 */
public enum FuncCtrlDisplayFormatDto {
	
	/** 略名 */
	AbbreviatedName(0),

	/** 勤務 */
	WorkInfo(1),
	
	/** シフト */
	Shift(2);

	public int value;
	
	private FuncCtrlDisplayFormatDto(int value) {
		this.value = value;
	}

}
