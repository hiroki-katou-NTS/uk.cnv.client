package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ManHourSummaryTableFormatDto {
    /** 工数集計表コード */
    private String code;
    /** 工数集計表名称 */
    private String name;
    /** 合計単位 */
    private int totalUnit;
    /** 表示形式 */
    private int displayFormat;
    /** 階層計・横計を表示する */
    private int dispHierarchy;
    /** */
    private List<SummaryItemDto> summaryItems;
}
