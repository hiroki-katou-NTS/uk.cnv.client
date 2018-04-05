package nts.uk.ctx.workflow.ac.employee;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.auth.pub.employee.EmployeePublisher;
import nts.uk.ctx.workflow.dom.adapter.employee.EmployeeWithRangeAdapter;
import nts.uk.ctx.workflow.dom.adapter.employee.EmployeeWithRangeLoginImport;

/**
 * @author sang.nv
 *
 */
@Stateless
public class EmployeeWithRangeImpl implements EmployeeWithRangeAdapter {

	@Inject
	EmployeePublisher employeePublisher;

	@Override
	public Optional<EmployeeWithRangeLoginImport> findEmployeeByAuthorizationAuthority(String companyID,
			String employeeCD) {
		Optional<EmployeeWithRangeLoginImport> employeeWithRangeLoginImport = this.employeePublisher
				.findByCompanyIDAndEmpCD(companyID, employeeCD).map(x -> {
					return new EmployeeWithRangeLoginImport(x.getBusinessName(), x.getPersonID(), x.getEmployeeCD(),
							x.getEmployeeID());
				});
		if (!employeeWithRangeLoginImport.isPresent())
			return Optional.empty();
		return employeeWithRangeLoginImport;
	}

	@Override
	public Optional<EmployeeWithRangeLoginImport> findByEmployeeByLoginRange(String companyID, String employeeCD) {
		Optional<EmployeeWithRangeLoginImport> employeeWithRangeLoginImport = this.employeePublisher
				.getByComIDAndEmpCD(companyID, employeeCD).map(x -> {
					return new EmployeeWithRangeLoginImport(x.getBusinessName(), x.getPersonID(), x.getEmployeeCD(),
							x.getEmployeeID());
				});
		if (!employeeWithRangeLoginImport.isPresent())
			return Optional.empty();
		return employeeWithRangeLoginImport;
	}
}
