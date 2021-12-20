package nts.uk.ctx.sys.gateway.dom.login.password;

import java.util.Optional;

import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;
import nts.uk.ctx.sys.shared.dom.user.User;

/**
 * 社員コードとパスワードを検証する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.GateWay.ログイン.パスワード認証.社員コードとパスワードを検証する
 * 
 * @author chungnt
 *
 */

public class VerifyEmployeeCodeAndPassword {

	/**
	 * [1] 検証する
	 * 
	 * @param require
	 * @param cid
	 *            会社ID
	 * @param employeeCode
	 *            社員コード
	 * @param password
	 *            パスワード
	 * @return
	 */
	public static InspectionResult verify(Require require, String cid, String employeeCode, String password) {

		// $社員 = require.社員を取得する(会社ID,社員コード)
		Optional<GetAnEmployeeImported> employeeInfo = require.getEmployee(cid, employeeCode);

		// if $社員.isEmpty
		if (!employeeInfo.isPresent()) {
			return InspectionResult.userVerificationFailure();
		}

		// $ユーザ = require.ユーザを取得する($社員.個人ID)
		Optional<User> user = require.getByAssociatedPersonId(employeeInfo.get().getPersonalId());

		// if $ユーザ.isEmpty
		if (!user.isPresent()) {
			return InspectionResult.userVerificationFailure();
		}

		// $ログインパスワード = require.ユーザーのログインパスワードを取得する($ユーザ.ユーザID)
		Optional<LoginPasswordOfUser> loginPassword = require.find(user.get().getUserID());

		// if $ユーザ.isEmpty
		if (!loginPassword.isPresent()) {
			return InspectionResult.passwordVerificationFailed();
		}

		// if not $ログインパスワード .現在のパスワードを照合する(パスワード)
		if (!loginPassword.get().matches(password)) {
			return InspectionResult.passwordVerificationFailed();
		}

		// return 検証結果#検証成功($社員)
		return InspectionResult.verificationSuccess(employeeInfo.get());
	}

	public static interface Require {

		// [R-1] 社員を取得する
		Optional<GetAnEmployeeImported> getEmployee(String cid, String employeeCode);

		// [R-2] ユーザを取得する
		Optional<User> getByAssociatedPersonId(String associatedPersonId);

		// [R-3] ユーザーのログインパスワードを取得する
		Optional<LoginPasswordOfUser> find(String userId);
	}

}
