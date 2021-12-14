package nts.uk.ctx.sys.auth.ac.employee;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.bs.employee.pub.employee.EmpInfoRegistered;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeBasicInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmpInfoExport;
import nts.uk.ctx.bs.employee.pub.employee.employeeInfo.EmployeeInfoPub;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.auth.dom.employee.dto.EmployeeImport;

@Stateless
public class AuthEmployeeAdapterImpl implements EmployeeAdapter{

	@Inject
	private SyEmployeePub employeePub;
	
	@Inject 
	private EmployeeInfoPub employeeInfoPub;
	
	private EmployeeImport toImport(EmployeeBasicInfoExport export){
		return new EmployeeImport(export.getPName(), export.getEmployeeId(), export.getEmployeeCode());
	}
	
	@Override
	public Optional<EmployeeImport> findByEmpId(String empId) {
		val exportEmployee = employeePub.findBySId(empId);
		if (exportEmployee == null) {
			return Optional.empty();
		}
		return Optional.of(toImport(employeePub.findBySId(empId)));
	}

	@Override
	public List<EmployeeImport> findByEmployeeId(String employeeId) {
		List<EmpInfoExport> lstExport = employeeInfoPub.getEmpInfoByPid(employeeId);
		return lstExport.stream().map(item->{
			return new EmployeeImport(item.getCompanyId(),item.getPId(),item.getEmployeeId(),item.getEmployeeCode());
		}).collect(Collectors.toList());
	}

	@Override
	public Optional<EmployeeImport> getEmpInfo(String cid, String pid) {
		Optional<EmpInfoRegistered> data = employeePub.getEmpInfo(cid, pid);
		if(!data.isPresent())
			return Optional.empty();
		EmployeeImport dataImport = new EmployeeImport(data.get().getCid(), data.get().getPid(), data.get().getSid(), data.get().getEmployeeCode(), data.get().getPersonName());
		return Optional.of(dataImport);
	}

}
