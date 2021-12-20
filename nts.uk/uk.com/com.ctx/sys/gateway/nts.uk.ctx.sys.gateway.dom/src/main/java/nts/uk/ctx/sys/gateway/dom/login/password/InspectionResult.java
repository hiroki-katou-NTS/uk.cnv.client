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
	private final boolean verificationSuccess;
	
	// 社員情報
	private final Optional<GetAnEmployeeImported> employeeInformation;
	
	// 検証失敗メッセージ
	private final Optional<String> verificationFailureMessage;
	
	/**
	 * 	[C-1] 検証成功
	 * @param employee
	 */
	public static InspectionResult verificationSuccess (GetAnEmployeeImported employee) {
		return new InspectionResult(true, Optional.ofNullable(employee), Optional.empty());
	}
	
	/**
	 * 	[C-2] ユーザ検証失敗
	 */
	public static InspectionResult userVerificationFailure() {
		return new InspectionResult(false, Optional.empty(), Optional.of("Msg_301"));
	}
	
	/**
	 * 	[C-3] パスワード検証失敗
	 */
	public static InspectionResult passwordVerificationFailed() {
		return new InspectionResult(false, Optional.empty(), Optional.of("Msg_302"));
	}
}
