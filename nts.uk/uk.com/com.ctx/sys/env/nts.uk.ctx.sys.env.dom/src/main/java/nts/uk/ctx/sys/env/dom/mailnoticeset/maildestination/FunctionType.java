package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.機能種類
 */
public enum FunctionType {

	// ログイン（パスワードを忘れた場合）
	LOGIN(1, "ログイン（パスワードを忘れた場合）"),
	
	// 【就業】申請・承認・指示・代行依頼
	REQUEST(6, "【就業】申請・承認・指示・代行依頼"),
	
	// 【就業】アラームリスト
	ALARM_LIST(9, "【就業】アラームリスト");
	
	public int value;
	public String name;
	
	private FunctionType(int value, String name) {
		this.value = value;
		this.name = name;
	}
}
