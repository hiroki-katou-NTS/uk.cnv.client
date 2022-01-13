package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;

import java.util.List;

/**
 * 	工数集計表フォーマット
 */
@AllArgsConstructor
@Getter
public class ManHourSummaryTableFormat extends AggregateRoot {
    /** 工数集計表コード*/
    private final ManHourSummaryTableCode code;
    /** 工数集計表名称 */
    private ManHourSummaryTableName name;
    /** フォーマット詳細設定 */
    private DetailFormatSetting detailFormatSetting;

    /**
     * [1] 工数集計表出力内容を作成する
     * @param dateList List<年月日>
     * @param yearMonthList List<年月>
     * @param workDetailList List<作業詳細データ>
     * @param masterNameInfo マスタ名称情報
     * @return 工数集計表出力内容
     */
    public ManHourSummaryTableOutputContent createOutputContent(List<GeneralDate> dateList, List<YearMonth> yearMonthList,
                                                                List<WorkDetailData> workDetailList, MasterNameInformation masterNameInfo) {
        return detailFormatSetting.createManHourSummaryTableOutputContent(dateList, yearMonthList, workDetailList, masterNameInfo);
    }
}
