package repository.employee.emloyeelicense;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.ContractCode;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicense;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicenseRepository;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.LicenseKey;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.MaxNumberLicenses;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.WarningNumberLicenses;

@Stateless
public class JpaEmployeeLicenseRepository extends JpaRepository implements EmployeeLicenseRepository {

	private static final String SELECT_BY_KEY = "SELECT c FROM BsymtLicense c WHERE c.bsymtEmployeeLicensePK.contractCD = :contractCD";

	@Override
	public Optional<EmployeeLicense> findByKey(String contractCD) {
		return Optional.of(new EmployeeLicense(
				new ContractCode(contractCD), 
				new MaxNumberLicenses(99999), 
				new WarningNumberLicenses(0), 
				new LicenseKey("DJSRQVLSBTOGWW")));
	}

}
