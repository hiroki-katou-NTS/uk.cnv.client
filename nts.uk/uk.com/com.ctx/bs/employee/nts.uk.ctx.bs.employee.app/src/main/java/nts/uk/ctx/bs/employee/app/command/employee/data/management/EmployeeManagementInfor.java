package nts.uk.ctx.bs.employee.app.command.employee.data.management;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;

/**
 * 社員データ管理情報を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.社員データ管理.App.社員データ管理情報を取得する
 * 
 * @author chungnt
 *
 */

@Stateless
public class EmployeeManagementInfor {

	@Inject
	private EmployeeDataMngInfoRepository employeeRepo;

	public EmployeeManagementInforDto getEmployeeManagementInfor(String cid, String employeeCode) {
		
		EmployeeManagementInforDto result = new EmployeeManagementInforDto();

		Optional<EmployeeDataMngInfo> OpEmp =  employeeRepo.findByEmployeCD(employeeCode, cid);
		
		if (OpEmp.isPresent()) {
			EmployeeDataMngInfo emp = OpEmp.get();
			
			result.setCompanyId(emp.getCompanyId());
			result.setEmployeeCode(emp.getEmployeeCode().v());
			result.setEmployeeId(emp.getEmployeeId());
			result.setPersonalId(emp.getPersonId());
		}
		
		return result;
	}
}
