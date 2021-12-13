package nts.uk.ctx.at.aggregation.dom.adapter.team;

import java.util.List;

/**
 * 社員所属チーム情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.社員チーム.社員所属チーム情報
 * @author dan_pv
 */
public interface EmployeeTeamInfoAdapter {
	
	/**
	 * 取得する
	 * @param employeeIds 社員リスト
	 * @return List<社員所属チーム情報Imported>
	 */
	public List<EmployeeTeamInfoImported> get(List<String> employeeIds);

}
