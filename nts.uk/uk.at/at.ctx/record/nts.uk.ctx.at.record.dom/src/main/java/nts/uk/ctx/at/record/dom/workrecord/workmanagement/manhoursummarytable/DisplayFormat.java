package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import nts.arc.enums.EnumAdaptor;

/**
 * 	表示形式
 */
public enum DisplayFormat {
    /** 10進数 */
    DECIMAL(0),
    /** 60進数 */
    HEXA_DECIMAL(1),
    /** 分単位 */
    MINUTE(2);

    public final int value;
    DisplayFormat(int type){
        this.value = type;
    }

    public static DisplayFormat of(int value) {
        return EnumAdaptor.valueOf(value, DisplayFormat.class);
    }
}
