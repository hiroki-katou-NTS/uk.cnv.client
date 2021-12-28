package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;


import nts.arc.enums.EnumAdaptor;

/**
 * 	合計単位
 */
public enum TotalUnit {
    /** 年月日 */
    DATE(0),
    /** 年月 */
    YEAR_MONTH(1);

    public final int value;
    TotalUnit(int type){
        this.value = type;
    }

    public static TotalUnit of(int value) {
        return EnumAdaptor.valueOf(value, TotalUnit.class);
    }
}
