package nts.uk.file.at.app.export.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableOutputContent;
import nts.uk.screen.at.app.kha003.ManHourSummaryTableFormatDto;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourHierarchyFlatData;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourSummaryTableOutputContentDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ManHourSummaryExportData {
    private ManHourSummaryTableFormatDto summaryTableFormat;
    private ManHourSummaryTableOutputContentDto outputContent;
    private ManHourPeriod period;
    private List<ManHourHierarchyFlatData> dataExport;
    private int totalLevel;
}
