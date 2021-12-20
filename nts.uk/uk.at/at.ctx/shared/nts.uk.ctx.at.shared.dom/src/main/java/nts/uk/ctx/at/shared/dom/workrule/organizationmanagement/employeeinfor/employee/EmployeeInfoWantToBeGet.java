package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee;

import lombok.Value;

/**
 * 取得したい社員情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.取得したい社員情報
 * @author dan_pv
 */
@Value
public class EmployeeInfoWantToBeGet {
	
	/**
	 * 職場を取得する
	 */
	private final boolean isGetWorkplace;
	
	/**
	 * 部門を取得する
	 */
	private final boolean isGetDepartment;
	
	/**
	 * 職位を取得する
	 */
	private final boolean isGetJobTitle;
	
	/**
	 * 雇用を取得する
	 */
	private final boolean isGetEmployment;
	
	/**
	 * 分類を取得する
	 */
	private final boolean isGetClassification;

}
