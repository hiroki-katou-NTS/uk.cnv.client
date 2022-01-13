package nts.uk.screen.at.app.kha003.exportcsv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableOutputContent;
import nts.uk.screen.at.app.kha003.ManHourSummaryTableFormatDto;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;

@AllArgsConstructor
@Getter
public class ManHourDataSummaryQuery {
    /** 工数集計表フォーマット */
    private ManHourSummaryTableFormatDto summaryTableFormat;

    /** 工数集計表出力内容 */
    private ManHourSummaryTableOutputContentDto outputContent;

    private ManHourPeriod period;
}
