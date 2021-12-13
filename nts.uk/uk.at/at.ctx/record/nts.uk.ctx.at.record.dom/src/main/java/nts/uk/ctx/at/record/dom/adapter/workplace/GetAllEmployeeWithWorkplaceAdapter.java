package nts.uk.ctx.at.record.dom.adapter.workplace;

import java.util.Map;

import nts.arc.time.GeneralDate;

/**
 * 
 * @author sonnlb
 * 
 *         全ての職場の所属社員を取得するAdapter
 *
 */
public interface GetAllEmployeeWithWorkplaceAdapter {
	/**
	 * @name 取得する
	 * @param companyId 会社ID	会社ID
	 * @param baseDate 	基準日	年月日
	 * @return 	所属職場リスト	List＜所属職場履歴項目＞
	 */
	Map<String, String> get(String companyId, GeneralDate baseDate);
}
