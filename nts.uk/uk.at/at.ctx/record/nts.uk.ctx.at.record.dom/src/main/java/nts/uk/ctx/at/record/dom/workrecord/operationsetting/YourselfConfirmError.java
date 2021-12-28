package nts.uk.ctx.at.record.dom.workrecord.operationsetting;
/**
 * 上司・本人確認の挙動
 *
 */
public enum YourselfConfirmError {
	/**
	 * エラーがあっても確認できる
	 */
	CAN_CHECK_WHEN_ERROR(0),
	/**
	 * エラーを訂正するまでチェックできない
	 */
	CANNOT_CHECKED_WHEN_ERROR(1);
//	/**
//	 * エラーを訂正するまで登録できない
//	 */
//	CANNOT_REGISTER_WHEN_ERROR(2);
	
	public final int value;
	
	private YourselfConfirmError(int value) {
		this.value = value;
	}
}
