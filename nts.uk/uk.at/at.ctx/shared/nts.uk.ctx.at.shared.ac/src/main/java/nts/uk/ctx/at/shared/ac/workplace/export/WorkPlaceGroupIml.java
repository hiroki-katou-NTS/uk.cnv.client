package nts.uk.ctx.at.shared.ac.workplace.export;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceGroupExport;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.WorkplaceGroupPublish;

/**
 * 職場グループImpl
 * @author HieuLt
 *
 */
@Stateless
public class WorkPlaceGroupIml implements WorkplaceGroupAdapter{
	
	@Inject
	private WorkplaceGroupPublish workplaceGroupPublish;
	
	@Override
	public List<WorkplaceGroupImport> getbySpecWorkplaceGroupID(List<String> lstWorkplaceGroupID) {
		//	return 職場グループPublish.職場グループIDを指定して取得する( 職場グループIDリスト )																								
		List<WorkplaceGroupExport> data = workplaceGroupPublish.getByWorkplaceGroupID(lstWorkplaceGroupID);
		List<WorkplaceGroupImport> result = data.stream().map(c -> new WorkplaceGroupImport(
				c.getWorkplaceGroupId(),
				c.getWorkplaceGroupCode(),
				c.getWorkplaceName(),
				c.getWorkplaceGroupType())).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<WorkplaceGroupImport> getAllWorkplaces(GeneralDate date, String workplaceGroupID) {
		// 	return 職場グループPublish.職場グループIDを指定して取得する( 職場グループIDリスト )																									
		//QA Tai lieu goi sai ham 
		List<WorkplaceGroupExport> data = workplaceGroupPublish.getAllWorkplaces(date, workplaceGroupID);
		List<WorkplaceGroupImport> result = data.stream().map(c -> new WorkplaceGroupImport(
				c.getWorkplaceGroupId(),
				c.getWorkplaceGroupCode(),
				c.getWorkplaceName(),
				c.getWorkplaceGroupType())).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<EmpOrganizationImport> getGetAllEmployees(GeneralDate date, String workplaceGroupID) {
		// return 職場グループPublish.所属する社員をすべて取得する( 職場グループIDリスト )
		List<EmpOrganizationExport>  data = workplaceGroupPublish.getAllEmployees(date, workplaceGroupID);
		List<EmpOrganizationImport> result = data.stream().map( c-> new EmpOrganizationImport(
			new EmployeeId(c.getEmpId()),
				c.getEmpCd(),
				c.getBusinessName(),
				c.getWorkplaceId(),
				c.getWorkplaceGroupId())).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<String> getReferableEmp(GeneralDate date, String empId, String workplaceGroupID) {
		//	return 職場グループPublish.参照可能な社員を取得する( 基準日, 社員ID, 職場グループID )																								
		List<String> result = workplaceGroupPublish.getReferableEmployees(date, empId, workplaceGroupID);
		return result;
	}

	@Override
	public List<String> getAllReferableEmp(GeneralDate date, String employeeId) {
		//return 職場グループPublish.参照可能な社員をすべて取得する( 基準日, 社員ID )
		List<String> result = workplaceGroupPublish.getAllReferableEmployees(date, employeeId);
		return result;
	}

	

}
