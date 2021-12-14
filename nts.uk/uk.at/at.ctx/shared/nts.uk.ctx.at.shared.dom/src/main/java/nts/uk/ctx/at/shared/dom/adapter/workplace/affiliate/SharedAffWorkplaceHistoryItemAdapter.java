package nts.uk.ctx.at.shared.dom.adapter.workplace.affiliate;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

public interface SharedAffWorkplaceHistoryItemAdapter {

    /**
     * 期間と職場一覧から所属職場履歴項目を取得する
     *
     * @param period      期間
     * @param workplaceId List<職場ID>
     * @return List<所属職場履歴項目>
     */
    List<AffWorkplaceHistoryItemImport> getListAffWkpHistItem(DatePeriod period, List<String> workplaceId);
    
    /**
	 * [No.571]職場の上位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
}
