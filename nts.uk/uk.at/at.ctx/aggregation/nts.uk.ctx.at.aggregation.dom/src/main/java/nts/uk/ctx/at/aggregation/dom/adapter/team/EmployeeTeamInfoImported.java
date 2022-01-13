package nts.uk.ctx.at.aggregation.dom.adapter.team;

import java.util.Optional;

import lombok.Value;

/**
 * 社員所属チーム情報Imported
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.社員チーム.社員所属チーム情報Imported
 * @author dan_pv
 */
@Value
public class EmployeeTeamInfoImported {

	/**
	 *  社員ID
	 */
	private final String employeeID;

	/**
	 * チームコード
	 */
	private final Optional<String> teamCode;

	/**
	 * チーム名称
	 */
	private final Optional<String> teamName;
	
	/**
	 * emptyで作る
	 * @param employeeID 社員ID
	 * @return
	 */
	public static EmployeeTeamInfoImported createWithEmpty(String employeeID) {
		
		return new EmployeeTeamInfoImported(employeeID, Optional.empty(), Optional.empty());
	}

}
