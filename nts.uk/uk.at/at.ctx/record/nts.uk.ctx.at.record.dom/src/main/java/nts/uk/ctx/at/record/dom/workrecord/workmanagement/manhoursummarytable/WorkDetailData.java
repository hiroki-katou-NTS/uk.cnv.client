package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import org.apache.logging.log4j.util.Strings;

/**
 * TP: 作業詳細データ
 */
@AllArgsConstructor
@Getter
public class WorkDetailData {
    /** 社員ID */
    private final String employeeId;
    /** 年月日 */
    private final GeneralDate date;
    /** 応援勤務枠NO */
    private final int supportWorkFrameNo;
    /** 所属職場ID */
    private final String affWorkplaceId;
    /** 勤務職場ID */
    private final String workplaceId;
    /** 作業コード1 */
    private final String workCode1;
    /** 作業コード2 */
    private final String workCode2;
    /** 作業コード3 */
    private final String workCode3;
    /** 作業コード4 */
    private final String workCode4;
    /** 作業コード5 */
    private final String workCode5;
    /** 総労働時間 */
    private final int totalWorkingHours;

    /**
     * [1] 集計項目をマッピングする
     * @param itemType 集計項目種類
     * @return string
     */
    public String mapSummaryItem(SummaryItemType itemType) {
        switch (itemType) {
            case AFFILIATION_WORKPLACE:
                return affWorkplaceId;
            case WORKPLACE:
                return workplaceId;
            case EMPLOYEE:
                return employeeId;
            case TASK1:
                return workCode1;
            case TASK2:
                return workCode2;
            case TASK3:
                return workCode3;
            case TASK4:
                return workCode4;
            case TASK5:
                return workCode5;
        }
        return Strings.EMPTY;
    }
}
