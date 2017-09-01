package nts.uk.ctx.bs.employee.pubimp.employee;

import java.util.Optional;

import javax.inject.Inject;

import nts.uk.ctx.bs.employee.dom.employeeinfo.Employee;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeRepository;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoDtoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;

public class EmployeeInfoPubImp implements EmployeeInfoPub {

	@Inject
	private EmployeeRepository repo;

	@Override
	public Optional<EmployeeInfoDtoExport> findByCidSid(String companyId, String employeeCode) {
		// TODO Auto-generated method stub
		
		Optional<Employee> domain = repo.findByEmployeeCode(companyId, employeeCode);

		if (!domain.isPresent()) {
			return Optional.empty();
		} else {
			Employee _domain = domain.get();
			return Optional.of(new EmployeeInfoDtoExport(_domain.getCompanyId(), _domain.getSCd().v(), _domain.getSId(),
					_domain.getPId()));
		}

	}

}
