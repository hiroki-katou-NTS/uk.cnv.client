package nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol;

/**
 * 表示形式
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.表示設定.機能制御.表示形式
 * @author dan_pv
 *
 */
public enum FuncCtrlDisplayFormat {
	
	/** 略名 */
	AbbreviatedName(0),

	/** 勤務 */
	WorkInfo(1),
	
	/** シフト */
	Shift(2);

	public int value;
	
	private FuncCtrlDisplayFormat(int value) {
		this.value = value;
	}

}
