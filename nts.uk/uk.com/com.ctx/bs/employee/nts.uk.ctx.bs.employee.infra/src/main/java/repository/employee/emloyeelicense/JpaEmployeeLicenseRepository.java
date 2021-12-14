package repository.employee.emloyeelicense;

import java.util.Optional;

import javax.ejb.Stateless;
import entity.employee.employeelicense.BsymtLicense;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicense;
import nts.uk.ctx.bs.employee.dom.employee.employeelicense.EmployeeLicenseRepository;

@Stateless
public class JpaEmployeeLicenseRepository extends JpaRepository implements EmployeeLicenseRepository {

	private static final String SELECT_BY_KEY = "SELECT c FROM BsymtLicense c WHERE c.bsymtEmployeeLicensePK.contractCD = :contractCD";

	@Override
	public Optional<EmployeeLicense> findByKey(String contractCD) {
		Optional<BsymtLicense> entity = Optional.empty();
		entity = this.queryProxy().query(SELECT_BY_KEY, BsymtLicense.class)
				.setParameter("contractCD", contractCD).getSingle();
		if (entity.isPresent()) {
			return Optional.of(entity.get().toDomain());
		} else {
			return Optional.empty();
		}
	}

}
