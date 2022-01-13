package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import lombok.Value;

/**
 * 所属分類Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported.所属分類Imported
 * @author dan_pv
 *
 */
@Value
public class ClassificationImported {

	/**
	 * 分類コード
	 */
	private final String classificationCode;
	
	/**
	 * 分類名称
	 */
	private final String classificationName;
}
