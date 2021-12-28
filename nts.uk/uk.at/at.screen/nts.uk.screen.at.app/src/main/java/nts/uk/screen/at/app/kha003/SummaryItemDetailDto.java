package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.DisplayInformation;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.SummaryItemDetail;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.VerticalValueDaily;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class SummaryItemDetailDto {
    private String code;
    /** 表示情報 */
    private DisplayInfoDto displayInfo;
    /** 子階層リスト */
    private List<SummaryItemDetailDto> childHierarchyList;
    /** 縦計リスト */
    private List<VerticalValueDailyDto> verticalTotalList;
    /** 期間合計 */
    private String totalPeriod;
}
