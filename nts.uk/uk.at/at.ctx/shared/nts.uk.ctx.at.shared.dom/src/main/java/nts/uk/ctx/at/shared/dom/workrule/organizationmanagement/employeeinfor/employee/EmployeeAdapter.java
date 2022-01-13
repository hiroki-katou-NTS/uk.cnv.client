package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee;

import java.util.List;
import java.util.Map;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto.EmployeeInfoImported;

/**
 * 社員Adapter
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員Adapter
 * @author dan_pv
 */
public interface EmployeeAdapter {

	/**
	 * [1] 社員コードから社員IDを取得する
	 *
	 * @param companyId the company id
	 * @param empCodes  the emp codes
	 * @return Map<String       ,   String>
	 */
	Map<String, String> getEmploymentMapCodeName(String companyId, List<String> empCodes);

	/**
	 * [2] 社員IDリストから社員コードと表示名を取得する
	 *
	 * @param sIds List<社員ID>
	 * @return List<社員コードと表示名Imported 	>
	 */
	List<EmployeeInfoImport> getByListSid(List<String> sIds);

	/**
	 * [3] 社員の情報を取得する[アルゴリズム.<<Public>> 社員の情報を取得する( 社員IDリスト, 基準日, 取得したい社員情報 )]
	 *
	 * @param employeeIds   List<社員ID>
	 * @param baseDate 年月日
	 * @param param         取得したい社員情報
	 * @return List<EmployeeInformationImport>
	 */
	List<EmployeeInfoImported> getEmployeeInfo(
			List<String> employeeIds, 
			GeneralDate baseDate, 
			EmployeeInfoWantToBeGet param);

}
