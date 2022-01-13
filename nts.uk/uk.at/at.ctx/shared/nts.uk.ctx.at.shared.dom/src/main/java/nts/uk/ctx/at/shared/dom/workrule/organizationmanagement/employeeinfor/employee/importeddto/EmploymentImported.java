package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import lombok.Value;

/**
 * 所属雇用Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported.所属雇用Imported
 * @author dan_pv
 */
@Value
public class EmploymentImported {

	/**
	 * 雇用コード
	 */
	private final String employmentCode;
	
	/**
	 * 雇用名称
	 */
	private final String employmentName;
	
}
