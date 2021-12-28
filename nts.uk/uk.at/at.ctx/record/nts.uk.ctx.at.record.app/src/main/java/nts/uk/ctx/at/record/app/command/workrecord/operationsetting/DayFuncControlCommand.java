package nts.uk.ctx.at.record.app.command.workrecord.operationsetting;

import lombok.Value;

@Value
public class DayFuncControlCommand {
	
	/**
	 * 会社ID
	 */
	private String cid;

	/**
	 * コメント
	 */
	private String comment;

	/**
	 * 36協定情報を表示する
	 */
	private boolean disp36Atr;
	
	/**
	 * フレックス勤務者のフレックス不足情報を表示する
	 */
	private boolean flexDispAtr;
	
	/**
     * 抽出時にエラーがある場合はエラー参照ダイアログを表示する
     * 
     */
    private boolean checkErrRefDisp;
    
	/**
	 * 日の本人確認を利用する
	 */
	private boolean daySelfChk;
	 
	/**
	 * 月の本人確認を利用する
	 */
	private boolean monSelfChK;
	 
	/**
	 * エラーがある場合の日の本人確認
	 */
	private Integer daySelfChkError;
	
	/**
	* 日の承認者確認を利用する
	*/
	private boolean dayBossChk;
	
	/**
	* 月の承認者確認を利用する
	*/
	private boolean monBossChk;
	
	/**
	* エラーがある場合の日の承認者確認
	*/
	private Integer dayBossChkError;
	
	private Long version;
}
