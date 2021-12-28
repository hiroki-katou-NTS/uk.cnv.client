package nts.uk.ctx.at.function.dom.adapter.workrecord.approvalmanagement;

import lombok.AllArgsConstructor;

/**
 * @author thuongtv
 *
 */
//上司・本人確認の挙動
@AllArgsConstructor
public enum ConfirmationOfManagerOrYouselfImport {
	
	/* エラーがあっても確認できる */
	CAN_CHECK(0, "Enum_ConfirmationOfManagerOrYouself_CanCheck"),
	/* エラーを訂正するまでチェックできない */
	CAN_NOT_CHECK(1, "Enum_ConfirmationOfManagerOrYouself_CanNotCheck");
//	/* エラーを訂正するまで登録できない */
//	CAN_NOT_REGISTER(2, "Enum_ConfirmationOfManagerOrYouself_CanNotRegister");

	public final int value;

	public final String nameId;
	
}
