package nts.uk.ctx.at.aggregation.dom.adapter.rank;

import java.util.List;

/**
 * 社員ランク情報
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.Imported.社員ランク.社員ランク情報
 * @author dan_pv
 */
public interface EmployeeRankInfoAdapter {
	
	/**
	 * 取得する
	 * @param employeeIds 社員リスト
	 * @return List<社員ランク情報Imported>
	 */
	public List<EmployeeRankInfoImported> get(List<String> employeeIds);

}
