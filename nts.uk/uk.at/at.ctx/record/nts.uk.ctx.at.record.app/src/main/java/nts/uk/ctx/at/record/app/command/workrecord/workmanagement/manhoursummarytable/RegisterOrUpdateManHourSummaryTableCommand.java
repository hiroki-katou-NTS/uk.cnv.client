package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class RegisterOrUpdateManHourSummaryTableCommand {
    /**
     * 工数集計表コード
     */
    private String code;
    /**
     * 工数集計表名称
     */
    private String name;
    /**
     * 合計単位
     */
    private int totalUnit;
    /**
     * 表示形式
     */
    private int displayFormat;
    /**
     * 階層計・横計を表示する
     */
    private int dispHierarchy;
    /** */
    private List<SummaryItemDto> summaryItems;

    public ManHourSummaryTableFormat toDomain() {
        return new ManHourSummaryTableFormat(
                new ManHourSummaryTableCode(this.code),
                new ManHourSummaryTableName(this.name),
                DetailFormatSetting.create(
                        DisplayFormat.of(this.displayFormat),
                        TotalUnit.of(this.totalUnit),
                        EnumAdaptor.valueOf(this.dispHierarchy, NotUseAtr.class),
                        summaryItems.stream().map(item -> new SummaryItem(item.getHierarchicalOrder(),
                                SummaryItemType.of(item.getSummaryItemType())))
                                .collect(Collectors.toList())
                )
        );
    }
}

@AllArgsConstructor
@Getter
class SummaryItemDto {
    private int hierarchicalOrder;
    private int summaryItemType;
}