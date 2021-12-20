package nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/**
 * 単一値との比較演算の種別
 *
 * @author Thanh.LNP
 */
@RequiredArgsConstructor
public enum SingleValueCompareType {
    /* 等しくない（≠） */
    NOT_EQUAL(0, "等しくない（≠）"),
    /* 等しい（＝） */
    EQUAL(1, "等しい（＝）"),
    /* 以下（≦） */
    LESS_OR_EQUAL(2, "以下（≦）"),
    /* 以上（≧） */
    GREATER_OR_EQUAL(3, "以上（≧）"),
    /* より小さい（＜） */
    LESS_THAN(4, "より小さい（＜）"),
    /* より大きい（＞） */
    GREATER_THAN(5, "より大きい（＞）");

    public final int value;
    public final String nameId;

    public static SingleValueCompareType of(int value) {
        return EnumAdaptor.valueOf(value, SingleValueCompareType.class);
    }
}
