package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import lombok.Value;

/**
 * 所属部門Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported.所属部門Imported
 * @author dan_pv
 */
@Value
public class DepartmentImported {

	/**
	 * 部門ID
	 */
	private final String departmentId;

	/**
	 * 部門コード
	 */
	private final String departmentCode;

	/**
	 * 部門総称
	 */
	private final String departmentGeneric;

	/**
	 * 部門表示名
	 */
	private final String departmentDisplayName;

}
