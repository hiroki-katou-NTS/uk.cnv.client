package nts.uk.ctx.bs.employee.dom.workplace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;

public class EmployeeAffiliationTest {

	@Test
	public void getters() {
		EmployeeAffiliation data = new EmployeeAffiliation("employeeID", Optional.empty(),Optional.empty()," workplaceID", Optional.empty());
		NtsAssert.invokeGetters(data);
	}
	
	
	@Test
	public void testAdd() {
		String employeeID = "employeeID";
		EmployeeCode employeeCode = new EmployeeCode("employeeCode");
		String businessName = "businessName";
		String workplaceID = "workplaceID";
		String workplaceGroupID = "workplaceGroupID";
		
		EmployeeAffiliation data = EmployeeAffiliation.add(employeeID, employeeCode, businessName, workplaceID, workplaceGroupID);
		assertThat(data.getEmployeeID()).isEqualTo(employeeID);
		assertThat(data.getEmployeeCode().get()).isEqualTo(employeeCode);
		assertThat(data.getBusinessName().get()).isEqualTo(businessName);
		assertThat(data.getWorkplaceID()).isEqualTo(workplaceID);
		assertThat(data.getWorkplaceGroupID().get()).isEqualTo(workplaceGroupID);
	}
	
	@Test
	public void testCreateWithoutInfo() {
		String employeeID = "employeeID";
		String workplaceID = "workplaceID";
		String workplaceGroupID = "workplaceGroupID";
		
		EmployeeAffiliation data = EmployeeAffiliation.createWithoutInfo(employeeID, workplaceID, workplaceGroupID);
		assertThat(data.getEmployeeID()).isEqualTo(employeeID);
		assertThat(data.getEmployeeCode().isPresent()).isFalse();
		assertThat(data.getBusinessName().isPresent()).isFalse();
		assertThat(data.getWorkplaceID()).isEqualTo(workplaceID);
		assertThat(data.getWorkplaceGroupID().get()).isEqualTo(workplaceGroupID);
	}
	
	@Test
	public void testCreateWithoutWG() {
		String employeeID = "employeeID";
		EmployeeCode employeeCode = new EmployeeCode("employeeCode");
		String businessName = "businessName";
		String workplaceID = "workplaceID";
		
		EmployeeAffiliation data = EmployeeAffiliation.createWithoutWG(employeeID, employeeCode, businessName, workplaceID);
		assertThat(data.getEmployeeID()).isEqualTo(employeeID);
		assertThat(data.getEmployeeCode().get()).isEqualTo(employeeCode);
		assertThat(data.getBusinessName().get()).isEqualTo(businessName);
		assertThat(data.getWorkplaceID()).isEqualTo(workplaceID);
		assertThat(data.getWorkplaceGroupID().isPresent()).isFalse();
	}
	
	@Test
	public void testCreateWithoutInfoAndWG() {
		String employeeID = "employeeID";
		String workplaceID = "workplaceID";
		
		EmployeeAffiliation data = EmployeeAffiliation.createWithoutInfoAndWG(employeeID, workplaceID);
		assertThat(data.getEmployeeID()).isEqualTo(employeeID);
		assertThat(data.getEmployeeCode().isPresent()).isFalse();
		assertThat(data.getBusinessName().isPresent()).isFalse();
		assertThat(data.getWorkplaceID()).isEqualTo(workplaceID);
		assertThat(data.getWorkplaceGroupID().isPresent()).isFalse();
	}

}
