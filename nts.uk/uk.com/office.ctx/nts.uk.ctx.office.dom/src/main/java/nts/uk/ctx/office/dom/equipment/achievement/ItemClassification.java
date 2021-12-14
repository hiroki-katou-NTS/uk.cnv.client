package nts.uk.ctx.office.dom.equipment.achievement;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目分類
 */
public enum ItemClassification {
	// 文字
	TEXT(0),
	
	// 数字
	NUMBER(1),
	
	// 時間
	TIME(2);
	
	public int value;

	private ItemClassification(int value) {
		this.value = value;
	}
}
