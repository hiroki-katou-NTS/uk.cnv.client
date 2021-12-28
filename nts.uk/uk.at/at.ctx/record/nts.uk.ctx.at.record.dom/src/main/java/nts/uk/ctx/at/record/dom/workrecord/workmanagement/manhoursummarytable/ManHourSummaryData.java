package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * TP: 工数集計データ
 */
@AllArgsConstructor
@Getter
public class ManHourSummaryData {
    /**
     * マスタ名称情報
     */
    private final MasterNameInformation masterNameInfo;
    /**
     * 作業詳細リスト
     */
    private final List<WorkDetailData> workDetailDataList;
}
