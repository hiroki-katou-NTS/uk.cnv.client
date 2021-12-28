package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.persistence.internal.xr.ValueObject;

/**
 * 	集計項目
 */
@Getter
@AllArgsConstructor
public class SummaryItem extends ValueObject {
    /** 階層順番 */
    private int hierarchicalOrder;
    /** 集計項目種類 */
    private SummaryItemType summaryItemType;
}
