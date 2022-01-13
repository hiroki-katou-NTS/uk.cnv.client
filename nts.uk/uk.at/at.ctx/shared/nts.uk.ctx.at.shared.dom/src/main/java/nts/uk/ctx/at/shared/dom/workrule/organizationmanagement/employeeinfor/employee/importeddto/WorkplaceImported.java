package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import lombok.Value;

/**
 * 所属職場Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported.所属職場Imported
 * @author dan_pv
 */
@Value
public class WorkplaceImported {

	/**
	 * 職場ID
	 */
	private final String workplaceId;

	/**
	 * 職場コード
	 */
	private final String workplaceCode;
	
	/**
	 * 職場総称
	 */
	private final String workplaceGenericName;
	
	/**
	 * 職場表示名
	 */
	private final String workplaceName;
}
