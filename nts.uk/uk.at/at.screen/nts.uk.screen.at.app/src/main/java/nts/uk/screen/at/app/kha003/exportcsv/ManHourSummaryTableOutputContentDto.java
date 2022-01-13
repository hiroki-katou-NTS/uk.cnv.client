package nts.uk.screen.at.app.kha003.exportcsv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItemDetail;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.VerticalValueDaily;
import nts.uk.screen.at.app.kha003.SummaryItemDetailDto;
import nts.uk.screen.at.app.kha003.VerticalValueDailyDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class ManHourSummaryTableOutputContentDto {
    /** 項目詳細 */
    private List<SummaryItemDetailDto> itemDetails;
    /** 縦計値 */
    private List<VerticalValueDailyDto> verticalTotalValues;
    /** 期間合計 */
    private String totalPeriod;
}
