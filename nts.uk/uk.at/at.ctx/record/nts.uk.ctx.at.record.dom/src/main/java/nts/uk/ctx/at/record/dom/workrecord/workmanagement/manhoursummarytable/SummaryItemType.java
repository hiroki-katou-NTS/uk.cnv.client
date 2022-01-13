package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.arc.enums.EnumAdaptor;

/**
 * 集計項目種類
 */
public enum SummaryItemType {
    /** 所属職場 */
    AFFILIATION_WORKPLACE(0, "所属職場"),
    /** 勤務職場 */
    WORKPLACE(1, "勤務職場"),
    /**  */
    EMPLOYEE(2, "社員"),
    /** 作業1 */
    TASK1(3, "作業1"),
    /** 作業2 */
    TASK2(4, "作業2"),
    /** 作業3 */
    TASK3(5, "作業3"),
    /** 作業4 */
    TASK4(6, "作業4"),
    /** 作業5 */
    TASK5(7, "作業5");

    public int value;

    public String nameId;

    SummaryItemType(int value, String nameId) {
        this.value = value;
        this.nameId = nameId;
    }

    public static SummaryItemType of(int value) {
        return EnumAdaptor.valueOf(value, SummaryItemType.class);
    }
}
