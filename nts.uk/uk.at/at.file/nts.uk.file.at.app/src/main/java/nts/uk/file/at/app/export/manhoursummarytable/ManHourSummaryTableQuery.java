package nts.uk.file.at.app.export.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableOutputContent;
import nts.uk.screen.at.app.kha003.ManHourSummaryTableFormatDto;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourSummaryTableOutputContentDto;

@AllArgsConstructor
@Getter
public class ManHourSummaryTableQuery {
    /** 工数集計表フォーマット */
    private ManHourSummaryTableFormatDto summaryTableFormat;

    /** 工数集計表出力内容 */
    private ManHourSummaryTableOutputContentDto outputContent;

    private ManHourPeriod period;
}
