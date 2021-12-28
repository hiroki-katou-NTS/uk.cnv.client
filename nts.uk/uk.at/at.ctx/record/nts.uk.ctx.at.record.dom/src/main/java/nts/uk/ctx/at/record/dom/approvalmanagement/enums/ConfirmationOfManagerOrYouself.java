/**
 * 5:02:27 PM Mar 9, 2018
 */
package nts.uk.ctx.at.record.dom.approvalmanagement.enums;

import lombok.AllArgsConstructor;

/**
 * @author hungnm
 *
 */
//上司・本人確認の挙動
@AllArgsConstructor
public enum ConfirmationOfManagerOrYouself {
	
	/* エラーがあっても確認できる */
	CAN_CHECK(0, "Enum_ConfirmationOfManagerOrYouself_CanCheck"),
	/* エラーを訂正するまでチェックできない */
	CAN_NOT_CHECK(1, "Enum_ConfirmationOfManagerOrYouself_CanNotCheck");
//	/* エラーを訂正するまで登録できない */
//	CAN_NOT_REGISTER(2, "Enum_ConfirmationOfManagerOrYouself_CanNotRegister");

	public final int value;

	public final String nameId;
	
}