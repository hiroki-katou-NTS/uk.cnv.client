package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;


/**
 * 職場グループAdapter
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.職場.Imported.職場グループAdapter
 * @author HieuLt
 */
public interface WorkplaceGroupAdapter {

	/**
	 * 職場グループIDを指定して取得する
	 * @param workplaceGroupIds 職場グループIDリスト
	 * @return List<職場グループImported>
	 */
	List<WorkplaceGroupImport>  getbySpecWorkplaceGroupID (List<String> workplaceGroupIds);

	/**
	 * 所属する社員をすべて取得する
	 * @param date 基準日
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員の所属組織Imported>
	 */
	List<EmpOrganizationImport> getGetAllEmployees(GeneralDate date, String workplaceGroupId);

	/**
	 * 参照可能な所属社員を取得する
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	List<String> getReferableEmp(String employeeId, GeneralDate date, DatePeriod period, String workplaceGroupId);

	/**
	 * 参照可能な社員をすべて取得する
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @return List<社員ID>
	 */
	List<String> getAllReferableEmp(String employeeId, GeneralDate date, DatePeriod period);
	
	
	String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date);

}
