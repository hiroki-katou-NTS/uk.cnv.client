package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;

/**
 * 日々縦計値
 */
@AllArgsConstructor
@Getter
public class VerticalValueDaily {
    /** 作業時間 */
    private int workingHours;
    /** 年月 */
    private YearMonth yearMonth;
    /** 年月日 */
    private GeneralDate date;
    
}
