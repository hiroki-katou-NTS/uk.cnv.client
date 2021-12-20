package nts.uk.ctx.sys.gateway.dom.login.password;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.gateway.dom.login.password.userpassword.LoginPasswordOfUser;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;
import nts.uk.ctx.sys.shared.dom.user.User;

/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class VerifyEmployeeCodeAndPasswordTest {

	@Injectable
	private VerifyEmployeeCodeAndPassword.Require require;

	private String cid = "Dummy";
	private String employeeCode = "Dummy";
	private String password = "Dummy";
	private String passwordDone = "password";
	private User user = User.createFromJavatype("Dummy", true, "password", "qwerty", GeneralDate.today(), 1, 1, "Dummy",
			"Dummy", "Dummy");
	private LoginPasswordOfUser loginPasswordOfUser = LoginPasswordOfUser.initialPassword("Dummy", "password");

	// if $社員.isEmpty
	// return 検証結果#ユーザ検証失敗()
	@Test
	public void test() {

		new Expectations() {
			{
				require.getEmployee(cid, employeeCode);
			}
		};

		InspectionResult result = VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, password);
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_301");
	}

	// if $ユーザ.isEmpty
	// return 検証結果#ユーザ検証失敗()
	@Test
	public void test1() {
		GetAnEmployeeImported employee = new GetAnEmployeeImported("Dummy", "Dummy", "Dummy", "Dummy"); // dummy

		new Expectations() {
			{
				require.getEmployee(cid, employeeCode);
				result = Optional.of(employee);

				require.getByAssociatedPersonId("Dummy");
			}
		};

		InspectionResult result = VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, password);
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_301");
	}

	// if not $ユーザ.認証チェック(パスワード)
	// return 検証結果#パスワード検証失敗()
	@Test
	public void test2() {
		GetAnEmployeeImported employee = new GetAnEmployeeImported("Dummy", "Dummy", "Dummy", "Dummy"); // dummy

		new Expectations() {
			{
				require.getEmployee(cid, employeeCode);
				result = Optional.of(employee);

				require.getByAssociatedPersonId("Dummy");
				result = Optional.of(user);
			}
		};

		InspectionResult result = VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, password);
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_302");
	}
	
	// if not $ログインパスワード .現在のパスワードを照合する(パスワード)
	@Test
	public void test3() {
		GetAnEmployeeImported employee = new GetAnEmployeeImported("Dummy", "Dummy", "Dummy", "Dummy"); // dummy

		new Expectations() {
			{
				require.getEmployee(cid, employeeCode);
				result = Optional.of(employee);

				require.getByAssociatedPersonId("Dummy");
				result = Optional.of(user);
				
				require.find("Dummy");
				result = Optional.of(loginPasswordOfUser);
			}
		};

		InspectionResult result = VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, password);
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_302");
	}

	// return 検証結果#検証成功($社員)
	@Test
	public void test4() {
		GetAnEmployeeImported employee = new GetAnEmployeeImported("Dummy", "Dummy", "Dummy", "Dummy"); // dummy

		new Expectations() {
			{
				require.getEmployee(cid, employeeCode);
				result = Optional.of(employee);

				require.getByAssociatedPersonId("Dummy");
				result = Optional.of(user);
				
				require.find("Dummy");
				result = Optional.of(loginPasswordOfUser);
			}
		};

		InspectionResult result = VerifyEmployeeCodeAndPassword.verify(require, cid, employeeCode, passwordDone);
		assertThat(result.isVerificationSuccess()).isEqualTo(true);
		assertThat(result.getEmployeeInformation().get().getCid()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getEmployeeCode()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getPersonalId()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getSid()).isEqualTo("Dummy");
		assertThat(result.getVerificationFailureMessage().isPresent()).isFalse();
	}
}
