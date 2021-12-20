package nts.uk.ctx.bs.employee.pub.workplace.workplacegroup;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceGroupExport;

/**
 * 職場グループPublish
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職場.職場グループ.Export.職場グループPublish
 * @author HieuLt
 */
public interface WorkplaceGroupPublish {

	/**
	 * 職場グループIDを指定して取得する
	 * @param workplaceGroupIds 職場グループIDリスト
	 * @return List<職場グループExported>
	 */
	List<WorkplaceGroupExport> getByWorkplaceGroupID (List<String> workplaceGroupIds);

	/**
	 * 所属する社員をすべて取得する
	 * @param date 基準日
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員の所属組織Exported>
	 */
	List<EmpOrganizationExport> getAllEmployees(GeneralDate date, String workplaceGroupId);

	/**
	 * 参照可能な所属社員を取得する
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @param workplaceGroupId 職場グループID
	 * @return List<社員ID>
	 */
	List<String> getReferableEmployees(String employeeId, GeneralDate date, DatePeriod period, String workplaceGroupId);
	/**
	 * 参照可能な社員をすべて取得する
	 * @param employeeId 社員ID
	 * @param date 基準日
	 * @param period 期間
	 * @return List<社員ID>
	 */
	List<String> getAllReferableEmployees(String employeeId, GeneralDate date, DatePeriod period);

}
