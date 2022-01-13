package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 工数集計表名称
 */
@StringMaxLength(40)
public class ManHourSummaryTableName extends StringPrimitiveValue<ManHourSummaryTableName> {
    private static final long serialVersionUID = 1L;

    public ManHourSummaryTableName(String rawValue) {
        super(rawValue);
    }
}
