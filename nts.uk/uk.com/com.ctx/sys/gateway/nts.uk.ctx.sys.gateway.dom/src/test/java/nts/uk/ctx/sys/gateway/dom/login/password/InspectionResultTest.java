package nts.uk.ctx.sys.gateway.dom.login.password;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;


/**
 * 
 * @author chungnt
 *
 */

public class InspectionResultTest {

	@Test
	public void testC1() {
		GetAnEmployeeImported employee = new GetAnEmployeeImported("Dummy", "Dummy", "Dummy", "Dummy"); //dummy
		InspectionResult result = InspectionResult.verificationSuccess(employee);
		assertThat(result.isVerificationSuccess()).isEqualTo(true);
		assertThat(result.getEmployeeInformation().get().getCid()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getEmployeeCode()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getPersonalId()).isEqualTo("Dummy");
		assertThat(result.getEmployeeInformation().get().getSid()).isEqualTo("Dummy");
		assertThat(result.getVerificationFailureMessage().isPresent()).isFalse();
	}
	
	@Test
	public void testC2() {
		InspectionResult result = InspectionResult.userVerificationFailure();
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_301");
	}
	
	@Test
	public void testC3() {
		InspectionResult result = InspectionResult.passwordVerificationFailed();
		assertThat(result.isVerificationSuccess()).isEqualTo(false);
		assertThat(result.getEmployeeInformation().isPresent()).isFalse();
		assertThat(result.getVerificationFailureMessage().get()).isEqualTo("Msg_302");
	}

}
