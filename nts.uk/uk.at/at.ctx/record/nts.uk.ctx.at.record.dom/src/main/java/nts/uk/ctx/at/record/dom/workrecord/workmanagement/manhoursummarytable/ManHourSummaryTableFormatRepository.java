package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import java.util.List;
import java.util.Optional;

public interface ManHourSummaryTableFormatRepository {

    /**
     * [1] Insert(工数集計表フォーマット)
     * @param domain 工数集計表フォーマット
     */
    void insert(ManHourSummaryTableFormat domain);

    /**
     * [2] Update(工数集計表フォーマット)
     * @param domain 工数集計表フォーマット
     */
    void update(ManHourSummaryTableFormat domain);

    /**
     * [3] Delete(会社ID,コード)
     * @param companyId 会社ID
     * @param code コード
     */
    void delete(String companyId, String code);

    /**
     * [4] Get*
     * @return List<工数集計表フォーマット>
     */
    List<ManHourSummaryTableFormat> getAll(String cid);

    /**
     * [5] Get
     * @param code コード
     * @return 工数集計表フォーマット
     */
    Optional<ManHourSummaryTableFormat> get(String cid, String code);

}
