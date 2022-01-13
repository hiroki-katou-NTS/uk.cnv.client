package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employee.importeddto;

import lombok.Value;

/**
 * 所属職位Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.組織管理.社員情報.社員.Imported.社員情報Imported.所属職位Imported
 * @author dan_pv
 */
@Value
public class PositionImported {

	/**
	 * 職位ID
	 */
	private final String positionId;

	/**
	 * 職位コード
	 */
	private final String positionCode;
	
	/**
	 * 職位名称
	 */
	private final String positionName;
	
}
