package nts.uk.ctx.at.aggregation.dom.adapter.rank;

import java.util.Optional;

import lombok.Value;

/**
 * 社員ランク情報Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.社員ランク.社員ランク情報Imported
 * @author dan_pv
 */
@Value
public class EmployeeRankInfoImported {
	
	/**
	 *  社員ID
	 */
	private final String employeeID;

	/**
	 * ランクコード
	 */
	private final Optional<String> rankCode;

	/**
	 * ランク記号
	 */
	private final Optional<String> rankSymbol;
	
	/**
	 * emptyで作る
	 * @param employeeId 社員ID
	 * @return
	 */
	public static EmployeeRankInfoImported createWithEmpty(String employeeId) {
		
		return new EmployeeRankInfoImported(employeeId, Optional.empty(), Optional.empty());
	}

}
