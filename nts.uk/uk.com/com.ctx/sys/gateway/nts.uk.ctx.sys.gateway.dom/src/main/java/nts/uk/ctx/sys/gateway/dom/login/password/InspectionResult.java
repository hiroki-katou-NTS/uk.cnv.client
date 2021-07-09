package nts.uk.ctx.sys.gateway.dom.login.password;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;

/**
 * 検証結果
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.GateWay.ログイン.パスワード認証.社員コードとパスワードを検証する.検証結果
 * @author chungnt
 *
 */

@Data
@AllArgsConstructor
public class InspectionResult {

	// 検証成功
	private boolean verificationSuccess;
	
	// 社員情報
	private Optional<GetAnEmployeeImported> employeeInformation;
	
	// 検証失敗メッセージ
	private Optional<String> verificationFailureMessage;
	
	/**
	 * 	[C-1] 検証成功
	 * @param employee
	 */
	public static InspectionResult create1 (GetAnEmployeeImported employee) {
		return new InspectionResult(true, Optional.ofNullable(employee), Optional.empty());
	}
	
	/**
	 * 	[C-2] ユーザ検証失敗
	 */
	public static InspectionResult create2() {
		return new InspectionResult(false, Optional.empty(), Optional.of("Msg_301"));
	}
	
	/**
	 * 	[C-3] パスワード検証失敗
	 */
	public static InspectionResult create3() {
		return new InspectionResult(false, Optional.empty(), Optional.of("Msg_302"));
	}
}
