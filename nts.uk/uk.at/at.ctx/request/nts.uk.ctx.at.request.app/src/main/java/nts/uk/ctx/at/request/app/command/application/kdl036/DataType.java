package nts.uk.ctx.at.request.app.command.application.kdl036;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/**
 * 対象データ種類
 */
@RequiredArgsConstructor
public enum DataType {

    /** 実績 */
    ACTUAL(0),
    
    /** 申請・スケジュール */
    APPLICATION_OR_SCHEDULE(1);
    
    public final int value;
    
    public static DataType of(int value) {
        return EnumAdaptor.valueOf(value, DataType.class);
    }
}
