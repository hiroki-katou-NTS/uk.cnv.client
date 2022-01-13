package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DetailFormatSettingDto {
    /** 表示形式 */
    private int displayFormat;
    /** 合計単位 */
    private int totalUnit;
    /** 縦計・横計を表示する */
    private int displayVerticalHorizontalTotal;
    private List<SummaryItemDto> summaryItems;
}
