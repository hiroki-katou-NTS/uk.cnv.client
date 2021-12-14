package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter;

import java.util.List;

import nts.arc.time.GeneralDate;


/**
 * 職場グループAdapter
 * @author HieuLt
 */
public interface WorkplaceGroupAdapter {
	
	/**
	 * [1] 職場グループIDを指定して取得する														
	 * @param lstWorkplaceGroupID
	 * @return
	 */
	List<WorkplaceGroupImport>  getbySpecWorkplaceGroupID (List<String> lstWorkplaceGroupID);
	/**
	 * [2] 所属する職場をすべて取得する
	 * @param date
	 * @param lstWorkplaceGroupID
	 * @return
	 */
	List<WorkplaceGroupImport> getAllWorkplaces(GeneralDate date, String workplaceGroupID);
	/**
	 * [3] 所属する社員をすべて取得する
	 * @param date
	 * @param lstWorkplaceGroupID
	 * @return
	 */
	List<EmpOrganizationImport> getGetAllEmployees(GeneralDate date, String workplaceGroupID);
	/**
	 * [4] 参照可能な社員を取得する
	 * @param date
	 * @param empId
	 * @param lstWorkplaceGroupID
	 * @return
	 */
	List<String> getReferableEmp(GeneralDate date,String empId, String workplaceGroupID);
	
	/**
	 * [5] 参照可能な社員をすべて取得する	
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @return
	 */
	List<String> getAllReferableEmp(GeneralDate date, String employeeId);

}
