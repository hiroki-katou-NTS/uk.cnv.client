package nts.uk.ctx.sys.gateway.dom.login.password.authenticate;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.gateway.dom.login.password.InspectionResult;
import nts.uk.ctx.sys.gateway.dom.login.password.VerifyEmployeeCodeAndPassword;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUserRepository;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeAdapter;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.GateWay.ログイン.パスワード認証.App.社員コードとパスワードを検証する
 * 社員コードとパスワードを検証する
 * @author chungnt
 *
 */

@Stateless
public class QrVerifyEmployeeCodeAndPassword {

	@Inject
	private UserRepository user;

	@Inject
	private GetAnEmployeeAdapter anEmployeeAdapter;

	@Inject
	private LoginPasswordOfUserRepository loginPasswordOfUserRepository;
	
	public InspectionResultDto verifyEmployeeCodeAndPassword (String cid, String employeeCode, String password) {
		VerifyEmployeeCodeAndPasswordImp require = new VerifyEmployeeCodeAndPasswordImp();
		
		return InspectionResultDto.toInspectionResultDto(VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, password), password);
	}
	
	private class VerifyEmployeeCodeAndPasswordImp implements VerifyEmployeeCodeAndPassword.Require {

		@Override
		public Optional<GetAnEmployeeImported> getEmployee(String cid, String employeeCode) {
			return anEmployeeAdapter.getEmployee(cid, employeeCode);
		}

		@Override
		public Optional<User> getByAssociatedPersonId(String associatedPersonId) {
			return user.getByAssociatedPersonId(associatedPersonId);
		}

		@Override
		public Optional<LoginPasswordOfUser> find(String userId) {
			return loginPasswordOfUserRepository.find(userId);
		}
		
	}
	
}


