package nts.uk.ctx.sys.shared.ac;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.pub.spr.EmployeeSprPub;
import nts.uk.ctx.bs.employee.pub.spr.export.EmpSprExport;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeAdapter;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetAnEmployeeImple implements GetAnEmployeeAdapter {

	@Inject
	private EmployeeSprPub employeeSprPub;
	
	@Override
	public Optional<GetAnEmployeeImported> getEmployee(String cid, String employeeCode) {

		// 「会社ID」「社員コード」より社員基本情報を取得
		Optional<EmpSprExport> opEmployeeSpr = employeeSprPub.getEmployeeID(cid, employeeCode);
		
		if (!opEmployeeSpr.isPresent()) {
			return Optional.empty();
		}

		EmpSprExport emp = opEmployeeSpr.get();
		
		GetAnEmployeeImported result = new GetAnEmployeeImported(
				emp.getCompanyID(),
				emp.getPersonID(),
				emp.getEmployeeID(),
				emp.getEmployeeCD());
		
		return Optional.of(result);
	}
}
