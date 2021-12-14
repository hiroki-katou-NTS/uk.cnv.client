package nts.uk.ctx.bs.employee.pub.workplace.workplacegroup;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceGroupExport;

/**
 * 職場グループPublish
 * @author HieuLt
 *
 */
public interface WorkplaceGroupPublish {
											
	/**
	 * [1] 職場グループIDを指定して取得する											
	 * @param lstWorkplaceGroup
	 * @return
	 */
	List<WorkplaceGroupExport> getByWorkplaceGroupID (List<String> lstWorkplaceGroupId);
	/**
	 * [2] 所属する職場をすべて取得する
	 * @param date
	 * @param workplaceGroupId
	 * @return 
	 */
	List<WorkplaceGroupExport> getAllWorkplaces(GeneralDate date , String workplaceGroupId);
	
	/**
	 * [3] 所属する社員をすべて取得する
	 * @param date
	 * @param workplaceGroupId
	 * @return
	 */
	List<EmpOrganizationExport> getAllEmployees(GeneralDate date , String workplaceGroupId);
	
	/**
	 * [4] 参照可能な社員を取得する								
	 * @param date
	 * @param empID
	 * @param workplaceGroupId
	 * @return
	 */
	List<String> getReferableEmployees(GeneralDate date , String empID ,String workplaceGroupId );
	
	/**
	 * [5] 参照可能な社員をすべて取得する
	 * @param date 基準日
	 * @param employeeId 社員ID
	 * @return List<社員ID>
	 */
	List<String> getAllReferableEmployees(GeneralDate date, String employeeId);
}
