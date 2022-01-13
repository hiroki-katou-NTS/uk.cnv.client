package nts.uk.ctx.at.shared.dom.workrecord.alarm.attendanceitemconditions;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/**
 * 範囲との比較演算の種別
 *
 * @author Thanh.LNP
 */
@RequiredArgsConstructor
public enum RangeCompareType {
    /* 範囲の間（境界値を含まない）（＜＞） */
    BETWEEN_RANGE_OPEN(6, "範囲の間（境界値を含まない）（＜＞）"),
    /* 範囲の間（境界値を含む）（≦≧） */
    BETWEEN_RANGE_CLOSED(7, "範囲の間（境界値を含む）（≦≧）"),
    /* 範囲の外（境界値を含まない）（＞＜） */
    OUTSIDE_RANGE_OPEN(8, "範囲の外（境界値を含まない）（＞＜）"),
    /* 範囲の外（境界値を含む）（≧≦） */
    OUTSIDE_RANGE_CLOSED(9, "範囲の外（境界値を含む）（≧≦）");

    public final int value;
    public final String nameId;

    public static RangeCompareType of(int value) {
        return EnumAdaptor.valueOf(value, RangeCompareType.class);
    }
}
