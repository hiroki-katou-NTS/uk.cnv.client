package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * 社員情報Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported
 * 
 * @author dan_pv
 */
@Value
@AllArgsConstructor
public class EmployeeInfoImported {

	/**
	 * 社員ID
	 */
	private final String employeeId;

	/**
	 * 社員コード
	 */
	private final String employeeCode;

	/**
	 * ビジネスネーム
	 */
	private final String businessName;

	/**
	 * ビジネスネームカナ
	 */
	private final String businessNameKana;

	/**
	 * 所属職場
	 */
	private final Optional<WorkplaceImported> workplace;

	/**
	 * 所属部門
	 */
	private final Optional<DepartmentImported> department;

	/**
	 * 所属職位
	 */
	private final Optional<PositionImported> position;

	/**
	 * 所属雇用
	 */
	private final Optional<EmploymentImported> employment;

	/**
	 * 所属分類
	 */
	private final Optional<ClassificationImported> classification;

}
