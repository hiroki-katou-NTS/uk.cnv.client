package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SummaryItemDto {
    /** A6_4 大分類パネル	*/
    private int hierarchicalOrder;
    private int itemType;
    /** 大分類名 */
    private String itemTypeName;
}
